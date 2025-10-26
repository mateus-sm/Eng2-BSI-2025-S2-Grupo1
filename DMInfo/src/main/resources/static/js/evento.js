const API_URL = 'http://localhost:8080/evento';

async function carregarEventos() {
    const resp = await fetch(`${API_URL}/all`);
    const eventos = await resp.json();
    const tbody = document.getElementById('eventosTable');
    tbody.innerHTML = '';

    eventos.forEach(e => {
        const row = document.createElement('tr');
        row.innerHTML = `
      <td>${e.id}</td>
      <td>${e.titulo}</td>
      <td>${e.descricao}</td>
      <td>${e.admId}</td>
      <td><button class="delete-btn" onclick="excluirEvento(${e.id})">Excluir</button></td>
    `;
        tbody.appendChild(row);
    });
}

async function excluirEvento(id) {
    if (confirm('Tem certeza que deseja excluir este evento?')) {
        await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        carregarEventos();
    }
}

document.getElementById('eventoForm').addEventListener('submit', async (ev) => {
    ev.preventDefault();
    const titulo = document.getElementById('titulo').value;
    const descricao = document.getElementById('descricao').value;
    const idAdmin = parseInt(document.getElementById('idAdmin').value);

    const evento = { titulo, descricao, adm: { id: idAdmin } };

    const resp = await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(evento)
    });

    if (resp.ok) {
        alert('Evento criado com sucesso!');
        ev.target.reset();
        carregarEventos();
    } else {
        const err = await resp.json();
        alert('Erro ao criar evento: ' + (err?.mensagem || 'Verifique o console'));
        console.error(err);
    }
});

carregarEventos();
