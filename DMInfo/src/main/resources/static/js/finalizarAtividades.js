// finalizarAtividades.js

function aplicarMascaraDinheiro(valor) {
    if (valor === null || valor === undefined || valor === '') {
        return '';
    }

    if (typeof valor === 'number') {
        valor = Math.round(valor * 100).toString();
    } else {
        valor = valor.toString().replace(/\D/g, '');
    }

    if (valor === '') {
        return '0,00';
    }

    while (valor.length < 3) {
        valor = '0' + valor;
    }

    let intPart = valor.slice(0, -2);
    let decimalPart = valor.slice(-2);

    intPart = intPart.replace(/^0+/, '') || '0';

    intPart = intPart.replace(/\B(?=(\d{3})+(?!\d))/g, ".");

    return `${intPart},${decimalPart}`;
}

function limparDinheiroParaEnvio(valorFormatado) {
    if (!valorFormatado) {
        return 0.0;
    }

    const valorLimpo = valorFormatado.toString().replace(/\./g, '').replace(',', '.').trim();

    const floatValue = parseFloat(valorLimpo);
    return isNaN(floatValue) ? 0.0 : floatValue;
}

function formatarDinheiro(valor) {
    if (valor === null || valor === undefined || isNaN(valor)) {
        return 'R$ 0,00';
    }
    const numericValue = typeof valor === 'string' ? parseFloat(valor) : valor;

    return numericValue.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function formatarDataParaExibir(dataString) {
    if (!dataString) {
        return '';
    }
    const data = new Date(dataString.replace(/-/g, '/'));
    return data.toLocaleDateString('pt-BR');
}

function getHojeFormatado() {
    const hoje = new Date();
    const ano = hoje.getFullYear();
    const mes = String(hoje.getMonth() + 1).padStart(2, '0');
    const dia = String(hoje.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
}

function formatarDataParaInput(dataString) {
    if (!dataString) {
        return '';
    }
    const data = new Date(dataString.replace(/-/g, '/'));
    const ano = data.getFullYear();
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const dia = String(data.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
}

// carregarAtividades agora aceita um termo de busca e filtra os dados antes de renderizar.
// O termo de busca é o único "cache" de filtro que existirá
async function carregarAtividades(ordenacao = 'alfabetica', termoBusca = '') {
    const tabelaCorpo = document.getElementById('tabela-corpo');
    const hojeFormatado = getHojeFormatado();
    tabelaCorpo.innerHTML = `<tr><td colspan="10" class="status-loading">Carregando atividades...</td></tr>`;
    const termo = termoBusca.toLowerCase();

    try {
        const response = await fetch('/apis/finalizar-atividades');
        if (!response.ok) {
            throw new Error(`Falha ao buscar dados: ${response.statusText}`);
        }

        let atividades = await response.json();

        // --- LÓGICA DE FILTRO (Busca) APLICADA AO ARRAY ANTES DA RENDERIZAÇÃO ---
        if (termo !== '') {
            atividades = atividades.filter(atividade => {
                const admin = (atividade.admin && atividade.admin.usuario ? atividade.admin.usuario.login : '').toLowerCase();
                const nomeAtividade = (atividade.atv ? atividade.atv.descricao : '').toLowerCase();
                const local = (atividade.local || '').toLowerCase();

                return nomeAtividade.includes(termo) ||
                    admin.includes(termo) ||
                    local.includes(termo);
            });
        }
        // --- FIM LÓGICA DE FILTRO ---

        tabelaCorpo.innerHTML = '';

        if (atividades.length === 0) {
            tabelaCorpo.innerHTML = `<tr><td colspan="10" class="status-empty">Nenhuma atividade ${termo !== '' ? 'encontrada com o termo de busca.' : 'cadastrada.'}</td></tr>`;
            return;
        }

        atividades.sort((a, b) => {
            const statusA = a.status === true;
            const statusB = b.status === true;
            const nomeA = a.atv ? a.atv.descricao.toLowerCase() : '';
            const nomeB = b.atv ? b.atv.descricao.toLowerCase() : '';

            if (ordenacao === 'status_aberta') {
                if (statusA !== statusB) {
                    return statusA ? 1 : -1;
                }
            } else if (ordenacao === 'status_finalizada') {
                if (statusA !== statusB) {
                    return statusA ? -1 : 1;
                }
            }

            if (nomeA < nomeB) return -1;
            if (nomeA > nomeB) return 1;
            return 0;
        });


        atividades.forEach(atividade => {
            const linha = document.createElement('tr');

            if (atividade.status === true) {
                linha.classList.add('status-finalizada');
            } else {
                linha.classList.add('status-aberta');
            }

            let adminUsuario = 'N/A';
            if (atividade.admin && atividade.admin.usuario) {
                adminUsuario = atividade.admin.usuario.login;
            }

            let atividadeNome = 'N/A';
            if (atividade.atv) {
                atividadeNome = atividade.atv.descricao;
            }

            const dataIniFormatada = formatarDataParaInput(atividade.dtIni);
            const dataFimFormatada = formatarDataParaInput(atividade.dtFim);
            const finalizadaChecked = atividade.status === true ? 'checked' : '';

            // Define a classe do label para carregamento inicial
            const labelClass = atividade.status === true ? 'is-checked' : '';

            const custoRealValorLimpo = atividade.custoreal;
            const custoRealFormatadoDisplay = formatarDinheiro(custoRealValorLimpo);
            const custoRealParaInput = aplicarMascaraDinheiro(custoRealValorLimpo);

            const observacoesText = atividade.observacoes || '';


            linha.innerHTML = `
                <td>${adminUsuario}</td>
                <td>${atividadeNome}</td>
                <td>${atividade.local || ''}</td>
                <td>${atividade.horario || ''}</td>
                <td>
                    <input type="date" 
                           class="input-data-ini" 
                           id="data-ini-${atividade.id}" 
                           value="${dataIniFormatada}">
                </td>
                <td>
                    <input type="date" 
                           class="input-data-fim" 
                           id="data-fim-${atividade.id}" 
                           value="${dataFimFormatada}"
                           min="${dataIniFormatada}">
                </td>
                <td>${formatarDinheiro(atividade.custoprevisto)}</td>
                <td id="custo-real-cell-${atividade.id}">
                    <div class="custo-real-cell-container">
                        <span class="custo-real-display" id="custo-real-display-${atividade.id}">
                            ${custoRealFormatadoDisplay}
                        </span>
                        <input type="text"
                               class="input-custo-real"
                               id="custo-real-input-${atividade.id}"
                               data-id="${atividade.id}"
                               value="${custoRealParaInput}"
                               style="display: none;">
                        <span class="btn-editar-custo" data-id="${atividade.id}">
                            &#9998;
                        </span>
                    </div>
                </td>
                <td id="observacoes-cell-${atividade.id}" data-observacoes="${observacoesText}">
                    ${observacoesText}
                </td>
                <td>
                    <div class="acao-container"> 
                        <label class="checkbox-container ${labelClass}">
                            <input type="checkbox" id="finalizar-${atividade.id}" ${finalizadaChecked} class="input-finalizar">
                            Finalizar
                        </label>
                        <button class="btn-salvar-linha" data-id="${atividade.id}" title="Salvar esta linha">
                            💾
                        </button>
                    </div>
                </td>
            `;
            tabelaCorpo.appendChild(linha);
        });

    } catch (error) {
        tabelaCorpo.innerHTML = `<tr><td colspan="10" class="status-error">${error.message}</td></tr>`;
    }
}

// CORRIGIDO: Função para disparar o carregamento e filtro ao clicar na lupa (SEM VARIÁVEL GLOBAL)
function filtrarAtividades() {
    // Pega o valor da ordenação atual para manter a ordem
    const ordenacaoAtual = document.getElementById('ordenacao-select').value;
    // Pega o valor do campo de busca
    const termoBusca = document.getElementById('barra-pesquisa').value;

    // Recarrega os dados da API (fetch) e renderiza a tabela com o filtro aplicado
    carregarAtividades(ordenacaoAtual, termoBusca);
}

function toggleCustoReal(id) {
    const displaySpan = document.getElementById(`custo-real-display-${id}`);
    const inputElement = document.getElementById(`custo-real-input-${id}`);
    const editButton = document.querySelector(`.btn-editar-custo[data-id="${id}"]`);

    if (displaySpan.style.display !== 'none') {
        displaySpan.style.display = 'none';
        editButton.style.display = 'none';
        inputElement.style.display = 'inline-block';
        inputElement.focus();
    } else {

        const valorDigitado = limparDinheiroParaEnvio(inputElement.value);
        displaySpan.textContent = formatarDinheiro(valorDigitado);

        inputElement.value = aplicarMascaraDinheiro(valorDigitado);

        displaySpan.style.display = 'inline-block';
        editButton.style.display = 'inline-block';
        inputElement.style.display = 'none';
    }
}

async function salvarDataUnica(id, botao) {
    const inputDtIni = document.getElementById(`data-ini-${id}`);
    const inputDtFim = document.getElementById(`data-fim-${id}`);
    const inputCustoReal = document.getElementById(`custo-real-input-${id}`);
    const inputFinalizar = document.getElementById(`finalizar-${id}`);

    // Lendo o valor de observação da célula de exibição
    const obsCell = document.getElementById(`observacoes-cell-${id}`);
    const observacoesValor = obsCell.textContent.trim();


    const novaDtIni = inputDtIni.value;
    const novaDtFim = inputDtFim.value;
    const novoStatus = inputFinalizar.checked;

    const novoCustoReal = limparDinheiroParaEnvio(inputCustoReal.value);

    let mensagemErro = '';

    // Pega a ordenação atual para recarregar a tabela corretamente no finally
    const ordenacaoAtual = document.getElementById('ordenacao-select').value;
    const termoBusca = document.getElementById('barra-pesquisa').value;

    // --- VALIDAÇÃO PARA FINALIZAR (Status = true) ---
    if (novoStatus === true) {
        const ontem = new Date();
        ontem.setDate(ontem.getDate() - 1);
        ontem.setHours(0, 0, 0, 0); // Zera hora para comparação de data

        // Usa replace(/-/g, '/') para garantir o parsing correto em diferentes navegadores
        const dtIniDate = novaDtIni ? new Date(novaDtIni.replace(/-/g, '/')) : null;
        const dtFimDate = novaDtFim ? new Date(novaDtFim.replace(/-/g, '/')) : null;

        // 1. dtIni não nula e > ontem
        if (!novaDtIni) {
            mensagemErro += '• Data Início não pode ser nula.\n';
        } else if (dtIniDate <= ontem) {
            mensagemErro += '• Data Início deve ser posterior à ontem.\n';
        }

        // 2. dtfim não nula e > ontem
        if (!novaDtFim) {
            mensagemErro += '• Data Fim não pode ser nula.\n';
        } else if (dtFimDate <= ontem) {
            mensagemErro += '• Data Fim deve ser posterior à ontem.\n';
        }

        // 3. Novo: dtFim não pode ser maior que dtIni
        if (dtIniDate && dtFimDate && dtFimDate < dtIniDate) {
            mensagemErro += '• Data Fim não pode ser anterior à Data Início.\n';
        }

        // 4. custo real > 0
        if (novoCustoReal <= 0) {
            mensagemErro += '• Custo Real deve ser maior que zero (R$ 0,00).\n';
        }

        if (mensagemErro !== '') {
            inputFinalizar.checked = false; // Desmarca a checkbox
            const labelContainer = inputFinalizar.closest('.checkbox-container');
            labelContainer.classList.remove('is-checked'); // Atualiza a classe visual
            alert("A atividade NÃO pode ser FINALIZADA devido às seguintes pendências:\n\n" + mensagemErro);
            return; // Interrompe a função
        }
    }
    // --- FIM DA VALIDAÇÃO PARA FINALIZAR ---


    // --- VALIDAÇÃO PARA DESFINALIZAR (Status = false) ---
    if (novoStatus === false) {
        // 1. dtIni deve ser nula
        if (novaDtIni !== '') {
            mensagemErro += '• Para desfinalizar, Data Início deve ser limpa/nula.\n';
        }

        // 2. dtfim deve ser nula
        if (novaDtFim !== '') {
            mensagemErro += '• Para desfinalizar, Data Fim deve ser limpa/nula.\n';
        }

        // 3. custo real deve ser 0
        if (novoCustoReal !== 0) {
            mensagemErro += '• Para desfinalizar, Custo Real deve ser R$ 0,00.\n';
        }

        if (mensagemErro !== '') {
            inputFinalizar.checked = true; // Mantém a checkbox marcada
            const labelContainer = inputFinalizar.closest('.checkbox-container');
            labelContainer.classList.add('is-checked'); // Atualiza a classe visual
            alert("A atividade NÃO pode ser DESFINALIZADA devido às seguintes pendências:\n\n" + mensagemErro);
            return; // Interrompe a função
        }
    }
    // --- FIM DA VALIDAÇÃO PARA DESFINALIZAR ---


    botao.disabled = true;
    botao.classList.add('salvando');
    botao.textContent = '...';

    try {
        const dadosParaEnvio = {
            id: id,
            dtIni: novaDtIni,
            dtFim: novaDtFim,
            custoreal: novoCustoReal,
            observacoes: observacoesValor,
            status: novoStatus
        };

        const response = await fetch(`/apis/finalizar-atividades/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosParaEnvio)
        });

        if (!response.ok) {
            const errorMessage = await response.text();
            throw new Error(`Falha no backend: ${errorMessage}`);
        }

        // --- ATUALIZAÇÃO DO VISUAL DA LINHA APÓS SUCESSO ---
        const displaySpan = document.getElementById(`custo-real-display-${id}`);
        displaySpan.textContent = formatarDinheiro(novoCustoReal);

        inputCustoReal.value = aplicarMascaraDinheiro(novoCustoReal);

        const linha = botao.closest('tr');
        const labelContainer = inputFinalizar.closest('.checkbox-container');

        if (novoStatus) {
            linha.classList.remove('status-aberta');
            linha.classList.add('status-finalizada');
            labelContainer.classList.add('is-checked');
        } else {
            linha.classList.remove('status-finalizada');
            linha.classList.add('status-aberta');
            labelContainer.classList.remove('is-checked');
        }
        // --- FIM ATUALIZAÇÃO DO VISUAL DA LINHA ---


        botao.classList.remove('salvando');
        botao.classList.add('sucesso');
        botao.textContent = '✅';

    } catch (error) {
        alert(`Erro ao salvar: ${error.message}`);
        botao.classList.remove('salvando');
        botao.textContent = '❌';
    } finally {
        // NOVO: Recarrega a tabela (fetch completo) para garantir que ordenação e filtro estejam 100% atualizados
        setTimeout(() => {
            botao.disabled = false;
            botao.classList.remove('sucesso');
            botao.textContent = '💾';

            // Recarrega a tabela após o salvamento, mantendo ordenação e filtro
            carregarAtividades(ordenacaoAtual, termoBusca);

        }, 2000);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    // Carrega a tabela sem termo de busca na inicialização
    carregarAtividades('alfabetica');

    const tabelaCorpo = document.getElementById('tabela-corpo');
    tabelaCorpo.addEventListener('change', (event) => {
        if (event.target.classList.contains('input-finalizar')) {
            const checkbox = event.target;
            // Usa .closest() para encontrar o elemento pai (.checkbox-container)
            const labelContainer = checkbox.closest('.checkbox-container');

            if (checkbox.checked) {
                labelContainer.classList.add('is-checked');
            } else {
                labelContainer.classList.remove('is-checked');
            }
        }
    });

    tabelaCorpo.addEventListener('click', (event) => {
        if (event.target.classList.contains('btn-salvar-linha')) {
            const botao = event.target;
            const id = botao.dataset.id;
            salvarDataUnica(id, botao);
        }
        else if (event.target.classList.contains('btn-editar-custo')) {
            const id = event.target.dataset.id;
            toggleCustoReal(id);
        }
    });

    tabelaCorpo.addEventListener('blur', (event) => {
        if (event.target.classList.contains('input-custo-real')) {
            const inputElement = event.target;
            const id = inputElement.dataset.id;

            inputElement.value = aplicarMascaraDinheiro(inputElement.value);
            toggleCustoReal(id);
        }
    }, true);
});