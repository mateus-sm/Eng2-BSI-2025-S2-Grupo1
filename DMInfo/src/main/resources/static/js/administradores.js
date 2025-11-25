const API_URL = '/apis/administrador';

let administradorModal, deleteModal;
let administradores = [];
let idParaExcluir = null;

function getToken() {
    const token = localStorage.getItem('user_token');
    if (!token) {

    }
    return token;
}

function handleAuthError() {
    alert("Sessão expirada ou não autorizada.");
}

async function carregarAdministradores() {


    try {
        const response = await fetch(API_URL, {
            method: 'GET',
        });

        if (!response.ok) {
            if (response.status === 401) return handleAuthError();
            throw new Error('Erro ao carregar administradores.');
        }

        administradores = await response.json();

        const tabela = document.getElementById('tabela-administradores');
        tabela.innerHTML = '';

        if (administradores.length === 0) {
            tabela.innerHTML = '<tr><td colspan="5" class="text-center">Nenhum administrador encontrado.</td></tr>';
            return;
        }

        administradores.forEach(admin => {
            const nomeUsuario = admin.usuario ? admin.usuario.nome : 'Usuário ID ' + admin.usuario.id;

            const row = `
                <tr>
                    <td>${admin.id}</td>
                    <td>${nomeUsuario || 'N/A'}</td>
                    <td>${formatarData(admin.dtIni)}</td>
                    <td>${formatarData(admin.dtFim)}</td>
                    <td class="text-center btn-group">
                        <button class="btn btn-warning btn-sm" onclick="abrirModalEdicao(${admin.id})">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="abrirModalDelete(${admin.id})">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>
            `;
            tabela.innerHTML += row;
        });
    } catch (error) {
        console.error(error);
    }
}


async function salvarAdministrador(event) {
    event.preventDefault();


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
        bodyJson = { dtFim: dtFim };
    } else {
        if (!usuarioId) {
            mostrarErroModal('O ID do Usuário é obrigatório.');
            return;
        }
        bodyJson = { usuario: { id: parseInt(usuarioId) } };
    }

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(bodyJson)
        });

        if (!response.ok) {
            const erro = await response.json();
            mostrarErroModal(erro.mensagem || erro.erro || 'Erro ao salvar.');
            return;
        }

        administradorModal.hide();
        carregarAdministradores();
        alert(ehUpdate ? "Atualizado com sucesso!" : "Cadastrado com sucesso!");

    } catch (error) {
        console.error('Erro ao salvar:', error);
        mostrarErroModal('Erro de conexão ou servidor.');
    }
}

function mostrarErroModal(msg) {
    const errorDiv = document.getElementById('modal-error-message');
    errorDiv.textContent = msg;
    errorDiv.classList.remove('d-none');
}

async function excluirAdministrador() {
    if (!idParaExcluir) return;

    try {
        const response = await fetch(`${API_URL}/${idParaExcluir}`, {
            method: 'DELETE',
        });

        if (!response.ok) throw new Error('Falha ao excluir.');

        deleteModal.hide();
        carregarAdministradores();
        idParaExcluir = null;
        alert("Excluído com sucesso!");

    } catch (error) {
        alert(`Erro: ${error.message}`);
    }
}

function abrirModalAdicionar() {
    document.getElementById('dtFim-error').classList.add('d-none');
    document.getElementById('modal-error-message').classList.add('d-none');
    document.getElementById('form-administrador').reset();
    document.getElementById('administradorId').value = '';

    document.getElementById('usuarioId').removeAttribute('disabled');
    document.getElementById('campo-dtFim').classList.add('d-none');

    document.getElementById('administradorModalLabel').textContent = 'Adicionar Administrador';
    administradorModal.show();
}

function abrirModalEdicao(id) {
    document.getElementById('dtFim-error').classList.add('d-none');
    document.getElementById('modal-error-message').classList.add('d-none');

    const admin = administradores.find(a => a.id === id);
    if (!admin) return;

    document.getElementById('administradorId').value = admin.id;
    document.getElementById('usuarioId').value = admin.usuario ? admin.usuario.id : '';
    document.getElementById('dtFim').value = admin.dtFim || '';

    document.getElementById('usuarioId').setAttribute('disabled', true);
    document.getElementById('campo-dtFim').classList.remove('d-none');

    document.getElementById('administradorModalLabel').textContent = 'Editar Administrador';
    administradorModal.show();
}

function abrirModalDelete(id) {
    idParaExcluir = id;
    deleteModal.show();
}

function formatarData(data) {
    if (!data) return '-';
    const [ano, mes, dia] = data.split('-');
    return `${dia}/${mes}/${ano}`;
}

window.addEventListener('DOMContentLoaded', () => {
    administradorModal = new bootstrap.Modal(document.getElementById('administradorModal'));
    deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));

    carregarAdministradores();

    document.getElementById('btn-novo-administrador').addEventListener('click', abrirModalAdicionar);
    document.getElementById('confirmDelete').addEventListener('click', excluirAdministrador);
});