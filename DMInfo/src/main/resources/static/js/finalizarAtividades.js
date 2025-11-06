function aplicarMascaraDinheiro(valor) {
    if (valor === null || valor === undefined || valor === '') {
        return '';
    }

    if (typeof valor === 'number') {
        valor = Math.round(valor * 100).toString();
    } else {
        valor = valor.toString().replace(/\D/g, '');
    }

    if (valor === '') {
        return '0,00';
    }

    while (valor.length < 3) {
        valor = '0' + valor;
    }

    let intPart = valor.slice(0, -2);
    let decimalPart = valor.slice(-2);

    intPart = intPart.replace(/^0+/, '') || '0';

    intPart = intPart.replace(/\B(?=(\d{3})+(?!\d))/g, ".");

    return `${intPart},${decimalPart}`;
}

function limparDinheiroParaEnvio(valorFormatado) {
    if (!valorFormatado) {
        return 0.0;
    }

    const valorLimpo = valorFormatado.toString().replace(/\./g, '').replace(',', '.').trim();

    const floatValue = parseFloat(valorLimpo);
    return isNaN(floatValue) ? 0.0 : floatValue;
}

function formatarDinheiro(valor) {
    if (valor === null || valor === undefined || isNaN(valor)) {
        return 'R$ 0,00';
    }
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

            const custoRealValorLimpo = atividade.custoreal;
            const custoRealFormatadoDisplay = formatarDinheiro(custoRealValorLimpo);
            const custoRealParaInput = aplicarMascaraDinheiro(custoRealValorLimpo);

            const observacoesText = atividade.observacoes || '';


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
                <td id="observacoes-cell-${atividade.id}" data-observacoes="${observacoesText}">
                    ${observacoesText}
                </td>
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

function toggleCustoReal(id) {
    const displaySpan = document.getElementById(`custo-real-display-${id}`);
    const inputElement = document.getElementById(`custo-real-input-${id}`);
    const editButton = document.querySelector(`.btn-editar-custo[data-id="${id}"]`);

    if (displaySpan.style.display !== 'none') {
        displaySpan.style.display = 'none';
        editButton.style.display = 'none';
        inputElement.style.display = 'inline-block';
        inputElement.focus();
    } else {

        const valorDigitado = limparDinheiroParaEnvio(inputElement.value);
        displaySpan.textContent = formatarDinheiro(valorDigitado);

        inputElement.value = aplicarMascaraDinheiro(valorDigitado);

        displaySpan.style.display = 'inline-block';
        editButton.style.display = 'inline-block';
        inputElement.style.display = 'none';
    }
}

async function salvarDataUnica(id, botao) {
    const inputDtIni = document.getElementById(`data-ini-${id}`);
    const inputDtFim = document.getElementById(`data-fim-${id}`);
    const inputCustoReal = document.getElementById(`custo-real-input-${id}`);
    const inputFinalizar = document.getElementById(`finalizar-${id}`);

    // CORREÇÃO: Lendo o valor de observação da célula de exibição
    const obsCell = document.getElementById(`observacoes-cell-${id}`);
    const observacoesValor = obsCell.textContent.trim();


    const novaDtIni = inputDtIni.value;
    const novaDtFim = inputDtFim.value;
    const novoStatus = inputFinalizar.checked;

    const novoCustoReal = limparDinheiroParaEnvio(inputCustoReal.value);

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


        const displaySpan = document.getElementById(`custo-real-display-${id}`);
        displaySpan.textContent = formatarDinheiro(novoCustoReal);

        inputCustoReal.value = aplicarMascaraDinheiro(novoCustoReal);

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

        }, 2000);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    carregarAtividades('alfabetica');

    const tabelaCorpo = document.getElementById('tabela-corpo');
    tabelaCorpo.addEventListener('click', (event) => {
        if (event.target.classList.contains('btn-salvar-linha')) {
            const botao = event.target;
            const id = botao.dataset.id;
            salvarDataUnica(id, botao);
        }
        else if (event.target.classList.contains('btn-editar-custo')) {
            const id = event.target.dataset.id;
            toggleCustoReal(id);
        }
    });

    tabelaCorpo.addEventListener('blur', (event) => {
        if (event.target.classList.contains('input-custo-real')) {
            const inputElement = event.target;
            const id = inputElement.dataset.id;

            inputElement.value = aplicarMascaraDinheiro(inputElement.value);
            toggleCustoReal(id);
        }
    }, true);
});