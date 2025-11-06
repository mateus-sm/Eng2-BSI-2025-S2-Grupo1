document.addEventListener('DOMContentLoaded', () => {

    const docElement = document.getElementById('documento');
    const cepElement = document.getElementById('cep');
    const telElement = document.getElementById('telefone');

    const docMask = IMask(docElement, {
        mask: [
            {
                mask: '000.000.000-00',
                maxLength: 11
            },
            {
                mask: '00.000.000/0000-00'
            }
        ]
    });

    const cepMask = IMask(cepElement, {
        mask: '00000-000'
    });

    const telMask = IMask(telElement, {
        mask: [
            { mask: '(00) 0000-0000' },
            { mask: '(00) 00000-0000' }
        ]
    });

    const form = document.getElementById('formDoador');
    const tabelaBody = document.getElementById('tabelaDoadores');
    const btnCancelar = document.getElementById('btnCancelar');
    const formTitulo = document.querySelector('.form-container h2');
    const hiddenId = document.getElementById('id');

    const apiUrl = '/apis/doador';

    function validarFormulario() {

        const nome = document.getElementById('nome').value.trim();
        const documento = document.getElementById('documento').value.trim().replace(/\D/g, ''); // Remove não-dígitos
        const email = document.getElementById('email').value.trim();
        const telefone = document.getElementById('telefone').value.trim().replace(/\D/g, ''); // Remove não-dígitos
        const cep = document.getElementById('cep').value.trim().replace(/\D/g, ''); // Remove não-dígitos
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

        // 2. Validação do Documento (CPF/CNPJ)
        if (!/^\d{11}$/.test(documento) && !/^\d{14}$/.test(documento)) {
            alert('Documento inválido. Deve ser um CPF (11 dígitos) ou CNPJ (14 dígitos), sem pontuação.');
            document.getElementById('documento').focus();
            return false;
        }

        // 3. Validação de Email
        if (!/\S+@\S+\.\S+/.test(email)) { // Regex simples de email
            alert('Formato de e-mail inválido.');
            document.getElementById('email').focus();
            return false;
        }

        // 4. Validação de Telefone (Ex: 10 ou 11 dígitos)
        if (!/^\d{10,11}$/.test(telefone)) {
            alert('Telefone inválido. Deve ter 10 ou 11 dígitos (com DDD).');
            document.getElementById('telefone').focus();
            return false;
        }

        // 5. Validação de CEP (Opcional, mas se preenchido, deve ter 8 dígitos)
        if (cep.length > 0 && !/^\d{8}$/.test(cep)) {
            alert('CEP inválido. Se preenchido, deve ter 8 dígitos, sem pontuação.');
            document.getElementById('cep').focus();
            return false;
        }

        // 6. Campos de Endereço (Ex: Opcionais, mas se um for preenchido, os outros também?)
        // Se o CEP for preenchido, talvez forçar os outros?
        if (cep.length > 0 && (rua.length === 0 || bairro.length === 0 || cidade.length === 0 || uf.length === 0)) {
            alert('Se o CEP for informado, por favor, preencha o restante do endereço (Rua, Bairro, Cidade, UF).');
            document.getElementById('rua').focus();
            return false;
        }

        // Se chegou até aqui, está tudo certo
        return true;
    }


    async function carregarDoadores() {

        try{
            const response = await fetch(apiUrl, {
                method: 'GET',
                headers: {
                }
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

        docMask.value = '';
        cepMask.value = '';
        telMask.value = '';
    }

    async function preencherFormularioParaEdicao(id) {

        try {
            const response = await fetch(`${apiUrl}/${id}`, {
                method: 'GET',
                headers: {
                }
            });

            if(!response.ok)
                throw new Error('Doador não encontrado');

            const doador = await response.json();

            document.getElementById('id').value = doador.id;
            document.getElementById('nome').value = doador.nome;
            docMask.value = doador.documento;
            telMask.value = doador.telefone;
            cepMask.value = doador.cep;

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

        docElement.value = docMask.unmaskedValue;
        cepElement.value = cepMask.unmaskedValue;
        telElement.value = telMask.unmaskedValue;

        if (!validarFormulario()) {
             docMask.updateValue();
             cepMask.updateValue();
             telMask.updateValue();
             return; //Para a execução se o formulário for inválido
        }


        const formData = new FormData(form);
        const doador = Object.fromEntries(formData.entries());

        doador.documento = docMask.unmaskedValue;
        doador.cep = cepMask.unmaskedValue;
        doador.telefone = telMask.unmaskedValue;


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
                headers: {
                }
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

    carregarDoadores();
});