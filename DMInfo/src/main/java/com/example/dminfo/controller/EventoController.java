//package com.example.dminfo.controller;
//
//import com.example.dminfo.model.Evento;
//import com.example.dminfo.model.Erro;
//import com.example.dminfo.services.EventoService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/evento")
//public class EventoController {
//
//    @Autowired
//    private EventoService service;
//
//    @GetMapping("/all")
//    public List<Evento> listar() {
//        return service.listar();
//    }
//
//    @PostMapping
//    public ResponseEntity<Object> create(@RequestBody Evento evento) {
//        if (evento != null) {
//            try {
//                Evento salvo = service.salvar(evento);
//                return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
//            } catch (Exception e) {
//                return ResponseEntity.badRequest().body(
//                        new Erro("Erro de Banco", "Não foi possível salvar: " + e.getMessage())
//                );
//            }
//        }
//        return ResponseEntity.badRequest().body(
//                new Erro("Erro de Objeto", "Objeto Evento inconsistente")
//        );
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Object> read(@PathVariable Integer id) {
//        Evento evento = service.getById(id);
//        if (evento == null) {
//            return ResponseEntity.badRequest().body(
//                    new Erro("Erro de Banco", "Evento não encontrado")
//            );
//        }
//        return ResponseEntity.ok(evento);
//    }
//
//    @PutMapping
//    public ResponseEntity<Object> update(@RequestBody Evento evento) {
//        if (evento != null) {
//            try {
//                Evento atualizado = service.salvar(evento); // sobrescreve o evento existente se tiver ID
//                return ResponseEntity.ok(atualizado);
//            } catch (Exception e) {
//                return ResponseEntity.badRequest().body(
//                        new Erro("Erro de Banco", "Não foi possível atualizar: " + e.getMessage())
//                );
//            }
//        }
//        return ResponseEntity.badRequest().body(
//                new Erro("Erro de Objeto", "Objeto Evento inconsistente")
//        );
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Object> delete(@PathVariable Integer id) {
//        if (service.excluir(id)) {
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.badRequest().body(
//                    new Erro("Erro de Busca", "Evento não encontrado")
//            );
//        }
//    }
//}
