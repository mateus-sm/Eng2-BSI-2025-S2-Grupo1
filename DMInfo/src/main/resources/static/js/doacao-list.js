document.addEventListener('DOMContentLoaded', () => {

    const doacaoApiUrl = '/apis/doacao';
    const tabelaBody = document.getElementById('tabelaDoacoes');

    function formatarMoeda(valor) {
        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor);
    }
    function formatarData(dataISO) {
        if (!dataISO) return 'Data inválida';
        const data = new Date(dataISO + 'T00:00:00');
        return data.toLocaleDateString('pt-BR');
    }

    async function excluirDoacao(id) {
        if (!confirm('Tem certeza que deseja excluir esta doação?')) return;

        try {
            const response = await fetch(`${doacaoApiUrl}/${id}`, { method: 'DELETE' });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.erro || 'Erro ao excluir doação');
            }

            alert('Doação excluída com sucesso!');
            carregarDoacoes();

        } catch (error) {
            console.error('Falha ao excluir:', error);
            alert(`Não foi possível excluir a doação. ${error.message}`);
        }
    }

    async function carregarDoacoes() {
        if (!tabelaBody) return;
        tabelaBody.innerHTML = '';

        try {
            const response = await fetch(doacaoApiUrl);
            if (!response.ok) {
                 const errorText = await response.text();
                 throw new Error(`Erro de Servidor (${response.status}): ${errorText.substring(0, 50)}...`);
            }

            const doacoes = await response.json();

            if (doacoes.length === 0) {
                 tabelaBody.innerHTML = '<tr><td colspan="7" class="text-center">Nenhuma doação registrada.</td></tr>';
                 return;
            }

            for (const doacao of doacoes) {
                try {
                    const tr = document.createElement('tr');
                    const obs = doacao.observacao || '';
                    const eItem = doacao.valor <= 1.0 && obs.includes('[ITEM]:');
                    const valorDisplay = formatarMoeda(doacao.valor);
                    const valorFinal = (eItem) ? 'N/A (Itens)' : valorDisplay;

                    const idDoacao = doacao.id_doacao;

                    const doadorNome = doacao.id_doador?.nome || 'Doador Desconhecido';
                    const adminNome = doacao.id_admin?.usuario?.nome || 'Admin Desconhecido';

                    tr.innerHTML = `
                        <td>${idDoacao}</td>
                        <td>${formatarData(doacao.data)}</td>
                        <td>${doadorNome}</td>
                        <td>${adminNome}</td>
                        <td>${valorFinal}</td>
                        <td>${obs.replace(/\n/g, '<br>')}</td>
                        <td>
                            <a href="doacao-form?id=${idDoacao}" class="btn btn-sm btn-warning btn-editar me-1">Editar</a>
                            <button type="button" class="btn btn-sm btn-danger btn-excluir" data-id="${idDoacao}">Excluir</button>
                        </td>
                    `;
                    tabelaBody.appendChild(tr);

                } catch (innerError) {
                    console.error(`Erro ao processar objeto de doação ID ${doacao.id_doacao || 'Desconhecido'}:`, innerError);
                }
            }

        } catch (error) {
            console.error('Falha ao carregar dados:', error);
            tabelaBody.innerHTML = `<tr><td colspan="7" class="text-center text-danger">Falha ao carregar dados. Verifique o console. Detalhes: ${error.message}</td></tr>`;
        }
    }

    if (tabelaBody) tabelaBody.addEventListener('click', (e) => {
        const target = e.target;

        if(target.classList.contains('btn-excluir'))
            excluirDoacao(target.getAttribute('data-id'));
    });

    carregarDoacoes();
});