package com.example.dminfo.view;

import org.springframework.ui.Model;
import com.example.dminfo.controller.ParametrosController;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class viewController {
    @Autowired
    private ParametrosController parametrosController;
    private static final String ADMIN_ID_SESSION_KEY = "ADMIN_ID_SESSION";

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
    public String paginaGerenciarDoador() {
        return "doador";
    }

    @GetMapping("/app/doacao")
    public String paginaGerenciarDoacao() { return "doacao"; }

    @GetMapping("/app/conquista")
    public String paginaGerenciarConquista() { return "conquista"; }

    @GetMapping("/login")
    public String paginaLogin() {
        return "login";
    }

    @GetMapping("/app/enviarfotosatividade")
    public String paginaEnviarFotosAtividade() {
        return "enviarFotosAtividade";
    }

    @GetMapping("/app/doacao-form")
    public String paginaGerenciarDoacaoForm(Model model, HttpSession session) {
        Integer idAdminLogado = (Integer) session.getAttribute(ADMIN_ID_SESSION_KEY);

        // 1. VERIFICAÇÃO DE SEGURANÇA CRÍTICA: Se o ID não for encontrado, redireciona para o login.
        if (idAdminLogado == null) {
            // Se o usuário não estiver logado, ele não pode acessar o formulário
            return "redirect:/login";
        }

        // 2. Se o ID for encontrado, injeta-o no modelo
        model.addAttribute("idAdminLogado", idAdminLogado);

        return "doacao-form";
    }
}
