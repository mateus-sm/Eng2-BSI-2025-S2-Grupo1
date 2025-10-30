// para os modais do Bootstrap
let administradorModal, deleteModal;
let idParaExcluir = null;

// URL base da sua API
const API_URL = '/administrador';

async function carregarAdministradores() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok)
            throw new Error('Falha ao carregar administrador.');

        const administradores = await response.json();
        const tabelaBody = document.getElementById('tabela-administradores');
        tabelaBody.innerHTML = ''; // Limpa a tabela

        if (administradores.length === 0) {
            tabelaBody.innerHTML = '<tr><td colspan="7" class="text-center">Nenhum administrador encontrado.</td></tr>';
            return;
        }

        administradores.forEach(administrador => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${administrador.id}</td>
                <td>${administrador.usuario ? administrador.usuario.nome : 'N/A'}</td>
                <td>${formatarData(administrador.dtIni)}</td>
                <td>${formatarData(administrador.dtFim)}</td>
                <td class="text-center">
                    <div class="btn-group" role="group">
                        <button class="btn btn-sm btn-outline-primary btn-editar" data-id="${administrador.id}">
                            <i class="bi bi-pencil-fill"></i> Editar
                        </button>
                        <button class="btn btn-sm btn-outline-danger btn-excluir" data-id="${administrador.id}">
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
        alert('Erro ao carregar administradores.');
    }
}

function abrirModalAdicionar() {
    const form = document.getElementById('form-administrador');
    form.reset();
    form.classList.remove('edit-mode'); // Remove a classe de edição
    document.getElementById('administradorId').value = '';
    document.getElementById('administradorModalLabel').innerText = 'Adicionar Administrador';

    // Habilita o campo 'usuarioId' que é desabilitado na edição
    document.getElementById('usuarioId').disabled = false;

    administradorModal.show();
}

async function abrirModalEditar(id) {
    try {
        const response = await fetch(`${API_URL}/get-by-id/${id}`);
        if (!response.ok) {
            throw new Error('Administrador não encontrado.');
        }
        const administrador = await response.json();

        // Preenche o formulário
        const form = document.getElementById('form-administrador');
        form.classList.add('edit-mode'); // Adiciona a classe de edição
        document.getElementById('administradorModalLabel').innerText = 'Editar Administrador';

        document.getElementById('administradorId').value = administrador.id;
        document.getElementById('codigo').value = administrador.codigo;
        document.getElementById('dtFim').value = administrador.dtFim || '';

        administradorModal.show();

    } catch (error) {
        console.error('Erro ao buscar Administrador:', error);
        alert(error.message);
    }
}

async function salvarAdministrador(event) {
    event.preventDefault();

    const id = document.getElementById('administradorId').value;
    const usuarioId = document.getElementById('usuarioId').value;
    const dtFim = document.getElementById('dtFim').value || null; // Envia null se vazio

    const ehUpdate = !!id; // Converte para booleano (true se 'id' não for vazio)

    let url = API_URL;
    let method = 'POST';
    let administradorJson = {}; // Objeto JSON que será enviado

    if (ehUpdate) {
        // --- UPDATE (PUT) ---
        url = `${API_URL}/${id}`;
        method = 'PUT';

        // O seu service.update espera o usuario e dtFim
        administradorJson = {
            usuario: { id: parseInt(usuarioId) }, // O ID do usuário não muda
            dtFim: dtFim
        };

    } else {
        // --- CREATE (POST) ---
        if (!usuarioId) {
            alert("Erro: O ID do Usuário é obrigatório.");
            return;
        }
        url = API_URL;
        method = 'POST';
        // O backend (Controller) espera um objeto 'usuario' aninhado com o 'id'
        administradorJson = {
            usuario: { id: parseInt(usuarioId) }
        };
    }

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(administradorJson) // Envia o JSON correto
        });

        if (!response.ok) {
            const erro = await response.json();
            // A sua classe Erro.java tem 'descricao'
            throw new Error(erro.descricao || 'Falha ao salvar administrador.');
        }

        // CORREÇÃO: Você também tinha deixado 'membroModal' aqui
        administradorModal.hide();
        carregarAdministradores(); // Recarrega a tabela

    } catch (error) {
        console.error('Erro ao salvar:', error);
        alert(`Erro: ${error.message}`);
    }
}

function abrirModalExcluir(id) {
    idParaExcluir = id; // Armazena o ID para o botão de confirmação
    deleteModal.show();
}

async function excluirAdministrador() {
    if (!idParaExcluir) return;

    try {
        const response = await fetch(`${API_URL}/${idParaExcluir}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const erro = await response.json();
            throw new Error(erro.mensagem || 'Falha ao excluir administrador.');
        }

        deleteModal.hide();
        carregarAdministradores(); // Recarrega a tabela
        idParaExcluir = null;

    } catch (error) {
        console.error('Erro ao excluir:', error);
        alert(`Erro: ${error.message}`);
    }
}

function formatarData(data) {
    if (!data) return '';
    const [ano, mes, dia] = data.split('-');
    return `${dia}/${mes}/${ano}`;
}

// --- INICIALIZAÇÃO ---
document.addEventListener('DOMContentLoaded', () => {
    // Instancia os modais do Bootstrap
    administradorModal = new bootstrap.Modal(document.getElementById('administradorModal'));
    deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));

    // Carrega os administradores na tabela
    carregarAdministradores();

    // Listeners dos botões principais
    document.getElementById('btn-novo-administrador').addEventListener('click', abrirModalAdicionar);
    document.getElementById('form-administrador').addEventListener('submit', salvarAdministrador);
    document.getElementById('btn-confirmar-exclusao').addEventListener('click', excluirAdministrador);
});