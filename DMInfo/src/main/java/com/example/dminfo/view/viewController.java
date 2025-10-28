package com.example.dminfo.view;

import com.example.dminfo.services.ParametrosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class viewController {
    @Autowired
    private ParametrosService service;

    @GetMapping("/app/parametrizacao")
    public String paginaParametrizacao() {
        if (service.existeParametro())
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
        // Isso vai carregar o arquivo 'calendario.html' da pasta 'templates'
        return "calendario";
    }

    @GetMapping("/app/membros")
    public String paginaGerenciarMembros() {
        return "membros";
    }
}
