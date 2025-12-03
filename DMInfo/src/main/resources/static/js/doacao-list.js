document.addEventListener('DOMContentLoaded', () => {

    const doacaoApiUrl = '/apis/doacao';
    const tabelaBody = document.getElementById('tabelaDoacoes');

    // --- FUNÇÕES VISUAIS (TOAST E MODAL) ---

    function mostrarNotificacao(mensagem, tipo = 'sucesso') {
        const toastEl = document.getElementById('toastNotificacao');
        if (!toastEl) return;
        const toastMsg = document.getElementById('toastMessage');
        const toastIcon = document.getElementById('toastIcon');

        toastEl.classList.remove('text-bg-success', 'text-bg-danger', 'text-bg-warning');
        if (tipo === 'sucesso') {
            toastEl.classList.add('text-bg-success');
            toastIcon.className = 'fas fa-check-circle';
        } else if (tipo === 'erro') {
            toastEl.classList.add('text-bg-danger');
            toastIcon.className = 'fas fa-times-circle';
        }

        toastMsg.textContent = mensagem;
        const toast = new bootstrap.Toast(toastEl, { delay: 4000 });
        toast.show();
    }

    function confirmarExclusao(id, callbackExclusao) {
        const modalEl = document.getElementById('modalConfirmacao');
        if (!modalEl) return;
        const modal = new bootstrap.Modal(modalEl);

        const btnConfirmar = document.getElementById('btnConfirmarAcao');
        const modalTitulo = document.getElementById('modalTitulo');
        const modalTexto = document.getElementById('modalTexto');

        modalTitulo.textContent = 'Excluir Doação';
        modalTexto.textContent = `Tem certeza que deseja excluir a doação ID ${id}? Esta ação é irreversível.`;
        btnConfirmar.className = 'btn btn-danger px-4';
        btnConfirmar.textContent = 'Sim, Excluir';

        // Clone para remover listeners anteriores
        const novoBtn = btnConfirmar.cloneNode(true);
        btnConfirmar.parentNode.replaceChild(novoBtn, btnConfirmar);

        novoBtn.addEventListener('click', () => {
            callbackExclusao();
            modal.hide();
        });

        modal.show();
    }
    // ----------------------------------------

    function formatarMoeda(valor) {
        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor);
    }
    function formatarData(dataISO) {
        if (!dataISO) return '-';
        const data = new Date(dataISO + 'T00:00:00');
        return data.toLocaleDateString('pt-BR');
    }

    async function processarExclusao(id) {
        try {
            const response = await fetch(`${doacaoApiUrl}/${id}`, { method: 'DELETE' });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.erro || 'Erro ao excluir doação');
            }

            mostrarNotificacao('Doação excluída com sucesso!', 'sucesso');
            carregarDoacoes();

        } catch (error) {
            console.error('Falha ao excluir:', error);
            mostrarNotificacao(`Erro: ${error.message}`, 'erro');
        }
    }

    async function carregarDoacoes() {
        if (!tabelaBody) return;
        tabelaBody.innerHTML = '';

        try {
            const response = await fetch(doacaoApiUrl);
            if (!response.ok) throw new Error(`Erro de Servidor: ${response.status}`);

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
                        <td>${obs.replace(/\n/g, '<br>').substring(0, 50)}${obs.length > 50 ? '...' : ''}</td>
                        <td>
                            <div class="d-flex justify-content-center gap-1">
                                <a href="doacao-form?id=${idDoacao}" class="btn btn-sm btn-warning btn-editar">
                                    <i class="fas fa-edit"></i>
                                </a>
                                <button type="button" class="btn btn-sm btn-danger btn-excluir" data-id="${idDoacao}">
                                    <i class="fas fa-trash-alt"></i>
                                </button>
                            </div>
                        </td>
                    `;
                    tabelaBody.appendChild(tr);

                } catch (innerError) {
                    console.error(innerError);
                }
            }

        } catch (error) {
            console.error(error);
            mostrarNotificacao('Falha ao carregar a lista de doações.', 'erro');
        }
    }

    if (tabelaBody) tabelaBody.addEventListener('click', (e) => {
        const target = e.target.closest('button');
        if (!target) return;

        if(target.classList.contains('btn-excluir')) {
            const id = target.getAttribute('data-id');
            // Chama o Modal customizado em vez do confirm
            confirmarExclusao(id, () => processarExclusao(id));
        }
    });

    carregarDoacoes();
});