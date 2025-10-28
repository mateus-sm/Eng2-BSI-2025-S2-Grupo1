function formatarDinheiro(valor) {
    if (valor === null || valor === undefined)
        return 'R$ 0,00';
    return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}
function formatarDataParaExibir(dataString) {
    if (!dataString)
        return '';
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
    if (!dataString) return '';
    const data = new Date(dataString.replace(/-/g, '/'));
    const ano = data.getFullYear();
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const dia = String(data.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
}

async function carregarAtividades() {
    const tabelaCorpo = document.getElementById('tabela-corpo');
    const hojeFormatado = getHojeFormatado();
    tabelaCorpo.innerHTML = `<tr><td colspan="10" class="status-loading">Carregando atividades...</td></tr>`;

    try {
        const response = await fetch('/finalizar-atividades');
        if (!response.ok)
            throw new Error(`Falha ao buscar dados: ${response.statusText}`);

        const atividades = await response.json();
        tabelaCorpo.innerHTML = '';

        if (atividades.length === 0) {
            tabelaCorpo.innerHTML = `<tr><td colspan="10" class="status-empty">Nenhuma atividade cadastrada.</td></tr>`;
            return;
        }

        atividades.forEach(atividade => {
            const linha = document.createElement('tr');
            const adminUsuario = (atividade.admin && atividade.admin.usuario) ? atividade.admin.usuario.usuario : 'N/A';
            const atividadeNome = atividade.atv ? atividade.atv.descricao : 'N/A';
            const dataFimFormatada = formatarDataParaInput(atividade.dtFim);

            linha.innerHTML = `
                <td>${adminUsuario}</td>
                <td>${atividadeNome}</td>
                <td>${atividade.local || ''}</td>
                <td>${atividade.horario || ''}</td>
                <td>${formatarDataParaExibir(atividade.dtIni)}</td>
                <td>
                    <input type="date" 
                           class="input-data-fim" 
                           id="data-fim-${atividade.id}" 
                           value="${dataFimFormatada}"
                           min="${hojeFormatado}">
                </td>
                <td>${formatarDinheiro(atividade.custoprevisto)}</td>
                <td>${formatarDinheiro(atividade.custoreal)}</td>
                <td>${atividade.observacoes || ''}</td>
                <td>
                    <button class="btn-salvar-linha" data-id="${atividade.id}" title="Salvar esta linha">
                        💾
                    </button>
                </td>
            `;
            tabelaCorpo.appendChild(linha);
        });

    } catch (error) {
        tabelaCorpo.innerHTML = `<tr><td colspan="10" class="status-error">${error.message}</td></tr>`;
    }
}

async function salvarDataUnica(id, botao) {
    const input = document.getElementById(`data-fim-${id}`);
    const novaData = input.value;

    botao.disabled = true;
    botao.classList.add('salvando');
    botao.textContent = '...';

    try {
        const response = await fetch(`/finalizar-atividades/${id}/data-fim`, {
            method: 'PUT',
            headers: { 'Content-Type': 'text/plain' },
            body: novaData
        });

        if (!response.ok) {
            throw new Error('Falha no backend.');
        }

        botao.classList.remove('salvando');
        botao.classList.add('sucesso');
        botao.textContent = '✅';

    } catch (error) {
        alert('Erro ao salvar a data.');
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
    carregarAtividades();
    const tabelaCorpo = document.getElementById('tabela-corpo');
    tabelaCorpo.addEventListener('click', (event) => {
        if (event.target.classList.contains('btn-salvar-linha')) {
            const botao = event.target;
            const id = botao.dataset.id;
            salvarDataUnica(id, botao);
        }
    });
});