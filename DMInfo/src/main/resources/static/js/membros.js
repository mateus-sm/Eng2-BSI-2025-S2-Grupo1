let membroModal, deleteModal;
let idParaExcluir = null;

const API_URL = '/apis/membro';

let listaMembrosOriginal = [];

async function carregarMembros() {
    try {
        const response = await fetch(API_URL);

        if (!response.ok)
            throw new Error('Falha ao carregar membros.');

        listaMembrosOriginal = await response.json();
        listaMembrosOriginal.sort((a, b) => a.id - b.id);

        aplicarFiltros();

    } catch (error) {
        console.error('Erro:', error);
    }
}

function aplicarFiltros() {
    const termo = document.getElementById('termoBusca').value.toLowerCase();
    const dtIniFiltro = document.getElementById('filtroDtIni').value;
    const dtFimFiltro = document.getElementById('filtroDtFim').value;

    const tabelaBody = document.getElementById('tabela-membros');
    tabelaBody.innerHTML = '';

    const membrosFiltrados = listaMembrosOriginal.filter(membro => {
        let matchNome = true;
        let matchData = true;

        if (termo) {
            const nomeUsuario = membro.usuario ? membro.usuario.nome.toLowerCase() : '';
            matchNome = nomeUsuario.includes(termo);
        }

        if (dtIniFiltro || dtFimFiltro) {
            const dataInicioMembro = membro.dtIni;
            if (dataInicioMembro) {
                if (dtIniFiltro && dataInicioMembro < dtIniFiltro)
                    matchData = false;
                if (dtFimFiltro && dataInicioMembro > dtFimFiltro)
                    matchData = false;
            }
        }

        return matchNome && matchData;
    });

    if (membrosFiltrados.length === 0) {
        tabelaBody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Nenhum registro encontrado com os filtros atuais.</td></tr>';
        return;
    }

    membrosFiltrados.forEach(membro => {
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
}

function abrirModalAdicionar() {
    limparErros();
    const form = document.getElementById('form-membro');
    form.reset();
    form.classList.remove('edit-mode');
    document.getElementById('membroId').value = '';
    document.getElementById('dtIniHidden').value = '';
    document.getElementById('membroModalLabel').innerText = 'Adicionar Membro';
    document.getElementById('usuarioId').disabled = false;
    membroModal.show();
}

async function abrirModalEditar(id) {
    limparErros();
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

        document.getElementById('dtIniHidden').value = membro.dtIni || '';

        membroModal.show();
    } catch (error) {
        console.error('Erro:', error);
        alert(error.message);
    }
}

async function salvarMembro(event) {
    event.preventDefault();
    limparErros();

    const id = document.getElementById('membroId').value;
    const usuarioIdInput = document.getElementById('usuarioId');
    const observacao = document.getElementById('observacao').value;
    const dtFim = document.getElementById('dtFim').value || null;

    const dtIniHidden = document.getElementById('dtIniHidden').value;

    if (!id && !usuarioIdInput.value) {
        mostrarErroCampo('usuarioId', 'O ID do Usuário é obrigatório.');
        return;
    }

    if (id && dtFim && dtIniHidden)
        if (dtFim < dtIniHidden) {
            mostrarErroCampo('dtFim', `A data fim não pode ser anterior ao início (${formatarData(dtIniHidden)}).`);
            return;
        }

    const ehUpdate = !!id;
    let url = API_URL;
    let method = 'POST';

    const membro = { observacao: observacao };

    if (ehUpdate) {
        url = `${API_URL}/${id}`;
        method = 'PUT';
        membro.dtFim = dtFim;
    }
    else
        membro.usuario = { id: parseInt(usuarioIdInput.value) };

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(membro)
        });

        if (!response.ok) {
            const erro = await response.json();
            throw new Error(erro.erro || erro.message || 'Falha ao salvar.');
        }

        membroModal.hide();
        carregarMembros();

    } catch (error) {
        console.error('Erro ao salvar:', error);
        const msg = error.message.toLowerCase();
        if (msg.includes('usuário') || msg.includes('id'))
            mostrarErroCampo('usuarioId', error.message);
        else
            mostrarErroGeral('msg-erro-modal', error.message);
    }
}

function abrirModalExcluir(id) {
    idParaExcluir = id;
    const divErro = document.getElementById('msg-erro-delete');
    divErro.classList.add('d-none');
    deleteModal.show();
}

async function excluirMembro() {
    if (!idParaExcluir) return;
    const divErro = document.getElementById('msg-erro-delete');
    divErro.classList.add('d-none');

    try {
        const response = await fetch(`${API_URL}/${idParaExcluir}`, { method: 'DELETE' });

        if (!response.ok) {
            const erro = await response.json();
            throw new Error(erro.erro || erro.message || 'Falha ao excluir.');
        }

        deleteModal.hide();
        carregarMembros();
        idParaExcluir = null;

    } catch (error) {
        divErro.innerText = error.message;
        divErro.classList.remove('d-none');
    }
}

function mostrarErroCampo(campoId, mensagem) {
    const input = document.getElementById(campoId);
    const feedback = document.getElementById('feedback-' + campoId);
    if (input)
        input.classList.add('is-invalid');
    if (feedback)
        feedback.innerText = mensagem;
}

function mostrarErroGeral(elementId, mensagem) {
    const div = document.getElementById(elementId);
    if (div) {
        div.innerText = mensagem;
        div.classList.remove('d-none');
    }
}

function limparErros() {
    document.querySelectorAll('.form-control').forEach(input => input.classList.remove('is-invalid'));
    const alertModal = document.getElementById('msg-erro-modal');
    if (alertModal)
        alertModal.classList.add('d-none');
    const alertDelete = document.getElementById('msg-erro-delete');
    if (alertDelete)
        alertDelete.classList.add('d-none');
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

    document.getElementById('termoBusca').addEventListener('keyup', aplicarFiltros);
    document.getElementById('filtroDtIni').addEventListener('change', aplicarFiltros);
    document.getElementById('filtroDtFim').addEventListener('change', aplicarFiltros);
});