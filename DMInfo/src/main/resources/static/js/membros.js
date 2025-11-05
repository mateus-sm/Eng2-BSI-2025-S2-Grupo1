// para os modais do Bootstrap
let membroModal, deleteModal;
let idParaExcluir = null;

// URL base da sua API
const API_URL = '/app/membro';

console.log("Arquivo membros.js carregado com SUCESSO.");

async function carregarMembros() {
    try {
        const response = await fetch(API_URL);

        if (!response.ok)
            throw new Error('Falha ao carregar membros.');

        const membros = await response.json();
        const tabelaBody = document.getElementById('tabela-membros');
        tabelaBody.innerHTML = '';

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

function abrirModalAdicionar() {
    const form = document.getElementById('form-membro');
    form.reset();
    form.classList.remove('edit-mode'); // Remove a classe de edição
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

        // Preenche o formulário
        const form = document.getElementById('form-membro');
        form.classList.add('edit-mode');
        document.getElementById('membroModalLabel').innerText = 'Editar Membro';
        document.getElementById('membroId').value = membro.id;
        document.getElementById('codigo').value = membro.codigo;
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
    const codigo = document.getElementById('codigo').value;
    const usuarioId = document.getElementById('usuarioId').value;
    const observacao = document.getElementById('observacao').value;
    const dtFim = document.getElementById('dtFim').value || null; // Envia null se vazio

    if (parseInt(codigo) <= 0) {
        alert("Erro: O Código do Membro deve ser um número positivo.");
        return;
    }

    const ehUpdate = !!id;
    let url = API_URL;
    let method = 'POST';
    const membro = {
        codigo: parseInt(codigo),
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
    if (!data) return '';
    const [ano, mes, dia] = data.split('-');
    return `${dia}/${mes}/${ano}`;
}

document.addEventListener('DOMContentLoaded', () => {
    console.log("Arquivo membros.js DOM carregado.");

    membroModal = new bootstrap.Modal(document.getElementById('membroModal'));
    deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));

    carregarMembros();

    // Listeners dos botões principais
    document.getElementById('btn-novo-membro').addEventListener('click', abrirModalAdicionar);

    console.log("Adicionando listener ao botão 'btn-salvar-membro'");
    document.getElementById('btn-salvar-membro').addEventListener('click', salvarMembro);

    console.log("Adicionando listener ao botão 'btn-confirmar-exclusao'");
    document.getElementById('btn-confirmar-exclusao').addEventListener('click', excluirMembro);
});