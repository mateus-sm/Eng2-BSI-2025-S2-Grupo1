const API_URL = '/apis/membro';
let statusModal;
let listaMembrosOriginal = [];

document.addEventListener('DOMContentLoaded', () => {
    statusModal = new bootstrap.Modal(document.getElementById('statusModal'));

    // Carrega dados iniciais
    carregarMembros();

    // Listeners de filtros
    document.getElementById('termoBusca').addEventListener('keyup', aplicarFiltros);
    document.getElementById('filtroStatus').addEventListener('change', aplicarFiltros);
});

async function carregarMembros() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('Falha ao carregar membros.');

        listaMembrosOriginal = await response.json();

        // Ordena por ID decrescente (mais novos primeiro)
        listaMembrosOriginal.sort((a, b) => b.id - a.id);

        aplicarFiltros();

    } catch (error) {
        console.error('Erro:', error);
        alert("Erro ao carregar dados do servidor.");
    }
}

function aplicarFiltros() {
    const termo = document.getElementById('termoBusca').value.toLowerCase();
    const filtroStatus = document.getElementById('filtroStatus').value; // todos, ativos, inativos

    const hoje = new Date().toISOString().split('T')[0]; // Data de hoje YYYY-MM-DD

    const tabelaBody = document.getElementById('tabela-membros');
    tabelaBody.innerHTML = '';

    // Filtra a lista
    const membrosFiltrados = listaMembrosOriginal.filter(membro => {
        // 1. Filtro de Texto (Nome)
        const nomeUsuario = membro.usuario ? membro.usuario.nome.toLowerCase() : '';
        if (!nomeUsuario.includes(termo)) return false;

        // 2. Filtro de Status
        const isAtivo = verificarSeAtivo(membro.dtFim, hoje);

        if (filtroStatus === 'ativos' && !isAtivo) return false;
        if (filtroStatus === 'inativos' && isAtivo) return false;

        return true;
    });

    // Atualiza contador
    document.getElementById('total-registros').textContent = membrosFiltrados.length;

    if (membrosFiltrados.length === 0) {
        tabelaBody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">Nenhum membro encontrado com estes filtros.</td></tr>';
        return;
    }

    // Renderiza
    membrosFiltrados.forEach(membro => {
        const isAtivo = verificarSeAtivo(membro.dtFim, hoje);

        const badge = isAtivo
            ? `<span class="badge bg-success"><i class="bi bi-check-circle"></i> Ativo</span>`
            : `<span class="badge bg-secondary"><i class="bi bi-x-circle"></i> Inativo</span>`;

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${membro.id}</td>
            <td class="fw-bold">${membro.usuario ? membro.usuario.nome : 'N/A'}</td>
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

// Lógica central: Ativo se DataFim for Nula OU DataFim >= Hoje
function verificarSeAtivo(dtFim, hoje) {
    if (!dtFim) return true; // Sem data fim = Ativo
    return dtFim >= hoje;    // Data fim no futuro ou hoje = Ativo
}

function abrirModalStatus(id) {
    const membro = listaMembrosOriginal.find(m => m.id === id);
    if (!membro) return;

    document.getElementById('membroId').value = membro.id;
    document.getElementById('nomeMembroDisplay').value = membro.usuario ? membro.usuario.nome : 'N/A';
    document.getElementById('dtFim').value = membro.dtFim || '';
    document.getElementById('observacaoOriginal').value = membro.observacao || ''; // Guarda a obs para não perder no update

    // Esconde erro anterior
    const erroDiv = document.getElementById('msg-erro-modal');
    erroDiv.classList.add('d-none');

    statusModal.show();
}

// Botões de atalho no modal
function definirData(acao) {
    const inputDtFim = document.getElementById('dtFim');
    if (acao === 'limpar') {
        inputDtFim.value = ''; // Torna ativo
    } else if (acao === 'hoje') {
        inputDtFim.value = new Date().toISOString().split('T')[0]; // Encerra hoje
    }
}

async function salvarStatus() {
    const id = document.getElementById('membroId').value;
    const dtFim = document.getElementById('dtFim').value || null;
    const observacao = document.getElementById('observacaoOriginal').value; // Mantém a observação antiga

    // Monta objeto para o PUT
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

        // Recarrega a lista para atualizar a tela
        await carregarMembros();

        alert('Status atualizado com sucesso!');

    } catch (error) {
        const erroDiv = document.getElementById('msg-erro-modal');
        erroDiv.innerText = error.message;
        erroDiv.classList.remove('d-none');
    }
}

function formatarData(data) {
    if (!data) return null;
    const [ano, mes, dia] = data.split('-');
    return `${dia}/${mes}/${ano}`;
}