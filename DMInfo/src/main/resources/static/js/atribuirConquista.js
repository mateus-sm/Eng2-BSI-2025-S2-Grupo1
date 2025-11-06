const API_ATRIBUIR = "http://localhost:8080/apis/atribuirconquistamembro"
const API_MEMBROS = "http://localhost:8080/apis/membro"
const API_CONQUISTAS = "http://localhost:8080/apis/conquista"
const API_USUARIOS = "http://localhost:8080/apis/usuario"
const API_ADMINISTRADOR = "http://localhost:8080/apis/administrador"

// DOM Elements
const form = document.getElementById("formAtribuicao")
const selectMembro = document.getElementById("selectMembro")
const selectConquista = document.getElementById("selectConquista")
const selectAdministrador = document.getElementById("selectAdministrador")
const dataAtribuicaoInput = document.getElementById("dataAtribuicao")
const observacaoInput = document.getElementById("observacao")
const idAtribuicaoInput = document.getElementById("idAtribuicao")
const tabela = document.getElementById("tabelaAtribuicoes")
const alertaContainer = document.getElementById("alertaContainer")
const totalAtribuicoes = document.getElementById("totalAtribuicoes")
const mensagemVazia = document.getElementById("mensagemVazia")

// Estado global
let todasAsAtribuicoes = []
const membros = {}
const conquistas = {}
const administradores = {}

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

selectMembro.addEventListener("change", () => validarCampo(selectMembro))
selectConquista.addEventListener("change", () => validarCampo(selectConquista))
selectAdministrador.addEventListener("change", () => validarCampo(selectAdministrador))
dataAtribuicaoInput.addEventListener("change", () => validarCampo(dataAtribuicaoInput))
observacaoInput.addEventListener("input", () => {
    if (observacaoInput.value.trim() !== "") {
        observacaoInput.classList.add("is-valid")
    } else {
        observacaoInput.classList.remove("is-valid")
    }
})

async function carregarDadosIniciais() {
    try {
        //Resolve membro, pra mostrar o nome tem que buscar em usuario
        const respMembros = await fetch(API_MEMBROS)
        if (!respMembros.ok) throw new Error("Erro ao carregar membros")
        const membrosData = await respMembros.json()

        for (const m of membrosData) {
            const respUsuario = await fetch(`${API_USUARIOS}/${m.id}`)
            if (!respUsuario.ok) throw new Error(`Erro ao carregar usuário ${m.id}`)
            const usuarioData = await respUsuario.json()

            const nome = usuarioData.nome || `Membro ${m.id}`
            membros[m.id] = nome

            const option = document.createElement("option")
            option.value = m.id
            option.textContent = nome
            selectMembro.appendChild(option)
        }

        //Resolve Conquista
        const respConquistas = await fetch(API_CONQUISTAS)
        if (!respConquistas.ok) throw new Error("Erro ao carregar conquistas")
        const conquistasData = await respConquistas.json()
        conquistasData.forEach((c) => {
            conquistas[c.id_conquista] = c.descricao
            const option = document.createElement("option")
            option.value = c.id_conquista
            option.textContent = c.descricao
            selectConquista.appendChild(option)
        })

        //Reolve adm, pra pegar o nome tem que buscar em usuario
        const respAdmin = await fetch(API_ADMINISTRADOR)
        if (!respAdmin.ok) throw new Error("Erro ao carregar administradores")
        const admData = await respAdmin.json()

        for (const a of admData) {
            const respUsuario = await fetch(`${API_USUARIOS}/${a.id}`)
            if (!respUsuario.ok) throw new Error(`Erro ao carregar usuário ${a.id_usuario}`)
            const usuarioData = await respUsuario.json()

            const nome = usuarioData.nome || `Admin ${a.id_admin}`
            administradores[a.id_admin] = nome

            const option = document.createElement("option")
            option.value = a.id_admin
            option.textContent = nome
            selectAdministrador.appendChild(option)
        }

        await listarAtribuicoes()
    } catch (erro) {
        console.error(erro)
        mostrarAlerta("Erro ao carregar dados iniciais: " + erro.message, "danger")
    }
}

async function listarAtribuicoes() {
    try {
        const resp = await fetch(API_ATRIBUIR)

        if (!resp.ok) throw new Error("Erro ao buscar atribuições")

        todasAsAtribuicoes = await resp.json()
        renderizarTabela()
    } catch (erro) {
        console.error(erro)
        mostrarAlerta("Erro ao carregar atribuições: " + erro.message, "danger")
    }
}

function renderizarTabela() {
    tabela.innerHTML = ""
    totalAtribuicoes.textContent = todasAsAtribuicoes.length

    if (todasAsAtribuicoes.length === 0) {
        mensagemVazia.classList.remove("d-none")
        return
    }

    mensagemVazia.classList.add("d-none")

    todasAsAtribuicoes.forEach((attr) => {
        const tr = document.createElement("tr")
        const nomeMembroFormatado = membros[attr.id_membro] || `ID: ${attr.id_membro}`
        const nomeConquistaFormatado = conquistas[attr.id_conquista] || `ID: ${attr.id_conquista}`
        const nomeAdminFormatado = administradores[attr.id_admin] || `ID: ${attr.id_admin}`
        const dataFormatada = attr.data ? new Date(attr.data).toLocaleDateString("pt-BR") : "—"

        tr.innerHTML = `
          <td class="text-center"><strong>${attr.id_atribuir_conquista}</strong></td>
          <td>${nomeMembroFormatado}</td>
          <td>${nomeConquistaFormatado}</td>
          <td>${nomeAdminFormatado}</td>
          <td>${dataFormatada}</td>
          <td>${attr.observacao || "—"}</td>
          <td class="text-center">
            <button class="btn btn-sm" onclick="editarAtribuicao(${attr.id_atribuir_conquista}, ${attr.id_membro}, ${attr.id_conquista}, ${attr.id_admin}, '${(attr.data || "").replace(/'/g, "\\'")}', '${(attr.observacao || "").replace(/'/g, "\\'")}')" title="Editar">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="#ffc94c" class="bi bi-pencil-square" viewBox="0 0 16 16">
                <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
                <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"/>
              </svg>
            </button>
            <button class="btn btn-sm" onclick="excluirAtribuicao(${attr.id_atribuir_conquista})" title="Excluir">
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

    validarCampo(selectMembro)
    validarCampo(selectConquista)
    validarCampo(selectAdministrador)
    validarCampo(dataAtribuicaoInput)

    if (!form.checkValidity()) {
        e.stopPropagation()
        form.classList.add("was-validated")
        return
    }

    const id = idAtribuicaoInput.value
    const idMembro = Number(selectMembro.value)
    const idConquista = Number(selectConquista.value)
    const idAdmin = Number(selectAdministrador.value)
    const data = dataAtribuicaoInput.value
    const observacao = observacaoInput.value.trim()

    const method = id ? "PUT" : "POST"

    const atribuicao = {
        id_membro: idMembro,
        id_conquista: idConquista,
        id_admin: idAdmin,
        data: data || null,
        observacao: observacao || null,
    }

    if (id) {
        atribuicao.id_atribuir_conquista = Number(id)
    }

    try {
        console.log("Enviando atribuição:", atribuicao)
        const resp = await fetch(API_ATRIBUIR, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(atribuicao),
        })

        if (!resp.ok) throw new Error("Erro ao salvar atribuição")

        mostrarAlerta(id ? "Atribuição atualizada com sucesso!" : "Atribuição criada com sucesso!", "success")

        form.reset()
        form.classList.remove("was-validated")
        selectMembro.classList.remove("is-valid", "is-invalid")
        selectConquista.classList.remove("is-valid", "is-invalid")
        selectAdministrador.classList.remove("is-valid", "is-invalid")
        dataAtribuicaoInput.classList.remove("is-valid", "is-invalid")
        observacaoInput.classList.remove("is-valid", "is-invalid")
        idAtribuicaoInput.value = ""
        await listarAtribuicoes()
    } catch (erro) {
        mostrarAlerta("Erro: " + erro.message, "danger")
    }
})

async function excluirAtribuicao(id) {
    if (!confirm("Deseja realmente excluir esta atribuição?")) return

    try {
        const resp = await fetch(`${API_ATRIBUIR}/${id}`, { method: "DELETE" })

        if (!resp.ok) throw new Error("Erro ao excluir atribuição")

        mostrarAlerta("Atribuição excluída com sucesso!", "success")
        listarAtribuicoes()
    } catch (erro) {
        mostrarAlerta("Erro: " + erro.message, "danger")
    }
}

function editarAtribuicao(id, idMembro, idConquista, idAdmin, data, observacao) {
    idAtribuicaoInput.value = id
    selectMembro.value = idMembro
    selectConquista.value = idConquista
    selectAdministrador.value = idAdmin
    dataAtribuicaoInput.value = data
    observacaoInput.value = observacao

    validarCampo(selectMembro)
    validarCampo(selectConquista)
    validarCampo(selectAdministrador)
    validarCampo(dataAtribuicaoInput)
    if (observacao) {
        observacaoInput.classList.add("is-valid")
    }

    form.classList.remove("was-validated")
    window.scrollTo({ top: 0, behavior: "smooth" })
}

// Carregar dados iniciais ao abrir a página
carregarDadosIniciais()
