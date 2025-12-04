function mostrarAlerta(tipo, mensagem) {
    const alertaContainer = document.getElementById('alertaContainer');
    alertaContainer.innerHTML = `
        <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            ${mensagem}
            <button class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;
    setTimeout(() => alertaContainer.innerHTML = "", 6000);
}

const API = "http://localhost:8080";

async function carregarMembros() {
    try {
        const resposta = await fetch(`${API}/apis/membro`);
        const membros = await resposta.json();
        console.log(membros);

        const select = document.getElementById("selectMembro");
        select.innerHTML = `<option value="">-- Selecione um membro --</option>`;

        membros.forEach(m => {
            select.innerHTML += `<option value="${m.id}">${m.usuario?.nome || "Sem nome"}</option>`;
        });

    } catch (e) {
        mostrarAlerta("danger", "Erro ao carregar membros.");
    }
}

function carregarMeses() {
    const meses = [
        "1 - Janeiro", "2 - Fevereiro", "3 - Março", "4 - Abril", "5 - Maio", "6 - Junho",
        "7 - Julho", "8 - Agosto", "9 - Setembro", "10 - Outubro", "11 - Novembro", "12 - Dezembro"
    ];

    const select = document.getElementById("selectMes");
    select.innerHTML = `<option value="">-- Mês --</option>`;

    meses.forEach((m, i) => {
        select.innerHTML += `<option value="${i + 1}">${m}</option>`;
    });
}

function carregarAnos() {
    const select = document.getElementById("selectAno");
    const anoAtual = new Date().getFullYear();

    select.innerHTML = `<option value="">-- Ano --</option>`;
    for (let a = anoAtual; a >= anoAtual - 10; a--) {
        select.innerHTML += `<option value="${a}">${a}</option>`;
    }
}

async function carregarMensalidades(filtro = "") {
    try {
        let url = `${API}/apis/mensalidade/listar`;
        if (filtro.trim() !== "") {
            url = `${API}/apis/mensalidade/buscar?nome=${encodeURIComponent(filtro)}`;
        }

        const resposta = await fetch(url);
        const lista = await resposta.json();

        const tabela = document.getElementById("tabelaMensalidades");
        const msgVazia = document.getElementById("mensagemVazia");
        const total = document.getElementById("totalMensalidades");

        tabela.innerHTML = "";

        if (!lista || lista.length === 0) {
            msgVazia.classList.remove("d-none");
            total.textContent = "0";
            return;
        }

        msgVazia.classList.add("d-none");

        lista.forEach(item => {
            tabela.innerHTML += `
                <tr>
                    <td class="text-center">${item.id_mensalidade}</td>
                    <td>${item.id_membro} - ${item.nome_membro || "Sem nome"}</td>
                    <td>${item.mes}</td>
                    <td>${item.ano}</td>
                    <td>R$ ${item.valor.toFixed(2)}</td>
                    <td>${item.dataPagamento || "--"}</td>
                    <td class="text-center">
                        <div class="btn-group" role="group">
                            <button class="btn btn-sm btn-outline-primary me-2 btn-editar" onclick="excluirMensalidade(${item.id_mensalidade})" >
                                <i class="bi bi-pencil-fill"></i> Editar
                            </button>
                            <button class="btn btn-sm btn-outline-danger btn-excluir" onclick="excluirMensalidade(${item.id_mensalidade})" >
                                <i class="bi bi-trash-fill"></i> Excluir
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        });

        document.querySelectorAll('.btn-editar').forEach(btn => {
            btn.addEventListener('click', (e) => abrirModalEditar(e.currentTarget.dataset.id));
        });
        document.querySelectorAll('.btn-excluir').forEach(btn => {
            btn.addEventListener('click', (e) => abrirModalExcluir(e.currentTarget.dataset.id));
        });

        total.textContent = lista.length;

    } catch (e) {
        mostrarAlerta("danger", "Erro ao carregar mensalidades.");
    }
}

async function carregarMensalidadesComFiltro() {
    const nome = document.getElementById("filtroNome").value.trim();
    const dataIni = document.getElementById("filtroDataInicial").value;
    const dataFim = document.getElementById("filtroDataFinal").value;

    let url = `${API}/apis/mensalidade/listar`;

    const params = [];

    if (nome !== "") params.push(`nome=${encodeURIComponent(nome)}`);
    if (dataIni !== "") params.push(`dataIni=${dataIni}`);
    if (dataFim !== "") params.push(`dataFim=${dataFim}`);

    if (params.length > 0)
        url = `${API}/apis/mensalidade/filtrar?${params.join("&")}`;

    try {
        const resposta = await fetch(url);
        const lista = await resposta.json();

        preencherTabela(lista);

    } catch (e) {
        mostrarAlerta("danger", "Erro ao filtrar mensalidades.");
    }
}

function preencherTabela(lista) {
    const tabela = document.getElementById("tabelaMensalidades");
    const msgVazia = document.getElementById("mensagemVazia");
    const total = document.getElementById("totalMensalidades");

    tabela.innerHTML = "";

    if (!lista || lista.length === 0) {
        msgVazia.classList.remove("d-none");
        total.textContent = "0";
        return;
    }

    msgVazia.classList.add("d-none");

    lista.forEach(item => {
        tabela.innerHTML += `
            <tr>
                <td class="text-center">${item.id_mensalidade}</td>
                <td>${item.id_membro} - ${item.nome_membro || "Sem nome"}</td>
                <td>${item.mes}</td>
                <td>${item.ano}</td>
                <td>R$ ${item.valor.toFixed(2)}</td>
                <td>${item.dataPagamento || "--"}</td>
                <td class="text-center">
                    <button class="btn btn-warning btn-sm me-1" onclick="editarMensalidade(${item.id_mensalidade})">Editar</button>
                    <button class="btn btn-danger btn-sm" onclick="excluirMensalidade(${item.id_mensalidade})">Excluir</button>
                </td>
            </tr>
        `;
    });

    total.textContent = lista.length;
}

function mostrarRegistro() {
    document.getElementById("areaRegistro").classList.remove("d-none");
}

function fecharRegistro() {
    document.getElementById("formMensalidade").reset();
    document.getElementById("areaRegistro").classList.add("d-none");
}

async function salvarMensalidade(event) {
    event.preventDefault();

    const id = document.getElementById("idMensalidade").value;
    const id_membro = document.getElementById("selectMembro").value;
    const mes = document.getElementById("selectMes").value;
    const ano = document.getElementById("selectAno").value;
    const valor = document.getElementById("valor").value;
    const dataPagamento = document.getElementById("dataPagamento").value;

    const dados = {
        id_mensalidade: id ? Number(id) : 0,
        id_membro: Number(id_membro),
        mes: Number(mes),
        ano: Number(ano),
        valor: Number(valor),
        dataPagamento
    };

    try {
        const res = await fetch(`${API}/apis/mensalidade/salvar`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dados)
        });

        if (!res.ok) {
            const erroMsg = await res.text();
            throw new Error(erroMsg);
        }

        mostrarAlerta("success", id ? "Alterado com sucesso!" : "Cadastrado com sucesso!");
        document.getElementById("formMensalidade").reset();
        carregarMensalidades();

    } catch (e) {
        mostrarAlerta("danger", e.message || "Erro ao salvar mensalidade.");
    }
}


async function editarMensalidade(id) {
    try {
        const res = await fetch(`${API}/apis/mensalidade/buscar/${id}`);
        const m = await res.json();

        document.getElementById("idMensalidade").value = m.id_mensalidade;
        document.getElementById("selectMembro").value = m.id_membro;
        document.getElementById("selectMes").value = m.mes;
        document.getElementById("selectAno").value = m.ano;
        document.getElementById("valor").value = m.valor;
        document.getElementById("dataPagamento").value = m.dataPagamento;

        mostrarAlerta("info", "Edição carregada!");

    } catch (e) {
        mostrarAlerta("danger", "Erro ao carregar mensalidade.");
    }
}

async function excluirMensalidade(id) {
    if (!confirm("Excluir registro?")) return;

    try {
        const res = await fetch(`${API}/apis/mensalidade/excluir/${id}`, { method: "DELETE" });

        if (!res.ok) {
            const erroMsg = await res.text();
            throw new Error(erroMsg);
        }

        mostrarAlerta("success", "Excluído com sucesso!");
        carregarMensalidades();

    } catch (e) {
        mostrarAlerta("danger", e.message || "Erro ao excluir registro.");
    }
}

window.onload = () => {
    carregarMembros();
    carregarMeses();
    carregarAnos();
    carregarMensalidades();
};
