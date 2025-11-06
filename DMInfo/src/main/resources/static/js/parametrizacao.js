document.addEventListener('DOMContentLoaded', () => {

    const inputs = {
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

    const camposObrigatorios = [
        'razaoSocial',
        'telefone',
        'rua',
        'cidade',
        'logoPequeno'
    ];

    const regexRegras = {
        cnpj: /^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/,
        telefone: /^\(\d{2}\) \d{5}-\d{4}$/,
        cep: /^\d{5}-\d{3}$/,
        email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        site: /^https?:\/\/.+\..+$/
    };

    const erros = {
        cnpj: 'Formato de CNPJ inválido. Ex: 00.000.000/0000-00.',
        cnpjInvalido: 'O CNPJ digitado é inválido (dígitos verificadores não batem).',
        telefone: 'Formato de telefone inválido. Ex: (00) 00000-0000.',
        cep: 'Formato de CEP inválido. Ex: 00000-000.',
        email: 'Formato de e-mail inválido.',
        site: 'URL do site inválida. Deve começar com http:// ou https://.'
    };

    const mensagemErroObrigatorio = 'Este campo é obrigatório.';


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

    function validarCNPJ(cnpj) {
        cnpj = cnpj.replace(/[^\d]+/g, '');

        if (cnpj.length !== 14)
            return false;

        if (/^(\d)\1+$/.test(cnpj))
            return false;

        let tamanho = 12;
        let numeros = cnpj.substring(0, tamanho);
        let soma = 0;
        let pos = tamanho - 7;
        for (let i = tamanho; i >= 1; i--) {
            soma += parseInt(numeros.charAt(tamanho - i)) * pos--;
            if (pos < 2) pos = 9;
        }

        let resultado;
        if (soma % 11 < 2) {
            resultado = 0;
        } else {
            resultado = 11 - (soma % 11);
        }

        if (resultado !== parseInt(cnpj.charAt(12)))
            return false;

        tamanho = 13;
        numeros = cnpj.substring(0, tamanho);
        soma = 0;
        pos = tamanho - 7;
        for (let i = tamanho; i >= 1; i--) {
            soma += parseInt(numeros.charAt(tamanho - i)) * pos--;
            if (pos < 2) pos = 9;
        }

        if (soma % 11 < 2) {
            resultado = 0;
        } else {
            resultado = 11 - (soma % 11);
        }

        if (resultado !== parseInt(cnpj.charAt(13)))
            return false;

        return true;
    }


    function validarCampo(elemento, nomeDoCampo) {
        if (!elemento)
            return true;

        const value = elemento.value.trim();
        const ehObrigatorio = camposObrigatorios.includes(nomeDoCampo);

        if (ehObrigatorio && value === '') {
            exibirErro(elemento, mensagemErroObrigatorio);
            return false;
        }

        if (!ehObrigatorio && value === '') {
            retirarErro(elemento);
            return true;
        }

        if (regexRegras[nomeDoCampo]) {
            const regra = regexRegras[nomeDoCampo];
            if (!regra.test(value)) {
                exibirErro(elemento, erros[nomeDoCampo]);
                return false;
            }
        }

        if (nomeDoCampo === 'cnpj') {
            if (!validarCNPJ(value)) {
                exibirErro(elemento, erros.cnpjInvalido);
                return false;
            }
        }

        retirarErro(elemento);
        return true;
    }

    function validarForm() {
        limparTodosErros();
        let valido = true;
        Object.entries(inputs).forEach(([nomeDoCampo, elemento]) => {
            if (!validarCampo(elemento, nomeDoCampo)) {
                valido = false;
            }
        });
        return valido;
    }


    if (inputs.telefone) IMask(inputs.telefone, { mask: '(00) 00000-0000' });
    if (inputs.cnpj) IMask(inputs.cnpj, { mask: '00.000.000/0000-00' });
    if (inputs.cep) IMask(inputs.cep, { mask: '00000-000' });

    function validarApenasCamposObrigatoriosVazios() {
        camposObrigatorios.forEach(nomeDoCampo => {
            const elemento = inputs[nomeDoCampo];
            if (elemento && elemento.value.trim() === '') {
                exibirErro(elemento, mensagemErroObrigatorio);
            }
            else if (elemento) {
                const erroMsg = elemento.nextElementSibling;
                if (erroMsg && erroMsg.textContent === mensagemErroObrigatorio) {
                    retirarErro(elemento);
                }
            }
        });
    }

    Object.entries(inputs).forEach(([nomeDoCampo, elemento]) => {
        if (!elemento) return;

        const ehObrigatorio = camposObrigatorios.includes(nomeDoCampo);

        if (ehObrigatorio) {
            elemento.addEventListener('blur', () => {
                validarApenasCamposObrigatoriosVazios();
            });
        } else {
            elemento.addEventListener('blur', () => {
                if (elemento.value.trim() !== '') {
                    validarCampo(elemento, nomeDoCampo);
                } else {
                    retirarErro(elemento);
                }
            });
        }
    });

    document.getElementById('formParametrizacao').addEventListener('submit', async (event) => {
        event.preventDefault();

        if (validarForm()) {
            const dados = {}
            Object.entries(inputs).forEach(([index, elemento]) => {
                if (elemento) {
                    dados[index] = elemento.value;
                }
            });
            delete dados.id;

            try {
                const response = await fetch('/apis/parametrizacao', { //
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(dados)
                });

                if (response.status === 201 || response.ok) {
                    alert('Parâmetros salvos com sucesso!');
                    document.getElementById('formParametrizacao').reset();
                    window.location.href = '/app/parametrizacao/exibir'; //

                } else {
                    const erroBody = await response.json();
                    alert(`Falha ao salvar: ${erroBody.message || 'Erro do servidor'}`);
                }

            } catch (error) {
                alert('Erro de conexão. Não foi possível enviar os dados.');
            }

        } else {
            alert('Formulário inválido. Corrija os campos destacados.');
        }
    });
});