package com.example.dminfo.view;

import com.example.dminfo.controller.EventoController;
import com.example.dminfo.controller.EnviarFotosAtividadeController;
import com.example.dminfo.model.Evento;
import com.example.dminfo.model.MembroErro;
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

    @GetMapping("/admin")
    public ResponseEntity<Object> listarAdmin(@RequestHeader(value = "Authorization", required = false) String token,
                                              @RequestParam(required = false) String descricao, // Novo
                                              @RequestParam(required = false) String ordenarPor) { // Novo


        List<Evento> eventos = controller.listar(descricao, ordenarPor);
        return ResponseEntity.ok(eventos);
    }

    @GetMapping
    public ResponseEntity<Object> listarPublico() {
        try {
            return ResponseEntity.ok(fotosController.listarTodosEventos());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MembroErro("Erro ao listar eventos: " + e.getMessage()));
        }
    }

    @GetMapping("/{idEvento}/atividades")
    public ResponseEntity<Object> listarAtividadesPorEvento(@PathVariable int idEvento) {
        try {
            return ResponseEntity.ok(fotosController.listarAtividadesPorEvento(idEvento));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MembroErro("Erro ao listar atividades: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Integer id) {

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

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody Evento evento) {

        try {
            Evento novoEvento = controller.salvar(evento);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoEvento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Integer id, @RequestBody Evento evento) {


        try {
            Evento atualizado = controller.atualizar(id, evento);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@RequestHeader(value = "Authorization", required = false) String token,
                                          @PathVariable Integer id) {
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