// Aguarda o DOM ser totalmente carregado
document.addEventListener('DOMContentLoaded', () => {

    const form = document.getElementById('formDoador');
    const tabelaBody = document.getElementById('tabelaDoadores');
    const btnCancelar = document.getElementById('btnCancelar');
    const formTitulo = document.querySelector('.form-container h2');
    const hiddenId = document.getElementById('id');

    const apiUrl = '/doador';

    // Função para carregar os doadores na tabela
    async function carregarDoadores() {
        try {
            const response = await fetch(apiUrl);
            if (!response.ok) {
                throw new Error('Erro ao buscar doadores');
            }
            const doadores = await response.json();

            // Limpa o corpo da tabela
            tabelaBody.innerHTML = '';

            // Preenche a tabela
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
        } catch (error) {
            console.error('Falha ao carregar doadores:', error);
            alert('Não foi possível carregar os doadores.');
        }
    }

    // Função para limpar e resetar o formulário
    function resetarFormulario() {
        form.reset();
        hiddenId.value = ''; // Limpa o ID oculto
        formTitulo.textContent = 'Cadastro de Doador';
        btnCancelar.classList.add('d-none'); // Esconde o botão cancelar
        form.querySelector('input').focus();
    }

    // Função para preencher o formulário para edição
    async function preencherFormularioParaEdicao(id) {
        try {
            const response = await fetch(`${apiUrl}/${id}`);
            if (!response.ok) {
                throw new Error('Doador não encontrado');
            }
            const doador = await response.json();

            // Preenche todos os campos do formulário
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

            // Ajusta a UI para modo de edição
            formTitulo.textContent = 'Editando Doador';
            btnCancelar.classList.remove('d-none'); // Mostra o botão cancelar
            window.scrollTo(0, 0); // Rola para o topo (onde está o formulário)

        } catch (error) {
            console.error('Falha ao buscar doador:', error);
            alert('Não foi possível carregar o doador para edição.');
        }
    }

    // Event Listener para Salvar (Criar ou Atualizar)
    form.addEventListener('submit', async (e) => {
        e.preventDefault(); // Impede o envio tradicional

        // Coleta os dados do formulário
        const formData = new FormData(form);
        const doador = Object.fromEntries(formData.entries());

        const id = hiddenId.value;
        const isEdicao = id > 0;

        const url = isEdicao ? `${apiUrl}/${id}` : apiUrl;
        const method = isEdicao ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(doador),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Erro ao salvar doador');
            }

            alert(`Doador ${isEdicao ? 'atualizado' : 'salvo'} com sucesso!`);
            resetarFormulario();
            carregarDoadores();

        } catch (error) {
            console.error('Falha ao salvar:', error);
            alert(`Não foi possível salvar o doador. ${error.message}`);
        }
    });

    // Event Listener para botões na tabela (Editar e Excluir)
    tabelaBody.addEventListener('click', (e) => {
        const target = e.target;
        const id = target.getAttribute('data-id');

        if (target.classList.contains('btn-editar')) {
            preencherFormularioParaEdicao(id);
        }

        if (target.classList.contains('btn-excluir')) {
            if (confirm('Tem certeza que deseja excluir este doador?')) {
                excluirDoador(id);
            }
        }
    });

    // Função para Excluir
    async function excluirDoador(id) {
         try {
            const response = await fetch(`${apiUrl}/${id}`, {
                method: 'DELETE',
            });

            if (!response.ok) {
                throw new Error('Erro ao excluir doador');
            }

            alert('Doador excluído com sucesso!');
            carregarDoadores();

        } catch (error) {
            console.error('Falha ao excluir:', error);
            alert('Não foi possível excluir o doador.');
        }
    }

    // Event Listener para o botão Cancelar
    btnCancelar.addEventListener('click', resetarFormulario);

    // Carga inicial dos dados
    carregarDoadores();
});