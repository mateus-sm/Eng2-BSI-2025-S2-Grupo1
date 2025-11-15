package com.example.dminfo.view;

import com.example.dminfo.controller.RecursoController;
import com.example.dminfo.model.Recurso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequestMapping("apis/recurso")
public class RecursoView {

    @Autowired
    private RecursoController recursoController;

    @GetMapping
    @ResponseBody
    public ResponseEntity<Object> listar() {
        try {
            return ResponseEntity.ok(recursoController.listar());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar recursos: " + e.getMessage());
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Object> criar(@RequestBody Recurso recurso) {
        try {
            Recurso salvo = recursoController.salvar(recurso);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar recurso: " + e.getMessage());
        }
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Object> atualizar(@RequestBody Recurso recurso) {
        try {
            recursoController.atualizar(recurso);
            return ResponseEntity.ok(recurso);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar recurso: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> buscarPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(recursoController.getById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> excluir(@PathVariable Integer id) {
        try {
            recursoController.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao excluir recurso: " + e.getMessage());
        }
    }
}