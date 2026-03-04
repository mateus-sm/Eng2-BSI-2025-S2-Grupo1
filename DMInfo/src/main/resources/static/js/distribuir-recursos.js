const API_RECURSOS = "http://localhost:8080/apis/recurso"
const API_DISTRIBUICOES = "http://localhost:8080/apis/distribuicao-recursos"

// DOM Elements
const formDistribuicao = document.getElementById("formDistribuicao");
const alertaContainer = document.getElementById("alertaContainer");
const tabelaCarrinho = document.getElementById("tabelaCarrinho");
const corpoCarrinho = document.getElementById("corpoCarrinho");
const btnAdicionar = document.getElementById("btnAdicionar");

// Estado Global
let carrinho = [];
const mapRecursos = {};
let todasDistribuicoes = [];

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
    configurarDataAtual();
    carregarDadosIniciais();
});

function configurarDataAtual() {
    const inputData = document.getElementById('dataDistribuicao');
    const hoje = new Date().toISOString().split('T')[0];
    inputData.value = hoje;
}

// ==========================================
// LÓGICA DO CARRINHO
// ==========================================
btnAdicionar.addEventListener('click', () => {
    const selectRecurso = document.getElementById('selectRecurso');
    const inputQuantidade = document.getElementById('quantidade');

    const idRecurso = parseInt(selectRecurso.value);
    const nomeRecurso = selectRecurso.options[selectRecurso.selectedIndex]?.text;
    const quantidade = parseInt(inputQuantidade.value);

    if (!idRecurso || isNaN(quantidade) || quantidade < 1) {
        mostrarAlerta("Selecione um recurso e informe uma quantidade válida.", "warning");
        return;
    }

    // Verifica se o item já está no carrinho
    const itemExistente = carrinho.find(item => item.idRecurso === idRecurso);
    if (itemExistente) {
        itemExistente.quantidade += quantidade;
    } else {
        carrinho.push({ idRecurso, nomeRecurso, quantidade });
    }

    // Limpa os inputs após adicionar
    selectRecurso.value = "";
    inputQuantidade.value = "";
    
    renderizarCarrinho();
});

function removerItem(idRecurso) {
    carrinho = carrinho.filter(item => item.idRecurso !== idRecurso);
    renderizarCarrinho();
}

function renderizarCarrinho() {
    corpoCarrinho.innerHTML = "";
    
    if (carrinho.length === 0) {
        tabelaCarrinho.classList.add('d-none');
        return;
    }

    tabelaCarrinho.classList.remove('d-none');

    carrinho.forEach(item => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="align-middle">${item.nomeRecurso}</td>
            <td class="text-center align-middle">${item.quantidade}</td>
            <td class="text-center">
                <button type="button" class="btn btn-sm btn-icone" title="Remover Item" onclick="removerItem(${item.idRecurso})">
                    <i class="bi bi-trash-fill fs-5"></i>
                </button>
            </td>
        `;
        corpoCarrinho.appendChild(tr);
    });
}

// ==========================================
// SALVAR DISTRIBUIÇÃO (POST PARA O BACK-END)
// ==========================================
formDistribuicao.addEventListener('submit', async (event) => {
    event.preventDefault();

    const valor = parseFloat(document.getElementById('valor').value) || 0;

    // Deve ter dinheiro ou item
    if (valor <= 0 && carrinho.length === 0) {
        mostrarAlerta("A distribuição deve conter pelo menos um item no carrinho ou um valor monetário!", "danger");
        return;
    }

    // Montando o DTO
    const payload = {
        admin: 1, // Alterar para pegar dinamicamente
        data: document.getElementById('dataDistribuicao').value,
        descricao: document.getElementById('descricaoDist').value,
        instituicaoReceptora: document.getElementById('instituicao').value,
        valor: valor,
        itens: carrinho.map(item => ({
            idRecurso: item.idRecurso,
            quantidade: item.quantidade
        }))
    };

    try {
        const resposta = await fetch(`${API_DISTRIBUICOES}/carrinho`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (resposta.ok) {
            mostrarAlerta("Distribuição registrada com sucesso!", "success");

            formDistribuicao.reset();
            configurarDataAtual();
            carrinho = [];
            renderizarCarrinho();

            carregarDadosIniciais();
        } else {
            const erroTexto = await resposta.text();
            mostrarAlerta(erroTexto || "Erro ao salvar distribuição.", "danger");
        }
    } catch (erro) {
        console.error(erro);
        mostrarAlerta("Erro de conexão com o servidor.", "danger");
    }
});

// ==========================================
// CARREGAR DADOS DAS APIS
// ==========================================
async function carregarDadosIniciais() {
    try {
        const respRecursos = await fetch(API_RECURSOS);
        if (respRecursos.ok) {
            const listaRecursos = await respRecursos.json();
            const selectRecurso = document.getElementById('selectRecurso');
            selectRecurso.innerHTML = '<option value="">Selecione um recurso...</option>';
            
            listaRecursos.forEach(r => {
                mapRecursos[r.id] = r.descricao; 

                const option = document.createElement("option");
                option.value = r.id;
                option.textContent = `${r.descricao} (Estoque: ${r.quantidade})`;
                selectRecurso.appendChild(option);
            });
        }

        const respDistribuicoes = await fetch(API_DISTRIBUICOES);
        if (respDistribuicoes.ok) {
            todasDistribuicoes = await respDistribuicoes.json(); // Salva na global
            renderizarTabelaDistribuicoes(todasDistribuicoes);   // Desenha a tabela
        }
    } catch (erro) {
        console.error(erro);
        mostrarAlerta("Erro ao comunicar com o servidor.", "danger");
    }
}

// ==========================================
// RENDERIZAR TABELA PRINCIPAL
// ==========================================
function renderizarTabelaDistribuicoes(lista) {
    const tabelaListagem = document.getElementById("tabelaListagem");
    tabelaListagem.innerHTML = "";
    
    if (lista.length === 0) {
        tabelaListagem.innerHTML = `<tr><td colspan="6" class="text-center text-muted py-4">Nenhuma distribuição encontrada com estes filtros.</td></tr>`;
        return;
    }

    lista.forEach(d => {
        const dataFormatada = d.data ? d.data.split('-').reverse().join('/') : "-";
        const valorFormatado = d.valor ? parseFloat(d.valor).toFixed(2) : "0.00";
        
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td class="text-center fw-bold align-middle">${d.id}</td>
            <td class="align-middle">${d.instituicaoReceptora || "-"}</td>
            <td class="align-middle">${dataFormatada}</td>
            <td class="align-middle">${d.descricao || "-"}</td>
            <td class="text-end align-middle">R$ ${valorFormatado}</td>
            <td class="text-center align-middle">
                <button type="button" class="btn btn-sm btn-icone" title="Ver Detalhes" onclick="abrirModalDetalhes(${d.id})">
                    <i class="bi bi-eye-fill fs-5 text-info"></i>
                </button>
                <button type="button" class="btn btn-sm btn-icone" title="Excluir Distribuição" onclick="excluirDistribuicao(${d.id})">
                    <i class="bi bi-trash-fill fs-5 text-danger"></i>
                </button>
            </td>
        `;
        tabelaListagem.appendChild(tr);
    });
}

// ==========================================
// LÓGICA DE FILTROS
// ==========================================
const inputFiltroInstituicao = document.getElementById("filtroInstituicao");
const inputFiltroData = document.getElementById("filtroData");

function aplicarFiltros() {
    const textoInstituicao = inputFiltroInstituicao.value.toLowerCase().trim();
    const dataFiltro = inputFiltroData.value;

    const listaFiltrada = todasDistribuicoes.filter(d => {
        const nomeOng = d.instituicaoReceptora ? d.instituicaoReceptora.toLowerCase() : "";
        const passouFiltroInstituicao = textoInstituicao === "" || nomeOng.includes(textoInstituicao);
        const passouFiltroData = dataFiltro === "" || d.data === dataFiltro;
        
        // Só retorna a linha se ela passar nos dois filtros ao mesmo tempo
        return passouFiltroInstituicao && passouFiltroData;
    });
    renderizarTabelaDistribuicoes(listaFiltrada);
}

function limparFiltros() {
    inputFiltroInstituicao.value = "";
    inputFiltroData.value = "";
    renderizarTabelaDistribuicoes(todasDistribuicoes);
}

inputFiltroInstituicao.addEventListener("input", aplicarFiltros);
inputFiltroData.addEventListener("change", aplicarFiltros);

// ==========================================
// VISUALIZAR DETALHES (MODAL)
// ==========================================
async function abrirModalDetalhes(idDistribuicao) {
    try {
        const resposta = await fetch(`${API_DISTRIBUICOES}/${idDistribuicao}`);
        if (!resposta.ok) throw new Error("Erro ao buscar detalhes da distribuição.");
        
        const d = await resposta.json();

        document.getElementById('detalheId').textContent = d.id;
        document.getElementById('detalheInstituicao').textContent = d.instituicaoReceptora;
        document.getElementById('detalheData').textContent = d.data ? d.data.split('-').reverse().join('/') : "-";
        document.getElementById('detalheDescricao').textContent = d.descricao;
        document.getElementById('detalheValor').textContent = `R$ ${d.valor ? parseFloat(d.valor).toFixed(2) : "0.00"}`;

        // Prepara tabela
        const tbodyItens = document.getElementById('detalheItensBody');
        const avisoSemItens = document.getElementById('detalheSemItens');
        tbodyItens.innerHTML = `<tr><td colspan="2" class="text-center text-muted">Buscando itens no servidor...</td></tr>`;
        avisoSemItens.classList.add('d-none');

        // Abre o Modal na tela
        const modal = new bootstrap.Modal(document.getElementById('modalDetalhes'));
        modal.show();

        const respItens = await fetch(`http://localhost:8080/apis/distribuicao-itens/por-distribuicao/${idDistribuicao}`);
        
        if (!respItens.ok) {
            tbodyItens.innerHTML = `<tr><td colspan="2" class="text-center text-danger">Erro ao carregar os itens.</td></tr>`;
            return;
        }

        const itens = await respItens.json();

        tbodyItens.innerHTML = "";

        // Renderiza os itens ou mostra a mensagem de "Sem Itens"
        if (itens.length === 0) {
            avisoSemItens.classList.remove('d-none');
        } else {
            itens.forEach(item => {
                const nomeDoRecurso = mapRecursos[item.recurso] || `Recurso ID #${item.recurso}`;

                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="align-middle">${nomeDoRecurso}</td>
                    <td class="text-center align-middle">${item.quantidade}</td>
                `;
                tbodyItens.appendChild(tr);
            });
        }

    } catch (erro) {
        console.error(erro);
        mostrarAlerta("Não foi possível carregar os detalhes.", "danger");
    }
}

// ==========================================
// EXCLUIR DISTRIBUIÇÃO
// ==========================================
async function excluirDistribuicao(idDistribuicao) {
    const confirmacao = confirm(`Atenção: Tem certeza que deseja excluir a distribuição #${idDistribuicao}?\n\nOs itens físicos (se houverem) retornarão automaticamente para o estoque.`);
    
    if (!confirmacao) {
        return;
    }

    try {
        const resposta = await fetch(`${API_DISTRIBUICOES}/${idDistribuicao}`, {
            method: 'DELETE'
        });

        if (resposta.ok) {
            mostrarAlerta("Distribuição excluída e estoque restaurado com sucesso!", "success");
            carregarDadosIniciais(); 
        } else {
            const erroTexto = await resposta.text();
            mostrarAlerta(erroTexto || "Erro ao excluir a distribuição.", "danger");
        }
    } catch (erro) {
        console.error(erro);
        mostrarAlerta("Erro de conexão ao tentar excluir.", "danger");
    }
}

// Função Utilitária para Alertas
function mostrarAlerta(mensagem, tipo = "success") {
    const alertId = `alerta-${Date.now()}`;
    const alertHtml = `
        <div id="${alertId}" class="alert alert-${tipo} alert-dismissible fade show shadow-sm" role="alert">
            ${mensagem}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    alertaContainer.innerHTML = alertHtml; // Substitui o alerta anterior para não acumular

    setTimeout(() => {
        const alertaElemento = document.getElementById(alertId);
        if (alertaElemento) alertaElemento.remove();
    }, 5000);
}