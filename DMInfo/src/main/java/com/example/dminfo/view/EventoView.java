package com.example.dminfo.view;

import com.example.dminfo.controller.EventoController;
import com.example.dminfo.controller.EnviarFotosAtividadeController;
import com.example.dminfo.model.Evento;
import com.example.dminfo.model.MembroErro;
import com.example.dminfo.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("apis/eventos")
public class EventoView {

    @Autowired
    private EventoController controller;
    @Autowired
    private EnviarFotosAtividadeController fotosController;

    private ResponseEntity<Object> checkToken(String token) {
        if (token == null || !Token.validarToken(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MembroErro("Acesso não autorizado ou Token inválido."));
        return null;
    }

    // Mapeia para GET http://localhost:8080/apis/eventos
    // Rota pública para carregar o dropdown de Eventos (não exige Token)
    @GetMapping
    public ResponseEntity<Object> listarPublico() {
        try {
            return ResponseEntity.ok(fotosController.listarTodosEventos());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MembroErro("Erro ao listar eventos: " + e.getMessage()));
        }
    }

    // Mapeia para GET http://localhost:8080/apis/eventos/{idEvento}/atividades
    // Rota pública para carregar o dropdown de Atividades (não exige Token)
    @GetMapping("/{idEvento}/atividades")
    public ResponseEntity<Object> listarAtividadesPorEvento(@PathVariable int idEvento) {
        try {
            return ResponseEntity.ok(fotosController.listarAtividadesPorEvento(idEvento));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MembroErro("Erro ao listar atividades: " + e.getMessage()));
        }
    }

    // Mapeia para GET http://localhost:8080/apis/eventos/admin
    @GetMapping("/admin")
    public ResponseEntity<Object> listarAdmin(@RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;

        List<Evento> eventos = controller.listar();
        return ResponseEntity.ok(eventos);
    }

    // Mapeia para GET http://localhost:8080/apis/eventos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;

        try {
            Evento evento = controller.getById(id);
            if (evento != null)
                return ResponseEntity.ok(evento);
            else
                return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    // Mapeia para POST http://localhost:8080/apis/eventos
    @PostMapping
    public ResponseEntity<Object> salvar(@RequestHeader("Authorization") String token, @RequestBody Evento evento) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;

        try {
            Evento novoEvento = controller.salvar(evento);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoEvento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    // Mapeia para PUT http://localhost:8080/apis/eventos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@RequestHeader("Authorization") String token, @PathVariable Integer id, @RequestBody Evento evento) {
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

    // Mapeia para DELETE http://localhost:8080/apis/eventos/{id}
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