package com.example.dminfo.view;

import com.example.dminfo.controller.DoadorController;
import com.example.dminfo.model.Doador;
import com.example.dminfo.util.MembroErro;
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
    public ResponseEntity<Object> listar() {return ResponseEntity.ok(controller.listar());}

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable Integer id) {
        Doador doador = controller.getById(id);
        if (doador == null)
            return ResponseEntity.badRequest().body(new MembroErro("Doador não encontrado."));
        return ResponseEntity.ok(doador);
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
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable Integer id) {
        try {
            if(controller.excluir(id))
                return ResponseEntity.noContent().build();
            else
                return ResponseEntity.badRequest().body(new MembroErro("Erro ao excluir."));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }
}