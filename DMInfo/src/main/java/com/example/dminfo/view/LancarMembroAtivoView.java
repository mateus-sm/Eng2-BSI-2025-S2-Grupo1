package com.example.dminfo.view;

import com.example.dminfo.controller.LancarMembroAtivoController;
import com.example.dminfo.model.Membro;
import com.example.dminfo.model.MembroErro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("apis/lancarmembroativo")
public class LancarMembroAtivoView {

    @Autowired
    private LancarMembroAtivoController controller;

    @GetMapping
    public ResponseEntity<Object> listar() {
        try {
            return ResponseEntity.ok(controller.listarTodos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro("Erro: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarStatus(@PathVariable Integer id, @RequestBody Membro membro) {
        try {
            controller.atualizarStatus(id, membro);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }
}