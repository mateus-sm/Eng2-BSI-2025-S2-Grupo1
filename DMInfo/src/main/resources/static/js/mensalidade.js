const API = "/apis/mensalidade";

let membrosCache = [];
let listaMensalidadesGlobal = [];

document.addEventListener("DOMContentLoaded", () => {
    carregarMembros();
    carregarMensalidades();

    const inputFiltro = document.getElementById("filtroNome");
    if(inputFiltro) {
        inputFiltro.addEventListener("keyup", filtrarTabela);
    }

    const inputDtIni = document.getElementById("filtroDataInicial");
    const inputDtFim = document.getElementById("filtroDataFinal");
    if(inputDtIni) inputDtIni.addEventListener("change", filtrarTabela);
    if(inputDtFim) inputDtFim.addEventListener("change", filtrarTabela);
});

async function carregarMensalidades() {
    try {
        const resposta = await fetch(`${API}/listar`);

        if (!resposta.ok) {
            const erroMsg = await resposta.text();
            throw new Error(erroMsg || "Erro ao carregar dados.");
        }

        listaMensalidadesGlobal = await resposta.json();

        filtrarTabela();

    } catch (e) {
        mostrarAlerta("danger", e.message);
    }
}

function filtrarTabela() {
    const termo = document.getElementById("filtroNome").value.toLowerCase();
    const dtIni = document.getElementById("filtroDataInicial").value;
    const dtFim = document.getElementById("filtroDataFinal").value;

    const tabela = document.getElementById("tabelaMensalidades");
    const msgVazia = document.getElementById("mensagemVazia");
    const total = document.getElementById("totalMensalidades");

    tabela.innerHTML = "";

    const filtrados = listaMensalidadesGlobal.filter(m => {
        const nomeMembro = (m.nome_membro || "").toLowerCase();
        if (!nomeMembro.includes(termo)) return false;

        if (dtIni && m.dataPagamento < dtIni) return false;
        if (dtFim && m.dataPagamento > dtFim) return false;

        return true;
    });

    if (total) total.textContent = filtrados.length;

    if (filtrados.length === 0) {
        if(msgVazia) msgVazia.classList.remove("d-none");
        return;
    }

    if(msgVazia) msgVazia.classList.add("d-none");

    filtrados.forEach(item => {
        tabela.innerHTML += `
            <tr>
                <td class="text-center">${item.id_mensalidade}</td>
                <td>${item.id_membro} - ${item.nome_membro || "Sem nome"}</td>
                <td>${item.mes}</td>
                <td>${item.ano}</td>
                <td>R$ ${item.valor ? item.valor.toFixed(2) : "0.00"}</td>
                <td>${formatarData(item.dataPagamento)}</td>
                <td class="text-center">
                    <button class="btn btn-warning btn-sm me-1" onclick="editarMensalidade(${item.id_mensalidade})">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-danger btn-sm" onclick="excluirMensalidade(${item.id_mensalidade})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    });
}

function carregarMensalidadesComFiltro() {
    filtrarTabela();
}


async function carregarMembros() {
    try {
        const resposta = await fetch(`/apis/membro`); // Ajuste a rota se necessário
        if (!resposta.ok) throw new Error();
        membrosCache = await resposta.json();
        configurarAutocomplete();
    } catch (e) {
        console.error("Erro ao carregar membros para autocomplete");
    }
}

function configurarAutocomplete() {
    const inputBusca = document.getElementById("buscaMembro");
    const inputId = document.getElementById("selectMembro");
    const listaDiv = document.getElementById("listaSugestoes");

    if(!inputBusca || !listaDiv) return;

    const renderizar = (filtro = "") => {
        listaDiv.innerHTML = "";
        const termo = filtro.toLowerCase();
        const filtrados = membrosCache.filter(m =>
            (m.usuario?.nome || "").toLowerCase().includes(termo)
        );

        if (filtrados.length === 0) {
            listaDiv.innerHTML = `<div class="list-group-item text-muted">Nenhum membro encontrado.</div>`;
        } else {
            filtrados.forEach(m => {
                const item = document.createElement("button");
                item.type = "button";
                item.className = "list-group-item list-group-item-action";
                item.textContent = `${m.usuario?.nome || "Sem nome"} (ID: ${m.id})`;
                item.onclick = () => {
                    inputBusca.value = m.usuario?.nome;
                    inputId.value = m.id;
                    listaDiv.style.display = "none";
                };
                listaDiv.appendChild(item);
            });
        }
        listaDiv.style.display = "block";
    };

    inputBusca.addEventListener("input", (e) => {
        inputId.value = "";
        renderizar(e.target.value);
    });

    inputBusca.addEventListener("focus", () => renderizar(inputBusca.value));

    document.addEventListener("click", (e) => {
        if (!inputBusca.contains(e.target) && !listaDiv.contains(e.target)) {
            listaDiv.style.display = "none";
        }
    });
}

function mostrarRegistro() {
    document.getElementById("areaRegistro").classList.remove("d-none");
}

function fecharRegistro() {
    document.getElementById("formMensalidade").reset();
    document.getElementById("areaRegistro").classList.add("d-none");
    const busca = document.getElementById("buscaMembro");
    if(busca) busca.value = "";
}

async function salvarMensalidade(event) {
    event.preventDefault();

    const id = document.getElementById("idMensalidade").value;
    const id_membro = document.getElementById("selectMembro").value;
    const mes = document.getElementById("selectMes").value;
    const ano = document.getElementById("selectAno").value;
    const valor = document.getElementById("valor").value;
    const dataPagamento = document.getElementById("dataPagamento").value;

    if (!validarDataReal(dataPagamento)) {
        mostrarAlerta("danger", "Data inválida! Verifique o calendário.");
        return;
    }

    const dados = {
        id_mensalidade: id ? Number(id) : 0,
        id_membro: Number(id_membro),
        mes: Number(mes),
        ano: Number(ano),
        valor: Number(valor),
        dataPagamento
    };

    try {
        const res = await fetch(`${API}/salvar`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dados)
        });

        if (!res.ok) {
            const erroMsg = await res.text();
            throw new Error(erroMsg);
        }

        mostrarAlerta("success", id ? "Alterado com sucesso!" : "Cadastrado com sucesso!");
        fecharRegistro();
        carregarMensalidades();

    } catch (e) {
        mostrarAlerta("danger", e.message || "Erro ao salvar.");
    }
}

async function editarMensalidade(id) {
    try {
        const res = await fetch(`${API}/buscar/${id}`);

        if(!res.ok) throw new Error("Erro ao buscar dados");

        const m = await res.json();

        mostrarRegistro();

        document.getElementById("idMensalidade").value = m.id_mensalidade;

        document.getElementById("selectMembro").value = m.id_membro;
        document.getElementById("buscaMembro").value = m.nome_membro || `ID ${m.id_membro}`;

        document.getElementById("selectMes").value = m.mes;
        document.getElementById("selectAno").value = m.ano;
        document.getElementById("valor").value = m.valor;
        document.getElementById("dataPagamento").value = m.dataPagamento;

    } catch (e) {
        mostrarAlerta("danger", "Erro ao carregar edição.");
    }
}

async function excluirMensalidade(id) {
    if (!confirm("Excluir registro?")) return;

    try {
        const res = await fetch(`${API}/excluir/${id}`, { method: "DELETE" });
        if (!res.ok) {
            const txt = await res.text();
            throw new Error(txt);
        }

        mostrarAlerta("success", "Excluído com sucesso!");
        carregarMensalidades();

    } catch (e) {
        mostrarAlerta("danger", e.message || "Erro ao excluir.");
    }
}

function formatarData(dt) {
    if (!dt) return "-";
    const [ano, mes, dia] = dt.split("-");
    return `${dia}/${mes}/${ano}`;
}

function validarDataReal(dataString) {
    if (!dataString) return false;
    const partes = dataString.split("-");
    if (partes.length !== 3) return false;
    const ano = parseInt(partes[0]);
    const mes = parseInt(partes[1]) - 1;
    const dia = parseInt(partes[2]);
    const dataObj = new Date(ano, mes, dia);
    if (dataObj.getFullYear() !== ano || dataObj.getMonth() !== mes || dataObj.getDate() !== dia) return false;
    return true;
}

function mostrarAlerta(tipo, mensagem) {
    const alertaContainer = document.getElementById('alertaContainer');
    if(alertaContainer) {
        alertaContainer.innerHTML = `
            <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
                ${mensagem}
                <button class="btn-close" data-bs-dismiss="alert"></button>
            </div>`;
        setTimeout(() => alertaContainer.innerHTML = "", 6000);
    } else {
        alert(mensagem);
    }
}

function carregarMeses() {
    const meses = [
        "1 - Janeiro", "2 - Fevereiro", "3 - Março", "4 - Abril", "5 - Maio", "6 - Junho",
        "7 - Julho", "8 - Agosto", "9 - Setembro", "10 - Outubro", "11 - Novembro", "12 - Dezembro"
    ];
    const select = document.getElementById("selectMes");
    if(select) {
        select.innerHTML = `<option value="">-- Mês --</option>`;
        meses.forEach((m, i) => {
            select.innerHTML += `<option value="${i + 1}">${m}</option>`;
        });
    }
}

function carregarAnos() {
    const select = document.getElementById("selectAno");
    if(select) {
        const anoAtual = new Date().getFullYear();
        select.innerHTML = `<option value="">-- Ano --</option>`;
        for (let a = anoAtual; a >= anoAtual - 10; a--) {
            select.innerHTML += `<option value="${a}">${a}</option>`;
        }
    }
}

document.addEventListener("DOMContentLoaded", () => {
    carregarMeses();
    carregarAnos();
});