document.addEventListener('DOMContentLoaded', () => {

    const form = document.getElementById('formDoador');
    const tabelaBody = document.getElementById('tabelaDoadores');
    const btnCancelar = document.getElementById('btnCancelar');
    const btnSalvar = document.getElementById('btnSalvar');
    const formTitulo = document.querySelector('.form-section h4');
    const hiddenId = document.getElementById('id');
    const inputPesquisa = document.getElementById('inputPesquisa');

    // Elementos do Form
    const inputNome = document.getElementById('nome');
    const inputDocumento = document.getElementById('documento');
    const inputTelefone = document.getElementById('telefone');
    const inputCep = document.getElementById('cep');
    const inputEmail = document.getElementById('email');
    const inputRua = document.getElementById('rua');
    const inputBairro = document.getElementById('bairro');
    const inputCidade = document.getElementById('cidade');
    const inputUf = document.getElementById('uf');
    const inputNumero = document.getElementById('numero');
    const inputContato = document.getElementById('contato');

    // Elementos do Toast
    const toastEl = document.getElementById('toastNotificacao');
    const toastMessage = document.getElementById('toastMessage');
    const toastIcon = document.getElementById('toastIcon');
    // Inicializa o Toast do Bootstrap
    const toast = new bootstrap.Toast(toastEl);

    const apiUrl = '/apis/doador';

    let listaGlobalDoadores = [];

    // --- FUNÇÃO DE NOTIFICAÇÃO (Substitui o Alert) ---
    function mostrarNotificacao(mensagem, isErro = false) {
        toastMessage.textContent = mensagem;

        if (isErro) {
            toastEl.classList.remove('text-bg-success');
            toastEl.classList.add('text-bg-danger'); // Fundo vermelho
            toastIcon.className = 'fas fa-exclamation-circle';
        } else {
            toastEl.classList.remove('text-bg-danger');
            toastEl.classList.add('text-bg-success'); // Fundo verde
            toastIcon.className = 'fas fa-check-circle';
        }

        toast.show();
    }

    // --- [NOVO] FUNÇÃO PARA ABRIR O MODAL DE EXCLUSÃO ---
    function confirmarExclusao(id, nome, callbackExclusao) {
        const modalEl = document.getElementById('modalConfirmacao');
        if (!modalEl) return;

        const modal = new bootstrap.Modal(modalEl);
        const btnConfirmar = document.getElementById('btnConfirmarAcao');
        const modalTexto = document.getElementById('modalTexto');

        // Atualiza o texto com o nome do doador
        modalTexto.innerHTML = `Tem certeza que deseja excluir o doador <strong>${nome}</strong>?<br>Esta ação será irreversível.`;

        // Clona o botão para remover eventos de cliques anteriores
        const novoBtn = btnConfirmar.cloneNode(true);
        btnConfirmar.parentNode.replaceChild(novoBtn, btnConfirmar);

        // Adiciona o evento de clique ao novo botão
        novoBtn.addEventListener('click', () => {
            callbackExclusao();
            modal.hide();
        });

        modal.show();
    }

    // --- MÁSCARAS (IMASK) ---
    if (typeof IMask !== 'undefined') {
        if(inputDocumento) {
            window.docMask = IMask(inputDocumento, {
                mask: [{ mask: '000.000.000-00', maxLength: 11 }, { mask: '00.000.000/0000-00' }]
            });
        }
        if(inputCep) window.cepMask = IMask(inputCep, { mask: '00000-000' });
        if(inputTelefone) {
            window.telMask = IMask(inputTelefone, {
                mask: [{ mask: '(00) 0000-0000' }, { mask: '(00) 00000-0000' }]
            });
        }
    }

    // --- VIACEP ---
    if(inputCep) {
        inputCep.addEventListener('blur', async () => {
            const cep = window.cepMask ? window.cepMask.unmaskedValue : inputCep.value.replace(/\D/g, '');
            if (cep.length === 8) {
                const placeholderOriginal = inputRua.placeholder;
                inputRua.placeholder = "Buscando...";
                try {
                    const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
                    const data = await response.json();
                    if (!data.erro) {
                        inputRua.value = data.logradouro;
                        inputBairro.value = data.bairro;
                        inputCidade.value = data.localidade;
                        inputUf.value = data.uf;
                        [inputRua, inputBairro, inputCidade, inputUf].forEach(el => clearError(el));
                        if(inputNumero) inputNumero.focus();
                    } else {
                        setError(inputCep, 'CEP não encontrado.');
                        inputRua.placeholder = placeholderOriginal;
                    }
                } catch (error) {
                    console.error('Erro ViaCEP:', error);
                    inputRua.placeholder = placeholderOriginal;
                }
            }
        });
    }

    // --- FORMATAÇÃO VISUAL ---
    function formatarDocumentoVisual(valor) {
        if (!valor) return '';
        const limpo = valor.replace(/\D/g, '');
        if (limpo.length === 11) return limpo.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
        else if (limpo.length === 14) return limpo.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, "$1.$2.$3/$4-$5");
        return valor;
    }

    function formatarTelefoneVisual(valor) {
        if (!valor) return '';
        const limpo = valor.replace(/\D/g, '');
        if (limpo.length === 10) return limpo.replace(/(\d{2})(\d{4})(\d{4})/, "($1) $2-$3");
        else if (limpo.length === 11) return limpo.replace(/(\d{2})(\d{5})(\d{4})/, "($1) $2-$3");
        return valor;
    }

    // --- CONTROLE DE ERRO VISUAL ---
    function setError(input, message) {
        if(!input) return;
        input.classList.add('invalidInput');
        let parent = input.parentElement;
        let errorDiv = parent.querySelector('.error-msg');
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'error-msg';
            parent.appendChild(errorDiv);
        }
        errorDiv.textContent = message;
    }

    function clearError(input) {
        if(!input) return;
        input.classList.remove('invalidInput');
        let parent = input.parentElement;
        if (parent) {
            const errorDiv = parent.querySelector('.error-msg');
            if (errorDiv) errorDiv.remove();
        }
    }

    const inputsValidaveis = [inputNome, inputDocumento, inputTelefone, inputCep, inputEmail, inputRua, inputBairro, inputCidade, inputUf];
    inputsValidaveis.forEach(input => {
        if(input) input.addEventListener('input', () => clearError(input));
    });

    // --- VALIDAÇÕES LÓGICAS ---
    function validarCPF(cpf) {
        if (!cpf || cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return false;
        let soma = 0, resto;
        for (let i = 1; i <= 9; i++) soma += parseInt(cpf.substring(i - 1, i)) * (11 - i);
        resto = (soma * 10) % 11;
        if ((resto === 10) || (resto === 11)) resto = 0;
        if (resto !== parseInt(cpf.substring(9, 10))) return false;
        soma = 0;
        for (let i = 1; i <= 10; i++) soma += parseInt(cpf.substring(i - 1, i)) * (12 - i);
        resto = (soma * 10) % 11;
        if ((resto === 10) || (resto === 11)) resto = 0;
        if (resto !== parseInt(cpf.substring(10, 11))) return false;
        return true;
    }

    function validarCNPJ(cnpj) {
        if (!cnpj || cnpj.length !== 14 || /^(\d)\1{13}$/.test(cnpj)) return false;
        let tamanho = cnpj.length - 2;
        let numeros = cnpj.substring(0, tamanho);
        let digitos = cnpj.substring(tamanho);
        let soma = 0;
        let pos = tamanho - 7;
        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos--;
            if (pos < 2) pos = 9;
        }
        let resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
        if (resultado !== parseInt(digitos.charAt(0))) return false;
        tamanho = tamanho + 1;
        numeros = cnpj.substring(0, tamanho);
        soma = 0;
        pos = tamanho - 7;
        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos--;
            if (pos < 2) pos = 9;
        }
        resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
        if (resultado !== parseInt(digitos.charAt(1))) return false;
        return true;
    }

    function validarDocumento(documento) {
        if (documento.length === 11) return validarCPF(documento);
        else if (documento.length === 14) return validarCNPJ(documento);
        return false;
    }

    function validarFormulario() {
        let isValid = true;
        const nome = inputNome.value.trim();
        const documento = window.docMask ? window.docMask.unmaskedValue : inputDocumento.value.trim().replace(/\D/g, '');
        const email = inputEmail.value.trim();
        const telefone = window.telMask ? window.telMask.unmaskedValue : inputTelefone.value.trim().replace(/\D/g, '');
        const cep = window.cepMask ? window.cepMask.unmaskedValue : inputCep.value.trim().replace(/\D/g, '');

        if (nome.length < 3) { setError(inputNome, 'Mínimo 3 caracteres'); isValid = false; }
        else clearError(inputNome);

        if (!validarDocumento(documento)) { setError(inputDocumento, 'Inválido'); isValid = false; }
        else clearError(inputDocumento);

        if (!/\S+@\S+\.\S+/.test(email)) { setError(inputEmail, 'Inválido'); isValid = false; }
        else clearError(inputEmail);

        if (telefone.length < 10) { setError(inputTelefone, 'Inválido'); isValid = false; }
        else clearError(inputTelefone);

        if (cep.length > 0) {
            if(!/^\d{8}$/.test(cep)) { setError(inputCep, 'Incompleto'); isValid = false; }
            if(inputRua.value.trim() === '') { setError(inputRua, 'Obrigatório'); isValid = false; }
            if(inputBairro.value.trim() === '') { setError(inputBairro, 'Obrigatório'); isValid = false; }
            if(inputCidade.value.trim() === '') { setError(inputCidade, 'Obrigatório'); isValid = false; }
            if(inputUf.value.trim() === '') { setError(inputUf, 'UF'); isValid = false; }
        }

        if(!isValid) {
            mostrarNotificacao('Verifique os campos em vermelho.', true);
        }

        return isValid;
    }

    // --- CRUD ---
    function renderizarTabela(lista) {
        tabelaBody.innerHTML = '';
        if (!lista || lista.length === 0) {
            tabelaBody.innerHTML = '<tr><td colspan="6" class="text-center text-muted p-4">Nenhum doador cadastrado.</td></tr>';
            return;
        }
        lista.forEach(doador => {
            const tr = document.createElement('tr');
            // [ALTERAÇÃO] Adicionado data-nome="${doador.nome}" para usar no Modal
            tr.innerHTML = `
                <td>${doador.id}</td>
                <td>${doador.nome}</td>
                <td>${formatarDocumentoVisual(doador.documento)}</td>
                <td>${doador.email}</td>
                <td>${formatarTelefoneVisual(doador.telefone)}</td>
                <td class="text-center">
                    <div class="d-flex justify-content-center gap-2">
                        <button class="btn btn-sm btn-outline-primary btn-editar" data-id="${doador.id}" title="Editar">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger btn-excluir" data-id="${doador.id}" data-nome="${doador.nome}" title="Excluir">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </div>
                </td>
            `;
            tabelaBody.appendChild(tr);
        });
    }

    async function carregarDoadores() {
        try{
            const response = await fetch(apiUrl);
            if(!response.ok) {
                if(response.status === 404) console.warn('API não conectada (404).');
                throw new Error('Erro API');
            }
            const doadores = await response.json();
            listaGlobalDoadores = doadores;
            renderizarTabela(listaGlobalDoadores);
        } catch(error){
            console.error('Falha ao carregar doadores:', error);
        }
    }

    inputPesquisa.addEventListener('input', (e) => {
        const termo = e.target.value.toLowerCase();
        const filtrados = listaGlobalDoadores.filter(d =>
            (d.nome && d.nome.toLowerCase().includes(termo)) ||
            (d.documento && d.documento.includes(termo)) ||
            (d.email && d.email.toLowerCase().includes(termo))
        );
        renderizarTabela(filtrados);
    });

    function resetarFormulario() {
        form.reset();
        hiddenId.value = '';
        if(formTitulo) formTitulo.textContent = 'Cadastro / Edição';
        btnCancelar.classList.add('d-none');
        if (window.docMask) window.docMask.value = '';
        if (window.cepMask) window.cepMask.value = '';
        if (window.telMask) window.telMask.value = '';

        form.querySelectorAll('.form-control').forEach(input => clearError(input));

        if(btnSalvar) {
            btnSalvar.disabled = false;
            btnSalvar.textContent = "Salvar Doador";
        }
        inputNome.focus();
    }

    async function preencherFormularioParaEdicao(id) {
        try {
            const response = await fetch(`${apiUrl}/${id}`);
            if(!response.ok) throw new Error('Doador não encontrado');
            const doador = await response.json();

            hiddenId.value = doador.id;
            inputNome.value = doador.nome;
            if (window.docMask) window.docMask.value = doador.documento;
            else inputDocumento.value = doador.documento;
            if (window.telMask) window.telMask.value = doador.telefone;
            else inputTelefone.value = doador.telefone;
            if (window.cepMask) window.cepMask.value = doador.cep;
            else inputCep.value = doador.cep;

            inputRua.value = doador.rua;
            inputBairro.value = doador.bairro;
            inputCidade.value = doador.cidade;
            inputUf.value = doador.uf;
            inputEmail.value = doador.email;

            if(inputContato && doador.contato) inputContato.value = doador.contato;
            if(inputNumero && doador.numero) inputNumero.value = doador.numero;

            if(formTitulo) formTitulo.textContent = 'Editando Doador';
            btnCancelar.classList.remove('d-none');
            form.querySelectorAll('.form-control').forEach(i => clearError(i));

            window.scrollTo({ top: 0, behavior: 'smooth' });

        } catch(error){
            console.error(error);
            mostrarNotificacao('Erro ao carregar doador.', true);
        }
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        if (!validarFormulario()) return;

        if(btnSalvar) {
            btnSalvar.disabled = true;
            btnSalvar.textContent = "Salvando...";
        }

        const formData = new FormData(form);
        const doador = Object.fromEntries(formData.entries());

        doador.documento = window.docMask ? window.docMask.unmaskedValue : doador.documento.replace(/\D/g, '');
        doador.telefone = window.telMask ? window.telMask.unmaskedValue : doador.telefone.replace(/\D/g, '');
        doador.cep = window.cepMask ? window.cepMask.unmaskedValue : doador.cep.replace(/\D/g, '');

        const id = hiddenId.value;
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${apiUrl}/${id}` : apiUrl;

        try{
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(doador),
            });

            if(!response.ok) {
                // Tenta ler o JSON de erro do backend
                const errorData = await response.json().catch(() => ({}));
                const errorMsg = errorData.erro || errorData.message || 'Erro ao salvar doador.';
                throw new Error(errorMsg);
            }

            mostrarNotificacao(`Doador ${id ? 'atualizado' : 'salvo'} com sucesso!`, false);
            resetarFormulario();
            carregarDoadores();

        }catch(error){
            console.error('Falha ao salvar:', error);
            mostrarNotificacao(error.message, true);

            if(btnSalvar) {
                btnSalvar.disabled = false;
                btnSalvar.textContent = "Salvar Doador";
            }
        }
    });

    // --- [ALTERAÇÃO] CLICK DO BOTÃO EXCLUIR CHAMA O MODAL ---
    tabelaBody.addEventListener('click', (e) => {
        const target = e.target.closest('button');
        if (!target) return;
        const id = target.getAttribute('data-id');

        if(target.classList.contains('btn-editar')) preencherFormularioParaEdicao(id);

        if(target.classList.contains('btn-excluir')) {
            const nomeDoador = target.getAttribute('data-nome') || 'Selecionado';
            // Chama a nova função confirmarExclusao
            confirmarExclusao(id, nomeDoador, () => excluirDoador(id));
        }
    });

    async function excluirDoador(id) {
        try{
            const response = await fetch(`${apiUrl}/${id}`, { method: 'DELETE' });
            if(!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.erro || errorData.message || 'Erro ao excluir');
            }
            mostrarNotificacao('Doador excluído com sucesso!', false);
            carregarDoadores();
        }catch(error){
            mostrarNotificacao(error.message, true);
        }
    }

    if(btnCancelar) btnCancelar.addEventListener('click', resetarFormulario);
    carregarDoadores();
});