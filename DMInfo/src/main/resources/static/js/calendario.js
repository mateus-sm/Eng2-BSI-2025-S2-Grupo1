const PALETA_CORES = [
    '#d90429',
    '#007f5f',
    '#023e8a',
    '#f77f00',
    '#6a040f',
    '#736eb1',
    '#00b4d8',
    '#2add70'
];

async function sincronizarAgenda() {
    const btn = document.getElementById('btn-sync-calendar');
    const iconeOriginal = '<i class="bi bi-calendar-check-fill"></i>';

    btn.disabled = true;
    btn.innerHTML = '<i class="bi bi-arrow-repeat"></i> Sincronizando...';

    try {
        const response = await fetch('/api/calendar/sync', { method: 'POST' });
        const resultText = await response.text();

        if (resultText.includes("Erro: A autorização do Google não foi concluída")) {
            alert("Autorização do Google Calendar é necessária. Redirecionando para o login do Google...");
            sessionStorage.setItem('googleAuthRedirect', 'true');
            window.location.href = '/api/calendar/oauth/start';
        }
        else
            alert(resultText);

    } catch (error) {
        console.error('Erro ao sincronizar:', error);
        alert('Ocorreu um erro inesperado ao tentar sincronizar.');
    } finally {
        btn.disabled = false;
        btn.innerHTML = `${iconeOriginal} Sincronizar Google Agenda`;
    }
}

function transformarAtividadeParaEvento(atividade) {
    let titulo;
    let idTipoAtividade;

    if (atividade.atv && atividade.atv.descricao)
        titulo = atividade.atv.descricao;
    else
        titulo = 'Atividade (sem nome)';

    if (atividade.atv)
        idTipoAtividade = atividade.atv.id;
    else
        idTipoAtividade = 0;

    const dataInicio = atividade.dtIni;
    const horaInicio = atividade.horario || '00:00:00';
    const startDateTime = `${dataInicio}T${horaInicio}`;

    const dataFim = atividade.dtFim || atividade.dtIni;
    const horaFim = '23:59:00';
    const fimDataHora = `${dataFim}T${horaFim}`;

    const corEvento = PALETA_CORES[idTipoAtividade % PALETA_CORES.length];

    return {
        id: atividade.id,
        title: titulo,
        start: startDateTime,
        end: fimDataHora,
        allDay: false,
        color: corEvento,
        borderColor: corEvento,
        textColor: '#ffffff'
    };
}

document.addEventListener('DOMContentLoaded', async () => {
    const calendarioElemento = document.getElementById('calendario');

    // Variáveis locais de estado
    let todasAtividades = [];
    let atividadesNoCalendarioIds = [];
    let todosOsMembros = []; // Lista completa de membros (id, nome, etc.)
    let membrosAtividadeAtual = [];
    let idAtividadeModal = null;

    function getOntem() {
        const hoje = new Date();
        hoje.setDate(hoje.getDate() - 1);
        hoje.setHours(0, 0, 0, 0);
        return hoje;
    }

    // Salva ou remove a associação Atividade-Calendário (BD)
    async function persistirEstado(id, acao) {
        const idNum = parseInt(id);
        const method = acao === 'adicionar' ? 'POST' : 'DELETE';

        // --- INÍCIO DA VALIDAÇÃO (Aplicável apenas ao adicionar) ---
        if (acao === 'adicionar') {
            const atividade = todasAtividades.find(a => a.id === idNum);

            if (!atividade) {
                alert("Erro: Atividade não encontrada nos dados carregados.");
                return;
            }

            let mensagemErro = '';

            // 1. Verificação de Datas (dtIni E dtfim não nulas)
            if (!atividade.dtIni || !atividade.dtFim || atividade.dtIni === '' || atividade.dtFim === '') {
                mensagemErro += "• A atividade deve ter Data de Início E Data de Fim preenchidas.\n";
            }

            // 2. Verificação de Membros (mínimo 3)
            // Faz a busca de membros associados em tempo real (mais seguro)
            const idsMembrosAtuais = await buscarMembrosPorAtividade(idNum);

            if (idsMembrosAtuais.length < 3) {
                mensagemErro += `• A atividade deve ter no mínimo 3 membros selecionados (Membros atuais: ${idsMembrosAtuais.length}).\n`;
            }

            // Se houver erros, exibe o alert e interrompe
            if (mensagemErro !== '') {
                alert(`A atividade não pode ser adicionada ao calendário devido às seguintes pendências:\n\n${mensagemErro}`);
                return;
            }
        }
        // --- FIM DA VALIDAÇÃO ---

        try {
            const response = await fetch(`/apis/calendario/${idNum}`, { method: method });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Falha na persistência: ${errorText}`);
            }

            // Sucesso! Recarrega o estado e o calendário
            await carregarEstadoEEventos();

        } catch (error) {
            alert(`Erro ao ${acao} a atividade no calendário: ${error.message}`);
        }
    }

    // Função para redirecionar para edição
    function editarAtividade(id) {
        window.location.href = `/app/finalizar-atividades?id=${id}`;
    }

    // -----------------------------------------------------
    // FUNÇÕES DE GERENCIAMENTO DE MEMBROS (MODAL)
    // -----------------------------------------------------

    function mostrarGerenciadorMembros() {
        const backdrop = document.getElementById('membros-modal-backdrop');
        const container = document.getElementById('membros-modal-container');
        if (backdrop && container) {
            backdrop.style.display = 'block';
            container.style.display = 'block';
        }
    }

    window.fecharGerenciadorMembros = function() {
        const backdrop = document.getElementById('membros-modal-backdrop');
        const container = document.getElementById('membros-modal-container');
        if (backdrop && container) {
            backdrop.style.display = 'none';
            container.style.display = 'none';
        }
    }

    // Busca a lista completa de membros do BD
    async function buscarTodosOsMembros() {
        try {
            const response = await fetch('/apis/membro');
            if (!response.ok) throw new Error("Falha ao buscar lista completa de membros.");
            // Garante que todosOsMembros estão prontos para uso em qualquer lugar
            todosOsMembros = await response.json();
        } catch (e) {
            console.error("Erro ao carregar lista de membros:", e);
            todosOsMembros = [];
        }
    }

    // Busca os IDs dos membros associados a uma atividade específica
    async function buscarMembrosPorAtividade(idCriacao) {
        try {
            const response = await fetch(`/apis/membro/atividade/${idCriacao}`);
            if (!response.ok) throw new Error("Falha ao buscar membros associados.");
            return await response.json(); // Retorna lista de IDs de membros associados
        } catch (e) {
            console.error(e);
            return [];
        }
    }

    // Busca nomes dos membros com base nos IDs e na lista 'todosOsMembros'
    async function buscarNomesDosMembros(idAtividade) {
        // 1. Garante que temos a lista completa de membros
        if (todosOsMembros.length === 0) {
            await buscarTodosOsMembros();
        }

        // 2. Busca os IDs associados a esta atividade
        const idsAssociados = await buscarMembrosPorAtividade(idAtividade);

        // 3. Mapeia IDs para nomes, usando a lista completa
        const nomesMembros = idsAssociados
            .map(id => {
                const membro = todosOsMembros.find(m => (m.id || m.id_membro) === id);
                return membro ? (membro.usuario ? membro.usuario.nome : membro.nome) : null;
            })
            .filter(nome => nome !== null) // Remove nulos
            .sort(); // Opcional: ordena alfabeticamente

        return nomesMembros;
    }

    window.manipularAssociacao = async (checkbox) => {
        const idMembro = parseInt(checkbox.dataset.membroId);
        const idAtividade = idAtividadeModal;
        const acao = checkbox.checked ? 'adicionar' : 'remover';

        const url = `/apis/membro/atividade/${idAtividade}/${idMembro}`;
        const method = checkbox.checked ? 'POST' : 'DELETE';

        try {
            const response = await fetch(url, { method: method });
            if (!response.ok) {
                checkbox.checked = !checkbox.checked;
                throw new Error(`Falha ao ${acao} membro na atividade.`);
            }

            if (acao === 'adicionar') {
                membrosAtividadeAtual.push(idMembro);
            } else {
                membrosAtividadeAtual = membrosAtividadeAtual.filter(id => id !== idMembro);
            }

        } catch (e) {
            alert("Erro de salvamento: " + e.message);
        }
    };

    function renderizarListaGerenciamentoMembros() {
        const modalTitulo = document.getElementById('membros-modal-titulo');
        const modalBody = document.getElementById('membros-modal-body');

        if (!modalBody) {
            console.warn("Estrutura HTML do modal incompleta.");
            return;
        }

        if (modalTitulo) {
            const atividadeAtual = todasAtividades.find(a => a.id === idAtividadeModal);
            const tituloAtividade = atividadeAtual ? (atividadeAtual.atv ? atividadeAtual.atv.descricao : 'Atividade') : 'ID Desconhecido';
            modalTitulo.textContent = `Gerenciar Membros: ${tituloAtividade} (ID: ${idAtividadeModal})`;
        }

        modalBody.innerHTML = '';

        if (todosOsMembros.length === 0) {
            modalBody.innerHTML = '<p>Nenhum membro cadastrado para atribuição.</p>';
            return;
        }

        todosOsMembros.forEach(membro => {
            const idMembro = membro.id || membro.id_membro;
            const nomeMembro = membro.usuario ? membro.usuario.nome : membro.nome;
            const estaAssociado = membrosAtividadeAtual.includes(idMembro);

            modalBody.innerHTML += `
                <div>
                    <label>
                        <input type="checkbox" 
                               data-membro-id="${idMembro}"
                               ${estaAssociado ? 'checked' : ''}
                               onchange="manipularAssociacao(this)">
                        ${nomeMembro || 'Nome não disponível'}
                    </label>
                </div>
            `;
        });
    }

    async function abrirGerenciadorMembros(idCriacao) {
        idAtividadeModal = parseInt(idCriacao);

        if (todosOsMembros.length === 0) {
            await buscarTodosOsMembros();
        }

        const idsAtivos = await buscarMembrosPorAtividade(idCriacao);
        membrosAtividadeAtual = idsAtivos;

        renderizarListaGerenciamentoMembros();
        mostrarGerenciadorMembros();
    }

    // -----------------------------------------------------
    // FUNÇÃO DE CARREGAMENTO E FILTRO
    // -----------------------------------------------------

    function renderizarListaAtividades(lista, idsAtivos) {
        const listaElemento = document.getElementById('lista-atividades-lateral');
        if (!listaElemento) return;

        listaElemento.innerHTML = '';

        if (lista.length === 0) {
            listaElemento.innerHTML = '<p class="status-empty">Nenhuma atividade cadastrada.</p>';
            return;
        }

        lista.forEach(atividade => {
            const estaNoCalendario = idsAtivos.includes(atividade.id);
            const cor = transformarAtividadeParaEvento(atividade).color;

            let dataDisplay = '';
            if (atividade.dtIni) {
                dataDisplay += `Início: ${atividade.dtIni}`;
            }
            if (atividade.dtFim) {
                dataDisplay += ` | Fim: ${atividade.dtFim}`;
            }

            const item = document.createElement('div');
            item.classList.add('atividade-item');
            item.dataset.id = atividade.id;
            item.style.borderLeft = `5px solid ${cor}`;

            item.innerHTML = `
                <div class="item-header">
                    <span class="item-title">${atividade.atv ? atividade.atv.descricao : 'Atividade'}</span>
                    <span class="item-local">${atividade.local || ''}</span>
                </div>
                <div class="item-data">
                    ${dataDisplay}
                </div>
                <div class="item-acoes">
                    <button class="btn-membros" data-id="${atividade.id}" title="Adicionar Membros">&#128100;</button> 
                    <button class="btn-editar" data-id="${atividade.id}" title="Editar">&#9998;</button>
                    <button class="btn-toggle-calendario ${estaNoCalendario ? 'btn-remover' : 'btn-adicionar'}" data-id="${atividade.id}" title="${estaNoCalendario ? 'Remover do Calendário' : 'Adicionar ao Calendário'}">
                        ${estaNoCalendario ? '&minus;' : '+'}
                    </button>
                </div>
            `;
            listaElemento.appendChild(item);
        });
    }

    async function carregarEstadoEEventos() {
        try {
            // Garante que a lista de todos os membros está carregada para uso posterior (eventClick)
            await buscarTodosOsMembros();

            const respostaAtividades = await fetch('/apis/calendario');
            if (!respostaAtividades.ok) throw new Error('Falha ao buscar atividades.');

            const respostaAtivos = await fetch('/apis/calendario/ativas');
            if (!respostaAtivos.ok) throw new Error('Falha ao buscar IDs ativos.');

            todasAtividades = await respostaAtividades.json();
            atividadesNoCalendarioIds = await respostaAtivos.json();

            const mostrarRealizadas = document.getElementById('mostrar-realizadas-checkbox')?.checked || false;
            let atividadesParaExibir = todasAtividades;

            if (!mostrarRealizadas) {
                const ontem = getOntem();

                atividadesParaExibir = todasAtividades.filter(atividade => {
                    if (!atividade.dtFim) return true;

                    const dataFim = new Date(atividade.dtFim.replace(/-/g, '/'));
                    return dataFim >= ontem;
                });
            }

            renderizarListaAtividades(atividadesParaExibir, atividadesNoCalendarioIds);
            calendario.refetchEvents();

        } catch (error) {
            console.error("Erro ao carregar estado inicial:", error);
            alert("Não foi possível carregar o estado das atividades. " + error.message);
        }
    }


    // -----------------------------------------------------
    // INICIALIZAÇÃO DO FULLCALENDAR
    // -----------------------------------------------------

    const calendario = new FullCalendar.Calendar(calendarioElemento, {
        initialView: 'dayGridMonth',
        locale: 'pt-br',
        eventDisplay: 'block',
        showNonCurrentDates: false,
        eventTimeFormat: {
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
        },
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        buttonText: {
            today: 'Hoje',
            month: 'Mês',
            week: 'Semana',
            day: 'Dia'
        },

        events: function(info, sucesso, falha) {
            try {
                const eventosAtivos = todasAtividades
                    .filter(atividade => atividadesNoCalendarioIds.includes(atividade.id))
                    .map(transformarAtividadeParaEvento);

                sucesso(eventosAtivos);
            } catch (error) {
                console.error("Erro ao carregar eventos filtrados:", error);
                falha(error);
            }
        },

        dateClick: function(info) {
            calendario.changeView('timeGridDay', info.dateStr);
        },

        // CORRIGIDO: Manipulador de clique do evento para incluir membros sem alert temporário
        eventClick: async function(info) {
            const idAtividade = info.event.id;

            const inicio = info.event.start.toLocaleString('pt-BR', {
                day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit'
            });
            const fim = info.event.end.toLocaleString('pt-BR', {
                day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit'
            });

            try {
                const nomesMembros = await buscarNomesDosMembros(idAtividade);

                let membrosFormatados;
                if (nomesMembros.length > 0) {
                    // CORRIGIDO: Usa '\n' para exibir um nome por linha
                    membrosFormatados = nomesMembros.join('\n');
                } else {
                    membrosFormatados = 'Nenhum membro associado.';
                }

                // Exibe o alerta FINAL com os membros
                alert(
                    `Atividade: ${info.event.title}\n\n` +
                    `Início: ${inicio}h\n` +
                    `Fim: ${fim}h\n\n` +
                    `Membros Participantes:\n${membrosFormatados}`
                );

            } catch (error) {
                console.error("Erro ao buscar membros para evento:", error);
                alert(
                    `Atividade: ${info.event.title}\n\n` +
                    `Início: ${inicio}h\n` +
                    `Fim: ${fim}h\n\n` +
                    `Membros Participantes:\nErro ao carregar a lista.`
                );
            }
        },

        eventContent: function(arg) {
            let conteudoHtml = '';

            if (arg.isStart && arg.timeText)
                conteudoHtml += `<span class="fc-event-time">${arg.timeText}</span> `;

            conteudoHtml += `<span class="fc-event-title">${arg.event.title}</span>`;

            return {
                html: conteudoHtml
            };
        }
    });

    // -----------------------------------------------------
    // CHAMA CARREGAMENTO INICIAL E LISTENERS
    // -----------------------------------------------------

    await carregarEstadoEEventos();
    calendario.render();

    document.getElementById('btn-sync-calendar').addEventListener('click', sincronizarAgenda);

    document.addEventListener('change', (event) => {
        if (event.target.id === 'mostrar-realizadas-checkbox') {
            carregarEstadoEEventos();
        }
    });

    document.addEventListener('click', (event) => {
        const target = event.target;

        if (target.classList.contains('btn-toggle-calendario')) {
            const id = target.dataset.id;
            const acao = target.classList.contains('btn-adicionar') ? 'adicionar' : 'remover';
            persistirEstado(id, acao);

        } else if (target.classList.contains('btn-editar')) {
            const id = target.dataset.id;
            editarAtividade(id);

        } else if (target.classList.contains('btn-membros')) {
            const id = target.dataset.id;
            abrirGerenciadorMembros(id);
        }

        if (target.id === 'membros-modal-backdrop' || target.classList.contains('fechar-modal-btn')) {
            fecharGerenciadorMembros();
        }
    });

    // Verifica se voltamos de uma autorização do Google
    if (sessionStorage.getItem('googleAuthRedirect') === 'true') {
        sessionStorage.removeItem('googleAuthRedirect');
        setTimeout(() => {
            alert("Autorização concluída! Tente sincronizar novamente.");
        }, 500);
    }
});