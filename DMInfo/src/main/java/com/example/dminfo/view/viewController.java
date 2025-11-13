package com.example.dminfo.view;

import com.example.dminfo.controller.ParametrosController;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class viewController {

    @Autowired
    private ParametrosController parametrosController;

    private static final String ADMIN_ID_SESSION_KEY = "ADMIN_ID_SESSION";

    @GetMapping("/login")
    public String paginaLogin(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String paginaRegistro() {
        return "register";
    }

    @GetMapping("/app/parametrizacao")
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
    public String paginaGerenciarAdministradores() {
        return "administradores";
    }

    @GetMapping("/app/doador")
    public String paginaGerenciarDoador(Model model, HttpSession session) {
        //FAZER A VERIFICACAO SE É ADMIN LOGADO OU NAO
//        Integer idAdminLogado = (Integer) session.getAttribute(ADMIN_ID_SESSION_KEY);
//        if (idAdminLogado == null)
//            return "redirect:/login";
//        model.addAttribute("idAdminLogado", idAdminLogado);
        return "doador";
    }

    @GetMapping("/app/doacao")
    public String paginaGerenciarDoacao(Model model, HttpSession session) {
        //FAZER A VERIFICACAO SE É ADMIN LOGADO OU NAO
//        Integer idAdminLogado = (Integer) session.getAttribute(ADMIN_ID_SESSION_KEY);
//        if (idAdminLogado == null)
//            return "redirect:/login";
//        model.addAttribute("idAdminLogado", idAdminLogado);
        return "doacao";
    }

    @GetMapping("/app/conquista")
    public String paginaGerenciarConquista() {
        return "conquista";
    }

    @GetMapping("/app/enviarfotosatividade")
    public String paginaEnviarFotosAtividade() {
        return "enviarFotosAtividade";
    }

    @GetMapping("/app/doacao-form")
    public String paginaGerenciarDoacaoForm(Model model, HttpSession session) {
        //FAZER A VERIFICACAO SE É ADMIN LOGADO OU NAO
//        Integer idAdminLogado = (Integer) session.getAttribute(ADMIN_ID_SESSION_KEY);
//        if (idAdminLogado == null)
//            return "redirect:/login";
//        model.addAttribute("idAdminLogado", idAdminLogado);
        return "doacao-form";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(ADMIN_ID_SESSION_KEY);
        session.invalidate();
        return "redirect:/login";
    }
}