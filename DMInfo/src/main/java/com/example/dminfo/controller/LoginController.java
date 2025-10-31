package com.example.dminfo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final String MASTER_TOKEN = "Admin@123";

    @PostMapping("/login")
    public String processarLogin(
            @RequestParam("token") String token,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        if (token != null && token.equals(MASTER_TOKEN)) {

            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogado", true);
            //redireciona para adms
            return "redirect:/app/administradores";

        } else {

            redirectAttributes.addFlashAttribute("error", true);
            return "redirect:/login";
        }
    }
}