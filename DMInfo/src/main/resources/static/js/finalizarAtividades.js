function aplicarMascaraDinheiro(valor) {
    if (valor === null || valor === undefined || valor === '') {
        return '';
    }

    // Se o valor for um NÚMERO (vindo do backend), convertemos para string de centavos
    if (typeof valor === 'number') {
        // Multiplica por 100, arredonda e converte para string. Ex: 1.00 -> 100
        valor = Math.round(valor * 100).toString();
    } else {
        // Se for string (vindo do input), limpa para dígitos. Ex: "1.000,50" -> "100050"
        valor = valor.toString().replace(/\D/g, '');
    }

    // Se o valor estiver vazio após a limpeza, retorna 0,00
    if (valor === '') {
        return '0,00';
    }

    // Adiciona zeros à esquerda para ter pelo menos 3 dígitos (para garantir os centavos, ex: '5' vira '005')
    while (valor.length < 3) {
        valor = '0' + valor;
    }

    // Separa a parte inteira (tudo menos os últimos 2 dígitos) e os centavos
    let intPart = valor.slice(0, -2);
    let decimalPart = valor.slice(-2);

    // Remove zeros à esquerda da parte inteira, exceto se for o último dígito
    intPart = intPart.replace(/^0+/, '') || '0';

    // Adiciona separador de milhar (ponto)
    intPart = intPart.replace(/\B(?=(\d{3})+(?!\d))/g, ".");

    // Formato final: 1.000,00 (sem R$)
    return `${intPart},${decimalPart}`;
}

function limparDinheiroParaEnvio(valorFormatado) {
    if (!valorFormatado) {
        return 0.0;
    }
    // Remove separadores de milhar (pontos) e troca a vírgula por ponto decimal
    // O .trim() remove espaços em branco
    const valorLimpo = valorFormatado.toString().replace(/\./g, '').replace(',', '.').trim();

    // Retorna o float. Garante que se a string for inválida, ele retorna 0.0
    const floatValue = parseFloat(valorLimpo);
    return isNaN(floatValue) ? 0.0 : floatValue;
}

function formatarDinheiro(valor) {
    if (valor === null || valor === undefined || isNaN(valor)) {
        return 'R$ 0,00';
    }
    // Garante que o valor seja tratado como um número antes da formatação
    const numericValue = typeof valor === 'string' ? parseFloat(valor) : valor;

    return numericValue.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function formatarDataParaExibir(dataString) {
    if (!dataString) {
        return '';
    }
    const data = new Date(dataString.replace(/-/g, '/'));
    return data.toLocaleDateString('pt-BR');
}

function getHojeFormatado() {
    const hoje = new Date();
    const ano = hoje.getFullYear();
    const mes = String(hoje.getMonth() + 1).padStart(2, '0');
    const dia = String(hoje.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
}

function formatarDataParaInput(dataString) {
    if (!dataString) {
        return '';
    }
    const data = new Date(dataString.replace(/-/g, '/'));
    const ano = data.getFullYear();
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const dia = String(data.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
}

async function carregarAtividades(ordenacao = 'alfabetica') {
    const tabelaCorpo = document.getElementById('tabela-corpo');
    const hojeFormatado = getHojeFormatado();
    tabelaCorpo.innerHTML = `<tr><td colspan="10" class="status-loading">Carregando atividades...</td></tr>`;

    try {
        const response = await fetch('/apis/finalizar-atividades');
        if (!response.ok) {
            throw new Error(`Falha ao buscar dados: ${response.statusText}`);
        }

        let atividades = await response.json();
        tabelaCorpo.innerHTML = '';

        if (atividades.length === 0) {
            tabelaCorpo.innerHTML = `<tr><td colspan="10" class="status-empty">Nenhuma atividade cadastrada.</td></tr>`;
            return;
        }

        // --- Lógica de Ordenação ---
        atividades.sort((a, b) => {
            const statusA = a.status === true;
            const statusB = b.status === true;
            const nomeA = a.atv ? a.atv.descricao.toLowerCase() : '';
            const nomeB = b.atv ? b.atv.descricao.toLowerCase() : '';

            if (ordenacao === 'status_aberta') {
                if (statusA !== statusB) {
                    return statusA ? 1 : -1;
                }
            } else if (ordenacao === 'status_finalizada') {
                if (statusA !== statusB) {
                    return statusA ? -1 : 1;
                }
            }

            if (nomeA < nomeB) return -1;
            if (nomeA > nomeB) return 1;
            return 0;
        });
        // --- Fim da Lógica de Ordenação ---


        atividades.forEach(atividade => {
            const linha = document.createElement('tr');

            if (atividade.status === true) {
                linha.classList.add('status-finalizada');
            } else {
                linha.classList.add('status-aberta');
            }

            let adminUsuario = 'N/A';
            if (atividade.admin && atividade.admin.usuario) {
                adminUsuario = atividade.admin.usuario.login;
            }

            let atividadeNome = 'N/A';
            if (atividade.atv) {
                atividadeNome = atividade.atv.descricao;
            }

            const dataIniFormatada = formatarDataParaInput(atividade.dtIni);
            const dataFimFormatada = formatarDataParaInput(atividade.dtFim);
            const finalizadaChecked = atividade.status === true ? 'checked' : '';

            // Prepara o valor para o input (sem formatação monetária, apenas número com vírgula)
            const custoRealValorLimpo = atividade.custoreal;
            const custoRealFormatadoDisplay = formatarDinheiro(custoRealValorLimpo);
            const custoRealParaInput = aplicarMascaraDinheiro(custoRealValorLimpo);


            linha.innerHTML = `
                <td>${adminUsuario}</td>
                <td>${atividadeNome}</td>
                <td>${atividade.local || ''}</td>
                <td>${atividade.horario || ''}</td>
                <td>
                    <input type="date" 
                           class="input-data-ini" 
                           id="data-ini-${atividade.id}" 
                           value="${dataIniFormatada}">
                </td>
                <td>
                    <input type="date" 
                           class="input-data-fim" 
                           id="data-fim-${atividade.id}" 
                           value="${dataFimFormatada}"
                           min="${dataIniFormatada}">
                </td>
                <td>${formatarDinheiro(atividade.custoprevisto)}</td>
                <td id="custo-real-cell-${atividade.id}">
                    <div class="custo-real-cell-container">
                        <span class="custo-real-display" id="custo-real-display-${atividade.id}">
                            ${custoRealFormatadoDisplay}
                        </span>
                        <input type="text"
                               class="input-custo-real"
                               id="custo-real-input-${atividade.id}"
                               data-id="${atividade.id}"
                               value="${custoRealParaInput}"
                               style="display: none;">
                        <span class="btn-editar-custo" data-id="${atividade.id}">
                            &#9998;
                        </span>
                    </div>
                </td>
                <td>${atividade.observacoes || ''}</td>
                <td>
                    <div class="acao-container"> 
                        <label class="checkbox-container">
                            <input type="checkbox" id="finalizar-${atividade.id}" ${finalizadaChecked} class="input-finalizar">
                            Finalizar
                        </label>
                        <button class="btn-salvar-linha" data-id="${atividade.id}" title="Salvar esta linha">
                            💾
                        </button>
                    </div>
                </td>
            `;
            tabelaCorpo.appendChild(linha);
        });

    } catch (error) {
        tabelaCorpo.innerHTML = `<tr><td colspan="10" class="status-error">${error.message}</td></tr>`;
    }
}

// --- Lógica de Alternância de Edição de Custo Real ---
function toggleCustoReal(id) {
    const displaySpan = document.getElementById(`custo-real-display-${id}`);
    const inputElement = document.getElementById(`custo-real-input-${id}`);
    const editButton = document.querySelector(`.btn-editar-custo[data-id="${id}"]`);

    if (displaySpan.style.display !== 'none') {
        // Entra no modo de edição
        displaySpan.style.display = 'none';
        editButton.style.display = 'none';
        inputElement.style.display = 'inline-block';
        inputElement.focus();
    } else {
        // Sai do modo de edição (Acontece após o blur, antes do salvamento)

        // 1. Aplica a formatação FINAL no display (usando o valor atual do input)
        const valorDigitado = limparDinheiroParaEnvio(inputElement.value);
        displaySpan.textContent = formatarDinheiro(valorDigitado);

        // 2. Garante que o input escondido mantenha a máscara BRL
        inputElement.value = aplicarMascaraDinheiro(valorDigitado);

        // Volta para o modo de exibição
        displaySpan.style.display = 'inline-block';
        editButton.style.display = 'inline-block';
        inputElement.style.display = 'none';
    }
}
// --- FIM DA LÓGICA DE ALTERNÂNCIA ---

async function salvarDataUnica(id, botao) {
    const inputDtIni = document.getElementById(`data-ini-${id}`);
    const inputDtFim = document.getElementById(`data-fim-${id}`);
    const inputCustoReal = document.getElementById(`custo-real-input-${id}`);
    const inputFinalizar = document.getElementById(`finalizar-${id}`);

    const novaDtIni = inputDtIni.value;
    const novaDtFim = inputDtFim.value;
    const novoStatus = inputFinalizar.checked;

    // Limpando o valor do Custo Real do INPUT (garantindo que o valor salvo seja o que está no input mascarado)
    const novoCustoReal = limparDinheiroParaEnvio(inputCustoReal.value);

    // Assumindo que Observações não é editável no momento
    const observacoesValor = '';

    botao.disabled = true;
    botao.classList.add('salvando');
    botao.textContent = '...';

    try {
        const dadosParaEnvio = {
            id: id,
            dtIni: novaDtIni,
            dtFim: novaDtFim,
            custoreal: novoCustoReal,
            observacoes: observacoesValor,
            status: novoStatus
        };

        const response = await fetch(`/apis/finalizar-atividades/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosParaEnvio)
        });

        if (!response.ok) {
            const errorMessage = await response.text();
            throw new Error(`Falha no backend: ${errorMessage}`);
        }

        // --- CORREÇÃO DE VALOR E OTIMIZAÇÃO DE PISCAMENTO ---

        // 1. Atualiza o valor exibido no span do Custo Real com a formatação correta (R$ X,XX)
        const displaySpan = document.getElementById(`custo-real-display-${id}`);
        displaySpan.textContent = formatarDinheiro(novoCustoReal);

        // 2. Garante que o input oculto tenha o valor mascarado (X.XXX,XX) para a próxima edição
        inputCustoReal.value = aplicarMascaraDinheiro(novoCustoReal);

        // Se o salvamento for sucesso, atualiza a classe da linha
        const linha = botao.closest('tr');
        if (novoStatus) {
            linha.classList.remove('status-aberta');
            linha.classList.add('status-finalizada');
        } else {
            linha.classList.remove('status-finalizada');
            linha.classList.add('status-aberta');
        }

        botao.classList.remove('salvando');
        botao.classList.add('sucesso');
        botao.textContent = '✅';

    } catch (error) {
        alert(`Erro ao salvar: ${error.message}`);
        botao.classList.remove('salvando');
        botao.textContent = '❌';
    } finally {
        setTimeout(() => {
            botao.disabled = false;
            botao.classList.remove('sucesso');
            botao.textContent = '💾';

            // REMOVIDA: A recarga completa da tabela foi removida para evitar o "piscamento".
            // O valor é atualizado localmente acima.

        }, 2000);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    // Carrega a tabela com o padrão 'alfabetica'
    carregarAtividades('alfabetica');

    const tabelaCorpo = document.getElementById('tabela-corpo');
    tabelaCorpo.addEventListener('click', (event) => {
        // 1. Botão Salvar
        if (event.target.classList.contains('btn-salvar-linha')) {
            const botao = event.target;
            const id = botao.dataset.id;
            salvarDataUnica(id, botao);
        }
        // 2. Botão de Edição do Custo Real
        else if (event.target.classList.contains('btn-editar-custo')) {
            const id = event.target.dataset.id;
            toggleCustoReal(id);
        }
    });

    // 3. Aplicar máscara e sair do modo de edição no BLUR do Custo Real
    tabelaCorpo.addEventListener('blur', (event) => {
        if (event.target.classList.contains('input-custo-real')) {
            const inputElement = event.target;
            const id = inputElement.dataset.id;

            // Aplica a máscara e sai do modo de edição (o toggleCustoReal faz isso)
            inputElement.value = aplicarMascaraDinheiro(inputElement.value);
            toggleCustoReal(id);
        }
    }, true); // Use 'true' para captura para pegar o evento de blur corretamente
});