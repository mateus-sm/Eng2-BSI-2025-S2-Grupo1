//package com.example.dminfo.controller;
//
//import com.example.dminfo.model.AtribuirConquistaMembro;
//import com.example.dminfo.services.AtribuirConquistaMembroService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/atribuir-conquista")
//public class AtribuirConquistaController {
//    @Autowired
//    private AtribuirConquistaMembroService ACMservice;
//
//    @GetMapping
//    public ResponseEntity<Object> listar() {
//        return ResponseEntity.ok(ACMservice.listar());
//    }
//
//    @PostMapping
//    public ResponseEntity<Object> salvar(@RequestBody AtribuirConquistaMembro atribuicao) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(ACMservice.salvar(atribuicao));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Object> excluir(@PathVariable Integer id) {
//        ACMservice.excluir(id);
//        return ResponseEntity.noContent().build();
//    }
//}
