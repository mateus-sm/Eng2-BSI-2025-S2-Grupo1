// para os modais do Bootstrap
let membroModal, deleteModal;
let idParaExcluir = null;

// URL base da sua API
const API_URL = '/apis/membro';

/**
 * Pega o token do localStorage. Se não encontrar, redireciona para o login.
 */
//function getToken() {
//    const token = localStorage.getItem('user_token');
//    if (!token) {
//        alert("Acesso não autorizado. Por favor, faça o login.");
//        window.location.href = '/app/login';
//        return null;
//    }
//    return token;
//}

/**
 * Lida com erros de autenticação (ex: token expirado)
 */
function handleAuthError() {
    localStorage.removeItem('user_token'); // Limpa o token inválido
    alert("Sua sessão expirou. Por favor, faça o login novamente.");
    window.location.href = '/app/login';
}

/**
 * Função principal: Carrega os membros da API e popula a tabela
 */
async function carregarMembros() {
    // Pega o token de autenticação
    const token = getToken();
    if (!token) return; // Para a execução se não houver token

    try {
        // Envia o token no cabeçalho
        const response = await fetch(API_URL, {
            method: 'GET',
            headers: {
                'Authorization': token
            }
        });

        if (!response.ok) {
            // Trata token expirado/inválido
            if (response.status === 401) {
                return handleAuthError();
            }
            throw new Error('Falha ao carregar membros.');
        }

        const membros = await response.json();
        const tabelaBody = document.getElementById('tabela-membros');
        tabelaBody.innerHTML = ''; // Limpa a tabela

        if (membros.length === 0) {
            tabelaBody.innerHTML = '<tr><td colspan="7" class="text-center">Nenhum membro encontrado.</td></tr>';
            return;
        }

        membros.forEach(membro => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${membro.id}</td>
                <td>${membro.codigo}</td>
                <td>${membro.usuario ? membro.usuario.nome : 'N/A'}</td>
                <td>${membro.observacao || ''}</td>
                <td>${formatarData(membro.dtIni)}</td>
                <td>${formatarData(membro.dtFim)}</td>
                <td class="text-center">
                    <div class="btn-group" role="group">
                        <button class="btn btn-sm btn-outline-primary btn-editar" data-id="${membro.id}">
                            <i class="bi bi-pencil-fill"></i> Editar
                        </button>
                        <button class="btn btn-sm btn-outline-danger btn-excluir" data-id="${membro.id}">
                            <i class="bi bi-trash-fill"></i> Excluir
                        </button>
                    </div>
                </td>
            `;
            tabelaBody.appendChild(tr);
        });

        // Adiciona listeners aos novos botões
        document.querySelectorAll('.btn-editar').forEach(btn => {
            btn.addEventListener('click', (e) => abrirModalEditar(e.currentTarget.dataset.id));
        });
        document.querySelectorAll('.btn-excluir').forEach(btn => {
            btn.addEventListener('click', (e) => abrirModalExcluir(e.currentTarget.dataset.id));
        });

    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao carregar membros.');
    }
}

/**
 * Abre o modal para criar um novo membro (limpa o formulário)
 */
function abrirModalAdicionar() {
    const form = document.getElementById('form-membro');
    form.reset();
    form.classList.remove('edit-mode'); // Remove a classe de edição
    document.getElementById('membroId').value = '';
    document.getElementById('membroModalLabel').innerText = 'Adicionar Membro';

    // Habilita o campo 'usuarioId' que é desabilitado na edição
    document.getElementById('usuarioId').disabled = false;

    membroModal.show();
}

/**
 * Abre o modal para editar um membro (busca dados e preenche o formulário)
 */
async function abrirModalEditar(id) {
    // Pega o token de autenticação
    const token = getToken();
    if (!token) return;

    try {
        // Envia o token no cabeçalho
        const response = await fetch(`${API_URL}/get-by-id/${id}`, {
            method: 'GET',
            headers: {
                'Authorization': token
            }
        });

        if (!response.ok) {
            // Trata token expirado/inválido
            if (response.status === 401) {
                return handleAuthError();
            }
            throw new Error('Membro não encontrado.');
        }

        const membro = await response.json();

        // Preenche o formulário
        const form = document.getElementById('form-membro');
        form.classList.add('edit-mode'); // Adiciona a classe de edição
        document.getElementById('membroModalLabel').innerText = 'Editar Membro';

        document.getElementById('membroId').value = membro.id;
        document.getElementById('codigo').value = membro.codigo;
        document.getElementById('usuarioId').value = membro.usuario.id;
        document.getElementById('usuarioId').disabled = true; // Não deve ser possível alterar o usuário
        document.getElementById('observacao').value = membro.observacao || '';
        document.getElementById('dtFim').value = membro.dtFim || '';

        membroModal.show();

    } catch (error) {
        console.error('Erro ao buscar membro:', error);
        alert(error.message);
    }
}

/**
 * Salva (Cria ou Atualiza) um membro
 */
async function salvarMembro(event) {
    event.preventDefault(); // Impede o submit tradicional do formulário

    // Pega o token de autenticação
    const token = getToken();
    if (!token)
        return;

    const id = document.getElementById('membroId').value;
    const codigo = document.getElementById('codigo').value;
    const usuarioId = document.getElementById('usuarioId').value;
    const observacao = document.getElementById('observacao').value;
    const dtFim = document.getElementById('dtFim').value || null; // Envia null se vazio

    if (parseInt(codigo) <= 0) {
        alert("Erro: O Código do Membro deve ser um número positivo.");
        return;
    }

    const ehUpdate = !!id; // Converte para booleano (true se 'id' não for vazio)

    let url = API_URL;
    let method = 'POST';

    // Objeto base para o JSON
    const membro = {
        codigo: parseInt(codigo),
        observacao: observacao
    };

    if (ehUpdate) {
        // --- UPDATE (PUT) ---
        url = `${API_URL}/${id}`;
        method = 'PUT';
        membro.dtFim = dtFim; // Adiciona dtFim apenas no update
    }
    else {
        // --- CREATE (POST) ---
        // O backend espera um objeto 'usuario' aninhado com o 'id'
        membro.usuario = { id: parseInt(usuarioId) };
    }

    try {
        // Envia o token no cabeçalho
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token
            },
            body: JSON.stringify(membro)
        });

        if (!response.ok) {
            // Trata token expirado/inválido
            if (response.status === 401) {
                return handleAuthError();
            }

            const erro = await response.json();
            // Lógica de erro corrigida (procura por 'erro.erro')
            const mensagemEspecifica = erro.erro || erro.mensagem || erro.detalhe || erro.message;
            throw new Error(mensagemEspecifica || 'Falha ao salvar membro.');
        }

        membroModal.hide();
        carregarMembros(); // Recarrega a tabela

    } catch (error) {
        console.error('Erro ao salvar:', error);
        alert(`Erro: ${error.message}`);
    }
}

/**
 * Abre o modal de confirmação de exclusão
 */
function abrirModalExcluir(id) {
    idParaExcluir = id; // Armazena o ID para o botão de confirmação
    deleteModal.show();
}

/**
 * Executa a exclusão do membro
 */
async function excluirMembro() {
    if (!idParaExcluir) return;

    // Pega o token de autenticação
    const token = getToken();
    if (!token) return;

    try {
        // Envia o token no cabeçalho
        const response = await fetch(`${API_URL}/${idParaExcluir}`, {
            method: 'DELETE',
            headers: {
                'Authorization': token
            }
        });

        if (!response.ok) {
            // Trata token expirado/inválido
            if (response.status === 401) {
                return handleAuthError();
            }

            const erro = await response.json();
            // Lógica de erro corrigida (procura por 'erro.erro')
            throw new Error(erro.erro || 'Falha ao excluir membro.');
        }

        deleteModal.hide();
        carregarMembros(); // Recarrega a tabela
        idParaExcluir = null;

    } catch (error) {
        console.error('Erro ao excluir:', error);
        alert(`Erro: ${error.message}`);
    }
}

/**
 * Utilitário: Formata data (YYYY-MM-DD) para (DD/MM/YYYY) ou retorna ''
 */
function formatarData(data) {
    if (!data) return '';
    const [ano, mes, dia] = data.split('-');
    return `${dia}/${mes}/${ano}`;
}

// --- INICIALIZAÇÃO ---
// Aguarda o DOM carregar completamente
document.addEventListener('DOMContentLoaded', () => {
    // Instancia os modais do Bootstrap
    membroModal = new bootstrap.Modal(document.getElementById('membroModal'));
    deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));

    // Carrega os membros na tabela (isso agora vai exigir um token)
    carregarMembros();

    // Listeners dos botões principais
    document.getElementById('btn-novo-membro').addEventListener('click', abrirModalAdicionar);
    document.getElementById('form-membro').addEventListener('submit', salvarMembro);
    document.getElementById('btn-confirmar-exclusao').addEventListener('click', excluirMembro);
});