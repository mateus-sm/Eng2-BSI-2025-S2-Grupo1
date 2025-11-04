// 👇 1. URL DA API CORRIGIDA
const API_URL = '/apis/administrador';

let administradorModal, deleteModal;
let administradores = []; // Armazena os dados localmente
let idParaExcluir = null;

/**
 * Pega o token do localStorage. Se não encontrar, redireciona para o login.
 */
function getToken() {
    const token = localStorage.getItem('user_token');
    if (!token) {
        alert("Acesso não autorizado. Por favor, faça o login.");
        window.location.href = '/app/login';
        return null;
    }
    return token;
}

/**
 * Lida com erros de autenticação (ex: token expirado)
 */
function handleAuthError() {
    localStorage.removeItem('user_token'); // Limpa o token inválido
    alert("Sua sessão expirou. Por favor, faça o login novamente.");
    window.location.href = '/app/login';
}

async function carregarAdministradores() {
    // 👇 2. ADICIONADO: Lógica de Token
    const token = getToken();
    if (!token) return;

    try {
        const response = await fetch(API_URL, {
            method: 'GET',
            headers: {
                'Authorization': token
            }
        });

        if (!response.ok) {
            if (response.status === 401) return handleAuthError();
            throw new Error('Erro ao carregar administradores.');
        }

        administradores = await response.json(); // Salva localmente

        const tabela = document.getElementById('tabela-administradores');
        tabela.innerHTML = '';

        if (administradores.length === 0) {
            tabela.innerHTML = '<tr><td colspan="5" class="text-center">Nenhum administrador encontrado.</td></tr>';
            return;
        }

        administradores.forEach(admin => {
            const row = `
                <tr>
                    <td>${admin.id}</td>
                    <td>${admin.usuario ? admin.usuario.nome : 'N/A'}</td>
                    <td>${formatarData(admin.dtIni)}</td>
                    <td>${formatarData(admin.dtFim)}</td>
                    <td class="text-center btn-group">
                        <button class="btn btn-warning btn-sm" onclick="abrirModalEdicao(${admin.id})">
                            <i class="bi bi-pencil"></i> Editar
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="abrirModalDelete(${admin.id})">
                            <i class="bi bi-trash"></i> Excluir
                        </button>
                    </td>
                </tr>
            `;
            tabela.innerHTML += row;
        });
    } catch (error) {
        console.error(error);
        const tabela = document.getElementById('tabela-administradores');
        tabela.innerHTML = `<tr><td colspan="5" class="text-center text-danger">${error.message}</td></tr>`;
    }
}

async function salvarAdministrador(event) {
    event.preventDefault();

    // Lógica de Token
    const token = getToken();
    if (!token) return;

    document.getElementById('modal-error-message').classList.add('d-none');
    document.getElementById('dtFim-error').classList.add('d-none');

    const id = document.getElementById('administradorId').value;
    const usuarioId = document.getElementById('usuarioId').value;
    const dtFim = document.getElementById('dtFim').value || null;

    const ehUpdate = !!id;
    let url = API_URL;
    let method = ehUpdate ? 'PUT' : 'POST';
    let bodyJson = {};

    if (ehUpdate) {
        url = `${API_URL}/${id}`;
        method = 'PUT';

        if (dtFim) {
            const adminOriginal = administradores.find(a => a.id == id);
            const dtIni = adminOriginal.dtIni;

            if (dtFim <= dtIni) {
                const errorDiv = document.getElementById('dtFim-error');
                errorDiv.textContent = 'A data fim deve ser maior que a data de início.';
                errorDiv.classList.remove('d-none');
                return;
            }
        }

        // No seu backend (AdministradorService.update), você só atualiza o dtFim.
        // O usuarioId não é usado no PUT.
        bodyJson = {
            dtFim: dtFim
        };

    } else {
        if (!usuarioId) {
            const errorDiv = document.getElementById('modal-error-message');
            errorDiv.textContent = 'O ID do Usuário é obrigatório.';
            errorDiv.classList.remove('d-none');
            return;
        }
        bodyJson = {
            usuario: { id: parseInt(usuarioId) }
        };
    }

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token // Envia o token
            },
            body: JSON.stringify(bodyJson)
        });

        if (!response.ok) {
            if (response.status === 401) return handleAuthError();

            const erro = await response.json();

            // Tratamento de Erro
            const errorDiv = document.getElementById('modal-error-message');
            errorDiv.textContent = erro.erro || 'Falha ao salvar administrador.';
            errorDiv.classList.remove('d-none');
            return;
        }

        administradorModal.hide();
        carregarAdministradores();

    } catch (error) {
        console.error('Erro ao salvar:', error);
        const errorDiv = document.getElementById('modal-error-message');
        errorDiv.textContent = error.message;
        errorDiv.classList.remove('d-none');
    }
}

async function excluirAdministrador() {
    if (!idParaExcluir) return;

    // Lógica de Token
    const token = getToken();
    if (!token) return;

    try {
        const response = await fetch(`${API_URL}/${idParaExcluir}`, {
            method: 'DELETE',
            headers: {
                'Authorization': token // Envia o token
            }
        });

        if (!response.ok) {
            if (response.status === 401) return handleAuthError();

            const erro = await response.json();
            // Tratamento de Erro
            throw new Error(erro.erro || 'Falha ao excluir.');
        }

        deleteModal.hide();
        carregarAdministradores();
        idParaExcluir = null;

    } catch (error) {
        console.error('Erro ao excluir:', error);
        alert(`Erro: ${error.message}`);
    }
}

function abrirModalAdicionar() {
    document.getElementById('dtFim-error').classList.add('d-none');
    document.getElementById('form-administrador').reset();
    document.getElementById('administradorId').value = '';
    document.getElementById('usuarioId').removeAttribute('disabled');

    document.getElementById('administradorModalLabel').textContent = 'Adicionar Administrador';
    document.querySelector('#form-administrador').classList.remove('edit-mode');
    document.querySelector('.modal-footer .btn-primary').textContent = 'Salvar';

    administradorModal.show();
}

function abrirModalEdicao(id) {
    document.getElementById('dtFim-error').classList.add('d-none');
    const admin = administradores.find(a => a.id === id);
    if (!admin) return;

    document.getElementById('administradorId').value = admin.id;
    document.getElementById('usuarioId').value = admin.usuario.id;
    document.getElementById('dtFim').value = admin.dtFim || '';
    document.getElementById('usuarioId').setAttribute('disabled', true);

    document.getElementById('administradorModalLabel').textContent = 'Editar Administrador';
    document.querySelector('#form-administrador').classList.add('edit-mode');
    document.querySelector('.modal-footer .btn-primary').textContent = 'Atualizar';

    administradorModal.show();
}

function abrirModalDelete(id) {
    idParaExcluir = id;
    deleteModal.show();
}

function formatarData(data) {
    if (!data) {
        return '';
    }
    const [ano, mes, dia] = data.split('-');
    return `${dia}/${mes}/${ano}`;
}

window.addEventListener('DOMContentLoaded', () => {
    administradorModal = new bootstrap.Modal(document.getElementById('administradorModal'));
    deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));

    // Carrega os dados iniciais (agora com token)
    carregarAdministradores();

    const form = document.getElementById('form-administrador');
    const btnNovo = document.getElementById('btn-novo-administrador');
    const btnConfirmarExclusao = document.getElementById('confirmDelete');

    form.addEventListener('submit', salvarAdministrador);
    btnNovo.addEventListener('click', abrirModalAdicionar);
    btnConfirmarExclusao.addEventListener('click', excluirAdministrador);
});