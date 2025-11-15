// lancamento_frequencia.js

// URL base para os endpoints gerais (onde '/membro' está)
const API_URL_BASE = 'http://localhost:8080/apis';
// URL para os endpoints de Frequência (onde '/atividades' e o POST estão)
const API_URL_FREQ = `${API_URL_BASE}/frequencia`;

let atividadeSelecionadaId = null;
let membroSelecionado = { id: null, nome: null };

// --- Funções Auxiliares ---
function formatDate(date) {
    if (!date) return '';
    // Garante que o formato de data seja 'YYYY-MM-DD'
    return new Date(date).toISOString().split('T')[0];
}

// Função de debounce para evitar spam na API enquanto o usuário digita
function debounce(func, timeout = 300){
    let timer;
    return (...args) => {
        clearTimeout(timer);
        timer = setTimeout(() => { func.apply(this, args); }, timeout);
    };
}

// ------------------------------------------
// 1. BUSCAR E LISTAR ATIVIDADES PELA DATA
// ------------------------------------------

document.getElementById('buscaAtividadeForm').addEventListener('submit', async (ev) => {
    ev.preventDefault(); // IMPEDE O RECARREGAMENTO DA PÁGINA

    const dataBusca = document.getElementById('dataBusca').value;
    const resultadosDiv = document.getElementById('atividadesResultados');

    if (!dataBusca) return;

    // Reseta a seção de lançamento
    document.getElementById('lancamentoSection').style.display = 'none';
    atividadeSelecionadaId = null;
    membroSelecionado = { id: null, nome: null };
    document.getElementById('membrosResultados').innerHTML = '';

    resultadosDiv.innerHTML = 'Buscando atividades...';

    try {
        const resp = await fetch(`${API_URL_FREQ}/atividades?data=${dataBusca}`);
        const data = await resp.json();

        if (!resp.ok) {
            resultadosDiv.innerHTML = `<p class="error">Erro ao buscar: ${data.mensagem || 'Falha na comunicação com a API.'}</p>`;
            return;
        }

        if (data.length === 0) {
            resultadosDiv.innerHTML = '<p>Nenhuma atividade encontrada para esta data.</p>';
            return;
        }

        let html = '<h3>Atividades Encontradas:</h3>';
        data.forEach(a => {
            const atividadeDesc = a.atv?.descricao || 'Atividade Base Desconhecida';
            const hora = a.horario ? a.horario.substring(0, 5) : 'N/A';

            html += `
                <div class="atividade-item">
                    <span>ID Realização: <strong>${a.id}</strong> | ${atividadeDesc} (${hora})</span>
                    <button onclick="selecionarAtividade(${a.id}, '${atividadeDesc}', '${formatDate(a.dtIni)}', '${hora}')">Selecionar</button>
                </div>
            `;
        });
        resultadosDiv.innerHTML = html;

    } catch (error) {
        resultadosDiv.innerHTML = `<p class="error">Erro de rede: ${error.message}</p>`;
    }
});


// ------------------------------------------
// 2. SELECIONAR ATIVIDADE E PREPARAR LANÇAMENTO
// ------------------------------------------

function selecionarAtividade(id, descricao, data, hora) {
    atividadeSelecionadaId = id;

    // Atualiza os campos na seção de lançamento
    document.getElementById('idAtividadeSelecionada').value = id;
    document.getElementById('idAtividadeSelecionadaInfo').textContent = id;
    document.getElementById('atividadeInfo').textContent = `${descricao} (${data} - ${hora})`;

    // Reseta a lista de frequências e mostra a seção
    document.getElementById('listaFrequencia').innerHTML = '';
    document.getElementById('lancamentoSection').style.display = 'block';
    document.getElementById('membroInfo').innerHTML = '';
    document.getElementById('membroNomeConfirmacao').textContent = 'N/A';
    document.getElementById('lancamentoForm').style.display = 'none';

    // Inicia o carregamento da lista completa de membros
    const buscaInput = document.getElementById('buscaNomeMembro');
    buscaInput.value = '';



    // Altera a mensagem para indicar que a lista será exibida
    document.getElementById('membrosResultados').innerHTML = 'Carregando lista completa de membros...';

    // REMOVE O LISTENER DE INPUT:
    // Não é mais necessário escutar o evento 'input'
    // buscaInput.addEventListener('input', debounce(buscarMembros, 300));

    // CHAMA A FUNÇÃO DE CARREGAMENTO IMEDIATAMENTE
    buscarMembros();
    carregarFrequenciasExistentes(id);

    alert(`Atividade ${id} selecionada. Prossiga para o registro de frequência.`);
    window.scrollTo({ top: document.getElementById('lancamentoSection').offsetTop, behavior: 'smooth' });
}

async function buscarMembros() {
    // MODIFICAÇÃO: Não lê o campo de input, força o termo a ser vazio.
    const termo = '';
    const resultadosDiv = document.getElementById('membrosResultados');

    resultadosDiv.innerHTML = 'Carregando membros...';

    try {
        // A requisição será: http://localhost:8080/apis/membro?busca= (termo vazio)
        //const urlBusca = `${API_URL_BASE}/membros?busca=${termo}`;
        const urlBusca = `${API_URL_BASE}/membro/get-all`;
        const resp = await fetch(urlBusca);
        const data = await resp.json();

        if (!resp.ok) {
            resultadosDiv.innerHTML = `<p class="error">Erro: ${data.mensagem || 'Falha na busca de membros.'}</p>`;
            return;
        }

        if (data.length === 0) {
            resultadosDiv.innerHTML = '<p>Nenhum membro encontrado.</p>';
            return;
        }

        // --- PROCESSAMENTO E INSERÇÃO NO HTML ---
        let html = '<h4>Selecione o Membro:</h4>';
        data.forEach(m => {
            const nome = m.usuario?.nome || `ID Usuário: ${m.usuario?.id}`;
            const nomeEscapado = nome.replace(/'/g, "\\'");

            html += `
                <div class="atividade-item" style="cursor: pointer;" 
                    onclick="selecionarMembro(${m.id}, '${nomeEscapado}')">
                    <span>ID Membro: <strong>${m.id}</strong> | ${nome}</span>
                </div>
            `;
        });
        resultadosDiv.innerHTML = html;

    } catch (error) {
        console.error("Erro na função buscarMembros:", error);
        resultadosDiv.innerHTML = `<p class="error">Erro de rede ou processamento: ${error.message}</p>`;
    }
}

/*
function selecionarAtividade(id, descricao, data, hora) {
    atividadeSelecionadaId = id;

    // Atualiza os campos na seção de lançamento
    document.getElementById('idAtividadeSelecionada').value = id;
    document.getElementById('idAtividadeSelecionadaInfo').textContent = id;
    document.getElementById('atividadeInfo').textContent = `${descricao} (${data} - ${hora})`;

    // Reseta a lista de frequências e mostra a seção
    document.getElementById('listaFrequencia').innerHTML = '';
    document.getElementById('lancamentoSection').style.display = 'block';
    document.getElementById('membroInfo').innerHTML = '';
    document.getElementById('membroNomeConfirmacao').textContent = 'N/A';
    document.getElementById('lancamentoForm').style.display = 'none';

    // Inicia a escuta para a busca de membros
    const buscaInput = document.getElementById('buscaNomeMembro');
    buscaInput.value = '';
    // Mensagem de instrução inicial
    document.getElementById('membrosResultados').innerHTML = 'Digite o nome do membro.';
    buscaInput.addEventListener('input', debounce(buscarMembros, 300));
    buscaInput.focus();

    alert(`Atividade ${id} selecionada. Prossiga para o registro de frequência.`);
    window.scrollTo({ top: document.getElementById('lancamentoSection').offsetTop, behavior: 'smooth' });
}


// ------------------------------------------
// 3. BUSCAR MEMBROS POR NOME (CORRIGIDO)
// ------------------------------------------

async function buscarMembros() {
    const termo = document.getElementById('buscaNomeMembro').value.trim();
    const resultadosDiv = document.getElementById('membrosResultados');

    // Optamos por buscar em qualquer termo, incluindo vazio, para permitir listar todos.
    resultadosDiv.innerHTML = 'Buscando membros...';

    try {
        // CORREÇÃO DA URL: Usa a API_URL_BASE e o endpoint /membro que foi confirmado.
        const urlBusca = `${API_URL_BASE}/membro?busca=${termo}`;
        const resp = await fetch(urlBusca);
        const data = await resp.json();

        if (!resp.ok) {
            resultadosDiv.innerHTML = `<p class="error">Erro: ${data.mensagem || 'Falha na busca de membros.'}</p>`;
            return;
        }

        if (data.length === 0) {
            resultadosDiv.innerHTML = '<p>Nenhum membro encontrado com este nome.</p>';
            return;
        }

        // --- PROCESSAMENTO E INSERÇÃO NO HTML ---
        let html = '<h4>Selecione o Membro:</h4>';
        data.forEach(m => {
            const nome = m.usuario?.nome || `ID Usuário: ${m.usuario?.id}`;
            const nomeEscapado = nome.replace(/'/g, "\\'");

            html += `
                <div class="atividade-item" style="cursor: pointer;" 
                    onclick="selecionarMembro(${m.id}, '${nomeEscapado}')">
                    <span>ID Membro: <strong>${m.id}</strong> | ${nome}</span>
                </div>
            `;
        });
        resultadosDiv.innerHTML = html;

    } catch (error) {
        console.error("Erro na função buscarMembros:", error);
        resultadosDiv.innerHTML = `<p class="error">Erro de rede ou processamento: ${error.message}</p>`;
    }
}


 */

function selecionarMembro(id, nome) {
    membroSelecionado.id = id;
    membroSelecionado.nome = nome;

    // Atualiza a seção de confirmação
    document.getElementById('membroNomeConfirmacao').textContent = nome;
    document.getElementById('idMembroSelecionado').value = id;
    document.getElementById('lancamentoForm').style.display = 'block';
    document.getElementById('membroInfo').innerHTML = ''; // Limpa mensagens anteriores

    alert(`Membro ${nome} (ID: ${id}) selecionado. Confirme o registro.`);
}

async function carregarFrequenciasExistentes(idAtividade) {
    const listaFrequencia = document.getElementById('listaFrequencia');

    // Limpa a lista antes de carregar
    listaFrequencia.innerHTML = '<li>Carregando registros anteriores...</li>';

    try {
        const url = `${API_URL_FREQ}/atividade/${idAtividade}`;
        const resp = await fetch(url);
        const frequencias = await resp.json();

        if (!resp.ok) {
            listaFrequencia.innerHTML = '<li>Erro ao carregar registros.</li>';
            console.error("Erro na API ao carregar frequências:", frequencias);
            return;
        }

        if (frequencias.length === 0) {
            listaFrequencia.innerHTML = '<li>Nenhuma frequência registrada para esta atividade.</li>';
            return;
        }

        listaFrequencia.innerHTML = ''; // Limpa o loader

        frequencias.forEach(f => {
            const nome = f.membro?.usuario?.nome || `ID Membro: ${f.membro?.id}`;
            const idMembro = f.membro?.id;

            listaFrequencia.innerHTML += `<li class="membro-presente">✅ <strong>${nome}</strong> (ID: ${idMembro}) - Registrado</li>`;
        });

    } catch (error) {
        console.error("Erro de rede ao carregar frequências:", error);
        listaFrequencia.innerHTML = '<li>Erro de rede ao carregar dados.</li>';
    }
}


// ------------------------------------------
// 4. LANÇAR FREQUÊNCIA (FORMULÁRIO DE SUBMISSÃO)
// ------------------------------------------

document.getElementById('lancamentoForm').addEventListener('submit', async (ev) => {
    ev.preventDefault();

    const idAtividade = atividadeSelecionadaId;
    const idMembro = membroSelecionado.id;
    const infoDiv = document.getElementById('membroInfo');
    const listaFrequencia = document.getElementById('listaFrequencia');
    const nomeMembro = membroSelecionado.nome;

    infoDiv.innerHTML = 'Registrando...';

    if (!idAtividade || !idMembro) {
        infoDiv.innerHTML = '<p class="error">Erro interno: Selecione uma atividade e um membro.</p>';
        return;
    }

    try {
        // Lança a Frequência (POST)
        const urlLancamento = `${API_URL_FREQ}?idAtividade=${idAtividade}&idMembro=${idMembro}`;
        const respLancamento = await fetch(urlLancamento, { method: 'POST' });

        if (respLancamento.ok) {
            infoDiv.innerHTML = `<p class="success">✅ Presença de <strong>${nomeMembro}</strong> registrada!</p>`;

            // Adiciona à lista de registrados
            listaFrequencia.innerHTML += `<li class="membro-presente"><strong>${nomeMembro}</strong> (ID: ${idMembro})</li>`;

            // Limpa o estado e a interface para o próximo lançamento
            membroSelecionado = { id: null, nome: null };
            document.getElementById('lancamentoForm').style.display = 'none';
            document.getElementById('membroNomeConfirmacao').textContent = 'N/A';

            // --- CORREÇÃO AQUI: RECARREGAR A LISTA COMPLETA ---

            // 1. Limpa o input de busca (embora não esteja sendo usado, por precaução)
            document.getElementById('buscaNomeMembro').value = '';

            // 2. Chama a função que lista TODOS os membros novamente.
            // Isso irá sobrescrever a div 'membrosResultados' com a lista completa.
            buscarMembros();

            // ----------------------------------------------------

        } else {
            const lancamentoData = await respLancamento.json().catch(() => ({}));
            infoDiv.innerHTML = `<p class="error">Falha no registro: ${lancamentoData.mensagem || 'Erro desconhecido. Verifique o console.'}</p>`;
        }

    } catch (error) {
        infoDiv.innerHTML = `<p class="error">Erro de rede: ${error.message}</p>`;
    }
});