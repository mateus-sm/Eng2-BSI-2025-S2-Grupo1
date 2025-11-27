package com.example.dminfo.view;

import com.example.dminfo.controller.DoadorController;
import com.example.dminfo.model.Doador;
import com.example.dminfo.model.MembroErro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("apis/doador")
public class DoadorView {

    @Autowired
    private DoadorController controller;

    @GetMapping
    public ResponseEntity<Object> listar() {
        try {
            return ResponseEntity.ok(controller.listar());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro("Erro ao listar: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(controller.getById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MembroErro(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody Doador doador) {
        try{
            Doador salvo = controller.salvar(doador);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Integer id, @RequestBody Doador doador) {
        try{
            Doador atualizado = controller.atualizar(id, doador);
            return ResponseEntity.ok(atualizado);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable Integer id) {
        try {
            controller.excluir(id);
            return ResponseEntity.noContent().build();
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }
}