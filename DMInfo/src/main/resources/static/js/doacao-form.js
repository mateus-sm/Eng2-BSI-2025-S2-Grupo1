document.addEventListener('DOMContentLoaded', () => {

    const doacaoApiUrl = '/apis/doacao';
    const doadorApiUrl = '/apis/doador';

    const form = document.getElementById('formDoacao');
    const formTitulo = document.getElementById('formTitulo');
    const hiddenId = document.getElementById('id_doacao_hidden');
    const btnCancelarEdicao = document.getElementById('btnCancelarEdicao');
    const btnSalvar = document.getElementById('btnSalvar');

    // Recupera ID do Admin
    const inputAdminLogado = document.getElementById('id_admin_logado');
    const idAdminValido = inputAdminLogado && inputAdminLogado.value ? inputAdminLogado.value : null;

    const selectDoador = document.getElementById('id_doador');
    const inputObservacao = document.getElementById('observacao');
    const selectTipoDoacao = document.getElementById('tipo_doacao');

    const grupoMonetaria = document.getElementById('grupo_monetaria');
    const inputValor = document.getElementById('valor');

    const grupoItem = document.getElementById('grupo_item');
    const inputItemDescricao = document.getElementById('inputItemDescricao');
    const inputItemQuantidade = document.getElementById('inputItemQuantidade');
    const btnAddItem = document.getElementById('btnAddItem');
    const tabelaItensAdicionados = document.getElementById('tabelaItensAdicionados');
    const feedbackItensDiv = document.getElementById('feedbackItens');

    let itensParaDoacao = [];
    let itemEditandoIndex = -1;
    let tipoDoacaoAtual = 'monetaria';

    // --- FUNÇÕES VISUAIS (SUBSTITUINDO ALERT/CONFIRM) ---

    function mostrarNotificacao(mensagem, tipo = 'sucesso') {
        const toastEl = document.getElementById('toastNotificacao');
        // Se o elemento não existir, fallback para alert (segurança)
        if (!toastEl) { alert(mensagem); return; }

        const toastBody = toastEl.querySelector('.toast-body');
        const toastMsg = document.getElementById('toastMessage');
        const toastIcon = document.getElementById('toastIcon');

        // Limpa cores anteriores
        toastEl.classList.remove('text-bg-success', 'text-bg-danger', 'text-bg-warning');

        if (tipo === 'sucesso') {
            toastEl.classList.add('text-bg-success');
            toastIcon.className = 'fas fa-check-circle';
        } else if (tipo === 'erro') {
            toastEl.classList.add('text-bg-danger');
            toastIcon.className = 'fas fa-times-circle';
        } else {
            toastEl.classList.add('text-bg-warning');
            toastIcon.className = 'fas fa-exclamation-circle';
        }

        toastMsg.textContent = mensagem;
        const toast = new bootstrap.Toast(toastEl, { delay: 4000 });
        toast.show();
    }

    function confirmarAcao(titulo, texto, callbackConfirmacao) {
        const modalEl = document.getElementById('modalConfirmacao');
        if (!modalEl) {
            // Fallback se o modal não existir no HTML
            if(confirm(texto)) callbackConfirmacao();
            return;
        }

        const modalTitulo = document.getElementById('modalTitulo');
        const modalTexto = document.getElementById('modalTexto');
        const btnConfirmar = document.getElementById('btnConfirmarAcao');

        modalTitulo.textContent = titulo;
        modalTexto.textContent = texto;

        const modal = new bootstrap.Modal(modalEl);

        // Clona botão para limpar eventos anteriores
        const novoBtn = btnConfirmar.cloneNode(true);
        btnConfirmar.parentNode.replaceChild(novoBtn, btnConfirmar);

        novoBtn.addEventListener('click', () => {
            callbackConfirmacao();
            modal.hide();
        });

        modal.show();
    }
    // ----------------------------------------------------

    // Validação Visual (Borda Vermelha)
    function setError(input, message) {
        input.classList.add('invalidInput');
        let parent = input.parentElement;
        let errorDiv = parent.querySelector('.error-msg');
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'error-msg';
            parent.appendChild(errorDiv);
        }
        errorDiv.textContent = message;
    }

    function clearError(input) {
        if (!input) return;
        input.classList.remove('invalidInput');
        let parent = input.parentElement;
        if (parent) {
            const errorDiv = parent.querySelector('.error-msg');
            if (errorDiv) errorDiv.remove();
        }
    }

    function limparTodaValidacao() {
        if (selectDoador) clearError(selectDoador);
        if (inputValor) clearError(inputValor);
        if (inputItemDescricao) clearError(inputItemDescricao);
        if(feedbackItensDiv) feedbackItensDiv.style.display = 'none';
    }

    if (selectDoador) selectDoador.addEventListener('change', () => clearError(selectDoador));
    if (inputValor) inputValor.addEventListener('input', () => clearError(inputValor));
    if (inputItemDescricao) inputItemDescricao.addEventListener('input', () => clearError(inputItemDescricao));


    // Máscaras e Formatação
    function formatarMoedaManual(e) {
        const input = e.target;
        let value = input.value.replace(/\D/g, '');
        if (value === '') { input.value = ''; return; }
        let numero = parseInt(value, 10) / 100;
        input.value = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(numero);
    }

    function getValorNumericoManual(valorFormatado) {
        if (!valorFormatado) return 0.0;
        let limpo = valorFormatado.replace(/R\$\s*/g, '').replace(/\./g, '').replace(',', '.');
        const numero = parseFloat(limpo);
        return isNaN(numero) ? 0.0 : numero;
    }

    if (inputItemQuantidade) inputItemQuantidade.addEventListener('input', (e) => { e.target.value = e.target.value.replace(/\D/g, ''); });
    if (inputValor) inputValor.addEventListener('input', formatarMoedaManual);


    // Parse de Itens da String de Observação
    function parseItensFromObservacao(obs) {
        const itens = [];
        let obsGeral = '';
        const itemRegex = /\[ITEM\]: (.*?) \| \[QTD\]: (\d+)/g;
        const parts = obs ? obs.split('\n---\n', 2) : [''];
        let itemBlock = parts[0];
        obsGeral = parts.length > 1 ? parts[1].trim() : '';

        let match;
        let foundItems = false;
        while ((match = itemRegex.exec(itemBlock)) !== null) {
            itens.push({ descricao: match[1].trim(), quantidade: parseInt(match[2], 10) });
            foundItems = true;
        }
        if (!foundItems && parts.length > 0) obsGeral = obs;
        return { itens, obsGeral };
    }


    // Gestão de Itens
    function sairModoEdicaoItem() {
        itemEditandoIndex = -1;
        if (btnAddItem) {
            btnAddItem.textContent = 'Adicionar';
            btnAddItem.classList.replace('btn-secondary', 'btn-success');
        }
        if (inputItemDescricao) inputItemDescricao.value = '';
        if (inputItemQuantidade) inputItemQuantidade.value = '';
        clearError(inputItemDescricao);
    }

    function renderizarTabelaItens() {
        if (!tabelaItensAdicionados) return;
        if (itensParaDoacao.length === 0) {
            tabelaItensAdicionados.innerHTML = '<tr><td colspan="4" class="text-center text-muted">Nenhum item adicionado.</td></tr>';
            sairModoEdicaoItem();
            if(feedbackItensDiv) feedbackItensDiv.style.display = 'none';
            return;
        } else {
            if(feedbackItensDiv) feedbackItensDiv.style.display = 'none';
        }

        tabelaItensAdicionados.innerHTML = itensParaDoacao.map((item, index) => `
            <tr id="item-row-${index}">
                <td>${index + 1}</td>
                <td>${item.descricao}</td>
                <td>${item.quantidade}</td>
                <td>
                    <button type="button" class="btn btn-sm btn-warning btn-editar-item me-1" data-index="${index}">Editar</button>
                    <button type="button" class="btn btn-sm btn-danger btn-remover-item" data-index="${index}"><i class="fas fa-trash-alt"></i></button>
                </td>
            </tr>
        `).join('');

        if (itemEditandoIndex === -1) sairModoEdicaoItem();
    }

    function adicionarItem() {
        if (!inputItemDescricao || !inputItemQuantidade) return;
        const descricao = inputItemDescricao.value.trim();
        const quantidade = parseInt(inputItemQuantidade.value, 10);

        if (descricao.length < 3) {
            mostrarNotificacao('A descrição do item deve ter pelo menos 3 caracteres.', 'erro');
            inputItemDescricao.focus();
            return;
        }
        if (isNaN(quantidade) || quantidade <= 0) {
            mostrarNotificacao('A quantidade deve ser um número positivo.', 'erro');
            inputItemQuantidade.focus();
            return;
        }

        if (itemEditandoIndex !== -1) {
            itensParaDoacao[itemEditandoIndex].descricao = descricao;
            itensParaDoacao[itemEditandoIndex].quantidade = quantidade;
            mostrarNotificacao('Item atualizado com sucesso.', 'sucesso');
            sairModoEdicaoItem();
        } else {
            const indexExistente = itensParaDoacao.findIndex(item => item.descricao.toLowerCase() === descricao.toLowerCase());
            if (indexExistente !== -1) {
                itensParaDoacao[indexExistente].quantidade += quantidade;
                mostrarNotificacao('Item já existia. Quantidade somada.', 'sucesso');
            } else {
                itensParaDoacao.push({ descricao, quantidade });
            }
        }

        inputItemDescricao.value = '';
        inputItemQuantidade.value = '';
        inputItemDescricao.focus();
        renderizarTabelaItens();
    }

    if (tabelaItensAdicionados) tabelaItensAdicionados.addEventListener('click', (e) => {
        const target = e.target.closest('button');
        if (!target) return;
        const index = parseInt(target.getAttribute('data-index'), 10);

        if (target.classList.contains('btn-remover-item')) {
            // Remoção de item é simples, não vamos usar modal pesado aqui para agilidade
            itensParaDoacao.splice(index, 1);
            renderizarTabelaItens();
        }

        if (target.classList.contains('btn-editar-item')) {
            const item = itensParaDoacao[index];
            if (inputItemDescricao) inputItemDescricao.value = item.descricao;
            if (inputItemQuantidade) inputItemQuantidade.value = item.quantidade;
            itemEditandoIndex = index;
            if (btnAddItem) {
                btnAddItem.textContent = 'Atualizar Item';
                btnAddItem.classList.replace('btn-success', 'btn-secondary');
            }
            renderizarTabelaItens();
            if (inputItemDescricao) inputItemDescricao.focus();
        }
    });

    if (btnAddItem) btnAddItem.addEventListener('click', adicionarItem);


    // Lógica de Visibilidade e Troca de Tipo
    function atualizarVisibilidadeForm() {
        const tipo = selectTipoDoacao.value;
        if (tipo === 'monetaria') {
            if (grupoMonetaria) grupoMonetaria.classList.remove('campo-escondido');
            if (grupoItem) grupoItem.classList.add('campo-escondido');
        } else {
            if (grupoMonetaria) grupoMonetaria.classList.add('campo-escondido');
            if (grupoItem) grupoItem.classList.remove('campo-escondido');
        }
    }

    if (selectTipoDoacao) tipoDoacaoAtual = selectTipoDoacao.value;

    if (selectTipoDoacao) selectTipoDoacao.addEventListener('change', (e) => {
        const novoTipo = selectTipoDoacao.value;
        let temDadosPendentes = false;

        if (tipoDoacaoAtual === 'monetaria') {
            const val = getValorNumericoManual(inputValor.value);
            if (val > 0) temDadosPendentes = true;
        } else if (tipoDoacaoAtual === 'item') {
            if (itensParaDoacao.length > 0 || inputItemDescricao.value.trim() !== '') {
                temDadosPendentes = true;
            }
        }

        if (temDadosPendentes) {
            e.preventDefault();
            selectTipoDoacao.value = tipoDoacaoAtual; // Volta visualmente

            confirmarAcao(
                'Mudar tipo de doação?',
                'Ao trocar o tipo (Dinheiro/Item), os dados preenchidos atualmente serão perdidos. Deseja continuar?',
                () => {
                    // Usuário confirmou
                    if (tipoDoacaoAtual === 'monetaria') {
                        inputValor.value = '';
                    } else {
                        itensParaDoacao = [];
                        sairModoEdicaoItem();
                        renderizarTabelaItens();
                    }
                    limparTodaValidacao();
                    tipoDoacaoAtual = novoTipo;
                    selectTipoDoacao.value = novoTipo;
                    atualizarVisibilidadeForm();
                }
            );
        } else {
            tipoDoacaoAtual = novoTipo;
            limparTodaValidacao();
            atualizarVisibilidadeForm();
        }
    });


    // API: Carregar Doadores
    async function carregarSelectDoadores() {
        if (!selectDoador) return;
        try {
            const response = await fetch(doadorApiUrl);
            if (!response.ok) throw new Error('Erro ao buscar doadores');
            const doadores = await response.json();
            selectDoador.innerHTML = '<option value="">Selecione um doador...</option>';
            doadores.forEach(doador => {
                const option = document.createElement('option');
                option.value = doador.id;
                option.textContent = `${doador.nome} (${doador.documento})`;
                selectDoador.appendChild(option);
            });
        } catch (error) {
            console.error(error);
            mostrarNotificacao('Erro ao carregar lista de doadores.', 'erro');
        }
    }

    // API: Preencher para Edição
    async function preencherFormularioParaEdicao(id) {
        try {
            const response = await fetch(`${doacaoApiUrl}/${id}`);
            if (!response.ok) throw new Error('Doação não encontrada.');
            const doacao = await response.json();

            if (hiddenId) hiddenId.value = doacao.id_doacao;
            if (formTitulo) formTitulo.textContent = `Editar Doação ID: ${doacao.id_doacao}`;
            if (btnSalvar) btnSalvar.textContent = 'Atualizar Doação';
            if (btnCancelarEdicao) btnCancelarEdicao.classList.remove('d-none');
            if (selectDoador) selectDoador.value = doacao.id_doador.id;

            limparTodaValidacao();

            if (doacao.valor > 1.0) {
                tipoDoacaoAtual = 'monetaria';
                if (selectTipoDoacao) selectTipoDoacao.value = 'monetaria';
                if (inputValor) inputValor.value = (doacao.valor).toFixed(2).replace('.', ',');
                if (inputObservacao) inputObservacao.value = doacao.observacao || '';
                itensParaDoacao = [];
            } else {
                tipoDoacaoAtual = 'item';
                if (selectTipoDoacao) selectTipoDoacao.value = 'item';
                if (inputValor) inputValor.value = '';
                const { itens, obsGeral } = parseItensFromObservacao(doacao.observacao || '');
                itensParaDoacao = itens;
                if (inputObservacao) inputObservacao.value = obsGeral;
            }

            atualizarVisibilidadeForm();
            renderizarTabelaItens();
            sairModoEdicaoItem();
            window.scrollTo({ top: 0, behavior: 'smooth' });

        } catch (error) {
            mostrarNotificacao('Não foi possível carregar os dados para edição.', 'erro');
        }
    }

    // Validação Final
    function validarFormulario() {
        let isValid = true;
        if (!idAdminValido) {
            mostrarNotificacao('Erro de Segurança: Sessão de admin inválida ou expirada. Faça login novamente.', 'erro');
            return false;
        }
        if (!selectDoador || selectDoador.value === "") {
            setError(selectDoador, "Por favor, selecione um doador.");
            isValid = false;
        } else { clearError(selectDoador); }

        const tipo = selectTipoDoacao ? selectTipoDoacao.value : 'monetaria';

        if (tipo === 'monetaria') {
            const valorNumerico = inputValor ? getValorNumericoManual(inputValor.value) : 0;
            if (!inputValor || isNaN(valorNumerico) || valorNumerico <= 0) {
                setError(inputValor, "O valor da doação deve ser positivo.");
                isValid = false;
            } else { clearError(inputValor); }
            if(feedbackItensDiv) feedbackItensDiv.style.display = 'none';
        } else {
            if (itensParaDoacao.length === 0) {
                isValid = false;
                if(feedbackItensDiv) feedbackItensDiv.style.display = 'block';
                setError(inputItemDescricao, "Adicione itens à lista abaixo.");
            } else {
                if(feedbackItensDiv) feedbackItensDiv.style.display = 'none';
                clearError(inputItemDescricao);
            }
            if(inputValor) clearError(inputValor);
        }
        return isValid;
    }

    // Submit do Formulário
    if (form) form.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!validarFormulario()) return;

        const isEdicao = hiddenId ? hiddenId.value !== '' : false;
        const idParaAtualizar = hiddenId ? hiddenId.value : '';
        const tipo = selectTipoDoacao ? selectTipoDoacao.value : 'monetaria';
        let valorParaSalvar = 0.0;
        const obsOriginal = inputObservacao ? inputObservacao.value.trim() : '';
        let obsParaSalvar = obsOriginal;

        if (tipo === 'monetaria') {
            valorParaSalvar = inputValor ? getValorNumericoManual(inputValor.value) : 0;
        } else {
            valorParaSalvar = 1.0;
            const itensFormatados = itensParaDoacao.map(item => `[ITEM]: ${item.descricao} | [QTD]: ${item.quantidade}`).join('\n');
            obsParaSalvar = obsOriginal === '' ? itensFormatados : `${itensFormatados}\n---\n${obsOriginal}`;
        }

        const doacao = {
            id_doacao: isEdicao ? parseInt(idParaAtualizar) : 0,
            id_doador: { id: parseInt(selectDoador ? selectDoador.value : '0') },
            id_admin: { id: parseInt(idAdminValido) },
            valor: valorParaSalvar,
            observacao: obsParaSalvar
        };

        const method = isEdicao ? 'PUT' : 'POST';
        const url = isEdicao ? `${doacaoApiUrl}/${idParaAtualizar}` : doacaoApiUrl;

        try {
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(doacao),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.erro || `Erro ao ${isEdicao ? 'atualizar' : 'salvar'}`);
            }

            mostrarNotificacao(`Doação ${isEdicao ? 'atualizada' : 'salva'} com sucesso!`, 'sucesso');

            // Delay para ler a mensagem antes de sair
            setTimeout(() => {
                window.location.href = 'doacao';
            }, 1500);

        } catch (error) {
            mostrarNotificacao(error.message, 'erro');
        }
    });

    if (btnCancelarEdicao) btnCancelarEdicao.addEventListener('click', (e) => {
        e.preventDefault();
        window.location.href = 'doacao';
    });

    // Inicialização
    renderizarTabelaItens();
    atualizarVisibilidadeForm();

    carregarSelectDoadores().then(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const idEdicao = urlParams.get('id');
        if (idEdicao) preencherFormularioParaEdicao(idEdicao);
    });
});