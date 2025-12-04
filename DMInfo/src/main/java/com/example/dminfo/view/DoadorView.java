package com.example.dminfo.view;

import com.example.dminfo.controller.DoadorController;
import com.example.dminfo.model.Doador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequestMapping("apis/doador")
public class DoadorView {

    @Autowired
    private DoadorController controller;

    @GetMapping
    @ResponseBody
    public ResponseEntity<Object> listar() {
        try {
            return ResponseEntity.ok(controller.listar());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar doadores: " + e.getMessage());
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Object> criar(@RequestBody Doador doador) {
        try {
            Doador salvo = controller.salvar(doador);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar doador: " + e.getMessage());
        }
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Object> atualizar(@RequestBody Doador doador) {
        try {
            controller.atualizar(doador);
            return ResponseEntity.ok(doador);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar doador: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> buscarPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(controller.getById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> excluir(@PathVariable Integer id) {
        try {
            controller.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao excluir doador: " + e.getMessage());
        }
    }
}