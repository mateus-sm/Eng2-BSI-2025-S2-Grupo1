// Aguarda o DOM carregar
document.addEventListener('DOMContentLoaded', () => {

    const loginForm = document.getElementById('login-form');
    const errorMessage = document.getElementById('error-message');

    // Escuta o evento 'submit' do formulário
    loginForm.addEventListener('submit', async (event) => {
        // Impede o envio tradicional do formulário
        event.preventDefault();

        // Esconde erros antigos
        errorMessage.classList.add('d-none');

        // Pega os valores dos campos
        const usuario = document.getElementById('usuario').value;
        const senha = document.getElementById('senha').value;

        // Chama a API de login que criamos
        try {
            const response = await fetch('/apis/usuario/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    usuario: usuario, // 'usuario' é o login
                    senha: senha
                })
            });

            // Converte a resposta
            const data = await response.json();

            if (!response.ok) {
                // Se a resposta for 400 (Bad Request), mostra o erro
                errorMessage.textContent = data.erro || 'Usuário ou senha inválidos.';
                errorMessage.classList.remove('d-none');
            } else {
                // SUCESSO!

                // 1. Salva o token no localStorage
                localStorage.setItem('user_token', data.token);

                // 2. Redireciona para a página principal (ex: membros)
                window.location.href = '/app/membros';
            }

        } catch (error) {
            console.error('Erro de rede ou fetch:', error);
            errorMessage.textContent = 'Erro de conexão. Tente novamente.';
            errorMessage.classList.remove('d-none');
        }
    });
});