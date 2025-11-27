package com.example.dminfo.view;

import com.example.dminfo.controller.DoacaoController;
import com.example.dminfo.model.Doacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequestMapping("apis/doacao")
public class DoacaoView {

    @Autowired
    private DoacaoController controller;

    @GetMapping
    public ResponseEntity<Object> listar() {
        try {
            return ResponseEntity.ok(controller.listar());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar doações: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscar(@PathVariable int id) {
        try {
            return ResponseEntity.ok(controller.buscar(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody Doacao doacao) {
        try {
            Doacao salva = controller.salvar(doacao);
            if (salva != null)
                return ResponseEntity.status(HttpStatus.CREATED).body(salva);
            else
                return ResponseEntity.badRequest().body("Erro ao gravar doação");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable int id, @RequestBody Doacao doacao) {
        try {
            doacao.setId_doacao(id);
            controller.atualizar(doacao);
            return ResponseEntity.ok(doacao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar doação: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> excluir(@PathVariable int id) {
        try {
            controller.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao excluir doação: " + e.getMessage());
        }
    }
}