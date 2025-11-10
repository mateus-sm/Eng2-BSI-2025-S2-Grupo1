package com.example.dminfo.controller;

import com.example.dminfo.model.Usuario;
import com.example.dminfo.dao.UsuarioDAO; // Importe o DAO
import com.example.dminfo.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern; // Para validar email

@Service
public class UsuarioController {

    // --- CORREÇÃO 1: Injetar APENAS O DAO ---
    @Autowired
    private UsuarioDAO usuarioDAO;

    // --- CORREÇÃO 2: REMOVER o usuarioModel ---
    // private Usuario usuarioModel; // REMOVA ISSO

    /**
     * Tenta logar um usuário.
     */
    public Map<String, Object> logar(String login, String senha) {
        Map<String, Object> response = new HashMap<>();

        Usuario usuario = usuarioDAO.getUsuario(login);

        if (usuario != null && usuario.getSenha().equals(senha)) {
            response.put("isLogado", true);
            response.put("token", Token.gerarToken(login));
        } else {
            response.put("isLogado", false);
            response.put("token", "");
        }
        return response;
    }

    public List<Usuario> listar() {
        return usuarioDAO.get("");
    }

    public Usuario getById(int id) {
        return usuarioDAO.get(id);
    }

    public Usuario update(int id, Usuario usuarioDetails) {
        // 1. Busca o usuário original
        Usuario usuarioExistente = usuarioDAO.get(id);
        if (usuarioExistente == null) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        // 2. Validações (ex: login não pode mudar para um que já existe)
        if (!usuarioDetails.getLogin().equals(usuarioExistente.getLogin()) && usuarioDAO.getUsuario(usuarioDetails.getLogin()) != null) {
            throw new RuntimeException("Este 'usuário' (login) já pertence a outra conta.");
        }
        // (Adicione mais validações de CPF/Email se necessário)

        // 3. Atualiza os campos do usuário existente com os novos detalhes
        usuarioExistente.setNome(usuarioDetails.getNome());
        usuarioExistente.setSenha(usuarioDetails.getSenha()); // Cuidado: idealmente a senha não deveria vir assim
        usuarioExistente.setLogin(usuarioDetails.getLogin());
        usuarioExistente.setTelefone(usuarioDetails.getTelefone());
        usuarioExistente.setEmail(usuarioDetails.getEmail());
        usuarioExistente.setRua(usuarioDetails.getRua());
        usuarioExistente.setCidade(usuarioDetails.getCidade());
        usuarioExistente.setBairro(usuarioDetails.getBairro());
        usuarioExistente.setCep(usuarioDetails.getCep());
        usuarioExistente.setUf(usuarioDetails.getUf());
        usuarioExistente.setCpf(usuarioDetails.getCpf());
        usuarioExistente.setDtnasc(usuarioDetails.getDtnasc());
        // Não alteramos dtini e dtfim

        // 4. Salva as alterações
        if (usuarioDAO.alterar(usuarioExistente)) {
            return usuarioExistente;
        }
        throw new RuntimeException("Erro ao atualizar usuário no banco de dados.");
    }

    public boolean excluir(int id) {
        return usuarioDAO.excluir(id);
    }

    /**
     * Valida os dados do usuário.
     * Esta lógica deve ficar no Service/Controller, usando o DAO para checagens.
     */
    public Map<String, String> validar(Usuario usuario) {
        Map<String, String> errors = new HashMap<>();

        // Validação de Nome
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            errors.put("nome_error", "Nome é obrigatório.");
        }

        // Validação da Senha (Exemplo)
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            errors.put("senha_error", "Senha é obrigatória.");
        }

        // Validação de CPF (Formato e Existência)
        // ATENÇÃO: Assumindo que o CPF vem LIMPO (só números) do front-end
        if (usuario.getCpf() == null || !usuario.getCpf().matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            errors.put("cpf_error", "Formato de CPF inválido (esperado 11 dígitos).");
        } else if (usuarioDAO.getUsuarioByCpf(usuario.getCpf()) != null) {
            errors.put("cpf_error", "Este CPF já está em uso.");
        }

        // Validação de Email (Formato e Existência)
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (usuario.getEmail() == null || !Pattern.matches(emailRegex, usuario.getEmail())) {
            errors.put("email_error", "Formato de E-mail inválido.");
        } else if (usuarioDAO.getUsuarioByEmail(usuario.getEmail()) != null) {
            errors.put("email_error", "Este E-mail já está em uso.");
        }

        // Validação de Login (Existência)
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            errors.put("usuario_error", "Login é obrigatório.");
        } else if (usuarioDAO.getUsuario(usuario.getLogin()) != null) {
            errors.put("usuario_error", "Este nome de usuário já está em uso.");
        }

        // Validação da Data de Nascimento
        if (usuario.getDtnasc() == null) {
            errors.put("dtnasc_error", "A data de nascimento é obrigatória.");
        }

        return errors;
    }

    /**
     * --- CORREÇÃO 3: MÉTODO SALVAR CORRIGIDO ---
     * A lógica de 'salvar' pertence ao Controller/Service.
     * Ele usa o DAO para gravar no banco.
     */
    public Usuario salvar(Usuario usuario) {
        // Regra de negócio: setar a data de início no momento do cadastro
        usuario.setDtini(LocalDate.now());

        // Chama o DAO para gravar no banco
        return usuarioDAO.gravar(usuario);
    }
}