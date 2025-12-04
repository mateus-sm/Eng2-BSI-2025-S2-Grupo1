package com.example.dminfo.view;

import com.example.dminfo.controller.FinalizarAtividadesController;
import com.example.dminfo.model.CriarRealizacaoAtividades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("apis/finalizar-atividades")
public class FinalizarAtividadesView {

    @Autowired
    private FinalizarAtividadesController atividadesController;

    @GetMapping
    public ResponseEntity<List<CriarRealizacaoAtividades>> listarAtividades() {
        try {
            List<CriarRealizacaoAtividades> atividades = atividadesController.listarTodas();
            return ResponseEntity.ok(atividades);
        } catch (Exception e) {
            System.err.println("Erro ao listar atividades: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> finalizarAtividadeCompleta(@PathVariable Integer id, @RequestBody CriarRealizacaoAtividades dados) {

        try {
            dados.setId(id);

            if (atividadesController.finalizarAtividade(dados))
                return ResponseEntity.ok("Atividade finalizada com sucesso.");
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ID de atividade não encontrado ou falha na atualização.");

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato de data inválido.");
        } catch (Exception e) {
            System.err.println("Erro ao finalizar atividade: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao salvar.");
        }
    }
}