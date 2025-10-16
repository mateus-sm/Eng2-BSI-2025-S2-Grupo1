package com.example.dminfo.controller;


import com.example.dminfo.model.Membro;
import com.example.dminfo.model.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping(value = "app")
public class SpringController {

    @GetMapping(value = "/membro")
    public ResponseEntity<Membro> membro(){
        Usuario user = new Usuario(
                "Kaiky",
                "12345",
                "kaiky_user",
                "11999999999",
                "kaiky@email.com",
                "Rua A",
                "Cidade B",
                "Bairro C",
                "SP",
                "123.456.789-00",
                new Date(), // dtIni
                null,       // dtFim
                new Date()  // dtNasc
        );

        Membro membro = new Membro(
                1,
                new Date(), // dtIniMembro
                null,       // dtFimMembro
                "Membro ativo",
                user        // <-- aqui passa o Usuario
        );

        return ResponseEntity.ok(membro);
    }

    @GetMapping(value = "/index")
    public ResponseEntity<Object> index(){
        return ResponseEntity.ok("Testando INDEX");
    }

    @GetMapping(value = "/cadastro")
    public ResponseEntity<Object> cadastro(){
        return ResponseEntity.ok("Testando CADASTRO");
    }
}
