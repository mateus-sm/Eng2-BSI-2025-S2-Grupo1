package com.example.dminfo.view;

import org.springframework.ui.Model;
import com.example.dminfo.controller.ParametrosController;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.dminfo.controller.UsuarioController;
import com.example.dminfo.model.Usuario;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter; // Para converter a data
import java.util.Map; // Para o login
import java.util.HashMap; // Para os erros de registro

@Controller
public class viewController {

    @Autowired
    private ParametrosController parametrosController;

    @Autowired
    private UsuarioController usuarioService;

    private static final String ADMIN_ID_SESSION_KEY = "ADMIN_ID_SESSION";

    @GetMapping("/login")
    public String paginaLogin(Model model) {
        // (model.addAttribute("sucesso", ...) é adicionado aqui pelo redirect do registro)
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam("usuario") String usuario,
            @RequestParam("senha") String senha,
            Model model) {

        Map<String, Object> response = usuarioService.logar(usuario, senha);
        boolean isLogado = (boolean) response.get("isLogado");

        if (isLogado) {
            return "redirect:/app/membros";
        } else {
            model.addAttribute("erro", "Usuário ou senha inválidos.");
            return "login";
        }
    }

    @GetMapping("/register")
    public String paginaRegistro() {
        return "register";
    }

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

        // --- Convertemos a data manualmente ---
        LocalDate dtnasc = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Map<String, String> errors = new HashMap<>(); // Criamos o mapa de erros

        try {
            if (dtnascStr != null && !dtnascStr.trim().isEmpty())
                dtnasc = LocalDate.parse(dtnascStr, formatter);
        } catch (Exception e) {
            errors.put("dtnasc_error", "Data em formato inválido. Use dd/mm/aaaa.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setCpf(cpf);
        novoUsuario.setLogin(usuario);
        novoUsuario.setSenha(senha);
        novoUsuario.setEmail(email);
        novoUsuario.setTelefone(telefone);
        novoUsuario.setDtnasc(dtnasc); // Passamos o objeto LocalDate (ou null)
        novoUsuario.setCep(cep);
        novoUsuario.setRua(rua);
        novoUsuario.setBairro(bairro);
        novoUsuario.setCidade(cidade);
        novoUsuario.setUf(uf);

        errors.putAll(usuarioService.validar(novoUsuario));

        if (!errors.isEmpty()) {
            // Se houver erros, adicionamos todos eles ao Model
            errors.forEach(model::addAttribute); // ex: model.addAttribute("cpf_error", "Este CPF...")

            model.addAttribute("nome", nome);
            model.addAttribute("cpf", cpf);
            model.addAttribute("usuario", usuario);
            model.addAttribute("email", email);
            model.addAttribute("telefone", telefone);
            model.addAttribute("dtnasc", dtnascStr); // Devolve a String da data
            model.addAttribute("cep", cep);
            model.addAttribute("rua", rua);
            model.addAttribute("bairro", bairro);
            model.addAttribute("cidade", cidade);
            model.addAttribute("uf", uf);

            return "register";
        }

        usuarioService.salvar(novoUsuario);

        redirectAttributes.addFlashAttribute("sucesso", "Conta criada com sucesso! Faça o login.");
        return "redirect:/login";
    }

    @GetMapping("app/parametrizacao")
    public String carregarPagina() {
        if (parametrosController.existeParametro()) {
            return "redirect:/app/parametrizacao/exibir";
        }
        return "parametrizacao";
    }

    @GetMapping("/app/parametrizacao/exibir")
    public String paginaExibirParametrizacao() {
        return "parametrizacaoExibir";
    }

    @GetMapping("/app/finalizar-atividades")
    public String paginaListarAtividades() {
        return "finalizarAtividades";
    }

    @GetMapping("/app/calendario")
    public String paginaCalendario() {
        return "calendario";
    }

    @GetMapping("/app/membros")
    public String paginaGerenciarMembros() {
        return "membros";
    }

    @GetMapping("/app/administradores")
    public String paginaGerenciarAdministradores() {return "administradores";}

    @GetMapping("/app/doador")
    public String paginaGerenciarDoador(Model model, HttpSession session) {
        Integer idAdminLogado = (Integer) session.getAttribute(ADMIN_ID_SESSION_KEY);
        if (idAdminLogado == null)
            return "redirect:/login";
        model.addAttribute("idAdminLogado", idAdminLogado);
        return "doador";
    }

    @GetMapping("/app/doacao")
    public String paginaGerenciarDoacao(Model model, HttpSession session) {
        Integer idAdminLogado = (Integer) session.getAttribute(ADMIN_ID_SESSION_KEY);
        if (idAdminLogado == null)
            return "redirect:/login";
        model.addAttribute("idAdminLogado", idAdminLogado);
        return "doacao";
    }

    @GetMapping("/app/conquista")
    public String paginaGerenciarConquista() { return "conquista"; }

    @GetMapping("/app/enviarfotosatividade")
    public String paginaEnviarFotosAtividade() {
        return "enviarFotosAtividade";
    }

    @GetMapping("/app/doacao-form")
    public String paginaGerenciarDoacaoForm(Model model, HttpSession session) {
        Integer idAdminLogado = (Integer) session.getAttribute(ADMIN_ID_SESSION_KEY);
        if (idAdminLogado == null)
            return "redirect:/login";
        model.addAttribute("idAdminLogado", idAdminLogado);
        return "doacao-form";
    }
}