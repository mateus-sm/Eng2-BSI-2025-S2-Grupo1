package com.example.dminfo.view;

import com.example.dminfo.util.MembroErro;
import com.example.dminfo.model.Membro;
import com.example.dminfo.controller.MembroController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequestMapping("app/membro")
public class MembroView {

    @Autowired
    private MembroController controller;

    @GetMapping
    public String carregarPagina(){
        return "membros";
    }

    @GetMapping
    public ResponseEntity<Object> listar() {
        return ResponseEntity.ok(controller.listar());
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Membro membro) {
        try {
            Membro novoMembro = controller.salvar(membro);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoMembro);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @GetMapping(value="/get-by-id/{id}")
    public ResponseEntity<Object> read(@PathVariable Integer id) {
        Membro membro = controller.getById(id);
        if (membro == null) {
            return ResponseEntity.badRequest().body(new MembroErro("Membro não encontrado"));
        }
        return ResponseEntity.ok(membro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody Membro membroDetails) {
        try {
            Membro membroAtualizado = controller.update(id, membroDetails);
            return ResponseEntity.ok(membroAtualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        try {
            if (controller.excluir(id)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().body(new MembroErro("Erro ao excluir."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }
}