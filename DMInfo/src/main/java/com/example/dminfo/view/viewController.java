package com.example.dminfo.view;

import com.example.dminfo.controller.ParametrosController;
import com.example.dminfo.model.Administrador;
import com.example.dminfo.util.SingletonDB;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class viewController {

    @Autowired
    private ParametrosController parametrosController;

    @Autowired
    private Administrador adminModel;

    private static final String USUARIO_SESSION_KEY = "idUsuarioLogado";

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

    @GetMapping("/app/mensalidade")
    public String paginaMensalidade() {
        return "mensalidade";
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

    @GetMapping("/app/conquista")
    public String paginaGerenciarConquista() {
        return "conquista";
    }

    @GetMapping("/app/enviarfotosatividade")
    public String paginaEnviarFotosAtividade() {
        return "enviarFotosAtividade";
    }

    @GetMapping("/app/distribuir-recursos")
    public String paginaDistribuirRecursos() {return "distribuir-recursos";}

    @GetMapping("/app/atribuirconquista")
    public String paginaAtribuirConquista() {return "atribuir-conquista";}

    @GetMapping("/app/principal")
    public String paginaPrincipal() {
        return "principal";
    }

    private Administrador getAdminLogado(HttpSession session) {
        Object idUsuarioObj = session.getAttribute(USUARIO_SESSION_KEY);

        if (idUsuarioObj == null) {
            return null;
        }

        Integer idUsuario = Integer.parseInt(idUsuarioObj.toString());

        return adminModel.getByUsuario(idUsuario, SingletonDB.getConexao());
    }

    @GetMapping("/app/doador")
    public String paginaGerenciarDoador(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Administrador admin = getAdminLogado(session);

        if (admin == null) {
            redirectAttributes.addFlashAttribute("erroPermissao", "Acesso Negado! \nSomente administradores podem acessar a gestão de doadores.");
            return "redirect:/app/principal";
        }

        model.addAttribute("idAdminLogado", admin.getId());
        return "doador";
    }

    @GetMapping("/app/doacao")
    public String paginaGerenciarDoacao(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Administrador admin = getAdminLogado(session);

        if (admin == null) {
            redirectAttributes.addFlashAttribute("erroPermissao", "Acesso Negado! \nSomente administradores podem acessar a gestão de doações.");
            return "redirect:/app/principal";
        }

        model.addAttribute("idAdminLogado", admin.getId());
        return "doacao";
    }

    @GetMapping("/app/doacaoForm")
    public String paginaGerenciarDoacaoForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Administrador admin = getAdminLogado(session);

        if (admin == null) {
            redirectAttributes.addFlashAttribute("erroPermissao", "Acesso Negado! \nVocê precisa ser administrador para registrar doações.");
            return "redirect:/app/principal";
        }

        model.addAttribute("idAdminLogado", admin.getId());
        return "doacaoForm";
    }

    @GetMapping("/app/lancarmembroativo")
    public String paginaLancarmembroativo() {
        return "lancarmembroativo";
    }

    @GetMapping("/app/evento")
    public String paginaEvento() {
        return "evento";
    }

    @GetMapping("/app/frequenciaAtividade")
    public String paginaFrequenciaAtividade() {
        return "frequenciaAtividade";
    }

    @GetMapping("/app/realizacaoAtividades")
    public String paginaRealizacaoAtividades() {
        return "realizacaoAtividades";
    }
}