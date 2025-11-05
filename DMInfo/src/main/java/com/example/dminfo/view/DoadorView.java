package com.example.dminfo.view;

import com.example.dminfo.controller.DoadorController;
import com.example.dminfo.model.Doador;
import com.example.dminfo.util.MembroErro; // Reutilizando a classe de Erro
import com.example.dminfo.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("apis/doador")
public class DoadorView {

    @Autowired
    private DoadorController controller;

    //Helper de validação de token
    private ResponseEntity<Object> checkToken(String token) {
        if (!Token.validarToken(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MembroErro("Acesso não autorizado."));
        return null; //Token é válido
    }

    @GetMapping
    public ResponseEntity<Object> listar(@RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;
        return ResponseEntity.ok(controller.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;

        Doador doador = controller.getById(id);
        if (doador == null)
            return ResponseEntity.badRequest().body(new MembroErro("Doador não encontrado."));
        return ResponseEntity.ok(doador);
    }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestHeader("Authorization") String token, @RequestBody Doador doador) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;

        try{
            Doador salvo = controller.salvar(doador);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@RequestHeader("Authorization") String token, @PathVariable Integer id, @RequestBody Doador doador) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;

        try{
            Doador atualizado = controller.atualizar(id, doador);
            return ResponseEntity.ok(atualizado);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;

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