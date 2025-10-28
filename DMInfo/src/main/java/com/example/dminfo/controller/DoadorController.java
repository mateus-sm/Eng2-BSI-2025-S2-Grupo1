package com.example.dminfo.controller;


import com.example.dminfo.model.Doador;
import com.example.dminfo.services.DoadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/doador")
public class DoadorController {

    @Autowired
    private DoadorService service;

    @GetMapping("/{id}")
    public ResponseEntity<Doador> buscarPorId(@PathVariable Integer id){
        Optional<Doador> doador = service.buscarPorId(id);
        if(doador.isPresent()){
            return ResponseEntity.ok(doador.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Doador> salvar(@RequestBody Doador doador){
        Doador salvo = service.salvar(doador);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doador> atualizar(@PathVariable Integer id, @RequestBody Doador doador) {
        try{
            Doador atualizado = service.atualizar(id, doador);
            return ResponseEntity.ok(atualizado);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable Integer id){
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}