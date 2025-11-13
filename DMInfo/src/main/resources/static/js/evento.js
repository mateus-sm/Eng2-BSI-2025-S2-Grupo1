const API_URL = 'http://localhost:8080/apis/eventos';
const API_ADMIN_URL = `${API_URL}/admin`;

function escapeHtml(text) {
    if (text === null || text === undefined) return '';
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.toString().replace(/[&<>"']/g, (m) => map[m]);
}

async function carregarEventos() {
    const searchDesc = document.getElementById('searchDesc').value;
    const sortField = document.getElementById('sortField').value;

    let url = new URL(API_ADMIN_URL);

    if (searchDesc) {
        url.searchParams.append('descricao', searchDesc);
    }
    if (sortField) {
        url.searchParams.append('ordenarPor', sortField);
    }

    // 💡 AJUSTE DE ROTA: Usando a rota /apis/eventos/admin
    const resp = await fetch(url);

    if (!resp.ok) {
        console.error("Falha ao carregar eventos:", resp.statusText);
        return;
    }

    const eventos = await resp.json();
    const tbody = document.getElementById('eventosTable');
    tbody.innerHTML = '';

    eventos.forEach(e => {
        const adminId = e.admin ? e.admin.id : 0;

        const tituloEscaped = escapeHtml(e.titulo).replace(/'/g, "\\'");
        const descricaoEscaped = escapeHtml(e.descricao).replace(/'/g, "\\'");

        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${e.id}</td>
            <td>${e.titulo}</td>
            <td>${e.descricao}</td>
            <td>${adminId}</td>
            <td>
                <button class="edit-btn" onclick="prepararEdicao(${e.id}, '${tituloEscaped}', '${descricaoEscaped}', ${adminId})">Editar</button>
                <button class="delete-btn" onclick="excluirEvento(${e.id})">Excluir</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function prepararEdicao(id, titulo, descricao, idAdmin) {
    document.getElementById('eventoId').value = id;
    document.getElementById('titulo').value = titulo;
    document.getElementById('descricao').value = descricao;
    document.getElementById('idAdmin').value = idAdmin;
    document.getElementById('submitBtn').textContent = 'Atualizar Evento';
    // Rola para o topo para facilitar a edição
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function resetForm() {
    document.getElementById('eventoForm').reset();
    document.getElementById('eventoId').value = ''; // Limpa o ID oculto
    document.getElementById('submitBtn').textContent = 'Criar Evento'; // Retorna para "Criar"
}


async function excluirEvento(id) {
    if (confirm('Tem certeza que deseja excluir este evento?')) {
        const resp = await fetch(`${API_URL}/${id}`, {
            method: 'DELETE',
            headers: {
                //     'Authorization': AUTH_TOKEN
            }
        });

        if (resp.status === 204 || resp.ok) {
            alert('Evento excluído com sucesso!');
        } else {
            const err = await resp.json().catch(() => ({ mensagem: 'Falha na exclusão.' }));
            alert('Erro ao excluir evento: ' + (err?.mensagem || 'Falha na exclusão.'));
            console.error("Erro ao excluir:", err);
        }

        carregarEventos();
    }
}

document.getElementById('eventoForm').addEventListener('submit', async (ev) => {
    ev.preventDefault();

    // Verifica se é Atualização (ID oculto preenchido)
    const id = document.getElementById('eventoId').value;
    const isUpdating = !!id;

    const titulo = document.getElementById('titulo').value;
    const descricao = document.getElementById('descricao').value;
    const idAdmin = parseInt(document.getElementById('idAdmin').value);

    const evento = {
        titulo,
        descricao,
        admin: { id: idAdmin }
    };

    const method = isUpdating ? 'PUT' : 'POST';
    const url = isUpdating ? `${API_URL}/${id}` : API_URL;

    const resp = await fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            // 'Authorization': AUTH_TOKEN
        },
        body: JSON.stringify(evento)
    });

    if (resp.ok) {
        alert(`Evento ${isUpdating ? 'atualizado' : 'criado'} com sucesso!`);
        resetForm();
        carregarEventos();
    } else {
        try {
            const err = await resp.json();
            alert(`Erro ao ${isUpdating ? 'atualizar' : 'criar'} evento: ` + (err?.mensagem || 'Verifique o console'));
            console.error("Erro detalhado:", err);
        } catch (e) {
            alert('Erro desconhecido. Status: ' + resp.status);
            console.error("Erro na resposta:", resp);
        }
    }
});

carregarEventos();