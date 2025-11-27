document.addEventListener('DOMContentLoaded', () => {

    const form = document.getElementById('formDoador');
    const tabelaBody = document.getElementById('tabelaDoadores');
    const btnCancelar = document.getElementById('btnCancelar');
    const formTitulo = document.querySelector('.form-section h4');
    const hiddenId = document.getElementById('id');
    const inputPesquisa = document.getElementById('inputPesquisa');

    // Elementos para Máscara e Validação
    const inputNome = document.getElementById('nome');
    const inputDocumento = document.getElementById('documento');
    const inputTelefone = document.getElementById('telefone');
    const inputCep = document.getElementById('cep');
    const inputEmail = document.getElementById('email');
    const inputRua = document.getElementById('rua');
    const inputBairro = document.getElementById('bairro');
    const inputCidade = document.getElementById('cidade');
    const inputUf = document.getElementById('uf');

    const apiUrl = '/apis/doador';

    let listaGlobalDoadores = [];

    // --- REINICIALIZAÇÃO DAS MÁSCARAS (IMASK) ---
    if (typeof IMask !== 'undefined') {
        window.docMask = IMask(inputDocumento, {
            mask: [
                { mask: '000.000.000-00', maxLength: 11 },
                { mask: '00.000.000/0000-00' }
            ]
        });

        window.cepMask = IMask(inputCep, { mask: '00000-000' });

        window.telMask = IMask(inputTelefone, {
            mask: [
                { mask: '(00) 0000-0000' },
                { mask: '(00) 00000-0000' }
            ]
        });
    }

    // --- FUNÇÕES AUXILIARES DE FORMATAÇÃO VISUAL ---
    function formatarDocumentoVisual(valor) {
        if (!valor) return '';
        const limpo = valor.replace(/\D/g, '');
        if (limpo.length === 11) { // CPF
            return limpo.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
        } else if (limpo.length === 14) { // CNPJ
            return limpo.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, "$1.$2.$3/$4-$5");
        }
        return valor;
    }

    function formatarTelefoneVisual(valor) {
        if (!valor) return '';
        const limpo = valor.replace(/\D/g, '');
        if (limpo.length === 10) {
            return limpo.replace(/(\d{2})(\d{4})(\d{4})/, "($1) $2-$3");
        } else if (limpo.length === 11) {
            return limpo.replace(/(\d{2})(\d{5})(\d{4})/, "($1) $2-$3");
        }
        return valor;
    }

    // --- FUNÇÕES DE CONTROLE VISUAL DE ERRO (ATUALIZADO) ---

    function setError(input, message) {
        // 1. Adiciona a borda vermelha
        input.classList.add('invalidInput');

        // 2. Procura se já existe a mensagem de erro no pai do input
        let parent = input.parentElement; // Pega a div .mb-3 ou .col
        let errorDiv = parent.querySelector('.error-msg');

        // 3. Se não existir, cria
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'error-msg';
            parent.appendChild(errorDiv);
        }

        // 4. Define o texto da mensagem
        errorDiv.textContent = message;
    }

    function clearError(input) {
        // 1. Remove a borda vermelha
        input.classList.remove('invalidInput');

        // 2. Remove a mensagem de erro se existir
        let parent = input.parentElement;
        if (parent) {
            const errorDiv = parent.querySelector('.error-msg');
            if (errorDiv) {
                errorDiv.remove();
            }
        }
    }

    // Remove o erro assim que o usuário digita algo
    [inputNome, inputDocumento, inputTelefone, inputCep, inputEmail, inputRua, inputBairro, inputCidade, inputUf].forEach(input => {
        if(input) {
            input.addEventListener('input', () => clearError(input));
        }
    });

    // --- VALIDAÇÕES LÓGICAS ---
    function validarCPF(cpf) {
        if (!cpf || cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return false;
        let soma = 0, resto;
        for (let i = 1; i <= 9; i++) soma += parseInt(cpf.substring(i - 1, i)) * (11 - i);
        resto = (soma * 10) % 11;
        if ((resto === 10) || (resto === 11)) resto = 0;
        if (resto !== parseInt(cpf.substring(9, 10))) return false;
        soma = 0;
        for (let i = 1; i <= 10; i++) soma += parseInt(cpf.substring(i - 1, i)) * (12 - i);
        resto = (soma * 10) % 11;
        if ((resto === 10) || (resto === 11)) resto = 0;
        if (resto !== parseInt(cpf.substring(10, 11))) return false;
        return true;
    }

    function validarCNPJ(cnpj) {
        if (!cnpj || cnpj.length !== 14 || /^(\d)\1{13}$/.test(cnpj)) return false;
        let tamanho = cnpj.length - 2;
        let numeros = cnpj.substring(0, tamanho);
        let digitos = cnpj.substring(tamanho);
        let soma = 0;
        let pos = tamanho - 7;
        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos--;
            if (pos < 2) pos = 9;
        }
        let resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
        if (resultado !== parseInt(digitos.charAt(0))) return false;
        tamanho = tamanho + 1;
        numeros = cnpj.substring(0, tamanho);
        soma = 0;
        pos = tamanho - 7;
        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos--;
            if (pos < 2) pos = 9;
        }
        resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
        if (resultado !== parseInt(digitos.charAt(1))) return false;
        return true;
    }

    function validarDocumento(documento) {
        if (documento.length === 11) return validarCPF(documento);
        else if (documento.length === 14) return validarCNPJ(documento);
        return false;
    }

    function validarFormulario() {
        let isValid = true;

        const nome = inputNome.value.trim();
        const documento = window.docMask ? window.docMask.unmaskedValue : inputDocumento.value.trim().replace(/\D/g, '');
        const email = inputEmail.value.trim();
        const telefone = window.telMask ? window.telMask.unmaskedValue : inputTelefone.value.trim().replace(/\D/g, '');
        const cep = window.cepMask ? window.cepMask.unmaskedValue : inputCep.value.trim().replace(/\D/g, '');
        const rua = inputRua.value.trim();
        const bairro = inputBairro.value.trim();
        const cidade = inputCidade.value.trim();
        const uf = inputUf.value.trim();

        // 1. Nome
        if (nome.length < 3) {
            setError(inputNome, 'Nome deve ter no mínimo 3 caracteres');
            isValid = false;
        } else {
            clearError(inputNome);
        }

        // 2. Documento
        if (!validarDocumento(documento)) {
            setError(inputDocumento, 'CPF ou CNPJ inválido');
            isValid = false;
        } else {
            clearError(inputDocumento);
        }

        // 3. Email
        if (!/\S+@\S+\.\S+/.test(email)) {
            setError(inputEmail, 'Formato de e-mail inválido');
            isValid = false;
        } else {
            clearError(inputEmail);
        }

        // 4. Telefone
        if (!/^\d{10,11}$/.test(telefone)) {
            setError(inputTelefone, 'Telefone incompleto');
            isValid = false;
        } else {
            clearError(inputTelefone);
        }

        // 5. CEP e Endereço
        if (cep.length > 0) {
            if(!/^\d{8}$/.test(cep)) {
                setError(inputCep, 'CEP incompleto');
                isValid = false;
            } else {
                clearError(inputCep);
            }

            // Para os campos de endereço, apenas marcamos vermelho sem mensagem para não poluir demais,
            // ou colocamos uma msg genérica "Campo obrigatório"
            if(rua.length === 0) { setError(inputRua, 'Obrigatório com CEP'); isValid = false; }
            if(bairro.length === 0) { setError(inputBairro, 'Obrigatório'); isValid = false; }
            if(cidade.length === 0) { setError(inputCidade, 'Obrigatório'); isValid = false; }
            if(uf.length === 0) { setError(inputUf, 'UF'); isValid = false; }
        }

        if(!isValid) {
            // Pequeno timeout para garantir que a UI atualizou antes do alert travar a tela
            setTimeout(() => alert('Verifique os campos destacados em vermelho.'), 10);
        }

        return isValid;
    }

    // --- LÓGICA DE TABELA E FILTRO ---

    function renderizarTabela(lista) {
        tabelaBody.innerHTML = '';

        if (lista.length === 0) {
            tabelaBody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Nenhum doador encontrado.</td></tr>';
            return;
        }

        lista.forEach(doador => {
            const tr = document.createElement('tr');

            const docFormatado = formatarDocumentoVisual(doador.documento);
            const telFormatado = formatarTelefoneVisual(doador.telefone);

            tr.innerHTML = `
                <td>${doador.id}</td>
                <td>${doador.nome}</td>
                <td>${docFormatado}</td>
                <td>${doador.email}</td>
                <td>${telFormatado}</td>
                <td class="text-center">
                    <div class="btn-group-actions">
                        <button class="btn btn-sm btn-outline-primary btn-editar" data-id="${doador.id}">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger btn-excluir" data-id="${doador.id}">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </div>
                </td>
            `;
            tabelaBody.appendChild(tr);
        });
    }

    async function carregarDoadores() {
        try{
            const response = await fetch(apiUrl, { method: 'GET' });
            if(!response.ok) throw new Error('Erro ao buscar doadores');

            const doadores = await response.json();
            listaGlobalDoadores = doadores;
            renderizarTabela(listaGlobalDoadores);

        } catch(error){
            console.error('Falha ao carregar doadores:', error);
            alert('Não foi possível carregar os doadores.');
        }
    }

    inputPesquisa.addEventListener('input', (e) => {
        const termo = e.target.value.toLowerCase();
        const doadoresFiltrados = listaGlobalDoadores.filter(d => {
            return (d.nome && d.nome.toLowerCase().includes(termo)) ||
                   (d.documento && d.documento.includes(termo)) ||
                   (d.email && d.email.toLowerCase().includes(termo));
        });
        renderizarTabela(doadoresFiltrados);
    });

    function resetarFormulario() {
        form.reset();
        hiddenId.value = '';
        formTitulo.textContent = 'Cadastro / Edição';
        btnCancelar.classList.add('d-none');

        if (window.docMask) window.docMask.value = '';
        if (window.cepMask) window.cepMask.value = '';
        if (window.telMask) window.telMask.value = '';

        const inputs = form.querySelectorAll('.form-control');
        inputs.forEach(input => clearError(input));

        form.querySelector('input').focus();
    }

    async function preencherFormularioParaEdicao(id) {
        try {
            const response = await fetch(`${apiUrl}/${id}`, { method: 'GET' });
            if(!response.ok) throw new Error('Doador não encontrado');

            const doador = await response.json();

            document.getElementById('id').value = doador.id;
            inputNome.value = doador.nome;

            if (window.docMask) window.docMask.value = doador.documento;
            else inputDocumento.value = doador.documento;

            if (window.telMask) window.telMask.value = doador.telefone;
            else inputTelefone.value = doador.telefone;

            if (window.cepMask) window.cepMask.value = doador.cep;
            else inputCep.value = doador.cep;

            inputRua.value = doador.rua;
            inputBairro.value = doador.bairro;
            inputCidade.value = doador.cidade;
            inputUf.value = doador.uf;
            inputEmail.value = doador.email;
            document.getElementById('contato').value = doador.contato;

            formTitulo.textContent = 'Editando Doador';
            btnCancelar.classList.remove('d-none');

            const inputs = form.querySelectorAll('.form-control');
            inputs.forEach(input => clearError(input));

            window.scrollTo(0, 0);

        } catch(error){
            console.error('Falha ao buscar doador:', error);
            alert('Não foi possível carregar o doador para edição.');
        }
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        if (!validarFormulario()) return;

        const formData = new FormData(form);
        const doador = Object.fromEntries(formData.entries());

        doador.documento = window.docMask ? window.docMask.unmaskedValue : doador.documento.replace(/\D/g, '');
        doador.telefone = window.telMask ? window.telMask.unmaskedValue : doador.telefone.replace(/\D/g, '');
        doador.cep = window.cepMask ? window.cepMask.unmaskedValue : doador.cep.replace(/\D/g, '');

        const id = hiddenId.value;
        const isEdicao = id > 0;
        const url = isEdicao ? `${apiUrl}/${id}` : apiUrl;
        const method = isEdicao ? 'PUT' : 'POST';

        try{
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(doador),
            });

            if(!response.ok){
                const errorData = await response.json();
                throw new Error(errorData.erro || 'Erro ao salvar doador');
            }

            alert(`Doador ${isEdicao ? 'atualizado' : 'salvo'} com sucesso!`);
            resetarFormulario();
            carregarDoadores();

        }catch(error){
            console.error('Falha ao salvar:', error);
            alert(`Não foi possível salvar o doador. ${error.message}`);
        }
    });

    tabelaBody.addEventListener('click', (e) => {
        const target = e.target.closest('button');
        if (!target) return;

        const id = target.getAttribute('data-id');

        if(target.classList.contains('btn-editar')) {
            preencherFormularioParaEdicao(id);
        }

        if(target.classList.contains('btn-excluir')){
            if (confirm('Tem certeza que deseja excluir este doador?')) {
                excluirDoador(id);
            }
        }
    });

    async function excluirDoador(id) {
        try{
            const response = await fetch(`${apiUrl}/${id}`, { method: 'DELETE' });

            if(!response.ok){
                let errorMsg = 'Erro ao excluir doador';
                try {
                    const errorData = await response.json();
                    errorMsg = errorData.erro || errorMsg;
                } catch(e) {}
                throw new Error(errorMsg);
            }

            alert('Doador excluído com sucesso!');
            carregarDoadores();

        }catch(error){
            console.error('Falha ao excluir:', error);
            alert('Não foi possível excluir o doador.');
        }
    }

    btnCancelar.addEventListener('click', resetarFormulario);

    carregarDoadores();
});