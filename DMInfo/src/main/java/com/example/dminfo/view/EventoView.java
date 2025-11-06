package com.example.dminfo.view;

import com.example.dminfo.controller.EventoController;
import com.example.dminfo.model.Evento;
import com.example.dminfo.util.MembroErro;
import com.example.dminfo.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("apis/evento")
public class EventoView {

    @Autowired
    private EventoController controller;

    private ResponseEntity<Object> checkToken(String token) {
        if (!Token.validarToken(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MembroErro("Acesso não autorizado."));
        return null; // Token é válido
    }

    @GetMapping
    public ResponseEntity<Object> listar(@RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;

        List<Evento> eventos = controller.listar();
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@RequestHeader("Authorization") String token,
                                              @PathVariable Integer id) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;

        Evento evento = controller.getById(id);
        if (evento == null)
            return ResponseEntity.badRequest().body(new MembroErro("Evento não encontrado."));
        return ResponseEntity.ok(evento);
    }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestHeader("Authorization") String token,
                                         @RequestBody Evento evento) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;

        try {
            Evento salvo = controller.salvar(evento);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@RequestHeader("Authorization") String token,
                                            @PathVariable Integer id,
                                            @RequestBody Evento evento) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;

        try {
            Evento atualizado = controller.atualizar(id, evento);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@RequestHeader("Authorization") String token,
                                          @PathVariable Integer id) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;

        try {
            if (controller.excluir(id))
                return ResponseEntity.noContent().build();
            else
                return ResponseEntity.badRequest().body(new MembroErro("Erro ao excluir evento."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }
}
