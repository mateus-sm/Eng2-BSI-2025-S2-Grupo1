document.addEventListener('DOMContentLoaded', () => {

    const inputs = {
        id: document.getElementById('id'),
        nomeFantasia: document.getElementById('nomeFantasia'),
        razaoSocial: document.getElementById('razaoSocial'),
        descricao: document.getElementById('descricao'),
        rua: document.getElementById('rua'),
        bairro: document.getElementById('bairro'),
        cidade: document.getElementById('cidade'),
        cep: document.getElementById('cep'),
        uf: document.getElementById('uf'),
        telefone: document.getElementById('telefone'),
        email: document.getElementById('email'),
        site: document.getElementById('site'),
        cnpj: document.getElementById('cnpj'),
        logoGrande: document.getElementById('logoGrande'),
        logoPequeno: document.getElementById('logoPequeno')
    };

    const regexRegras = {
        cnpj: /^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/,
        telefone: /^\(\d{2}\)\d{4,5}-\d{4}$/,
        cep: /^\d{5}-\d{3}$/,
        uf: /^[A-Z]{2}$/,
        email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        site: /^https?:\/\/.+\..+$/
    };

    const erros = {
        cnpj: 'CNPJ inválido',
        telefone: 'Formato de telefone inválido. Ex: (00)00000-0000.',
        cep: 'Formato de CEP inválido. Ex: 00000-000.',
        uf: 'UF inválida. Use 2 letras maiúsculas (ex: SP).',
        email: 'Formato de e-mail inválido.',
        site: 'URL do site inválida. Deve começar com http:// ou https://.'
    };

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
            if (pos < 2) {
                pos = 9;
            }
        }

        let resultado = soma % 11;

        if (resultado < 2) {
            resultado = 0;
        } else {
            resultado = 11 - resultado;
        }

        if (resultado != digitos.charAt(0)) {
            return false;
        }

        tamanho = tamanho + 1;
        numeros = cnpj.substring(0, tamanho);
        soma = 0;
        pos = tamanho - 7;

        for (let i = tamanho; i >= 1; i--) {
            soma += numeros.charAt(tamanho - i) * pos;
            pos--;
            if (pos < 2) {
                pos = 9;
            }
        }

        resultado = soma % 11;

        if (resultado < 2) {
            resultado = 0;
        } else {
            resultado = 11 - resultado;
        }

        return resultado == digitos.charAt(1);
    }

    const mensagemErro = 'Esse campo é obrigatório.';

    const camposObrigatorios = [
        'razaoSocial',
        'rua',
        'cidade',
        'telefone',
        'logoPequeno'
    ];

    const imaskInstances = {};

    const maskOptions = {
        cnpj: {
            mask: '00.000.000/0000-00'
        },
        cep: {
            mask: '00000-000'
        }
    };

    try {
        if (inputs.cnpj) imaskInstances.cnpj = IMask(inputs.cnpj, maskOptions.cnpj);
        if (inputs.cep) imaskInstances.cep = IMask(inputs.cep, maskOptions.cep);
    } catch (e) {
        console.error("Erro ao aplicar máscaras (IMask.js). Verifique se a biblioteca foi carregada.", e);
    }


    function aplicarMascaraTelefone(value) {
        value = value.replace(/\D/g, '');

        if (value.length > 10) {
            value = value.replace(/^(\d{2})(\d{5})(\d{4}).*/, '($1)$2-$3');
        } else if (value.length > 6) {
            value = value.replace(/^(\d{2})(\d{4})(\d{4}).*/, '($1)$2-$3');
        } else if (value.length > 2) {
            value = value.replace(/^(\d{2})(\d+)/, '($1)$2');
        } else if (value.length > 0) {
            value = value.replace(/^(\d*)/, '($1');
        }

        return value;
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
        if (elementoIrmao && elementoIrmao.classList.contains('error-message'))
            elementoIrmao.remove();
    }

    function limparTodosErros() {
        const erros = document.querySelectorAll('.error-message');
        erros.forEach(error => error.remove());
    }

    function validarCampo(elemento, index) {
        if(index === 'id')
            return true;

        const value = elemento.value.trim();

        if (camposObrigatorios.includes(index) && value === '') {
            exibirErro(elemento, mensagemErro);
            return false;
        }

        if (value !== '') {
            if (regexRegras[index]) {
                const regra = regexRegras[index];
                if (!regra.test(value)) {
                    exibirErro(elemento, erros[index]);
                    return false;
                }
            }

            if (index === 'cnpj') {
                const valorLimpo = value.replace(/\D/g, '');
                if (!validarCNPJ(valorLimpo)) {
                    exibirErro(elemento, erros.cnpj); J
                    return false;
                }
            }
        }

        retirarErro(elemento);
        return true;
    }

    function validarTodosObrigatorios() {
        camposObrigatorios.forEach(index => {
            const elemento = inputs[index];
            if (elemento) {
                validarCampo(elemento, index);
            }
        });
    }

    function validarForm() {
        limparTodosErros();
        let valido = true;

        Object.entries(inputs).forEach(([index, elemento]) => {
            if (!validarCampo(elemento, index)) {
                valido = false;
            }
        });
        return valido;
    }

    Object.entries(inputs).forEach(([index, elemento]) => {
        if (index === 'id') {
            return;
        }

        if (index === 'telefone') {
            elemento.addEventListener('input', (event) => {
                const start = elemento.selectionStart;
                const end = elemento.selectionEnd;
                const oldValue = elemento.value;
                const newValue = aplicarMascaraTelefone(oldValue);

                elemento.value = newValue;

                const diff = newValue.length - oldValue.length;
                elemento.setSelectionRange(start + diff, end + diff);
            });
        }

        elemento.addEventListener('blur', () => {
            const value = elemento.value.trim();

            if (camposObrigatorios.includes(index)) {
                if (value === '') {
                    validarTodosObrigatorios();
                } else {
                    validarCampo(elemento, index);
                }
            } else {
                validarCampo(elemento, index);
            }
        });
    });


    document.getElementById('formParametrizacao').addEventListener('submit', async (event) => {
        event.preventDefault();

        if (validarForm()) {
            const dados = {}

            Object.entries(inputs).forEach(([index, elemento]) => {
                if (index !== 'id' && elemento) {
                    const value = elemento.value.trim();

                    let valorLimpo;

                    if (value === '') {
                        valorLimpo = null;
                    } else {
                        valorLimpo = value;
                    }
                    if (valorLimpo !== null) {

                        if (imaskInstances[index]) {
                            valorLimpo = imaskInstances[index].unmaskedValue;
                        }
                        else if (index === 'telefone' || index === 'cep' || index === 'cnpj') {
                            valorLimpo = valorLimpo.replace(/\D/g, '');
                        }
                    }

                    dados[index] = valorLimpo;
                }
            });

            try {
                const response = await fetch('/apis/parametrizacao', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(dados)
                });

                if (response.status === 201 || response.ok) {
                    const salvo = await response.json();
                    alert('Parâmetros salvos com sucesso!');
                    document.getElementById('formParametrizacao').reset();
                    window.location.href = '/app/parametrizacao/exibir';

                } else {
                    const erroBody = await response.json();
                    alert(`Falha ao salvar: ${erroBody.erro || erroBody.message || 'Erro do servidor'}`);
                }

            } catch (error) {
                alert('Erro de conexão. Não foi possível enviar os dados.');
            }

        } else {
            alert('Formulário inválido. Corrija os erros.');
        }

    });
});