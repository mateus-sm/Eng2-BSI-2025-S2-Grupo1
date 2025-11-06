document.addEventListener('DOMContentLoaded', async () => {

    const loadingDiv = document.getElementById('loading');
    const erroDiv = document.getElementById('erro');
    const dadosContainer = document.getElementById('dados-container');
    const btnExcluir = document.getElementById('btnExcluir');

    let idParaExcluir = null;

    try {
        const response = await fetch('/apis/parametrizacao'); //

        if (!response.ok) {
            if(response.status === 404) {
                throw new Error(`Nenhum parâmetro cadastrado.`);
            }
            throw new Error(`Erro ao buscar dados: ${response.statusText}`);
        }

        const dados = await response.json();

        if (dados) {
            idParaExcluir = dados.id;

            // --- CORREÇÃO AQUI: TODOS OS CAMPOS ADICIONADOS ---
            document.getElementById('id').textContent = dados.id;
            document.getElementById('nomeFantasia').textContent = dados.nomeFantasia;
            document.getElementById('razaoSocial').textContent = dados.razaoSocial;
            document.getElementById('cnpj').textContent = dados.cnpj;
            document.getElementById('descricao').textContent = dados.descricao;
            document.getElementById('rua').textContent = dados.rua;
            document.getElementById('bairro').textContent = dados.bairro;
            document.getElementById('cidade').textContent = dados.cidade;
            document.getElementById('uf').textContent = dados.uf;
            document.getElementById('cep').textContent = dados.cep;
            document.getElementById('telefone').textContent = dados.telefone;
            document.getElementById('email').textContent = dados.email;
            document.getElementById('site').textContent = dados.site;
            document.getElementById('logoGrande').textContent = dados.logoGrande;
            document.getElementById('logoPequeno').textContent = dados.logoPequeno;
            // --- FIM DA CORREÇÃO ---

            loadingDiv.style.display = 'none';
            dadosContainer.style.display = 'block';
            btnExcluir.style.display = 'block';

            btnExcluir.addEventListener('click', async () => {
                if (!idParaExcluir) {
                    alert('Erro: ID do registro não encontrado.');
                    return;
                }

                if (confirm('Tem certeza que deseja excluir?')) {
                    try {
                        const response = await fetch(`/apis/parametrizacao/${idParaExcluir}`, { //
                            method: 'DELETE'
                        });

                        if (response.ok || response.status === 204) {
                            alert('Registro excluído com sucesso!');
                            window.location.href = '/app/parametrizacao'; //
                        } else {
                            alert(`Falha ao excluir. Status ${response.status}.`);
                        }
                    } catch (error) {
                        alert('Erro de conexão ao tentar excluir.');
                    }
                }
            });

        } else {
            erroDiv.textContent = 'Resposta vazia do servidor.';
            erroDiv.style.display = 'block';
            loadingDiv.style.display = 'none';
        }

    } catch (error) {
        console.error('Falha na requisição:', error);
        erroDiv.textContent = error.message;
        erroDiv.style.display = 'block';
        loadingDiv.style.display = 'none';
    }
});