const API_ITENS = "http://localhost:8080/apis/distribuicao-itens"
const API_RECURSOS = "http://localhost:8080/apis/recurso"
const API_DISTRIBUICOES = "http://localhost:8080/apis/distribuicao-recursos"

// DOM Elements
const form = document.getElementById("formDistribuicao")
const selectRecurso = document.getElementById("selectRecurso")
const selectDistribuicao = document.getElementById("selectDistribuicao")
const quantidadeInput = document.getElementById("quantidade")
const idRecursoOriginal = document.getElementById("idRecursoOriginal")
const idDistribuicaoOriginal = document.getElementById("idDistribuicaoOriginal")
const tabela = document.getElementById("tabelaItens")
const alertaContainer = document.getElementById("alertaContainer")
const totalItens = document.getElementById("totalItens")
const mensagemVazia = document.getElementById("mensagemVazia")
const filtroDistribuicao = document.getElementById("filtroDistribuicao")
const filtroRecurso = document.getElementById("filtroRecurso")

// Estado global
let todosOsItens = []
let itensFiltrados = []
const recursos = {}
const distribuicoes = {}

function mostrarAlerta(mensagem, tipo = "success") {
    const alertId = `alerta-${Date.now()}`
    const alertHtml = `
        <div id="${alertId}" class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            ${mensagem}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `
    alertaContainer.insertAdjacentHTML("beforeend", alertHtml)
}

function validarCampo(campo) {
    if (campo.value.trim() !== "" && campo.checkValidity()) {
        campo.classList.remove("is-invalid")
        campo.classList.add("is-valid")
    } else if (campo.value.trim() === "" || !campo.checkValidity()) {
        campo.classList.remove("is-valid")
        campo.classList.add("is-invalid")
    }
}

// Validação em tempo real
selectRecurso.addEventListener("change", () => validarCampo(selectRecurso))
selectDistribuicao.addEventListener("change", () => validarCampo(selectDistribuicao))
quantidadeInput.addEventListener("input", () => validarCampo(quantidadeInput))

// Listeners de filtro
filtroDistribuicao.addEventListener("change", aplicarFiltros)
filtroRecurso.addEventListener("change", aplicarFiltros)

async function carregarDadosIniciais() {
    try {
        // Carregar recursos
        const respRecursos = await fetch(API_RECURSOS)
        if (!respRecursos.ok) throw new Error("Erro ao carregar recursos")
        const recursosData = await respRecursos.json()
        recursosData.forEach((r) => {
            recursos[r.id] = {
                descricao: r.descricao || "Sem descrição",
                tipo: r.tipo || "Sem tipo",
                quantidade: r.quantidade || 0
            }
            const option = document.createElement("option")
            option.value = r.id
            option.textContent = `${r.tipo || "Sem tipo"} - ${r.descricao || "Sem descrição"} (Qtd: ${r.quantidade || 0})`
            selectRecurso.appendChild(option)

            const optionFiltro = document.createElement("option")
            optionFiltro.value = r.id
            optionFiltro.textContent = `${r.tipo || "Sem tipo"} - ${r.descricao || "Sem descrição"}`
            filtroRecurso.appendChild(optionFiltro)
        })

        // Carregar distribuições
        const respDistribuicoes = await fetch(API_DISTRIBUICOES)
        if (!respDistribuicoes.ok) throw new Error("Erro ao carregar distribuições")
        const distribuicoesData = await respDistribuicoes.json()
        distribuicoesData.forEach((d) => {
            const dataFormatada = d.data ? new Date(d.data).toLocaleDateString("pt-BR") : "Sem data"
            distribuicoes[d.id] = {
                descricao: d.descricao || "Sem descrição",
                instituicao: d.instituicaoReceptora || "Sem instituição",
                data: d.data || null,
                valor: d.valor || 0
            }
            const option = document.createElement("option")
            option.value = d.id
            option.textContent = `${d.instituicaoReceptora || "Sem instituição"} - ${dataFormatada}`
            selectDistribuicao.appendChild(option)

            const optionFiltro = document.createElement("option")
            optionFiltro.value = d.id
            optionFiltro.textContent = `${d.instituicaoReceptora || "Sem instituição"} - ${dataFormatada}`
            filtroDistribuicao.appendChild(optionFiltro)
        })

        await listarItens()
    } catch (erro) {
        console.error(erro)
        mostrarAlerta("Erro ao carregar dados iniciais: " + erro.message, "danger")
    }
}

async function listarItens() {
    try {
        const resp = await fetch(API_ITENS)
        if (!resp.ok) throw new Error("Erro ao buscar itens de distribuição")

        todosOsItens = await resp.json()
        aplicarFiltros()
    } catch (erro) {
        console.error(erro)
        mostrarAlerta("Erro ao carregar itens: " + erro.message, "danger")
    }
}

function aplicarFiltros() {
    const filtroDistId = filtroDistribuicao.value
    const filtroRecId = filtroRecurso.value

    itensFiltrados = todosOsItens.filter((item) => {
        const matchDistribuicao = !filtroDistId || item.distribuicao == filtroDistId
        const matchRecurso = !filtroRecId || item.recurso == filtroRecId
        return matchDistribuicao && matchRecurso
    })

    renderizarTabela()
}

function limparFiltros() {
    filtroDistribuicao.value = ""
    filtroRecurso.value = ""
    aplicarFiltros()
}

function renderizarTabela() {
    tabela.innerHTML = ""
    totalItens.textContent = itensFiltrados.length

    if (itensFiltrados.length === 0) {
        mensagemVazia.classList.remove("d-none")
        return
    }

    mensagemVazia.classList.add("d-none")

    itensFiltrados.forEach((item) => {
        const tr = document.createElement("tr")
        const recurso = recursos[item.recurso] || { tipo: "Desconhecido", descricao: "Desconhecido" }
        const distribuicao = distribuicoes[item.distribuicao] || { instituicao: "Desconhecida", data: null }

        tr.innerHTML = `
          <td>${recurso.descricao}</td>
          <td>${distribuicao.instituicao}</td>
          <td><strong>${item.quantidade || 0}</strong></td>
          <td class="text-center">
            <button class="btn btn-sm" onclick="editarItem(${item.recurso}, ${item.distribuicao}, ${item.quantidade})" title="Editar">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="#ffc94c" class="bi bi-pencil-square" viewBox="0 0 16 16">
                <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
                <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"/>
              </svg>
            </button>
            <button class="btn btn-sm" onclick="excluirItem(${item.recurso}, ${item.distribuicao})" title="Excluir">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="red" class="bi bi-trash3" viewBox="0 0 16 16">
                <path d="M6.5 1h3a.5.5 0 0 1 .5.5v1H6v-1a.5.5 0 0 1 .5-.5M11 2.5v-1A1.5 1.5 0 0 0 9.5 0h-3A1.5 1.5 0 0 0 5 1.5v1H1.5a.5.5 0 0 0 0 1h.538l.853 10.66A2 2 0 0 0 4.885 16h6.23a2 2 0 0 0 1.994-1.84l.853-10.66h.538a.5.5 0 0 0 0-1zm1.958 1-.846 10.58a1 1 0 0 1-.997.92h-6.23a1 1 0 0 1-.997-.92L3.042 3.5zm-7.487 1a.5.5 0 0 1 .528.47l-.5 8.5a.5.5 0 0 1-.998.06L5 5.03a.5.5 0 0 1 .47-.53Zm5.058 0a.5.5 0 0 1 .47.53l-.5 8.5a.5.5 0 1 1-.998-.06l.5-8.5a.5.5 0 0 1 .528-.47M8 4.5a.5.5 0 0 1 .5.5v8.5a.5.5 0 0 1-1 0V5a.5.5 0 0 1 .5-.5"/>
              </svg>
            </button>
          </td>
        `
        tabela.appendChild(tr)
    })
}

form.addEventListener("submit", async (e) => {
    e.preventDefault()

    validarCampo(selectRecurso)
    validarCampo(selectDistribuicao)
    validarCampo(quantidadeInput)

    if (!form.checkValidity()) {
        e.stopPropagation()
        form.classList.add("was-validated")
        return
    }

    const idRecurso = Number(selectRecurso.value)
    const idDistribuicao = Number(selectDistribuicao.value)
    const quantidade = Number(quantidadeInput.value)
    const isEdicao = idRecursoOriginal.value && idDistribuicaoOriginal.value

    const item = {
        recurso: idRecurso,
        distribuicao: idDistribuicao,
        quantidade: quantidade,
    }

    try {
        let resp

        if (isEdicao) {
            const idRecursoAntigo = Number(idRecursoOriginal.value)
            const idDistribuicaoAntiga = Number(idDistribuicaoOriginal.value)

            if (idRecurso !== idRecursoAntigo || idDistribuicao !== idDistribuicaoAntiga) {
                await fetch(`${API_ITENS}?idRecurso=${idRecursoAntigo}&idDistribuicao=${idDistribuicaoAntiga}`, {
                    method: "DELETE",
                })

                resp = await fetch(API_ITENS, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(item),
                })
            } else {
                resp = await fetch(API_ITENS, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(item),
                })
            }
        } else {
            resp = await fetch(API_ITENS, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(item),
            })
        }

        if (!resp.ok) throw new Error("Erro ao salvar item")

        mostrarAlerta(isEdicao ? "Item atualizado com sucesso!" : "Item criado com sucesso!", "success")

        form.reset()
        form.classList.remove("was-validated")
        selectRecurso.classList.remove("is-valid", "is-invalid")
        selectDistribuicao.classList.remove("is-valid", "is-invalid")
        quantidadeInput.classList.remove("is-valid", "is-invalid")
        idRecursoOriginal.value = ""
        idDistribuicaoOriginal.value = ""
        await listarItens()
    } catch (erro) {
        mostrarAlerta("Erro: " + erro.message, "danger")
    }
})

async function excluirItem(idRecurso, idDistribuicao) {
    if (!confirm("Deseja realmente excluir este item de distribuição?")) return

    try {
        const resp = await fetch(`${API_ITENS}?idRecurso=${idRecurso}&idDistribuicao=${idDistribuicao}`, {
            method: "DELETE",
        })

        if (!resp.ok) throw new Error("Erro ao excluir item")

        mostrarAlerta("Item excluído com sucesso!", "success")
        listarItens()
    } catch (erro) {
        mostrarAlerta("Erro: " + erro.message, "danger")
    }
}

function editarItem(idRecurso, idDistribuicao, quantidade) {
    idRecursoOriginal.value = idRecurso
    idDistribuicaoOriginal.value = idDistribuicao
    selectRecurso.value = idRecurso
    selectDistribuicao.value = idDistribuicao
    quantidadeInput.value = quantidade

    validarCampo(selectRecurso)
    validarCampo(selectDistribuicao)
    validarCampo(quantidadeInput)

    form.classList.remove("was-validated")
    window.scrollTo({ top: 0, behavior: "smooth" })
}

carregarDadosIniciais()
