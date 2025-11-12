let membroModal, deleteModal;
let idParaExcluir = null;

const API_URL = '/apis/membro';

async function sincronizarAgenda() {
    const response = await fetch('/api/calendar/sync', { method: 'POST' });
    const result = await response.text();

    if (result.includes("Erro: A autorização do Google não foi concluída")) {
        alert("Autorização do Google Calendar é necessária. Redirecionando para o login do Google...");
        window.location.href = '/api/calendar/oauth/start';
    } else {
        alert(result);
    }
}

async function carregarMembros() {
    try {
        const termoBusca = document.getElementById('termoBusca').value;
        let url = API_URL;

        if (termoBusca && termoBusca.trim() !== '')
            url = `${API_URL}?filtro=${encodeURIComponent(termoBusca)}`;

        const response = await fetch(url);

        if (!response.ok)
            throw new Error('Falha ao carregar membros.');

        const membros = await response.json();
        const tabelaBody = document.getElementById('tabela-membros');
        tabelaBody.innerHTML = '';

        if (membros.length === 0) {
            tabelaBody.innerHTML = '<tr><td colspan="6" class="text-center">Nenhum membro encontrado.</td></tr>';
            return;
        }

        membros.forEach(membro => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${membro.id}</td>
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

        document.querySelectorAll('.btn-editar').forEach(btn => {
            btn.addEventListener('click', (e) => abrirModalEditar(e.currentTarget.dataset.id));
        });
        document.querySelectorAll('.btn-excluir').forEach(btn => {
            btn.addEventListener('click', (e) => abrirModalExcluir(e.currentTarget.dataset.id));
        });

    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao carregar membros. Verifique o console para mais detalhes.');
    }
}

function abrirModalAdicionar() {
    const form = document.getElementById('form-membro');
    form.reset();
    form.classList.remove('edit-mode');
    document.getElementById('membroId').value = '';
    document.getElementById('membroModalLabel').innerText = 'Adicionar Membro';
    document.getElementById('usuarioId').disabled = false;
    membroModal.show();
}

async function abrirModalEditar(id) {
    try {
        const response = await fetch(`${API_URL}/get-by-id/${id}`);

        if (!response.ok)
            throw new Error('Membro não encontrado.');

        const membro = await response.json();

        const form = document.getElementById('form-membro');
        form.classList.add('edit-mode');
        document.getElementById('membroModalLabel').innerText = 'Editar Membro';
        document.getElementById('membroId').value = membro.id;
        document.getElementById('usuarioId').value = membro.usuario.id;
        document.getElementById('usuarioId').disabled = true;
        document.getElementById('observacao').value = membro.observacao || '';
        document.getElementById('dtFim').value = membro.dtFim || '';

        membroModal.show();

    } catch (error) {
        console.error('Erro ao buscar membro:', error);
        alert(error.message);
    }
}

async function salvarMembro(event) {
    event.preventDefault();

    const id = document.getElementById('membroId').value;
    const usuarioId = document.getElementById('usuarioId').value;
    const observacao = document.getElementById('observacao').value;
    const dtFim = document.getElementById('dtFim').value || null;

    const ehUpdate = !!id;
    let url = API_URL;
    let method = 'POST';

    const membro = {
        observacao: observacao
    };

    if (ehUpdate) {
        url = `${API_URL}/${id}`;
        method = 'PUT';
        membro.dtFim = dtFim;
    }
    else
        membro.usuario = { id: parseInt(usuarioId) };

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(membro)
        });

        if (!response.ok) {
            const erro = await response.json();
            const mensagemEspecifica = erro.erro || erro.mensagem || erro.detalhe || erro.message;
            throw new Error(mensagemEspecifica || 'Falha ao salvar membro.');
        }

        membroModal.hide();
        carregarMembros();

    } catch (error) {
        console.error('Erro ao salvar:', error);
        alert(`Erro: ${error.message}`);
    }
}

function abrirModalExcluir(id) {
    idParaExcluir = id;
    deleteModal.show();
}

async function excluirMembro() {
    if (!idParaExcluir)
        return;

    try {
        const response = await fetch(`${API_URL}/${idParaExcluir}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const erro = await response.json();
            throw new Error(erro.erro || 'Falha ao excluir membro.');
        }

        deleteModal.hide();
        carregarMembros();
        idParaExcluir = null;

    } catch (error) {
        console.error('Erro ao excluir:', error);
        alert(`Erro: ${error.message}`);
    }
}

function formatarData(data) {
    if (!data)
        return '';
    const [ano, mes, dia] = data.split('-');
    return `${dia}/${mes}/${ano}`;
}

document.addEventListener('DOMContentLoaded', () => {

    membroModal = new bootstrap.Modal(document.getElementById('membroModal'));
    deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));

    carregarMembros();

    document.getElementById('btn-novo-membro').addEventListener('click', abrirModalAdicionar);
    document.getElementById('btn-salvar-membro').addEventListener('click', salvarMembro);
    document.getElementById('btn-confirmar-exclusao').addEventListener('click', excluirMembro);

    document.getElementById('termoBusca').addEventListener('keyup', carregarMembros);
    document.getElementById('btn-sync-calendar').addEventListener('click', sincronizarAgenda);
});