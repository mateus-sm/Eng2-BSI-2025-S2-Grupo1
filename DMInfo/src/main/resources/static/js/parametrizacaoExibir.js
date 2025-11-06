document.addEventListener('DOMContentLoaded', async () => {
    const loadingDiv = document.getElementById('loading');
    const erroDiv = document.getElementById('erro');
    const dadosContainer = document.getElementById('dados-container');
    const btnEditar = document.getElementById('btnEditar');
    const btnSalvar = document.getElementById('btnSalvar');
    const btnCancelar = document.getElementById('btnCancelar');
    const btnExcluir = document.getElementById('btnExcluir');

    let dadosAtuais = {};
    let idParaExcluir = null;
    let isEditing = false;

    const campos = [
        'nomeFantasia', 'razaoSocial', 'cnpj', 'descricao', 'rua',
        'bairro', 'cidade', 'uf', 'cep', 'telefone',
        'email', 'site', 'logoGrande', 'logoPequeno'
    ];

    const elementos = {};
    campos.forEach(campo => {
        elementos[campo] = {
            display: document.getElementById(`${campo}_display`),
            input: document.getElementById(`${campo}_input`)
        };
    });

    function formatarCNPJ(cnpj) {
        if (!cnpj) return '';
        return cnpj.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5');
    }

    function formatarTelefone(telefone) {
        if (!telefone) return '';
        if (telefone.length === 11) {
            return telefone.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1)$2-$3');
        } else if (telefone.length === 10) {
            return telefone.replace(/^(\d{2})(\d{4})(\d{4})$/, '($1)$2-$3');
        }
        return telefone;
    }

    function formatarCEP(cep) {
        if (!cep) return '';
        return cep.replace(/^(\d{5})(\d{3})$/, '$1-$2');
    }

    function aplicarMascaraInput(field, value) {
        if (!value) {
            return '';
        }

        value = value.replace(/\D/g, '');

        if (field === 'cnpj') {
            if (value.length > 12) value = value.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2}).*/, '$1.$2.$3/$4-$5');
            else if (value.length > 8) value = value.replace(/^(\d{2})(\d{3})(\d{3})(\d{4}).*/, '$1.$2.$3/$4');
            else if (value.length > 5) value = value.replace(/^(\d{2})(\d{3})(\d{3}).*/, '$1.$2.$3');
            else if (value.length > 2) value = value.replace(/^(\d{2})(\d+)/, '$1.$2');
        } else if (field === 'cep') {
            if (value.length > 5) value = value.replace(/^(\d{5})(\d+)/, '$1-$2');
        } else if (field === 'telefone') {
            if (value.length > 10) {
                value = value.replace(/^(\d{2})(\d{5})(\d{4}).*/, '($1)$2-$3');
            } else if (value.length > 6) {
                value = value.replace(/^(\d{2})(\d{4})(\d{4}).*/, '($1)$2-$3');
            } else if (value.length > 2) {
                value = value.replace(/^(\d{2})(\d+)/, '($1)$2');
            } else if (value.length > 0) {
                value = value.replace(/^(\d*)/, '($1');
            }
        }
        return value;
    }

    function validarCNPJ(cnpj) {
        if (!cnpj || cnpj.length !== 14 || /^(0{14}|1{14}|2{14}|3{14}|4{14}|5{14}|6{14}|7{14}|8{14}|9{14})$/.test(cnpj)) {
            return false;
        }
        let tamanho = cnpj.length - 2;
        let numeros = cnpj.substring(0, tamanho);
        const digitos = cnpj.substring(tamanho);
        let soma = 0;
        let pos = tamanho - 7;
        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos;
            pos--;
            if (pos < 2) { pos = 9; }
        }
        let resultado = soma % 11;
        if (resultado < 2) { resultado = 0; } else { resultado = 11 - resultado; }
        if (resultado != digitos.charAt(0)) { return false; }

        tamanho = tamanho + 1;
        numeros = cnpj.substring(0, tamanho);
        soma = 0;
        pos = tamanho - 7;
        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos;
            pos--;
            if (pos < 2) { pos = 9; }
        }
        resultado = soma % 11;
        if (resultado < 2) { resultado = 0; } else { resultado = 11 - resultado; }
        return resultado == digitos.charAt(1);
    }

    const regexRegras = {
        cnpj: /^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/,
        telefone: /^\(\d{2}\)\d{4,5}-\d{4}$/,
        cep: /^\d{5}-\d{3}$/,
        uf: /^[A-Z]{2}$/,
        email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        site: /^https?:\/\/.+\..+$/
    };

    const erros = {
        cnpj: 'CNPJ inválido.',
        telefone: 'Formato de telefone inválido. Ex: (00)00000-0000.',
        cep: 'Formato de CEP inválido. Ex: 00000-000.',
        uf: 'UF inválida. Use 2 letras maiúsculas (ex: SP).',
        email: 'Formato de e-mail inválido.',
        site: 'URL do site inválida. Deve começar com http:// ou https://.'
    };

    const camposObrigatorios = [
        'razaoSocial',
        'rua',
        'cidade',
        'telefone',
        'logoPequeno'
    ];

    function validarCampo(elementoInput, index) {
        const value = elementoInput.value.trim();

        retirarErro(elementoInput);

        if (camposObrigatorios.includes(index) && value === '') {
            exibirErro(elementoInput, 'Esse campo é obrigatório.');
            return false;
        }

        if (value !== '') {
            if (regexRegras[index]) {
                const regra = regexRegras[index];
                if (!regra.test(value)) {
                    exibirErro(elementoInput, erros[index]);
                    return false;
                }
            }

            if (index === 'cnpj') {
                const valorLimpo = value.replace(/\D/g, '');
                if (!validarCNPJ(valorLimpo)) {
                    exibirErro(elementoInput, erros.cnpj);
                    return false;
                }
            }
        }

        return true;
    }

    function exibirErro(input, message) {
        retirarErro(input);
        const erroDiv = document.createElement('div');
        erroDiv.className = 'text-danger error-message';
        erroDiv.textContent = message;
        input.insertAdjacentElement('afterend', erroDiv);
    }

    function retirarErro(input) {
        const elementoIrmao = input.nextElementSibling;
        if (elementoIrmao && elementoIrmao.classList.contains('error-message')) {
            elementoIrmao.remove();
        }
    }

    function preencherDados(dados) {
        document.getElementById('id_display').textContent = dados.id || '';

        campos.forEach(campo => {
            const valor = dados[campo] || '';
            let valorFormatado = valor;

            if (campo === 'cnpj') valorFormatado = formatarCNPJ(valor);
            else if (campo === 'telefone') valorFormatado = formatarTelefone(valor);
            else if (campo === 'cep') valorFormatado = formatarCEP(valor);

            elementos[campo].display.textContent = valorFormatado;
        });
    }

    function setEditMode(editing) {
        isEditing = editing;

        campos.forEach(campo => {
            const { display, input } = elementos[campo];

            const valorDB = dadosAtuais[campo] || '';

            if (editing) {
                display.style.display = 'none';
                input.style.display = 'inline-block';

                if (campo === 'cnpj' || campo === 'cep' || campo === 'telefone') {
                    input.value = aplicarMascaraInput(campo, valorDB);
                }
                else {
                    input.value = valorDB;
                }

            } else {
                // Modo Exibição
                display.style.display = 'inline';
                input.style.display = 'none';
                retirarErro(input);
            }
        });

        if (editing) {
            btnEditar.style.display = 'none';
            btnExcluir.style.display = 'none';
            btnSalvar.style.display = 'inline-block';
            btnCancelar.style.display = 'inline-block';
        } else {
            btnEditar.style.display = 'inline-block';
            btnExcluir.style.display = 'inline-block';
            btnSalvar.style.display = 'none';
            btnCancelar.style.display = 'none';
        }
    }

    campos.forEach(campo => {
        const inputElement = elementos[campo].input;

        if (campo === 'cnpj' || campo === 'cep' || campo === 'telefone') {
            inputElement.addEventListener('input', (event) => {
                const start = inputElement.selectionStart;
                const end = inputElement.selectionEnd;
                const oldValue = inputElement.value;

                const newValue = aplicarMascaraInput(campo, oldValue);

                inputElement.value = newValue;

                const diff = newValue.length - oldValue.length;
                inputElement.setSelectionRange(start + diff, end + diff);
            });
        }

        inputElement.addEventListener('blur', () => {
            if (isEditing) {
                validarCampo(inputElement, campo);
            }
        });
    });

    btnEditar.addEventListener('click', () => {
        setEditMode(true);
    });

    btnCancelar.addEventListener('click', () => {
        // Redefine os valores de exibição com os dados originais
        preencherDados(dadosAtuais);
        setEditMode(false);
    });

    btnSalvar.addEventListener('click', async () => {
        let valido = true;
        const novosDados = { id: dadosAtuais.id };

        campos.forEach(campo => {
            const inputElement = elementos[campo].input;
            if (!validarCampo(inputElement, campo)) {
                valido = false;
            }

            let valor = inputElement.value.trim();
            let valorLimpo;

            if (valor === '') {
                valorLimpo = null;
            } else {
                valorLimpo = valor;
            }

            if (valorLimpo !== null) {
                if (campo === 'cnpj' || campo === 'cep' || campo === 'telefone') {
                    valorLimpo = valorLimpo.replace(/\D/g, '');
                }
            }

            novosDados[campo] = valorLimpo;
        });

        if (!valido) {
            alert('Formulário inválido. Corrija os erros para salvar.');
            return;
        }

        try {
            const response = await fetch(`/apis/parametrizacao/${novosDados.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(novosDados)
            });

            if (response.status === 200 || response.ok) {
                const salvo = await response.json();
                alert('Parâmetros atualizados com sucesso!');

                dadosAtuais = salvo;
                preencherDados(dadosAtuais);
                setEditMode(false);

            } else {
                const erroBody = await response.json();
                alert(`Falha ao salvar: ${erroBody.erro || erroBody.message || 'Erro do servidor'}`);
            }

        } catch (error) {
            alert('Erro de conexão ao tentar salvar.');
        }
    });

    btnExcluir.addEventListener('click', async () => {
        if (!idParaExcluir) {
            alert('Erro: ID do registro não encontrado.');
            return;
        }

        if (confirm('Tem certeza que deseja excluir?')) {
            try {
                const response = await fetch(`/apis/parametrizacao/${idParaExcluir}`, {
                    method: 'DELETE'
                });

                if (response.ok || response.status === 204) {
                    alert('Registro excluído com sucesso!');
                    window.location.href = '/app/parametrizacao';
                } else {
                    alert(`Falha ao excluir. Status ${response.status}.`);
                }
            } catch (error) {
                alert('Erro de conexão ao tentar excluir.');
            }
        }
    });

    try {
        const response = await fetch('/apis/parametrizacao');

        if (!response.ok) {
            if(response.status === 404) {
                throw new Error(`Nenhum parâmetro cadastrado.`);
            }
            throw new Error(`Erro ao buscar dados: ${response.statusText}`);
        }

        const dados = await response.json();

        if (dados && dados.id) {
            dadosAtuais = dados;
            idParaExcluir = dados.id;

            preencherDados(dadosAtuais);
            loadingDiv.style.display = 'none';
            dadosContainer.style.display = 'block';

            setEditMode(false);

        } else {
            erroDiv.textContent = 'Nenhum parâmetro cadastrado.';
            erroDiv.style.display = 'block';
            loadingDiv.style.display = 'none';
        }

    } catch (error) {
        console.error('Falha na requisição:', error);
        erroDiv.textContent = error.message;
        erroDiv.style.display = 'block';
        loadingDiv.style.display = 'none';
    }
});