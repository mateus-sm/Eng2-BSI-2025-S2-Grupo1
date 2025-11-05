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
    logotipogrande: document.getElementById('logoGrande'),
    logotipopequeno: document.getElementById('logoPequeno')
};

const regexRegras = {
    cnpj: /^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/,
    telefone: /^\(\d{2}\)\d{5}-\d{4}$/,
    cep: /^\d{5}-\d{3}$/,
    uf: /^[A-Z]{2}$/, 
    email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, 
    site: /^https?:\/\/.+\..+$/
};

const erros = {
    cnpj: 'Formato de CNPJ inválido. Ex: 00.000.000/0000-00.',
    telefone: 'Formato de telefone inválido. Ex: (00)00000-0000.',
    cep: 'Formato de CEP inválido. Ex: 00000-000.',
    uf: 'UF inválida. Use 2 letras maiúsculas (ex: SP).',
    email: 'Formato de e-mail inválido.',
    site: 'URL do site inválida. Deve começar com http:// ou https://.'
};

const mensagemErro = 'Esse campo é obrigatório.';

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

    if (value === '') {
        exibirErro(elemento, mensagemErro);
        return false;
    }

    if (regexRegras[index]) {
        const regra = regexRegras[index];
        if (!regra.test(value)) {
            exibirErro(elemento, erros[index]);
            return false;
        }
    }

    retirarErro(elemento);
    return true;
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
    if (index === 'id')
        return;

    elemento.addEventListener('blur', () => {
        validarCampo(elemento, index);
    });
});

document.getElementById('formParametrizacao').addEventListener('submit', async (event) => {
    event.preventDefault(); 

    if (validarForm()) {
        const dados = {}
        Object.entries(inputs).forEach(([index, elemento]) => {
            if (index !== 'id') { 
                dados[index] = elemento.value;
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
                const erroBody = await response.text(); 
                alert(`Falha ao salvar. Servidor respondeu com status ${response.status}.`);
            }

        } catch (error) {
            alert('Erro de conexão. Não foi possível enviar os dados.');
        }

    } else 
        alert('Formulário inválido. Corrija os erros.');
    
});