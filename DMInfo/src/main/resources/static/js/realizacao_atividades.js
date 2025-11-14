// ATUALIZAÇÕES: Adicionado GET por ID e Lógica de Filtros
const API_URL = 'http://localhost:8080/apis/realizacao-atividades';
//const AUTH_TOKEN = 'seu_token_aqui';

// Função auxiliar para formatar a data para o formato yyyy-MM-dd
function formatDate(date) {
    if (!date) return '';
    return new Date(date).toISOString().split('T')[0];
}

// ------------------------------------------
// LÓGICA DE LISTAGEM, PESQUISA E ORDENAÇÃO
// ------------------------------------------
async function carregarAtividades() {
    const searchDesc = document.getElementById('searchDesc').value;
    const sortField = document.getElementById('sortField').value;

    let url = new URL(API_URL);
    if (searchDesc) url.searchParams.append('descricao', searchDesc);
    if (sortField) url.searchParams.append('ordenarPor', sortField);

    const resp = await fetch(url);

    if (!resp.ok) {
        const err = await resp.json().catch(() => ({ mensagem: resp.statusText }));
        console.error("Falha ao carregar atividades:", err.mensagem);
        return;
    }

    const atividades = await resp.json();
    const tbody = document.getElementById('atividadesTable');
    tbody.innerHTML = '';

    atividades.forEach(a => {
        const adminNome = a.admin?.usuario?.login || `ID ${a.admin?.id || 'N/A'}`;
        const atividadeDesc = a.atv?.descricao || `ID ${a.atv?.id || 'N/A'}`;
        const statusText = a.status ? 'Finalizada' : 'Em Andamento';
        const statusClass = a.status ? 'status-finalizada' : 'status-andamento';

        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${a.id}</td>
            <td>${atividadeDesc}</td>
            <td>${adminNome}</td>
            <td>${a.local || 'N/A'}</td>
            <td>${formatDate(a.dtIni)} ${a.horario || ''}</td>
            <td>R$ ${a.custoprevisto.toFixed(2)}</td>
            <td>${a.custoreal > 0 ? `R$ ${a.custoreal.toFixed(2)}` : '-'}</td>
            <td class="${statusClass}">${statusText}</td>
            <td>
                <button class="edit-btn" onclick="prepararEdicao(${a.id})" ${a.status ? 'disabled' : ''}>Editar</button>
                ${!a.status ? `<button class="finalize-btn" onclick="document.getElementById('finalizarId').value = ${a.id}; document.getElementById('dtFim').focus();">Finalizar</button>` : ''}
                <button class="delete-btn" onclick="excluirAtividade(${a.id})">Excluir</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// ------------------------------------------
// LÓGICA CRUD (CRIAR/ATUALIZAR)
// ------------------------------------------

// NOVO: Busca e carrega dados no formulário para edição
async function prepararEdicao(id) {
    const resp = await fetch(`${API_URL}/${id}`);

    if (!resp.ok) {
        alert('Erro ao carregar dados para edição.');
        return;
    }

    const a = await resp.json();

    // Preenche o formulário
    document.getElementById('atividadeId').value = a.id;
    document.getElementById('idAtividade').value = a.atv.id;
    document.getElementById('idAdmin').value = a.admin.id;

    // Converte Time (HH:MM:SS) para input type="time" (HH:MM) se necessário
    document.getElementById('horario').value = a.horario ? a.horario.substring(0, 5) : '';

    document.getElementById('local').value = a.local;
    document.getElementById('dtIni').value = formatDate(a.dtIni);
    document.getElementById('custoPrevisto').value = a.custoprevisto;
    document.getElementById('observacoes').value = a.observacoes;

    document.getElementById('submitBtn').textContent = 'Atualizar Atividade';
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function resetForm() {
    document.getElementById('atividadeForm').reset();
    document.getElementById('atividadeId').value = '';
    document.getElementById('submitBtn').textContent = 'Criar Atividade';
}

async function excluirAtividade(id) {
    if (!confirm('Tem certeza que deseja excluir esta atividade?')) return;

    const resp = await fetch(`${API_URL}/${id}`, {
        method: 'DELETE',
        headers: {
            //     'Authorization': AUTH_TOKEN
        }
    });

    if (resp.status === 204 || resp.ok) {
        alert('Atividade excluída com sucesso!');
    } else {
        const err = await resp.json().catch(() => ({ mensagem: 'Falha na exclusão.' }));
        alert('Erro ao excluir atividade: ' + (err?.mensagem || 'Falha na exclusão.'));
    }
    carregarAtividades();
}

document.getElementById('atividadeForm').addEventListener('submit', async (ev) => {
    ev.preventDefault();

    const id = document.getElementById('atividadeId').value;
    const isUpdating = !!id;

    // Coleta dados do formulário
    const horaInput = document.getElementById('horario').value;
    let horarioFormatado = null;

    if (horaInput) {
        // Caso venha "HH:mm" (input padrão), virará "HH:mm:00"
        if (horaInput.length === 5) {
            horarioFormatado = horaInput + ":00";
        }
        // Caso venha "HH:mm:ss" (input com step=1), mantém
        else if (horaInput.length === 8) {
            horarioFormatado = horaInput;
        }
    }

    const atividade = {
        atv: { id: parseInt(document.getElementById('idAtividade').value) },
        admin: { id: parseInt(document.getElementById('idAdmin').value) },

        horario: horarioFormatado, // já normalizado

        local: document.getElementById('local').value,
        dtIni: document.getElementById('dtIni').value,
        custoprevisto: parseFloat(document.getElementById('custoPrevisto').value),
        observacoes: document.getElementById('observacoes').value,
    };


    const method = isUpdating ? 'PUT' : 'POST';
    const url = isUpdating ? `${API_URL}/${id}` : API_URL;

    const resp = await fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            // 'Authorization': AUTH_TOKEN
        },
        body: JSON.stringify(atividade)
    });

    if (resp.ok) {
        alert(`Atividade ${isUpdating ? 'atualizada' : 'criada'} com sucesso!`);
        resetForm();
        carregarAtividades();
    } else {
        const err = await resp.json().catch(() => ({ mensagem: 'Falha na comunicação.' }));
        alert(`Erro ao ${isUpdating ? 'atualizar' : 'criar'}: ` + (err?.mensagem || 'Verifique o console.'));
    }
});

// ------------------------------------------
// LÓGICA DE FINALIZAÇÃO (PUT /finalizar) - Mantida
// ------------------------------------------

document.getElementById('finalizarForm').addEventListener('submit', async (ev) => {
    ev.preventDefault();

    const id = document.getElementById('finalizarId').value;

    const dadosFinalizacao = {
        dtFim: document.getElementById('dtFim').value,
        custoreal: parseFloat(document.getElementById('custoReal').value),
        observacoes: document.getElementById('observacoesFim').value,
    };

    if (!id || id <= 0) {
        alert("Por favor, informe um ID válido para finalizar a atividade.");
        return;
    }
    if (!dadosFinalizacao.dtFim) {
        alert("A data fim é obrigatória.");
        return;
    }

    const resp = await fetch(`${API_URL}/${id}/finalizar`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            // 'Authorization': AUTH_TOKEN
        },
        body: JSON.stringify(dadosFinalizacao)
    });

    if (resp.ok) {
        alert('Atividade finalizada com sucesso!');
        ev.target.reset();
        carregarAtividades();
    } else {
        const err = await resp.json().catch(() => ({ mensagem: 'Falha na comunicação.' }));
        alert('Erro ao finalizar atividade: ' + (err?.mensagem || 'Verifique o console.'));
    }
});


// Inicializa o carregamento da lista
carregarAtividades();