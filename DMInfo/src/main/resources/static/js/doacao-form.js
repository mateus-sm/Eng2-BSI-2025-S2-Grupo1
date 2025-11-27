document.addEventListener('DOMContentLoaded', () => {

    const doacaoApiUrl = '/apis/doacao';
    const doadorApiUrl = '/apis/doador';

    const form = document.getElementById('formDoacao');
    const formTitulo = document.getElementById('formTitulo');

    const hiddenId = document.getElementById('id_doacao_hidden');
    const btnCancelarEdicao = document.getElementById('btnCancelarEdicao');
    const btnSalvar = document.getElementById('btnSalvar');

    // --- CORREÇÃO 1: RECUPERAR ID DO ADMIN ---
    // Pega o valor do input hidden injetado pelo Thymeleaf
    const inputAdminLogado = document.getElementById('id_admin_logado');
    const idAdminValido = inputAdminLogado && inputAdminLogado.value ? inputAdminLogado.value : null;

    // Debug no console para garantir
    console.log("ID do Admin recuperado para envio:", idAdminValido);

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

    // Controle para não perder dados ao trocar de tipo
    let tipoDoacaoAtual = 'monetaria';

    // --- FUNÇÕES DE VALIDAÇÃO VISUAL (Estilo InvalidInput) ---

    function setError(input, message) {
        // Adiciona a classe de erro (borda vermelha)
        input.classList.add('invalidInput');

        // Verifica se já existe a mensagem de erro
        let parent = input.parentElement;
        let errorDiv = parent.querySelector('.error-msg');

        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'error-msg'; // Classe definida no CSS
            parent.appendChild(errorDiv);
        }

        // Atualiza o texto da mensagem
        errorDiv.textContent = message;
    }

    function clearError(input) {
        if (!input) return;

        // Remove a borda vermelha
        input.classList.remove('invalidInput');

        // Remove a mensagem de erro se existir
        let parent = input.parentElement;
        if (parent) {
            const errorDiv = parent.querySelector('.error-msg');
            if (errorDiv) {
                errorDiv.remove();
            }
        }
    }

    function limparTodaValidacao() {
        // Remove erros dos campos principais
        if (selectDoador) clearError(selectDoador);
        if (inputValor) clearError(inputValor);
        if (inputItemDescricao) clearError(inputItemDescricao);

        // Esconde a div de feedback dos itens
        if(feedbackItensDiv) feedbackItensDiv.style.display = 'none';
    }

    // Listeners para limpar o erro assim que o usuário interagir
    if (selectDoador) selectDoador.addEventListener('change', () => clearError(selectDoador));
    if (inputValor) inputValor.addEventListener('input', () => clearError(inputValor));
    if (inputItemDescricao) inputItemDescricao.addEventListener('input', () => clearError(inputItemDescricao));

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

        const parts = obs ? obs.split('\n---\n', 2) : [''];
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

        clearError(inputItemDescricao); // Limpa erro caso estivesse marcado
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

        // Se estivermos editando um item específico, apenas atualizamos ele
        if (itemEditandoIndex !== -1) {
            itensParaDoacao[itemEditandoIndex].descricao = descricao;
            itensParaDoacao[itemEditandoIndex].quantidade = quantidade;
            alert('Item atualizado com sucesso.');
            sairModoEdicaoItem();
        }
        else {
            // Se for NOVO item, verificamos se já existe na lista (case insensitive)
            const indexExistente = itensParaDoacao.findIndex(item =>
                item.descricao.toLowerCase() === descricao.toLowerCase()
            );

            if (indexExistente !== -1) {
                // Já existe! Soma a quantidade
                itensParaDoacao[indexExistente].quantidade += quantidade;

                // Opcional: Efeito visual ou alerta rápido
                // alert(`O item "${descricao}" já existia. Quantidade atualizada para ${itensParaDoacao[indexExistente].quantidade}.`);
            } else {
                // Não existe, adiciona novo
                itensParaDoacao.push({ descricao, quantidade });
            }
        }

        // Limpa campos e foca
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

    // --- FUNÇÕES DE LAYOUT E PROTEÇÃO DE DADOS ---

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

    // Inicializa o valor atual no carregamento
    if (selectTipoDoacao) {
        tipoDoacaoAtual = selectTipoDoacao.value;
    }

    if (selectTipoDoacao) selectTipoDoacao.addEventListener('change', (e) => {
        const novoTipo = selectTipoDoacao.value;
        let temDadosPendentes = false;

        // Verifica se há dados no tipo anterior
        if (tipoDoacaoAtual === 'monetaria') {
            const val = getValorNumericoManual(inputValor.value);
            if (val > 0) temDadosPendentes = true;
        } else if (tipoDoacaoAtual === 'item') {
            if (itensParaDoacao.length > 0) temDadosPendentes = true;
            // Verifica também se o usuário começou a digitar um item
            if (inputItemDescricao.value.trim() !== '' || inputItemQuantidade.value.trim() !== '') {
                temDadosPendentes = true;
            }
        }

        if (temDadosPendentes) {
            const confirmar = confirm(`Atenção: Você tem dados preenchidos como doação ${tipoDoacaoAtual === 'monetaria' ? 'Monetária' : 'de Itens'}.\n\nMudar o tipo agora irá APAGAR esses dados.\n\nDeseja continuar e limpar os dados atuais?`);

            if (confirmar) {
                // Limpa os dados do tipo anterior
                if (tipoDoacaoAtual === 'monetaria') {
                    inputValor.value = '';
                } else {
                    itensParaDoacao = [];
                    sairModoEdicaoItem();
                    renderizarTabelaItens();
                }

                limparTodaValidacao();
                tipoDoacaoAtual = novoTipo;
                atualizarVisibilidadeForm();

            } else {
                // Cancela a troca
                selectTipoDoacao.value = tipoDoacaoAtual;
                e.preventDefault();
            }
        } else {
            // Se não tem dados, muda direto
            tipoDoacaoAtual = novoTipo;
            limparTodaValidacao();
            atualizarVisibilidadeForm();
        }
    });


    // --- FUNÇÕES DE API ---

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

            limparTodaValidacao();

            if (doacao.valor > 1.0) {
                // Modo Monetário
                tipoDoacaoAtual = 'monetaria';
                if (selectTipoDoacao) selectTipoDoacao.value = 'monetaria';
                if (inputValor) inputValor.value = (doacao.valor).toFixed(2).replace('.', ',');
                if (inputObservacao) inputObservacao.value = doacao.observacao || '';

                itensParaDoacao = [];
            } else {
                // Modo Item
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
            console.error('Erro ao buscar doação para edição:', error);
            alert('Não foi possível carregar os dados para edição.');
        }
    }

    function validarFormulario() {
        let isValid = true;

        // 1. CORREÇÃO DE SEGURANÇA: Valida o ID do Admin
        if (!idAdminValido) {
            alert('Erro Crítico de Segurança: Administrador não identificado. A sessão pode ter expirado. Faça login novamente.');
            return false;
        }

        // 2. Valida Doador
        if (!selectDoador || selectDoador.value === "") {
            setError(selectDoador, "Por favor, selecione um doador.");
            isValid = false;
        } else {
            clearError(selectDoador);
        }

        // 3. Validação Específica (Monetária vs. Item)
        const tipo = selectTipoDoacao ? selectTipoDoacao.value : 'monetaria';

        if (tipo === 'monetaria') {
            const valorNumerico = inputValor ? getValorNumericoManual(inputValor.value) : 0;
            if (!inputValor || isNaN(valorNumerico) || valorNumerico <= 0) {
                setError(inputValor, "O valor da doação deve ser positivo.");
                isValid = false;
            } else {
                clearError(inputValor);
            }
            if(feedbackItensDiv) feedbackItensDiv.style.display = 'none';

        } else { // Tipo Item
            if (itensParaDoacao.length === 0) {
                isValid = false;
                // Mostra erro na tabela
                if(feedbackItensDiv) feedbackItensDiv.style.display = 'block';
                // Marca o campo de descrição para chamar atenção
                setError(inputItemDescricao, "Adicione itens à lista abaixo.");
            } else {
                if(feedbackItensDiv) feedbackItensDiv.style.display = 'none';
                clearError(inputItemDescricao);
            }

            if(inputValor) clearError(inputValor);
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
        tipoDoacaoAtual = 'monetaria';

        limparTodaValidacao();
        sairModoEdicaoItem();
        renderizarTabelaItens();

        if(selectTipoDoacao) selectTipoDoacao.value = 'monetaria';
        atualizarVisibilidadeForm();
        if(selectDoador) selectDoador.focus();
    }

    // --- Evento Submit (Lógica de Salvamento) ---
    if (form) form.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Remove erros antigos antes de validar
        // limparTodaValidacao(); // Opcional, o validarFormulario já limpa se estiver certo

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
            valorParaSalvar = 1.0; // Valor simbólico

            const itensFormatados = itensParaDoacao.map(item =>
                `[ITEM]: ${item.descricao} | [QTD]: ${item.quantidade}`
            ).join('\n');

            obsParaSalvar = obsOriginal === '' ? itensFormatados : `${itensFormatados}\n---\n${obsOriginal}`;
        }

        const doacao = {
            id_doacao: isEdicao ? parseInt(idParaAtualizar) : 0,
            id_doador: { id: parseInt(selectDoador ? selectDoador.value : '0') },

            // --- CORREÇÃO: Enviando o ID do Admin capturado no início ---
            id_admin: { id: parseInt(idAdminValido) },

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
            window.location.href = 'doacao';

        } catch (error) {
            console.error('Falha ao salvar/atualizar:', error);
            alert(`Não foi possível ${isEdicao ? 'atualizar' : 'salvar'} a doação. ${error.message}`);
        }
    });
    // --- FIM SUBMIT ---

    if (btnCancelarEdicao) btnCancelarEdicao.addEventListener('click', (e) => {
        e.preventDefault();
        window.location.href = 'doacao';
    });

    // --- Lógica de Inicialização ---
    renderizarTabelaItens();
    atualizarVisibilidadeForm();

    // Carrega doadores e, se houver ID na URL, carrega para edição
    carregarSelectDoadores().then(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const idEdicao = urlParams.get('id');

        if (idEdicao) {
            preencherFormularioParaEdicao(idEdicao);
        }
    });
});