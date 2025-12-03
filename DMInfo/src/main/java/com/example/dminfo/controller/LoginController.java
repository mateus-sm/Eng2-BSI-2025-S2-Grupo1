package com.example.dminfo.controller;

import com.example.dminfo.model.Usuario;
import com.example.dminfo.dao.UsuarioDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
public class LoginController {

    @Autowired
    private UsuarioDAO usuarioDAO;

    private static final String ADMIN_ID_SESSION_KEY = "ADMIN_ID_SESSION";

    // === LOGIN ===
    @PostMapping("/login")
    public String processLogin(
            @RequestParam("usuario") String usuario,
            @RequestParam("senha") String senha,
            HttpSession session,
            Model model) {

        // Busca usuário no banco
        Usuario usuarioEncontrado = usuarioDAO.getUsuario(usuario);

        if (usuarioEncontrado != null && usuarioEncontrado.getSenha().equals(senha)) {
            // Login bem-sucedido
            session.setAttribute(ADMIN_ID_SESSION_KEY, usuarioEncontrado.getId());
            return "principal";
        } else {
            // Login falhou
            model.addAttribute("erro", "Usuário ou senha inválidos.");
            return "login";
        }
    }

    // === LOGOUT ===
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // === REGISTRO ===
    @PostMapping("/register")
    public String processarRegistro(
            @RequestParam("nome") String nome,
            @RequestParam("cpf") String cpf,
            @RequestParam("usuario") String usuario,
            @RequestParam("senha") String senha,
            @RequestParam("email") String email,
            @RequestParam("telefone") String telefone,
            @RequestParam("dtnasc") String dtnascStr,
            @RequestParam("cep") String cep,
            @RequestParam("rua") String rua,
            @RequestParam("bairro") String bairro,
            @RequestParam("cidade") String cidade,
            @RequestParam("uf") String uf,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Conversão da data
        LocalDate dtnasc = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Map<String, String> errors = new HashMap<>();

        try {
            if (dtnascStr != null && !dtnascStr.trim().isEmpty())
                dtnasc = LocalDate.parse(dtnascStr, formatter);
        } catch (Exception e) {
            errors.put("dtnasc_error", "Data em formato inválido. Use dd/mm/aaaa.");
        }

        // Cria o usuário
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setCpf(cpf);
        novoUsuario.setLogin(usuario);
        novoUsuario.setSenha(senha);
        novoUsuario.setEmail(email);
        novoUsuario.setTelefone(telefone);
        novoUsuario.setDtnasc(dtnasc);
        novoUsuario.setCep(cep);
        novoUsuario.setRua(rua);
        novoUsuario.setBairro(bairro);
        novoUsuario.setCidade(cidade);
        novoUsuario.setUf(uf);
        novoUsuario.setDtini(LocalDate.now()); // Data de início = hoje

        // Validação
        errors.putAll(validarRegistro(novoUsuario));

        if (!errors.isEmpty()) {
            errors.forEach(model::addAttribute);
            // Mantém os dados no formulário
            model.addAttribute("nome", nome);
            model.addAttribute("cpf", cpf);
            model.addAttribute("usuario", usuario);
            model.addAttribute("email", email);
            model.addAttribute("telefone", telefone);
            model.addAttribute("dtnasc", dtnascStr);
            model.addAttribute("cep", cep);
            model.addAttribute("rua", rua);
            model.addAttribute("bairro", bairro);
            model.addAttribute("cidade", cidade);
            model.addAttribute("uf", uf);

            return "register";
        }

        // Salva no banco
        usuarioDAO.gravar(novoUsuario);

        redirectAttributes.addFlashAttribute("sucesso", "Conta criada com sucesso! Faça o login.");
        return "redirect:/login";
    }

    private Map<String, String> validarRegistro(Usuario usuario) {
        Map<String, String> errors = new HashMap<>();

        // Validação de Nome
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            errors.put("nome_error", "Nome é obrigatório.");
        }

        // Validação da Senha
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            errors.put("senha_error", "Senha é obrigatória.");
        }

        // Validação de CPF
        if (usuario.getCpf() == null || !usuario.getCpf().matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            errors.put("cpf_error", "Formato de CPF inválido (esperado: 000.000.000-00).");
        } else if (usuarioDAO.getUsuarioByCpf(usuario.getCpf()) != null) {
            errors.put("cpf_error", "Este CPF já está em uso.");
        }

        // Validação de Email
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (usuario.getEmail() == null || !Pattern.matches(emailRegex, usuario.getEmail())) {
            errors.put("email_error", "Formato de E-mail inválido.");
        } else if (usuarioDAO.getUsuarioByEmail(usuario.getEmail()) != null) {
            errors.put("email_error", "Este E-mail já está em uso.");
        }

        // Validação de Login
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
}