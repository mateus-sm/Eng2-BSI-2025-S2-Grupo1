package com.example.dminfo.view;

import com.example.dminfo.controller.DistribuicaoDeRecursosController;
import com.example.dminfo.model.DistribuicaoDeRecursos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequestMapping("apis/distribuicao-recursos")
public class DistribuicaoDeRecursosView {

    @Autowired
    private DistribuicaoDeRecursosController distribuicaoController;

    @GetMapping
    @ResponseBody
    public ResponseEntity<Object> listar() {
        try {
            return ResponseEntity.ok(distribuicaoController.listar());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar distribuições: " + e.getMessage());
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Object> criar(@RequestBody DistribuicaoDeRecursos distribuicao) {
        try {
            DistribuicaoDeRecursos salva = distribuicaoController.salvar(distribuicao);
            return ResponseEntity.status(HttpStatus.CREATED).body(salva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar distribuição: " + e.getMessage());
        }
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Object> atualizar(@RequestBody DistribuicaoDeRecursos distribuicao) {
        try {
            distribuicaoController.atualizar(distribuicao);
            return ResponseEntity.ok(distribuicao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar distribuição: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> buscarPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(distribuicaoController.getById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> excluir(@PathVariable Integer id) {
        try {
            distribuicaoController.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao excluir distribuição: " + e.getMessage());
        }
    }
}