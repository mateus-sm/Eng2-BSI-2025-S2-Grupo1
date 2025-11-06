const API_URL = "http://localhost:8080/apis/conquista";
const form = document.getElementById("formConquista");
const descricaoInput = document.getElementById("descricao");
const idInput = document.getElementById("idConquista");
const tabela = document.getElementById("tabelaConquistas");
const btnCancelar = document.getElementById("btnCancelar");
//const btnSalvar = document.getElementById("btnSalvar");

async function listarConquistas() {
    tabela.innerHTML = "";
    const resp = await fetch(API_URL);
    const conquistas = await resp.json();
    conquistas.forEach(c => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${c.id}</td>
          <td>${c.descricao}</td>
          <td class="text-center">
            <button class="btn btn-sm me-1" onclick="editarConquista(${c.id}, '${c.descricao}')">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="#ffc94c" class="bi bi-pencil-square" viewBox="0 0 16 16">
                <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
                <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"/>
              </svg>
            </button>
            <button class="btn btn-sm" onclick="excluirConquista(${c.id})">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="red" class="bi bi-trash3" viewBox="0 0 16 16">
                <path d="M6.5 1h3a.5.5 0 0 1 .5.5v1H6v-1a.5.5 0 0 1 .5-.5M11 2.5v-1A1.5 1.5 0 0 0 9.5 0h-3A1.5 1.5 0 0 0 5 1.5v1H1.5a.5.5 0 0 0 0 1h.538l.853 10.66A2 2 0 0 0 4.885 16h6.23a2 2 0 0 0 1.994-1.84l.853-10.66h.538a.5.5 0 0 0 0-1zm1.958 1-.846 10.58a1 1 0 0 1-.997.92h-6.23a1 1 0 0 1-.997-.92L3.042 3.5zm-7.487 1a.5.5 0 0 1 .528.47l.5 8.5a.5.5 0 0 1-.998.06L5 5.03a.5.5 0 0 1 .47-.53Zm5.058 0a.5.5 0 0 1 .47.53l-.5 8.5a.5.5 0 1 1-.998-.06l.5-8.5a.5.5 0 0 1 .528-.47M8 4.5a.5.5 0 0 1 .5.5v8.5a.5.5 0 0 1-1 0V5a.5.5 0 0 1 .5-.5"/>
              </svg>
            </button>
          </td>
        `;
        tabela.appendChild(tr);
    });
}

form.addEventListener("submit", async e => {
    e.preventDefault();
    const descricao = descricaoInput.value.trim();
    const id = idInput.value;

    const method = id ? "PUT" : "POST";
    const url = API_URL;

    const conquista = id ? { id: Number(id), descricao } : { descricao };

    await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(conquista)
    });

    form.reset();
    idInput.value = "";
    btnCancelar.classList.add("d-none");
    await listarConquistas();
});

async function excluirConquista(id) {
    if (!confirm("Deseja realmente excluir esta conquista?")) return;
    await fetch(`${API_URL}/${id}`, { method: "DELETE" });
    listarConquistas();
}

function editarConquista(id, descricao) {
    idInput.value = id;
    descricaoInput.value = descricao;
    btnCancelar.classList.remove("d-none");
}

btnCancelar.addEventListener("click", () => {
    form.reset();
    idInput.value = "";
    btnCancelar.classList.add("d-none");
});

listarConquistas();