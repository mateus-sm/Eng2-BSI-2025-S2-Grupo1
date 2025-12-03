const API_URL = "/apis/administrador";
let administradorModal, deleteModal;
let administradores = [];
let idParaExcluir = null;
let modoEdicao = false;

document.addEventListener("DOMContentLoaded", () => {
    // Inicializa os Modais do Bootstrap
    const modalEl = document.getElementById("administradorModal");
    if (modalEl) administradorModal = new bootstrap.Modal(modalEl);

    const deleteEl = document.getElementById("deleteModal");
    if (deleteEl) deleteModal = new bootstrap.Modal(deleteEl);

    // Carrega dados iniciais
    carregarAdministradores();
    carregarUsuariosSelect();

    // Event Listeners dos Botões
    const btnNovo = document.getElementById("btn-novo-administrador");
    if (btnNovo) btnNovo.addEventListener("click", abrirModalAdicionar);

    const btnConfirmDelete = document.getElementById("confirmDelete");
    if (btnConfirmDelete) btnConfirmDelete.addEventListener("click", excluirAdministrador);

    const btnSalvarEdicao = document.getElementById("btnSalvarEdicao");
    if (btnSalvarEdicao) btnSalvarEdicao.addEventListener("click", salvarEdicaoAdmin);

    // Registra comportamentos de interface
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

// --- MÁSCARAS (IDÊNTICAS AO REGISTER.HTML) ---
function registrarMascaras() {
    // Máscara de CPF
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

    // Máscara de Telefone
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

    // Máscara de Data
    const dateInput = document.getElementById("novo_dtnasc");
    if (dateInput) {
        dateInput.addEventListener("input", (e) => {
            let value = e.target.value.replace(/\D/g, '');
            value = value.replace(/(\d{2})(\d)/, '$1/$2');
            value = value.replace(/(\d{2})(\d{1,4})$/, '$1/$2');
            if (value.length > 10) value = value.substring(0, 10);
            e.target.value = value;
        });
    }

    // Busca de CEP (ViaCEP)
    const cepInput = document.getElementById("novo_cep");
    if (cepInput) {
        cepInput.addEventListener("blur", () => {
            const cep = cepInput.value.replace(/\D/g, "");
            if (cep.length !== 8) return;

            // Feedback visual de carregamento
            document.getElementById("novo_rua").value = "...";

            fetch(`https://viacep.com.br/ws/${cep}/json/`)
                .then(r => r.json())
                .then(d => {
                    if (!d.erro) {
                        document.getElementById('novo_rua').value = d.logradouro || "";
                        document.getElementById('novo_bairro').value = d.bairro || "";
                        document.getElementById('novo_cidade').value = d.localidade || "";
                        document.getElementById('novo_uf').value = d.uf || "";
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

// --- FORMATAÇÃO E UTILITÁRIOS ---

function formatarData(dt) {
    if (!dt) return "-";
    const [ano, mes, dia] = dt.split("-");
    return `${dia}/${mes}/${ano}`;
}

// Converte dd/mm/aaaa para aaaa-mm-dd (para o banco)
function formatarDataParaBanco(data) {
    if (!data) return null;
    // Se já estiver certo
    if (data.match(/^\d{4}-\d{2}-\d{2}$/)) return data;
    // Se tiver barra
    if (data.includes("/")) {
        const partes = data.split("/");
        if (partes.length === 3) {
            return `${partes[2]}-${partes[1]}-${partes[0]}`;
        }
    }
    return null;
}

// --- CARREGAMENTO DE DADOS ---

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
        mostrarErroGlobal("Erro ao carregar administradores. Verifique se o backend está rodando.");
    }
}

async function carregarUsuariosSelect() {
    const select = document.getElementById("usuarioId");
    if (!select) return;

    select.innerHTML = "<option value=''>Selecione um usuário...</option>";
    try {
        // Envia parametro vazio para garantir que o backend aceite
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

// --- LÓGICA DE MODAIS ---

async function abrirModalAdicionar() {
    modoEdicao = false;
    document.getElementById("form-administrador").reset();
    document.getElementById("administradorId").value = "";
    hideErrorModal();

    // Reseta para a aba de usuário existente
    const rbExistente = document.getElementById("radioUsuarioExistente");
    if (rbExistente) {
        rbExistente.checked = true;
        rbExistente.dispatchEvent(new Event('change')); // Força atualização visual
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

// --- SALVAR (CRIAÇÃO) ---

function salvarAdministrador(event) {
    event.preventDefault();
    hideErrorModal();

    const rbExistente = document.getElementById("radioUsuarioExistente");

    // LÓGICA 1: USUÁRIO JÁ EXISTE NO BANCO
    if (rbExistente && rbExistente.checked) {
        const idUsuario = document.getElementById("usuarioId").value;
        if (!idUsuario) {
            mostrarErroModal("Selecione um usuário existente na lista.");
            return;
        }
        return criarAdministradorComUsuarioExistente(idUsuario);
    }

    // LÓGICA 2: CRIAR NOVO USUÁRIO (Formulário completo)
    if (!validarFormularioNovoUsuario()) {
        return; // Para se a validação falhar
    }

    let dados = coletarDadosNovoUsuario();
    // Limpamos apenas o que o Java não gosta (ex: parenteses do telefone),
    // mas mantemos o formato do CPF se o Java regex exigir.
    return criarNovoUsuarioEAdministrador(dados);
}

function validarFormularioNovoUsuario() {
    // Validação simples de campos obrigatórios no front
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

    // Valida tamanho do CPF
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
        usuario: document.getElementById("novo_usuario").value.trim(), // Login
        senha: document.getElementById("novo_senha").value.trim(),
        telefone: document.getElementById("novo_telefone").value, // Vamos limpar depois
        email: document.getElementById("novo_email").value.trim(),
        cpf: document.getElementById("novo_cpf").value, // Mantemos formato
        dtnasc: document.getElementById("novo_dtnasc").value, // Vamos converter
        rua: document.getElementById("novo_rua").value.trim(),
        cep: document.getElementById("novo_cep").value,
        bairro: document.getElementById("novo_bairro").value.trim(),
        cidade: document.getElementById("novo_cidade").value.trim(),
        uf: document.getElementById("novo_uf").value.trim()
    };
}

async function criarNovoUsuarioEAdministrador(dados) {
    try {
        // PREPARAÇÃO DOS DADOS PARA O BACKEND
        const payloadUsuario = {
            ...dados,
            // Login no Model Java chama 'login', no Register chama 'usuario'.
            // Seu UsuarioController mapeia 'usuario' da request?
            // O ideal é enviar o nome que o Java espera. Se for Usuario.java:
            login: dados.usuario,

            // Limpa telefone (Java geralmente salva limpo ou aceita tudo,
            // mas no Register.html ele limpa).
            telefone: dados.telefone.replace(/\D/g, ""),

            // Converte data dd/mm/aaaa -> aaaa-mm-dd
            dtnasc: formatarDataParaBanco(dados.dtnasc),

            // O CPF vai formatado mesmo, pois o regex do Java exige pontos e traço
            cpf: dados.cpf
        };

        const respUsuario = await fetch("/apis/usuario", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payloadUsuario)
        });

        const text = await respUsuario.text();

        if (!respUsuario.ok) {
            // Tenta ler o JSON de erro do backend (MembroErro)
            try {
                const j = JSON.parse(text);
                mostrarErroModal(j.mensagem || j.erro || "Erro ao criar usuário.");
            } catch {
                mostrarErroModal(text || "Erro desconhecido ao criar usuário.");
            }
            return;
        }

        // Se criou o usuário, pega o ID e cria o Administrador
        const usuarioCriado = JSON.parse(text);
        await criarAdministradorComUsuarioExistente(usuarioCriado.id);

    } catch (e) {
        console.error(e);
        mostrarErroModal("Erro de conexão ao criar usuário.");
    }
}

async function criarAdministradorComUsuarioExistente(idUsuario) {
    // Data de hoje para o início
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

        // Sucesso total
        if (administradorModal) administradorModal.hide();
        carregarAdministradores();

    } catch (e) {
        console.error(e);
        mostrarErroModal("Erro ao salvar administrador.");
    }
}

// --- EDICÃO E EXCLUSÃO ---

async function salvarEdicaoAdmin() {
    const idAdmin = this.getAttribute("data-id");
    const dtFimVal = document.getElementById("editDtFim").value;

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
            const j = await resp.json().catch(() => ({ mensagem: "Erro ao excluir" }));
            alert(j.mensagem || "Erro ao excluir."); // Usa alert pois o modal de erro fica dentro do outro modal
            return;
        }
        deleteModal.hide();
        idParaExcluir = null;
        carregarAdministradores();
    } catch (e) {
        alert("Erro inesperado.");
    }
}

function abrirModalDelete(id) {
    idParaExcluir = id;
    if (deleteModal) deleteModal.show();
}

// --- MENSAGENS DE ERRO ---

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
    // Pode exibir em um toast ou alert
    console.warn(msg);
}