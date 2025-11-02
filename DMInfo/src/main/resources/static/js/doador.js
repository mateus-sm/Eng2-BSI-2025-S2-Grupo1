// Aguarda o DOM ser totalmente carregado
document.addEventListener('DOMContentLoaded', () => {

    const form = document.getElementById('formDoador');
    const tabelaBody = document.getElementById('tabelaDoadores');
    const btnCancelar = document.getElementById('btnCancelar');
    const formTitulo = document.querySelector('.form-container h2');
    const hiddenId = document.getElementById('id');

    const apiUrl = '/apis/doador';

    function getToken() {
        const token = localStorage.getItem('user_token');
        if(!token){
            alert("Acesso não autorizado. Por favor, faça o login.");
            window.location.href = '/login.html';
            return null;
        }
        return token;
    }

    function handleAuthError() {
        localStorage.removeItem('user_token'); // Limpa o token inválido
        alert("Sua sessão expirou. Por favor, faça o login novamente.");
        window.location.href = '/login.html';
    }

    async function carregarDoadores() {
        const token = getToken();
        if(!token)
            return;

        try{
            const response = await fetch(apiUrl, {
                method: 'GET',
                headers: {
                    'Authorization': token
                }
            });

            if(!response.ok){
                if (response.status === 401) return handleAuthError();
                throw new Error('Erro ao buscar doadores');
            }
            const doadores = await response.json();

            //Limpa o corpo da tabela
            tabelaBody.innerHTML = '';

            //Preenche a tabela
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
        hiddenId.value = ''; // Limpa o ID oculto
        formTitulo.textContent = 'Cadastro de Doador';
        btnCancelar.classList.add('d-none'); // Esconde o botão cancelar
        form.querySelector('input').focus();
    }

    async function preencherFormularioParaEdicao(id) {
        const token = getToken();
        if(!token)
            return;

        try {
            const response = await fetch(`${apiUrl}/${id}`, {
                method: 'GET',
                headers: {
                    'Authorization': token
                }
            });

            if(!response.ok){
                if (response.status === 401) return handleAuthError();
                throw new Error('Doador não encontrado');
            }
            const doador = await response.json();

            //Preenche todos os campos do formulário
            document.getElementById('id').value = doador.id;
            document.getElementById('nome').value = doador.nome;
            document.getElementById('documento').value = doador.documento;
            document.getElementById('rua').value = doador.rua;
            document.getElementById('bairro').value = doador.bairro;
            document.getElementById('cidade').value = doador.cidade;
            document.getElementById('uf').value = doador.uf;
            document.getElementById('cep').value = doador.cep;
            document.getElementById('email').value = doador.email;
            document.getElementById('telefone').value = doador.telefone;
            document.getElementById('contato').value = doador.contato;

            formTitulo.textContent = 'Editando Doador';
            btnCancelar.classList.remove('d-none'); //Mostra o botão cancelar
            window.scrollTo(0, 0); //Rola para o topo

        }catch(error){
            console.error('Falha ao buscar doador:', error);
            alert('Não foi possível carregar o doador para edição.');
        }
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault(); //Impede o envio tradicional

        const token = getToken();
        if(!token)
            return;

        //Coleta os dados do formulário
        const formData = new FormData(form);
        const doador = Object.fromEntries(formData.entries());

        const id = hiddenId.value;
        const isEdicao = id > 0;

        const url = isEdicao ? `${apiUrl}/${id}` : apiUrl;
        const method = isEdicao ? 'PUT' : 'POST';

        try{
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': token
                },
                body: JSON.stringify(doador),
            });

            if(!response.ok){
                if(response.status === 401)
                    return handleAuthError();

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
        const token = getToken();
        if(!token)
            return;

        try{
            const response = await fetch(`${apiUrl}/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': token
                }
            });

            if(!response.ok){
                if (response.status === 401) return handleAuthError();

                //Tenta ler o erro, mas se não tiver (ex: noContent() com erro), usa o fallback
                let errorMsg = 'Erro ao excluir doador';
                try {
                    const errorData = await response.json();
                    errorMsg = errorData.erro || errorMsg;
                } catch(e) {
                    //Sem corpo no erro, usa o fallback
                }
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