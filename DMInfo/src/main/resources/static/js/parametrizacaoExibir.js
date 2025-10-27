// Conteúdo de: /static/js/parametrizacaoExibir.js

// Executa o script quando o HTML da página estiver pronto
document.addEventListener('DOMContentLoaded', async () => {

    // Pega todos os elementos da página
    const loadingDiv = document.getElementById('loading');
    const erroDiv = document.getElementById('erro');
    const dadosContainer = document.getElementById('dados-container');
    const btnExcluir = document.getElementById('btnExcluir');

    let idParaExcluir = null; // Variável para guardar o ID

    try {
        // 1. Fazer a chamada GET para o backend
        const response = await fetch('/parametrizacao');

        if (!response.ok) {
            throw new Error(`Erro ao buscar dados: ${response.statusText}`);
        }

        const listaParametros = await response.json();

        if (listaParametros && listaParametros.length > 0) {
            const dados = listaParametros[0];

            idParaExcluir = dados.id; // Salva o ID

            // 4. Preenche o HTML
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

            // Mostra os dados
            loadingDiv.style.display = 'none';
            dadosContainer.style.display = 'block';

            // ==========================================
            // CORREÇÃO: MOSTRA O BOTÃO E ADICIONA O CLIQUE
            // ==========================================
            btnExcluir.style.display = 'block'; // 1. Mostra o botão

            // 2. Adiciona o evento de clique SÓ DEPOIS que ele está visível
            btnExcluir.addEventListener('click', async () => {
                if (!idParaExcluir) {
                    alert('Erro: ID do registro não encontrado.');
                    return;
                }

                if (confirm('Tem certeza que deseja excluir permanentemente este registro?')) {
                    try {
                        const response = await fetch(`/parametrizacao/${idParaExcluir}`, {
                            method: 'DELETE'
                        });

                        if (response.ok || response.status === 204) {
                            alert('Registro excluído com sucesso!');
                            window.location.href = '/app/parametrizacao';
                        } else {
                            alert(`Falha ao excluir. O servidor respondeu com status ${response.status}.`);
                        }
                    } catch (error) {
                        console.error('Erro ao excluir:', error);
                        alert('Erro de conexão ao tentar excluir o registro.');
                    }
                }
            });

        } else {
            // Caso o banco esteja vazio
            erroDiv.textContent = 'Nenhum parâmetro cadastrado no banco de dados.';
            erroDiv.style.display = 'block';
            loadingDiv.style.display = 'none';
        }

    } catch (error) {
        // Tratar erros de rede ou do fetch
        console.error('Falha na requisição:', error);
        erroDiv.textContent = `Erro ao carregar dados: ${error.message}`;
        erroDiv.style.display = 'block';
        loadingDiv.style.display = 'none';
    }
});