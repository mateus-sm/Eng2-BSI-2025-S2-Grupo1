package com.example.dminfo.view;

import com.example.dminfo.controller.ParametrosController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class viewController {
    @Autowired
    private ParametrosController controller;

    @GetMapping("/app/parametrizacao")
    public String paginaParametrizacao() {
        if (controller.existeParametro())
            return "redirect:/app/parametrizacao/exibir";
        else
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
    public String paginaGerenciarDoador() {
        return "doador";
    }

    @GetMapping("/app/conquista")
    public String paginaGerenciarConquista() {
        return "conquista";
    }

    @GetMapping("/app/login")
    public String paginaLogin() {
        return "login";
    }
}
