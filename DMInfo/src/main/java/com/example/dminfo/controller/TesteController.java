package com.example.dminfo.controller;


import com.example.dminfo.model.Membro;
import com.example.dminfo.services.MembroService;
import com.example.dminfo.services.TesteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "app")
public class TesteController {
    @Autowired
    private MembroService membroService;
    @Autowired
    private TesteService testeService;

    @PostMapping(value = "/membro")
    public ResponseEntity<Membro> inserirMembro() {
        Membro membroSalvo = testeService.criarMembro();
        return ResponseEntity.status(HttpStatus.CREATED).body(membroSalvo);
    }

    @GetMapping(value = "/usuarios")
    public ResponseEntity<Object> usuarios(){
        return ResponseEntity.ok(testeService.listarUsuarios());
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
