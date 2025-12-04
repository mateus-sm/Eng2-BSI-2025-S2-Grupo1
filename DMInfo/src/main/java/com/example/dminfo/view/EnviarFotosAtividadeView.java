package com.example.dminfo.view;

import com.example.dminfo.controller.EnviarFotosAtividadeController;
import com.example.dminfo.model.Atividade;
import com.example.dminfo.model.EnviarFotosAtividade;
import com.example.dminfo.model.Evento;
import com.example.dminfo.model.MembroErro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class EnviarFotosAtividadeView {

    @Autowired
    private EnviarFotosAtividadeController controller;

    @GetMapping("/apis/atividade/form/eventos")
    public ResponseEntity<Object> listarTodosEventos() {
        try {
            List<Evento> eventos = controller.listarTodosEventos();
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MembroErro("Erro ao buscar eventos: " + e.getMessage()));
        }
    }

    @GetMapping("/apis/atividade/form/eventos/{id}/atividades")
    public ResponseEntity<Object> listarAtividadesPorEvento(@PathVariable("id") int idEvento) {
        try {
            List<Atividade> atividades = controller.listarAtividadesPorEvento(idEvento);
            return ResponseEntity.ok(atividades);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MembroErro("Erro ao buscar atividades: " + e.getMessage()));
        }
    }

    @GetMapping("/apis/atividade/{idAtividade}/fotos")
    public ResponseEntity<Object> listarFotos(@PathVariable int idAtividade) {
        try {
            List<EnviarFotosAtividade> fotos = controller.listarPorAtividade(idAtividade);
            return ResponseEntity.ok(fotos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @PostMapping("/apis/atividade/{idAtividade}/fotos")
    public ResponseEntity<Object> enviarFoto(@PathVariable int idAtividade, @RequestParam("foto") MultipartFile file, HttpSession session) {
        try {
            Integer idUsuario = (Integer) session.getAttribute("ADMIN_ID_SESSION");

            if (idUsuario == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MembroErro("Sessão expirada ou usuário não logado."));

            EnviarFotosAtividade fotoSalva = controller.salvar(file, idUsuario, idAtividade);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("id", fotoSalva.getId(),
                            "foto", fotoSalva.getFoto(),
                            "mensagem", "Sucesso"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @DeleteMapping("/apis/fotos/{idFoto}")
    public ResponseEntity<Object> excluirFoto(@PathVariable int idFoto) {
        try {
            controller.excluirFoto(idFoto);
            return ResponseEntity.ok(Map.of("mensagem", "Foto excluída com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @GetMapping("/apis/fotos/{idFoto}")
    public ResponseEntity<Object> buscarFotoPorId(@PathVariable int idFoto) {
        try {
            return ResponseEntity.ok(controller.getById(idFoto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }
}