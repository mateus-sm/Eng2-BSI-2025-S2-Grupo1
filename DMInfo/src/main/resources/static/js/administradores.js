const API_URL = "/apis/administrador";
let administradorModal, deleteModal;
let administradores = [];
let idParaExcluir = null;
let modoEdicao = false;

document.addEventListener("DOMContentLoaded", () => {
    administradorModal = new bootstrap.Modal(document.getElementById("administradorModal"));
    deleteModal = new bootstrap.Modal(document.getElementById("deleteModal"));

    carregarAdministradores();
    carregarUsuariosSelect();

    document.getElementById("btn-novo-administrador").addEventListener("click", abrirModalAdicionar);
    document.getElementById("confirmDelete").addEventListener("click", excluirAdministrador);

    registrarEventosRadio();
    registrarMascaras();
});


function registrarEventosRadio() {
    const rbExistente = document.getElementById("radioUsuarioExistente");
    const rbNovo = document.getElementById("radioNovoUsuario");

    rbExistente.addEventListener("change", () => {
        document.getElementById("blocoUsuarioExistente").classList.remove("d-none");
        document.getElementById("blocoNovoUsuario").classList.add("d-none");
    });

    rbNovo.addEventListener("change", () => {
        document.getElementById("blocoUsuarioExistente").classList.add("d-none");
        document.getElementById("blocoNovoUsuario").classList.remove("d-none");
    });
}

function formatarData(dt) {
    if (!dt) return "-";
    const [ano, mes, dia] = dt.split("-");
    return `${dia}/${mes}/${ano}`;
}

function formatarDataParaBanco(data) {
    if (!data) return null;
    if (data.includes("-")) return data;
    const partes = data.split("/");
    if (partes.length !== 3) return null;
    return `${partes[2].padStart(4,"0")}-${partes[1].padStart(2,"0")}-${partes[0].padStart(2,"0")}`;
}

function limparMascaras(usuario) {
    return {
        ...usuario,
        telefone: usuario.telefone ? usuario.telefone.replace(/\D/g, "") : "",
        cep: usuario.cep ? usuario.cep.replace(/\D/g, "") : "",
        cpf: usuario.cpf ? usuario.cpf.replace(/\D/g, "") : "",
        dtnasc: formatarDataParaBanco(usuario.dtnasc)
    };
}

async function carregarAdministradores() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error("Erro ao buscar administradores.");

        administradores = await response.json();
        const tabela = document.getElementById("tabela-administradores");
        tabela.innerHTML = "";

        if (!administradores || administradores.length === 0) {
            tabela.innerHTML = `<tr><td colspan="5" class="text-center">Nenhum administrador encontrado.</td></tr>`;
            return;
        }

        administradores.forEach(admin => {
            tabela.innerHTML += `
                <tr>
                    <td>${admin.id}</td>
                    <td>${admin.usuario?.nome ?? "N/A"}</td>
                    <td>${formatarData(admin.dtIni)}</td>
                    <td>${formatarData(admin.dtFim)}</td>
                    <td class="text-center btn-group">
                        <button class="btn btn-warning btn-sm" onclick="abrirModalEdicaoById(${admin.id})">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="abrirModalDelete(${admin.id})">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>
            `;
        });

    } catch (e) {
        console.error(e);
        mostrarErroGlobal("Erro ao carregar administradores (veja console).");
    }
}

async function carregarUsuariosSelect() {
    const select = document.getElementById("usuarioId");

    if (!select) return;
    select.innerHTML = "<option value=''>Selecione um usuário...</option>";

    try {
        const resp = await fetch("/apis/usuario");
        if (!resp.ok) {
            console.error("Falha ao carregar usuários");
            return;
        }
        const lista = await resp.json();
        lista.forEach(u => {
            select.innerHTML += `<option value="${u.id}">${u.id} - ${u.nome}</option>`;
        });
    } catch (e) {
        console.error("Erro ao carregar usuários.", e);
    }
}

async function abrirModalAdicionar() {
    modoEdicao = false;
    document.getElementById("form-administrador").reset();
    document.getElementById("administradorId").value = "";
    hideErrorModal();

    document.getElementById("radioUsuarioExistente").checked = true;
    document.getElementById("blocoUsuarioExistente").classList.remove("d-none");
    document.getElementById("blocoNovoUsuario").classList.add("d-none");

    document.getElementById("administradorModalLabel").textContent = "Adicionar Administrador";

    await carregarUsuariosSelect();

    const select = document.getElementById("usuarioId");
    if (select) select.disabled = false;

    const dtIniEl = document.getElementById("dtIni");
    if (dtIniEl) dtIniEl.remove();

    administradorModal.show();
}

function abrirModalEdicaoById(id) {
    const admin = administradores.find(a => a.id === id);
    if (!admin) {
        mostrarErroModal("Administrador não encontrado para edição.");
        return;
    }
    abrirModalEdicao(admin);
}

function abrirModalEdicao(admin) {

    document.getElementById("editUsuario").value = admin.usuario.id + " - " + admin.usuario.nome;

    document.getElementById("editDtIni").value = admin.dtIni;
    document.getElementById("editDtFim").value = admin.dtFim;

    document.getElementById("btnSalvarEdicao").setAttribute("data-id", admin.id);

    let modal = new bootstrap.Modal(document.getElementById("modalEditarAdmin"));
    modal.show();
}

function salvarAdministrador(event) {
    event.preventDefault();
    hideErrorModal();

    const rbExistente = document.getElementById("radioUsuarioExistente");

    if (rbExistente && rbExistente.checked) {
        const idUsuario = document.getElementById("usuarioId").value;
        if ((!idUsuario || idUsuario === "") && !modoEdicao) {
            mostrarErroModal("Selecione um usuário existente.");
            return;
        }
        return criarAdministradorComUsuarioExistente(idUsuario);
    }

    let dados = coletarDadosNovoUsuario();
    dados = limparMascaras(dados);
    return criarNovoUsuarioEAdministrador(dados);
}

async function criarNovoUsuarioEAdministrador(novoUsuario) {
    try {
        const payloadUsuario = {
            nome: novoUsuario.nome,
            login: novoUsuario.usuario,
            senha: novoUsuario.senha,
            telefone: novoUsuario.telefone,
            email: novoUsuario.email,
            cpf: novoUsuario.cpf,
            dtnasc: novoUsuario.dtnasc,
            rua: novoUsuario.rua,
            cep: novoUsuario.cep,
            bairro: novoUsuario.bairro,
            cidade: novoUsuario.cidade,
            uf: novoUsuario.uf
        };


        const respUsuario = await fetch("/apis/usuario", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payloadUsuario)
        });

        const text = await respUsuario.text();
        if (!respUsuario.ok) {
            try {
                const j = JSON.parse(text);
                mostrarErroModal(j.mensagem || j.erro || JSON.stringify(j));
            } catch {
                mostrarErroModal(text || "Erro ao criar usuário.");
            }
            return;
        }

        const usuarioCriado = JSON.parse(text);
        const novoIdUsuario = usuarioCriado.id;


        await criarAdministradorComUsuarioExistente(novoIdUsuario);

    } catch (e) {
        console.error(e);
        mostrarErroModal("Erro inesperado ao criar usuário + administrador.");
    }
}

async function criarAdministradorComUsuarioExistente(idUsuario) {
    const idAdmin = document.getElementById("administradorId").value;
    const dtFimEl = document.getElementById("dtFim");
    const dtFim = dtFimEl ? dtFimEl.value || null : null;

    const isCriacao = !idAdmin || idAdmin === "";
    const dtIni = isCriacao ? new Date().toISOString().split("T")[0] : (document.getElementById("dtIni") ? document.getElementById("dtIni").value || null : null);

    const dados = {
        usuario: { id: parseInt(idUsuario) },
        dtIni: dtIni
    };

    try {
        let url = "/apis/administrador";
        let method = "POST";

        if (!isCriacao) {
            url = `/apis/administrador/${idAdmin}`;
            method = "PUT";

            const body = { dtFim: dtFim || null };
            const resp = await fetch(url, {
                method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(body)
            });

            const txt = await resp.text();
            if (!resp.ok) {
                try {
                    const j = JSON.parse(txt);
                    mostrarErroModal(j.mensagem || j.erro || txt);
                } catch {
                    mostrarErroModal(txt || "Erro ao atualizar administrador.");
                }
                return;
            }
        } else {

            const resp = await fetch(url, {
                method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dados)
            });

            const txt = await resp.text();
            if (!resp.ok) {
                try {
                    const j = JSON.parse(txt);
                    mostrarErroModal(j.mensagem || j.erro || txt);
                } catch {
                    mostrarErroModal(txt || "Erro ao criar administrador.");
                }
                return;
            }
        }


        administradorModal.hide();
        modoEdicao = false;
        carregarAdministradores();

    } catch (e) {
        console.error(e);
        mostrarErroModal("Erro inesperado ao salvar administrador.");
    }
}

function coletarDadosNovoUsuario() {
    return {
        nome: document.getElementById("novo_nome").value.trim(),
        usuario: document.getElementById("novo_usuario").value.trim(),
        senha: document.getElementById("novo_senha").value.trim(),
        telefone: document.getElementById("novo_telefone").value,
        email: document.getElementById("novo_email").value.trim(),
        cpf: document.getElementById("novo_cpf").value,
        dtnasc: document.getElementById("novo_dtnasc").value,
        rua: document.getElementById("novo_rua").value.trim(),
        cep: document.getElementById("novo_cep").value,
        bairro: document.getElementById("novo_bairro").value.trim(),
        cidade: document.getElementById("novo_cidade").value.trim(),
        uf: document.getElementById("novo_uf").value.trim()
    };
}

function abrirModalDelete(id) {
    idParaExcluir = id;
    deleteModal.show();
}

async function excluirAdministrador() {
    if (!idParaExcluir) return;

    try {
        const resp = await fetch(`${API_URL}/${idParaExcluir}`, { method: "DELETE" });
        if (!resp.ok) {
            const txt = await resp.text();
            mostrarErroModal(txt || "Erro ao excluir.");
            return;
        }
        deleteModal.hide();
        idParaExcluir = null;
        carregarAdministradores();
    } catch (e) {
        console.error(e);
        mostrarErroModal("Erro inesperado ao excluir.");
    }
}

function mostrarErroModal(msg) {
    const errorDiv = document.getElementById("modal-error-message");
    errorDiv.textContent = msg;
    errorDiv.classList.remove("d-none");
}

function hideErrorModal() {
    const errorDiv = document.getElementById("modal-error-message");
    if (errorDiv) {
        errorDiv.classList.add("d-none");
        errorDiv.textContent = "";
    }
}

function mostrarErroGlobal(msg) {
    alert(msg);
}

function registrarMascaras() {
    const cpf = document.getElementById("novo_cpf");
    if (cpf) {
        cpf.addEventListener("input", (e) => {
            let v = e.target.value.replace(/\D/g, "");
            v = v.replace(/(\d{3})(\d)/, "$1.$2")
                .replace(/(\d{3})(\d)/, "$1.$2")
                .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
            e.target.value = v.substring(0, 14);
        });
    }

    const tel = document.getElementById("novo_telefone");
    if (tel) {
        tel.addEventListener("input", (e) => {
            let v = e.target.value.replace(/\D/g, "");
            v = v.replace(/^(\d{2})(\d)/, "($1) $2")
                .replace(/(\d{5})(\d{1,4})$/, "$1-$2");
            e.target.value = v.substring(0, 15);
        });
    }

    const nasc = document.getElementById("novo_dtnasc");
    if (nasc) {
        nasc.addEventListener("input", (e) => {
            let v = e.target.value.replace(/\D/g, "");
            v = v.replace(/(\d{2})(\d)/, "$1/$2")
                .replace(/(\d{2})(\d{1,4})$/, "$1/$2");
            e.target.value = v.substring(0, 10);
        });
    }

    const cep = document.getElementById("novo_cep");
    if (cep) {
        cep.addEventListener("blur", () => {
            const num = cep.value.replace(/\D/g, "");
            if (num.length !== 8) return;

            fetch(`https://viacep.com.br/ws/${num}/json/`)
                .then(r => r.json())
                .then(data => {
                    if (data.erro) return;
                    document.getElementById("novo_rua").value = data.logradouro || "";
                    document.getElementById("novo_bairro").value = data.bairro || "";
                    document.getElementById("novo_cidade").value = data.localidade || "";
                    document.getElementById("novo_uf").value = data.uf || "";
                });
        });
    }
}

document.getElementById("btnSalvarEdicao").addEventListener("click", async function () {

    const idAdmin = this.getAttribute("data-id");

    const body = {
        dtFim: document.getElementById("editDtFim").value || null
    };

    const response = await fetch(`/apis/administrador/${idAdmin}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
    });

    if (response.ok) {
        alert("Administrador atualizado com sucesso!");
        location.reload();
    } else {
        const txt = await response.text();
        alert("Erro ao salvar: " + txt);
    }
});


