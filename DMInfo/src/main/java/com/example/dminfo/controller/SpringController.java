package com.example.dminfo.controller;


import com.example.dminfo.model.Membro;
import com.example.dminfo.model.Usuario;
import com.example.dminfo.services.MembroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping(value = "app")
public class SpringController {
    @Autowired
    private MembroService membroService;

    @PostMapping(value = "/membro")
    public ResponseEntity<Membro> inserirMembro() {
        Membro membroSalvo = membroService.criarMembro();
        return ResponseEntity.status(HttpStatus.CREATED).body(membroSalvo);
    }

    @GetMapping(value = "/membro/{id}")
    public ResponseEntity<Membro> exibirMembro(@PathVariable Integer id) {
        Membro membro = membroService.buscarMembroPorId(id);
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
