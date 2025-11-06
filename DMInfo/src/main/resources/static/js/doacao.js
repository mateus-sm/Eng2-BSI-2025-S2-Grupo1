document.addEventListener('DOMContentLoaded', () => {

    const doacaoApiUrl = '/apis/doacao';
    const doadorApiUrl = '/apis/doador';

    const form = document.getElementById('formDoacao');
    const formTitulo = document.getElementById('formTitulo');

    const hiddenId = document.getElementById('id_doacao_hidden');
    const btnCancelarEdicao = document.getElementById('btnCancelarEdicao');
    const btnSalvar = document.getElementById('btnSalvar');

    const selectDoador = document.getElementById('id_doador');
    const inputAdminId = document.getElementById('id_admin');
    const inputObservacao = document.getElementById('observacao');

    const selectTipoDoacao = document.getElementById('tipo_doacao');

    const grupoMonetaria = document.getElementById('grupo_monetaria');
    const inputValor = document.getElementById('valor');

    const grupoItem = document.getElementById('grupo_item');
    const inputItemDescricao = document.getElementById('item_descricao');
    const inputItemQuantidade = document.getElementById('item_quantidade');

    const tabelaBody = document.getElementById('tabelaDoacoes');

    let descricaoItemEdicao = '';

    function formatarMoedaManual(e) {
        const input = e.target;
        let value = input.value;
        value = value.replace(/\D/g, '');
        if (value === '') {
            input.value = '';
            return;
        }
        let numero = parseInt(value, 10) / 100;
        input.value = new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(numero);
    }

    function getValorNumericoManual(valorFormatado) {
        if (!valorFormatado) return 0.0;
        let limpo = valorFormatado.replace(/R\$\s*/g, '');
        limpo = limpo.replace(/\./g, '');
        limpo = limpo.replace(',', '.');
        const numero = parseFloat(limpo);
        return isNaN(numero) ? 0.0 : numero;
    }

    inputValor.addEventListener('input', formatarMoedaManual);

    inputItemQuantidade.addEventListener('input', (e) => {
        e.target.value = e.target.value.replace(/\D/g, '');
    });

    function atualizarVisibilidadeForm() {
        const tipo = selectTipoDoacao.value;
        if (tipo === 'monetaria') {
            grupoMonetaria.classList.remove('campo-escondido');
            grupoItem.classList.add('campo-escondido');
        } else {
            grupoMonetaria.classList.add('campo-escondido');
            grupoItem.classList.remove('campo-escondido');
        }
    }

    async function carregarSelectDoadores() {
        try {
            const response = await fetch(doadorApiUrl);
            if (!response.ok) throw new Error('Erro ao buscar doadores');
            const doadores = await response.json();
            selectDoador.innerHTML = '<option value="">Selecione um doador...</option>';
            doadores.forEach(doador => {
                const option = document.createElement('option');
                option.value = doador.id;
                option.textContent = `${doador.nome} (${doador.documento})`;
                selectDoador.appendChild(option);
            });
        } catch (error) {
            console.error('Falha ao carregar doadores:', error);
            selectDoador.innerHTML = '<option value="">Erro ao carregar doadores</option>';
        }
    }

    function formatarMoeda(valor) {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(valor);
    }
     function formatarData(dataISO) {
         if (!dataISO) return 'Data inválida';
         const data = new Date(dataISO + 'T00:00:00');
         return data.toLocaleDateString('pt-BR');
     }

    async function carregarDoacoes() {
        try {
            const response = await fetch(doacaoApiUrl);
            if (!response.ok) throw new Error('Erro ao buscar doações');
            const doacoes = await response.json();
            tabelaBody.innerHTML = '';
            if (doacoes.length === 0) {
                 tabelaBody.innerHTML = '<tr><td colspan="7" class="text-center">Nenhuma doação registrada.</td></tr>';
                 return;
            }
            doacoes.forEach(doacao => {
                const tr = document.createElement('tr');
                const obs = doacao.observacao || '';
                const eItem = obs.startsWith('[ITEM]:');
                const valorDisplay = formatarMoeda(doacao.valor);
                const valorFinal = (eItem && doacao.valor === 1.0) ? 'N/A (Item)' : valorDisplay;

                const idDoacao = doacao.id_doacao;

                tr.innerHTML = `
                    <td>${idDoacao}</td>
                    <td>${formatarData(doacao.data)}</td>
                    <td>${doacao.id_doador ? doacao.id_doador.nome : 'Doador não encontrado'}</td>
                    <td>${doacao.id_admin && doacao.id_admin.usuario ? doacao.id_admin.usuario.nome : 'Admin não encontrado'}</td>
                    <td>${valorFinal}</td>
                    <td>${obs.replace(/\n/g, '<br>')}</td>
                    <td>
                        <button type="button" class="btn btn-sm btn-warning btn-editar" data-id="${idDoacao}">Editar</button>
                        <button type="button" class="btn btn-sm btn-danger btn-excluir" data-id="${idDoacao}">Excluir</button>
                    </td>
                `;
                tabelaBody.appendChild(tr);
            });
        } catch (error) {
            console.error('Falha ao carregar doações:', error);
            tabelaBody.innerHTML = '<tr><td colspan="7" class="text-center">Falha ao carregar doações.</td></tr>';
        }
    }

    async function preencherFormularioParaEdicao(id) {
        try {
            const response = await fetch(`${doacaoApiUrl}/${id}`);
            if (!response.ok) throw new Error('Doação não encontrada.');
            const doacao = await response.json();

            hiddenId.value = doacao.id_doacao;
            formTitulo.textContent = `Editar Doação ID: ${doacao.id_doacao}`;
            btnSalvar.textContent = 'Atualizar Doação';
            btnCancelarEdicao.classList.remove('d-none');

            selectDoador.value = doacao.id_doador.id;
            inputAdminId.value = doacao.id_admin.id;
            inputObservacao.value = doacao.observacao || '';

            const obs = doacao.observacao || '';
            const eItem = obs.startsWith('[ITEM]:');

            if (eItem) {
                selectTipoDoacao.value = 'item';

                const itemMatch = obs.match(/\[ITEM\]: (.*?) \| \[QTD\]: (\d+)/);
                if (itemMatch) {
                    descricaoItemEdicao = itemMatch[1].trim();
                    inputItemDescricao.value = descricaoItemEdicao;
                    inputItemQuantidade.value = itemMatch[2];
                }

                inputValor.value = '';

            } else {
                selectTipoDoacao.value = 'monetaria';
                inputValor.value = formatarMoeda(doacao.valor).replace('R$', '').trim();
                descricaoItemEdicao = '';
                inputItemDescricao.value = '';
                inputItemQuantidade.value = '';
            }

            atualizarVisibilidadeForm();
            window.scrollTo({ top: 0, behavior: 'smooth' });

        } catch (error) {
            console.error('Erro ao buscar doação para edição:', error);
            alert('Não foi possível carregar os dados para edição.');
        }
    }

    async function excluirDoacao(id) {
        try {
            const response = await fetch(`${doacaoApiUrl}/${id}`, {
                method: 'DELETE',
            });

            if (!response.ok) {
                let errorMsg = 'Erro ao excluir doação';
                try {
                    const errorData = await response.json();
                    errorMsg = errorData.erro || errorMsg;
                } catch(e) { /* Sem corpo no erro, usa o padrão */ }
                throw new Error(errorMsg);
            }

            alert('Doação excluída com sucesso!');
            carregarDoacoes();

        } catch (error) {
            console.error('Falha ao excluir:', error);
            alert(`Não foi possível excluir a doação. ${error.message}`);
        }
    }

    function validarFormulario() {
        if (selectDoador.value === "") {
            alert('Por favor, selecione um doador.');
            selectDoador.focus();
            return false;
        }
        if (inputAdminId.value === "" || isNaN(inputAdminId.value) || parseInt(inputAdminId.value) <= 0) {
            alert('Por favor, informe um ID de Administrador válido.');
            inputAdminId.focus();
            return false;
        }
        const tipo = selectTipoDoacao.value;
        if (tipo === 'monetaria') {
            const valorNumerico = getValorNumericoManual(inputValor.value);
            if (isNaN(valorNumerico) || valorNumerico <= 0) {
                 alert('Para doação monetária, o valor deve ser positivo.');
                 inputValor.focus();
                 return false;
            }
        } else {
             if (inputItemDescricao.value.trim() === '') {
                 alert('Por favor, descreva o item doado.');
                 inputItemDescricao.focus();
                 return false;
             }
             const qtdNumerica = parseInt(inputItemQuantidade.value, 10);
             if (isNaN(qtdNumerica) || qtdNumerica <= 0) {
                 alert('A quantidade do item deve ser positiva.');
                 inputItemQuantidade.focus();
                 return false;
             }
        }
        return true;
    }

    function resetarFormulario() {
        form.reset();
        hiddenId.value = ''; // Limpa o ID em modo de edição
        formTitulo.textContent = 'Registrar Nova Doação';
        btnSalvar.textContent = 'Salvar Doação';
        btnCancelarEdicao.classList.add('d-none');

        inputValor.value = '';
        inputItemQuantidade.value = '';
        selectTipoDoacao.value = 'monetaria';
        atualizarVisibilidadeForm();
        selectDoador.focus();
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        if (!validarFormulario()) {
            return;
        }

        const isEdicao = hiddenId.value !== '';
        const idParaAtualizar = hiddenId.value;

        const tipo = selectTipoDoacao.value;
        let valorParaSalvar = 0.0;
        const obsOriginal = inputObservacao.value.trim();
        let obsParaSalvar = obsOriginal;

        if (tipo === 'monetaria') {
            valorParaSalvar = getValorNumericoManual(inputValor.value);
            if (isEdicao) {
                 obsParaSalvar = obsOriginal;
            }
        } else {
            valorParaSalvar = 1.0; // Valor simbólico
            const desc = inputItemDescricao.value.trim();
            const qtd = inputItemQuantidade.value;
            const obsItem = `[ITEM]: ${desc} | [QTD]: ${qtd}`;
            obsParaSalvar = obsOriginal === '' ? obsItem : `${obsItem}\n---\n${obsOriginal}`;
        }

        const doacao = {
            id_doacao: isEdicao ? parseInt(idParaAtualizar) : 0,
            id_doador: { id: parseInt(selectDoador.value) },
            id_admin: { id: parseInt(inputAdminId.value) },
            valor: valorParaSalvar,
            observacao: obsParaSalvar
        };

        const method = isEdicao ? 'PUT' : 'POST';
        const url = isEdicao ? `${doacaoApiUrl}/${idParaAtualizar}` : doacaoApiUrl;

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(doacao),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.erro || `Erro ao ${isEdicao ? 'atualizar' : 'salvar'} doação`);
            }

            alert(`Doação ${isEdicao ? 'atualizada' : 'salva'} com sucesso!`);
            resetarFormulario();
            carregarDoacoes();

        } catch (error) {
            console.error('Falha ao salvar/atualizar:', error);
            alert(`Não foi possível ${isEdicao ? 'atualizar' : 'salvar'} a doação. ${error.message}`);
        }
    });

    selectTipoDoacao.addEventListener('change', atualizarVisibilidadeForm);

    tabelaBody.addEventListener('click', (e) => {
        const target = e.target;
        const id = target.getAttribute('data-id');

        if(target.classList.contains('btn-editar'))
            preencherFormularioParaEdicao(id);

        if(target.classList.contains('btn-excluir')){
            if (confirm('Tem certeza que deseja excluir esta doação?'))
                excluirDoacao(id);
        }
    });

    btnCancelarEdicao.addEventListener('click', (e) => {
        e.preventDefault();
        resetarFormulario();
    });

    atualizarVisibilidadeForm();
    carregarSelectDoadores();
    carregarDoacoes();
});