package com.example.dminfo.view;

import com.example.dminfo.controller.CalendarioController;
import com.example.dminfo.model.CriarRealizacaoAtividades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("apis/calendario")
public class CalendarioView {
    @Autowired
    private CalendarioController calendarioController;

    @GetMapping
    public ResponseEntity<List<CriarRealizacaoAtividades>> listarAtividadesParaCalendario() {
        try {
            List<CriarRealizacaoAtividades> atividades = calendarioController.listarTodasAtividades();
            return ResponseEntity.ok(atividades);
        } catch (Exception e) {
            System.err.println("Erro ao listar atividades para o calendário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<Integer>> listarIdsAtivos() {
        try {
            List<Integer> ids = calendarioController.listarAtividadesAtivasIds();
            return ResponseEntity.ok(ids);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> adicionarAoCalendario(@PathVariable Integer id) {
        try {
            if (calendarioController.adicionarAtividadeAoCalendario(id)) {
                return ResponseEntity.ok("Atividade adicionada ao calendário.");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falha ao adicionar atividade.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao salvar.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removerDoCalendario(@PathVariable Integer id) {
        try {
            if (calendarioController.removerAtividadeDoCalendario(id)) {
                return ResponseEntity.ok("Atividade removida do calendário.");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Atividade não encontrada ou falha na remoção.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao remover.");
        }
    }
}