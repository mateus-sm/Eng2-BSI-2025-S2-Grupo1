const API_URL = "/apis/administrador";
let administradorModal, deleteModal;
let administradores = [];
let idParaExcluir = null;
let modoEdicao = false;

document.addEventListener("DOMContentLoaded", () => {
    const modalEl = document.getElementById("administradorModal");
    if (modalEl) administradorModal = new bootstrap.Modal(modalEl);

    const deleteEl = document.getElementById("deleteModal");
    if (deleteEl) deleteModal = new bootstrap.Modal(deleteEl);

    carregarAdministradores();
    carregarUsuariosSelect();

    const btnNovo = document.getElementById("btn-novo-administrador");
    if (btnNovo) btnNovo.addEventListener("click", abrirModalAdicionar);

    const btnConfirmDelete = document.getElementById("confirmDelete");
    if (btnConfirmDelete) btnConfirmDelete.addEventListener("click", excluirAdministrador);

    const btnSalvarEdicao = document.getElementById("btnSalvarEdicao");
    if (btnSalvarEdicao) btnSalvarEdicao.addEventListener("click", salvarEdicaoAdmin);

    registrarEventosRadio();
    registrarMascaras();
});


function registrarEventosRadio() {
    const rbExistente = document.getElementById("radioUsuarioExistente");
    const rbNovo = document.getElementById("radioNovoUsuario");

    if (rbExistente) {
        rbExistente.addEventListener("change", () => {
            document.getElementById("blocoUsuarioExistente").classList.remove("d-none");
            document.getElementById("blocoNovoUsuario").classList.add("d-none");
            hideErrorModal();
        });
    }

    if (rbNovo) {
        rbNovo.addEventListener("change", () => {
            document.getElementById("blocoUsuarioExistente").classList.add("d-none");
            document.getElementById("blocoNovoUsuario").classList.remove("d-none");
            hideErrorModal();
        });
    }
}

function registrarMascaras() {
    const cpfInput = document.getElementById("novo_cpf");
    if (cpfInput) {
        cpfInput.addEventListener("input", (e) => {
            let value = e.target.value.replace(/\D/g, '');
            value = value.replace(/(\d{3})(\d)/, '$1.$2');
            value = value.replace(/(\d{3})(\d)/, '$1.$2');
            value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
            if (value.length > 14) value = value.substring(0, 14);
            e.target.value = value;
        });
    }

    const telInput = document.getElementById("novo_telefone");
    if (telInput) {
        telInput.addEventListener("input", (e) => {
            let value = e.target.value.replace(/\D/g, '');
            value = value.replace(/^(\d{2})(\d)/, '($1) $2');
            value = value.replace(/(\d{5})(\d{1,4})$/, '$1-$2');
            if (value.length > 15) value = value.substring(0, 15);
            e.target.value = value;
        });
    }

    // // Máscara de Data
    // const dateInput = document.getElementById("novo_dtnasc");
    // if (dateInput) {
    //     dateInput.addEventListener("input", (e) => {
    //         let value = e.target.value.replace(/\D/g, '');
    //         value = value.replace(/(\d{2})(\d)/, '$1/$2');
    //         value = value.replace(/(\d{2})(\d{1,4})$/, '$1/$2');
    //         if (value.length > 10) value = value.substring(0, 10);
    //         e.target.value = value;
    //     });
    // }

    const cepInput = document.getElementById("novo_cep");
    if (cepInput) {
        cepInput.addEventListener("input", (e) => {
            let value = e.target.value.replace(/\D/g, "");
            value = value.replace(/^(\d{5})(\d)/, "$1-$2");
            if (value.length > 9) value = value.substring(0, 9);
            e.target.value = value;
        });

        cepInput.addEventListener("blur", () => {
            const cep = cepInput.value.replace(/\D/g, "");
            if (cep.length !== 8) return;

            document.getElementById("novo_rua").value = "...";

            fetch(`https://viacep.com.br/ws/${cep}/json/`)
                .then(r => r.json())
                .then(data => {
                    if (!data.erro) {
                        document.getElementById('novo_rua').value = data.logradouro || "";
                        document.getElementById('novo_bairro').value = data.bairro || "";
                        document.getElementById('novo_cidade').value = data.localidade || "";
                        document.getElementById('novo_uf').value = data.uf || "";
                    } else {
                        document.getElementById("novo_rua").value = "";
                        alert("CEP não encontrado.");
                    }
                })
                .catch(() => {
                    document.getElementById("novo_rua").value = "";
                });
        });
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

    if (dataObj.getFullYear() !== ano ||
        dataObj.getMonth() !== mes ||
        dataObj.getDate() !== dia) {
        return false;
    }

    return true;
}
async function carregarAdministradoresComFiltro() {
    const nome = document.getElementById("filtroNome").value.trim();
    const dataIni = document.getElementById("filtroDataInicial").value;
    const dataFim = document.getElementById("filtroDataFinal").value;

    if (dataIni && !validarDataReal(dataIni)) {
        alert("Data Inicial inválida (ex: 30 de fevereiro). Verifique o filtro.");
        return;
    }
    if (dataFim && !validarDataReal(dataFim)) {
        alert("Data Final inválida (ex: 30 de fevereiro). Verifique o filtro.");
        return;
    }

    let url = API_URL;

    const params = [];

    if (nome !== "") params.push(`nome=${encodeURIComponent(nome)}`);
    if (dataIni !== "") params.push(`dataIni=${dataIni}`);
    if (dataFim !== "") params.push(`dataFim=${dataFim}`);

    if (params.length > 0)
        url = `${API_URL}/filtrar?${params.join("&")}`;

    try {
        const resposta = await fetch(url);
        if (!resposta.ok) {
            const erroTxt = await resposta.text();
            throw new Error(erroTxt || "Erro ao buscar dados");
        }

        administradores = await resposta.json();

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
                    <td class="text-center">
                        <div class="btn-group" role="group">
                            <button class="btn btn-sm btn-outline-primary me-2 btn-editar" onclick="abrirModalEdicaoById(${admin.id})">
                                <i class="bi bi-pencil-fill"></i> Editar
                            </button>
                            <button class="btn btn-sm btn-outline-danger btn-excluir" onclick="abrirModalDelete(${admin.id})">
                                <i class="bi bi-trash-fill"></i> Excluir
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        });

    } catch (e) {
        console.error(e);
        mostrarErroModal(e.message || "Erro ao filtrar administradores.");
    }
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
                    <td class="text-center">
                        <div class="btn-group" role="group">
                            <button class="btn btn-sm btn-outline-primary me-2 btn-editar" onclick="abrirModalEdicaoById(${admin.id})">
                                <i class="bi bi-pencil-fill"></i> Editar
                            </button>
                            <button class="btn btn-sm btn-outline-danger btn-excluir" onclick="abrirModalDelete(${admin.id})">
                                <i class="bi bi-trash-fill"></i> Excluir
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        });

    } catch (e) {
        console.error(e);
        mostrarErroGlobal("Erro ao carregar administradores. Verifique se o backend está rodando.");
    }
}

async function carregarUsuariosSelect() {
    const select = document.getElementById("usuarioId");
    if (!select) return;

    select.innerHTML = "<option value=''>Selecione um usuário...</option>";
    try {
        const resp = await fetch("/apis/usuario?nome=");
        if (!resp.ok) return;

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

    const rbExistente = document.getElementById("radioUsuarioExistente");
    if (rbExistente) {
        rbExistente.checked = true;
        rbExistente.dispatchEvent(new Event('change'));
    }

    document.getElementById("administradorModalLabel").textContent = "Adicionar Administrador";
    await carregarUsuariosSelect();

    if (administradorModal) administradorModal.show();
}

function abrirModalEdicaoById(id) {
    const admin = administradores.find(a => a.id === id);
    if (!admin) return;

    document.getElementById("editUsuario").value = admin.usuario.id + " - " + admin.usuario.nome;
    document.getElementById("editDtIni").value = admin.dtIni;
    document.getElementById("editDtFim").value = admin.dtFim;

    const btnSalvar = document.getElementById("btnSalvarEdicao");
    if(btnSalvar) btnSalvar.setAttribute("data-id", admin.id);

    const modalEl = document.getElementById("modalEditarAdmin");
    const modal = new bootstrap.Modal(modalEl);
    modal.show();
}

function salvarAdministrador(event) {
    event.preventDefault();
    hideErrorModal();

    const rbExistente = document.getElementById("radioUsuarioExistente");

    if (rbExistente && rbExistente.checked) {
        const idUsuario = document.getElementById("usuarioId").value;
        if (!idUsuario) {
            mostrarErroModal("Selecione um usuário existente na lista.");
            return;
        }
        return criarAdministradorComUsuarioExistente(idUsuario);
    }

    if (!validarFormularioNovoUsuario()) {
        return;
    }

    let dados = coletarDadosNovoUsuario();
    return criarNovoUsuarioEAdministrador(dados);
}

function validarFormularioNovoUsuario() {
    const camposObrigatorios = [
        { id: "novo_nome", msg: "Nome é obrigatório." },
        { id: "novo_usuario", msg: "Login é obrigatório." },
        { id: "novo_senha", msg: "Senha é obrigatória." },
        { id: "novo_email", msg: "E-mail é obrigatório." },
        { id: "novo_cpf", msg: "CPF é obrigatório." },
        { id: "novo_dtnasc", msg: "Data de Nascimento é obrigatória." }
    ];

    for (let campo of camposObrigatorios) {
        const el = document.getElementById(campo.id);
        if (!el || !el.value.trim()) {
            mostrarErroModal(campo.msg);
            el.focus();
            return false;
        }
    }

    const cpf = document.getElementById("novo_cpf").value;
    if (cpf.length < 14) {
        mostrarErroModal("CPF incompleto.");
        return false;
    }

    return true;
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

async function criarNovoUsuarioEAdministrador(dados) {
    try {
        const payloadUsuario = {
            ...dados,
            login: dados.usuario,
            telefone: dados.telefone.replace(/\D/g, ""),
            cpf: dados.cpf.replace(/\D/g, ""),
            cep: dados.cep.replace(/\D/g, ""),
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
                mostrarErroModal(j.mensagem || j.erro || "Erro ao criar usuário.");
            } catch {
                mostrarErroModal(text || "Erro desconhecido ao criar usuário.");
            }
            return;
        }

        const usuarioCriado = JSON.parse(text);
        await criarAdministradorComUsuarioExistente(usuarioCriado.id);

    } catch (e) {
        console.error(e);
        mostrarErroModal("Erro de conexão ao criar usuário.");
    }
}

async function criarAdministradorComUsuarioExistente(idUsuario) {
    const dtIni = new Date().toISOString().split("T")[0];

    const payloadAdmin = {
        usuario: { id: parseInt(idUsuario) },
        dtIni: dtIni
    };

    try {
        const resp = await fetch("/apis/administrador", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payloadAdmin)
        });

        const txt = await resp.text();
        if (!resp.ok) {
            try {
                const j = JSON.parse(txt);
                mostrarErroModal(j.mensagem || j.erro || txt);
            } catch {
                mostrarErroModal("Erro ao transformar usuário em administrador.");
            }
            return;
        }

        if (administradorModal) administradorModal.hide();
        carregarAdministradores();

    } catch (e) {
        console.error(e);
        mostrarErroModal("Erro ao salvar administrador.");
    }
}

async function salvarEdicaoAdmin() {
    const idAdmin = this.getAttribute("data-id");
    const dtFimVal = document.getElementById("editDtFim").value;

    if (dtFimVal && !validarDataReal(dtFimVal)) {
        alert("Data Fim inválida (ex: 30 de fevereiro). Verifique a data informada.");
        return;
    }

    const body = {
        id: parseInt(idAdmin),
        dtFim: dtFimVal || null
    };

    try {
        const response = await fetch(`/apis/administrador/${idAdmin}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });

        if (response.ok) {
            alert("Atualizado com sucesso!");
            location.reload();
        } else {
            const txt = await response.text();
            alert("Erro: " + txt);
        }
    } catch(e) {
        alert("Erro ao conectar.");
    }
}

async function excluirAdministrador() {
    if (!idParaExcluir) return;

    try {
        const resp = await fetch(`${API_URL}/${idParaExcluir}`, { method: "DELETE" });
        if (!resp.ok) {
            const j = await resp.json().catch(() => ({}));
            const msg = j.erro || j.mensagem || "Erro desconhecido ao excluir.";

            alert(msg);
            return;
        }
        deleteModal.hide();
        idParaExcluir = null;
        carregarAdministradores();
    } catch (e) {
        alert("Erro inesperado de conexão.");
    }
}

function abrirModalDelete(id) {
    idParaExcluir = id;
    if (deleteModal) deleteModal.show();
}

function mostrarErroModal(msg) {
    const errorDiv = document.getElementById("modal-error-message");
    if (errorDiv) {
        errorDiv.textContent = msg;
        errorDiv.classList.remove("d-none");
    } else {
        alert(msg);
    }
}

function hideErrorModal() {
    const errorDiv = document.getElementById("modal-error-message");
    if (errorDiv) {
        errorDiv.classList.add("d-none");
        errorDiv.textContent = "";
    }
}

function mostrarErroGlobal(msg) {
    console.warn(msg);
}