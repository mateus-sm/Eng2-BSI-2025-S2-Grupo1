const API_URL = "/apis/lancarmembroativo";
let statusModal;
let listaMembrosOriginal = [];

document.addEventListener('DOMContentLoaded', () => {
    statusModal = new bootstrap.Modal(document.getElementById('statusModal'));

    carregarMembros();

    document.getElementById('termoBusca').addEventListener('keyup', aplicarFiltros);
    document.getElementById('filtroStatus').addEventListener('change', aplicarFiltros);
});

async function carregarMembros() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('Falha ao carregar membros.');

        listaMembrosOriginal = await response.json();
        console.log("Dados recebidos:", listaMembrosOriginal);

        aplicarFiltros();

    } catch (error) {
        console.error(error);
        alert("Erro ao conectar com o servidor.");
    }
}

function aplicarFiltros() {
    const termo = document.getElementById('termoBusca').value.toLowerCase();
    const filtroStatus = document.getElementById('filtroStatus').value;
    const hoje = new Date().toISOString().split('T')[0];

    const tabelaBody = document.getElementById('tabela-membros');
    tabelaBody.innerHTML = '';

    const membrosFiltrados = listaMembrosOriginal.filter(membro => {
        const nomeUsuario = (membro.usuario && membro.usuario.nome) ? membro.usuario.nome.toLowerCase() : '';

        if (!nomeUsuario.includes(termo)) return false;

        const isAtivo = verificarSeAtivo(membro.dtFim, hoje);

        if (filtroStatus === 'ativos' && !isAtivo) return false;
        if (filtroStatus === 'inativos' && isAtivo) return false;

        return true;
    });

    document.getElementById('total-registros').textContent = membrosFiltrados.length;

    if (membrosFiltrados.length === 0) {
        tabelaBody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">Nenhum membro encontrado.</td></tr>';
        return;
    }

    membrosFiltrados.forEach(membro => {
        const isAtivo = verificarSeAtivo(membro.dtFim, hoje);

        const badge = isAtivo
            ? `<span class="badge bg-success fs-6 px-3"><i class="bi bi-check-circle"></i> Ativo</span>`
            : `<span class="badge bg-secondary fs-6 px-3"><i class="bi bi-x-circle"></i> Inativo</span>`;

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${membro.id}</td>
            <td class="fw-bold">${membro.usuario ? (membro.usuario.nome || 'Sem Nome') : 'Usuário N/A'}</td>
            <td>${formatarData(membro.dtIni)}</td>
            <td class="${isAtivo ? 'text-muted' : 'text-danger'}">${formatarData(membro.dtFim) || '<em>Indefinido</em>'}</td>
            <td class="text-center">${badge}</td>
            <td class="text-center">
                <button class="btn btn-sm btn-outline-primary" onclick="abrirModalStatus(${membro.id})">
                    <i class="bi bi-calendar-event"></i> Alterar Data Fim
                </button>
            </td>
        `;
        tabelaBody.appendChild(tr);
    });
}

function verificarSeAtivo(dtFim, hoje) {
    if (!dtFim) return true;
    return dtFim > hoje;
}

function abrirModalStatus(id) {
    const membro = listaMembrosOriginal.find(m => m.id === id);
    if (!membro) return;

    document.getElementById('membroId').value = membro.id;
    document.getElementById('nomeMembroDisplay').value = membro.usuario ? membro.usuario.nome : 'N/A';
    document.getElementById('dtFim').value = membro.dtFim || '';
    document.getElementById('observacaoOriginal').value = membro.observacao || '';

    const erroDiv = document.getElementById('msg-erro-modal');
    if(erroDiv) erroDiv.classList.add('d-none');

    statusModal.show();
}

function definirData(acao) {
    const inputDtFim = document.getElementById('dtFim');
    if (acao === 'limpar') {
        inputDtFim.value = '';
    } else if (acao === 'hoje') {
        inputDtFim.value = new Date().toISOString().split('T')[0];
    }
}

async function salvarStatus() {
    const id = document.getElementById('membroId').value;
    const dtFim = document.getElementById('dtFim').value || null;
    const observacao = document.getElementById('observacaoOriginal').value;

    const payload = {
        observacao: observacao,
        dtFim: dtFim
    };

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const erro = await response.json();
            throw new Error(erro.erro || erro.message || 'Falha ao atualizar status.');
        }

        statusModal.hide();
        await carregarMembros();
        alert('Status atualizado com sucesso!');

    } catch (error) {
        alert(error.message); // Simplificado para alert se o modal der erro
    }
}

function formatarData(data) {
    if (!data) return null;
    const [ano, mes, dia] = data.split('-');
    return `${dia}/${mes}/${ano}`;
}