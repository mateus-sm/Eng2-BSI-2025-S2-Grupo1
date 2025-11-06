package com.example.dminfo.view;

import com.example.dminfo.controller.ConquistaController;
import com.example.dminfo.model.Conquista;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequestMapping("apis/conquista")
public class ConquistaView {

    @Autowired
    private ConquistaController conquistaController;

    @GetMapping
    @ResponseBody
    public ResponseEntity<Object> listar() {
        try {
            return ResponseEntity.ok(conquistaController.listar());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar conquistas: " + e.getMessage());
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Object> criar(@RequestBody Conquista conquista) {
        try {
            Conquista salva = conquistaController.salvar(conquista);
            return ResponseEntity.status(HttpStatus.CREATED).body(salva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar conquista: " + e.getMessage());
        }
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Object> atualizar(@RequestBody Conquista conquista) {
        try {
            conquistaController.atualizar(conquista);
            return ResponseEntity.ok(conquista);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar conquista: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> buscarPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(conquistaController.getById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> excluir(@PathVariable Integer id) {
        try {
            conquistaController.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao excluir conquista: " + e.getMessage());
        }
    }
}
