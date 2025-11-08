document.addEventListener('DOMContentLoaded', () => {

    const form = document.getElementById('formDoador');
    const tabelaBody = document.getElementById('tabelaDoadores');
    const btnCancelar = document.getElementById('btnCancelar');
    const formTitulo = document.querySelector('.form-container h2');
    const hiddenId = document.getElementById('id');

    // Elementos para Máscara
    const inputNome = document.getElementById('nome');
    const inputDocumento = document.getElementById('documento');
    const inputTelefone = document.getElementById('telefone');
    const inputCep = document.getElementById('cep');

    const apiUrl = '/apis/doador';

    // --- REINICIALIZAÇÃO DAS MÁSCARAS (IMASK) ---

    // 1. Máscara para Documento (alterna entre CPF e CNPJ)
    // Assumindo que IMask está carregado via HTML
    if (typeof IMask !== 'undefined') {
        window.docMask = IMask(inputDocumento, {
            mask: [
                {
                    mask: '000.000.000-00',
                    maxLength: 11 // Define o limite para mudar para CNPJ
                },
                {
                    mask: '00.000.000/0000-00'
                }
            ]
        });

        // 2. Máscara para CEP
        window.cepMask = IMask(inputCep, {
            mask: '00000-000'
        });

        // 3. Máscara para Telefone (alterna entre fixo e celular)
        window.telMask = IMask(inputTelefone, {
            mask: [
                { mask: '(00) 0000-0000' },
                { mask: '(00) 00000-0000' }
            ]
        });
    }

    // --- FUNÇÕES DE VALIDAÇÃO REAL DE CPF/CNPJ (MANTIDAS) ---

    function validarCPF(cpf) {
        if (!cpf || cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return false;

        let soma;
        let resto;
        soma = 0;

        for (let i = 1; i <= 9; i++) {
            soma = soma + parseInt(cpf.substring(i - 1, i)) * (11 - i);
        }
        resto = (soma * 10) % 11;
        if ((resto === 10) || (resto === 11)) resto = 0;
        if (resto !== parseInt(cpf.substring(9, 10))) return false;

        soma = 0;
        for (let i = 1; i <= 10; i++) {
            soma = soma + parseInt(cpf.substring(i - 1, i)) * (12 - i);
        }
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
        if (documento.length === 11) {
            return validarCPF(documento);
        } else if (documento.length === 14) {
            return validarCNPJ(documento);
        }
        return false;
    }
    // --- FIM FUNÇÕES DE VALIDAÇÃO REAL ---

    function validarFormulario() {

        const nome = document.getElementById('nome').value.trim();
        // A validação usa o valor "limpo" diretamente da máscara (unmaskedValue)
        const documento = window.docMask ? window.docMask.unmaskedValue : inputDocumento.value.trim().replace(/\D/g, '');
        const email = document.getElementById('email').value.trim();
        const telefone = window.telMask ? window.telMask.unmaskedValue : document.getElementById('telefone').value.trim().replace(/\D/g, '');
        const cep = window.cepMask ? window.cepMask.unmaskedValue : document.getElementById('cep').value.trim().replace(/\D/g, '');
        const rua = document.getElementById('rua').value.trim();
        const bairro = document.getElementById('bairro').value.trim();
        const cidade = document.getElementById('cidade').value.trim();
        const uf = document.getElementById('uf').value.trim();

        // 1. Validação do Nome
        if (nome.length < 3) {
            alert('O nome deve ter pelo menos 3 caracteres.');
            document.getElementById('nome').focus();
            return false;
        }

        // 2. Validação do Documento (CPF/CNPJ Real)
        if (!validarDocumento(documento)) {
            alert('Documento inválido. Deve ser um CPF (11 dígitos) ou CNPJ (14 dígitos) válido.');
            inputDocumento.focus();
            return false;
        }

        // 3. Validação de Email
        if (!/\S+@\S+\.\S+/.test(email)) {
            alert('Formato de e-mail inválido.');
            document.getElementById('email').focus();
            return false;
        }

        // 4. Validação de Telefone (Exige 10 ou 11 dígitos, sem pontuação)
        if (!/^\d{10,11}$/.test(telefone)) {
            alert('Telefone inválido. Deve ter 10 ou 11 dígitos (com DDD).');
            document.getElementById('telefone').focus();
            return false;
        }

        // 5. Validação de CEP
        if (cep.length > 0 && !/^\d{8}$/.test(cep)) {
            alert('CEP inválido. Se preenchido, deve ter 8 dígitos, sem pontuação.');
            document.getElementById('cep').focus();
            return false;
        }

        // 6. Campos de Endereço
        if (cep.length > 0 && (rua.length === 0 || bairro.length === 0 || cidade.length === 0 || uf.length === 0)) {
            alert('Se o CEP for informado, por favor, preencha o restante do endereço (Rua, Bairro, Cidade, UF).');
            document.getElementById('rua').focus();
            return false;
        }

        return true;
    }


    async function carregarDoadores() {

        try{
            const response = await fetch(apiUrl, {
                method: 'GET',
                headers: {}
            });

            if(!response.ok)
                throw new Error('Erro ao buscar doadores');

            const doadores = await response.json();

            tabelaBody.innerHTML = '';
            doadores.forEach(doador => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${doador.id}</td>
                    <td>${doador.nome}</td>
                    <td>${doador.documento}</td>
                    <td>${doador.email}</td>
                    <td>${doador.telefone}</td>
                    <td>
                        <button class="btn btn-sm btn-warning btn-editar" data-id="${doador.id}">Editar</button>
                        <button class="btn btn-sm btn-danger btn-excluir" data-id="${doador.id}">Excluir</button>
                    </td>
                `;
                tabelaBody.appendChild(tr);
            });
        }catch(error){
            console.error('Falha ao carregar doadores:', error);
            alert('Não foi possível carregar os doadores.');
        }
    }

    function resetarFormulario() {
        form.reset();
        hiddenId.value = '';
        formTitulo.textContent = 'Cadastro de Doador';
        btnCancelar.classList.add('d-none');
        form.querySelector('input').focus();

        // Limpa os valores das máscaras ao resetar (se imask estiver disponível)
        if (window.docMask) window.docMask.value = '';
        if (window.cepMask) window.cepMask.value = '';
        if (window.telMask) window.telMask.value = '';
    }

    async function preencherFormularioParaEdicao(id) {

        try {
            const response = await fetch(`${apiUrl}/${id}`, {
                method: 'GET',
                headers: {}
            });

            if(!response.ok)
                throw new Error('Doador não encontrado');

            const doador = await response.json();

            document.getElementById('id').value = doador.id;
            document.getElementById('nome').value = doador.nome;
            // Usa as instâncias de máscara para preencher o valor formatado
            if (window.docMask) window.docMask.value = doador.documento;
            if (window.telMask) window.telMask.value = doador.telefone;
            if (window.cepMask) window.cepMask.value = doador.cep;

            document.getElementById('rua').value = doador.rua;
            document.getElementById('bairro').value = doador.bairro;
            document.getElementById('cidade').value = doador.cidade;
            document.getElementById('uf').value = doador.uf;
            document.getElementById('email').value = doador.email;
            document.getElementById('contato').value = doador.contato;

            formTitulo.textContent = 'Editando Doador';
            btnCancelar.classList.remove('d-none');
            window.scrollTo(0, 0);

        }catch(error){
            console.error('Falha ao buscar doador:', error);
            alert('Não foi possível carregar o doador para edição.');
        }
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        if (!validarFormulario())
            return;

        const formData = new FormData(form);
        const doador = Object.fromEntries(formData.entries());

        // Garante que os valores enviados são os limpos da máscara (unmaskedValue)
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
                headers: {
                    'Content-Type': 'application/json',
                },
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
        const target = e.target;
        const id = target.getAttribute('data-id');

        if(target.classList.contains('btn-editar'))
            preencherFormularioParaEdicao(id);

        if(target.classList.contains('btn-excluir')){
            if (confirm('Tem certeza que deseja excluir este doador?'))
                excluirDoador(id);
        }
    });

    async function excluirDoador(id) {

        try{
            const response = await fetch(`${apiUrl}/${id}`, {
                method: 'DELETE',
                headers: {}
            });

            if(!response.ok){
                let errorMsg = 'Erro ao excluir doador';
                try {
                    const errorData = await response.json();
                    errorMsg = errorData.erro || errorMsg;
                } catch(e) { /* Sem corpo no erro */ }
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

    // Carrega o select doador e inicializa as máscaras (se imask estiver disponível)
    carregarDoadores();
});