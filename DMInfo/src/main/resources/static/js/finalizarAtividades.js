document.addEventListener('DOMContentLoaded', async () => {

    const tabelaCorpo = document.getElementById('tabela-corpo');

    try {
        const response = await fetch('/finalizar-atividades');

        if (!response.ok) {
            throw new Error(`Falha ao buscar dados: ${response.statusText}`);
        }

        const atividades = await response.json();
        tabelaCorpo.innerHTML = '';

        if (atividades.length === 0) {
            tabelaCorpo.innerHTML = `<tr><td colspan="7" class="status-empty">Nenhuma atividade cadastrada.</td></tr>`;
            return;
        }

        atividades.forEach(atividade => {
            const linha = document.createElement('tr');

            // ==========================================================
            // CORREÇÃO AQUI
            // ==========================================================

            // 1. CORREÇÃO ADMIN: [object Object]
            // Acessamos um nível mais fundo: atividade.admin.usuario.usuario
            // (Assumindo que o nome do campo na sua classe Usuario é 'usuario')
            const adminUsuario = (atividade.admin && atividade.admin.usuario)
                ? atividade.admin.usuario.usuario
                : 'N/A';

            // 2. CORREÇÃO ATIVIDADE: undefined
            // A propriedade é 'descricao', não 'nome'
            const atividadeNome = atividade.atv ? atividade.atv.descricao : 'N/A';

            // ==========================================================

            linha.innerHTML = `
                <td>${adminUsuario}</td>
                <td>${atividadeNome}</td>
                <td>${atividade.local || ''}</td>
                <td>${atividade.horario || ''}</td>
                <td>${formatarData(atividade.dtIni)}</td>
                <td>${formatarData(atividade.dtFim)}</td>
                <td>${formatarDinheiro(atividade.custoprevisto)}</td> 
            `;

            tabelaCorpo.appendChild(linha);
        });

    } catch (error) {
        console.error('Falha ao carregar atividades:', error);
        tabelaCorpo.innerHTML = `<tr><td colspan="7" class="status-error">${error.message}</td></tr>`;
    }
});

/**
 * Formata um número para R$
 */
function formatarDinheiro(valor) {
    if (valor === null || valor === undefined) return 'R$ 0,00';
    return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

/**
 * Formata uma data (ex: 2025-10-27 => 27/10/2025)
 */
function formatarData(dataString) {
    if (!dataString) return ''; // <-- Isso faz o "Data Fim" ficar em branco (correto)
    const data = new Date(dataString.replace(/-/g, '/'));
    return data.toLocaleDateString('pt-BR');
}