document.addEventListener('DOMContentLoaded', () => {

    const doacaoApiUrl = '/apis/doacao';
    const doadorApiUrl = '/apis/doador';

    const form = document.getElementById('formDoacao');
    const formTitulo = document.getElementById('formTitulo');

    const hiddenId = document.getElementById('id_doacao_hidden');
    const btnCancelarEdicao = document.getElementById('btnCancelarEdicao');
    const btnSalvar = document.getElementById('btnSalvar');

    const selectDoador = document.getElementById('id_doador');
    // NOVO: Puxa o valor do ID Admin do campo hidden (injetado pela sessão)
    const inputAdminLogado = document.getElementById('id_admin_logado');
    const idAdmin = inputAdminLogado ? inputAdminLogado.value : '1'; // Default para '1' se não logado/injetado

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

    // --- FUNÇÕES DE VALIDAÇÃO VISUAL E FEEDBACK ---
    function aplicarFeedback(elementId, isValid, feedbackText) {
        const input = document.getElementById(elementId);
        const feedback = document.getElementById(`feedback${elementId.charAt(0).toUpperCase() + elementId.slice(1)}`);

        if (!input) return;

        if (isValid) {
            input.classList.remove('is-invalid');
            input.classList.add('is-valid');
        } else {
            input.classList.remove('is-valid');
            input.classList.add('is-invalid');
            if (feedback && feedbackText) {
                feedback.textContent = feedbackText;
            }
        }
    }

    function limparValidacao() {
        // ID Admin NÃO está mais nesta lista de limpeza, pois não é um input visível
        const fields = ['id_doador', 'valor'];
        fields.forEach(id => {
            const input = document.getElementById(id);
            if(input) {
                input.classList.remove('is-valid', 'is-invalid');
            }
        });
        if(feedbackItensDiv) feedbackItensDiv.style.display = 'none';

        // Remove explicitamente a validação do ID do Admin se o campo ainda existisse
        const oldAdminInput = document.getElementById('id_admin');
        if (oldAdminInput) oldAdminInput.classList.remove('is-valid', 'is-invalid');
    }
    // --- FIM VALIDAÇÃO VISUAL ---


    // --- FUNÇÕES DE MÁSCARAS E VALOR ---
    function formatarMoedaManual(e) {
        const input = e.target;
        let value = input.value;
        value = value.replace(/\D/g, '');
        if (value === '') {
            input.value = '';
            return;
        }
        let numero = parseInt(value, 10) / 100;
        input.value = new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(numero);
    }

    function getValorNumericoManual(valorFormatado) {
        if (!valorFormatado) return 0.0;
        let limpo = valorFormatado.replace(/R\$\s*/g, '');
        limpo = limpo.replace(/\./g, '');
        limpo = limpo.replace(',', '.');
        const numero = parseFloat(limpo);
        return isNaN(numero) ? 0.0 : numero;
    }

    if (inputItemQuantidade) inputItemQuantidade.addEventListener('input', (e) => {
        e.target.value = e.target.value.replace(/\D/g, '');
    });
    if (inputValor) inputValor.addEventListener('input', formatarMoedaManual);
    // --- FIM MÁSCARAS ---

    // --- FUNÇÃO PARA PARSEAR A STRING DE ITENS ---
    function parseItensFromObservacao(obs) {
        const itens = [];
        let obsGeral = '';
        const itemRegex = /\[ITEM\]: (.*?) \| \[QTD\]: (\d+)/g;

        const parts = obs.split('\n---\n', 2);
        let itemBlock = parts[0];
        obsGeral = parts.length > 1 ? parts[1].trim() : '';

        let match;
        let foundItems = false;
        while ((match = itemRegex.exec(itemBlock)) !== null) {
            itens.push({
                descricao: match[1].trim(),
                quantidade: parseInt(match[2], 10)
            });
            foundItems = true;
        }

        if (!foundItems && parts.length > 0) {
            obsGeral = obs;
        }

        return { itens, obsGeral };
    }
    // --- FIM PARSE ---


    // --- GESTÃO DA TABELA DE ITENS ---
    function sairModoEdicaoItem() {
        itemEditandoIndex = -1;
        if (btnAddItem) {
            btnAddItem.textContent = 'Adicionar';
            btnAddItem.classList.remove('btn-secondary', 'btn-info');
            btnAddItem.classList.add('btn-success');
        }
        if (inputItemDescricao) inputItemDescricao.value = '';
        if (inputItemQuantidade) inputItemQuantidade.value = '';
    }

    function renderizarTabelaItens() {
        if (!tabelaItensAdicionados) return;

        if (itensParaDoacao.length === 0) {
            tabelaItensAdicionados.innerHTML = '<tr><td colspan="4" class="text-center text-muted">Nenhum item adicionado.</td></tr>';
            sairModoEdicaoItem();
            if(feedbackItensDiv) feedbackItensDiv.style.display = 'none';
            return;
        }

        tabelaItensAdicionados.innerHTML = itensParaDoacao.map((item, index) => `
            <tr id="item-row-${index}">
                <td>${index + 1}</td>
                <td>${item.descricao}</td>
                <td>${item.quantidade}</td>
                <td>
                    <button type="button" class="btn btn-sm btn-warning btn-editar-item me-1" data-index="${index}">Editar</button>
                    <button type="button" class="btn btn-sm btn-danger btn-remover-item" data-index="${index}" title="Remover item">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
            </tr>
        `).join('');

        if (itemEditandoIndex === -1) {
            sairModoEdicaoItem();
        }
    }

    function adicionarItem() {
        if (!inputItemDescricao || !inputItemQuantidade) return;

        const descricao = inputItemDescricao.value.trim();
        const quantidade = parseInt(inputItemQuantidade.value, 10);

        if (descricao.length < 3) {
            alert('A descrição do item deve ter pelo menos 3 caracteres.');
            inputItemDescricao.focus();
            return;
        }
        if (isNaN(quantidade) || quantidade <= 0) {
            alert('A quantidade deve ser um número positivo.');
            inputItemQuantidade.focus();
            return;
        }

        if (itemEditandoIndex !== -1) {
            itensParaDoacao[itemEditandoIndex].descricao = descricao;
            itensParaDoacao[itemEditandoIndex].quantidade = quantidade;
            alert('Item atualizado com sucesso.');
            sairModoEdicaoItem();
        }
        else {
            itensParaDoacao.push({ descricao, quantidade });
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
            if (confirm(`Tem certeza que deseja remover o item "${itensParaDoacao[index].descricao}"?`)) {
                itensParaDoacao.splice(index, 1);
                renderizarTabelaItens();
            }
        }

        if (target.classList.contains('btn-editar-item')) {
            const item = itensParaDoacao[index];
            if (inputItemDescricao) inputItemDescricao.value = item.descricao;
            if (inputItemQuantidade) inputItemQuantidade.value = item.quantidade;

            itemEditandoIndex = index;
            if (btnAddItem) {
                btnAddItem.textContent = 'Atualizar Item';
                btnAddItem.classList.remove('btn-success');
                btnAddItem.classList.add('btn-secondary');
            }

            renderizarTabelaItens();
            if (inputItemDescricao) inputItemDescricao.focus();
        }
    });

    if (btnAddItem) btnAddItem.addEventListener('click', adicionarItem);

    // --- FUNÇÕES DE LAYOUT E API ---

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
            console.error('Falha ao carregar doadores:', error);
            selectDoador.innerHTML = '<option value="">Erro ao carregar doadores</option>';
        }
    }

    async function preencherFormularioParaEdicao(id) {
        try {
            const response = await fetch(`${doacaoApiUrl}/${id}`);
            if (!response.ok) throw new Error('Doação não encontrada.');
            const doacao = await response.json();

            // Configuração do formulário para Edição
            if (hiddenId) hiddenId.value = doacao.id_doacao;
            if (formTitulo) formTitulo.textContent = `Editar Doação ID: ${doacao.id_doacao}`;
            if (btnSalvar) btnSalvar.textContent = 'Atualizar Doação';
            if (btnCancelarEdicao) btnCancelarEdicao.classList.remove('d-none');

            if (selectDoador) selectDoador.value = doacao.id_doador.id;

            limparValidacao();

            if (doacao.valor > 1.0) {
                if (selectTipoDoacao) selectTipoDoacao.value = 'monetaria';
                if (inputValor) inputValor.value = (doacao.valor).toFixed(2).replace('.', ',');
                if (inputObservacao) inputObservacao.value = doacao.observacao || '';

                itensParaDoacao = [];
            } else {
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
            console.error('Erro ao buscar doação para edição:', error);
            alert('Não foi possível carregar os dados para edição.');
        }
    }

    function validarFormulario() {
        let isValid = true;

        if (!selectDoador || selectDoador.value === "") {
            aplicarFeedback('id_doador', false, "Por favor, selecione um doador.");
            isValid = false;
        } else {
            aplicarFeedback('id_doador', true);
        }

        // REMOVIDA A VALIDAÇÃO DO ADMIN ID, POIS É AUTOMÁTICA.

        // 3. Validação Específica (Monetária vs. Item)
        const tipo = selectTipoDoacao ? selectTipoDoacao.value : 'monetaria';

        if (tipo === 'monetaria') {
            const valorNumerico = inputValor ? getValorNumericoManual(inputValor.value) : 0;
            if (!inputValor || isNaN(valorNumerico) || valorNumerico <= 0) {
                aplicarFeedback('valor', false, "O valor da doação deve ser positivo.");
                isValid = false;
            } else {
                aplicarFeedback('valor', true);
            }

            if(feedbackItensDiv) feedbackItensDiv.style.display = 'none';

        } else { // Tipo Item
            if (itensParaDoacao.length === 0) {
                isValid = false;
                if(feedbackItensDiv) feedbackItensDiv.style.display = 'block';
            } else {
                if(feedbackItensDiv) feedbackItensDiv.style.display = 'none';
            }

            if(inputValor) inputValor.classList.remove('is-valid', 'is-invalid');
        }

        return isValid;
    }

    function resetarFormulario() {
        if(form) form.reset();
        if(hiddenId) hiddenId.value = '';
        if(formTitulo) formTitulo.textContent = 'Registrar Nova Doação';
        if(btnSalvar) btnSalvar.textContent = 'Salvar Doação';
        if(btnCancelarEdicao) btnCancelarEdicao.classList.add('d-none');

        if(inputValor) inputValor.value = '';
        if(inputItemDescricao) inputItemDescricao.value = '';
        if(inputItemQuantidade) inputItemQuantidade.value = '';

        itensParaDoacao = [];

        limparValidacao();
        sairModoEdicaoItem();
        renderizarTabelaItens();

        if(selectTipoDoacao) selectTipoDoacao.value = 'monetaria';
        atualizarVisibilidadeForm();
        if(selectDoador) selectDoador.focus();
    }

    // --- Evento Submit (Lógica de Salvamento) ---
    if (form) form.addEventListener('submit', async (e) => {
        e.preventDefault();

        limparValidacao();

        if (!validarFormulario()) {
            return;
        }

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

            const itensFormatados = itensParaDoacao.map(item =>
                `[ITEM]: ${item.descricao} | [QTD]: ${item.quantidade}`
            ).join('\n');

            obsParaSalvar = obsOriginal === '' ? itensFormatados : `${itensFormatados}\n---\n${obsOriginal}`;
        }

        const doacao = {
            id_doacao: isEdicao ? parseInt(idParaAtualizar) : 0,
            id_doador: { id: parseInt(selectDoador ? selectDoador.value : '0') },
            // MUDANÇA CRÍTICA: Puxa o ID do campo hidden (que deve vir da sessão)
            id_admin: { id: parseInt(idAdmin) },
            valor: valorParaSalvar,
            observacao: obsParaSalvar
        };

        const method = isEdicao ? 'PUT' : 'POST';
        const url = isEdicao ? `${doacaoApiUrl}/${idParaAtualizar}` : doacaoApiUrl;

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(doacao),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.erro || `Erro ao ${isEdicao ? 'atualizar' : 'salvar'} doação`);
            }

            alert(`Doação ${isEdicao ? 'atualizada' : 'salva'} com sucesso!`);
            // Redireciona de volta para a lista após salvar/atualizar
            window.location.href = 'doacao';

        } catch (error) {
            console.error('Falha ao salvar/atualizar:', error);
            alert(`Não foi possível ${isEdicao ? 'atualizar' : 'salvar'} a doação. ${error.message}`);
        }
    });
    // --- FIM SUBMIT ---

    if (selectTipoDoacao) selectTipoDoacao.addEventListener('change', () => {
        atualizarVisibilidadeForm();
        if(selectTipoDoacao.value === 'monetaria') {
            itensParaDoacao = [];
            renderizarTabelaItens();
        }
    });

    if (btnCancelarEdicao) btnCancelarEdicao.addEventListener('click', (e) => {
        e.preventDefault();
        // Redireciona de volta para a lista ao cancelar
        window.location.href = 'doacao';
    });

    // --- Lógica de Inicialização ---
    renderizarTabelaItens();
    atualizarVisibilidadeForm();
    carregarSelectDoadores();

    // Verifica se é modo de edição e carrega os dados
    const urlParams = new URLSearchParams(window.location.search);
    const idEdicao = urlParams.get('id');

    if (idEdicao) {
        preencherFormularioParaEdicao(idEdicao);
    }
});