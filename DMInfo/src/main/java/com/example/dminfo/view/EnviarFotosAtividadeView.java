package com.example.dminfo.view;

import com.example.dminfo.controller.EnviarFotosAtividadeController;
import com.example.dminfo.model.EnviarFotosAtividade;
import com.example.dminfo.util.MembroErro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/apis/atividade/{idAtividade}/fotos")
public class EnviarFotosAtividadeView {

    @Autowired
    private EnviarFotosAtividadeController controller;

    @GetMapping
    public ResponseEntity<Object> listarFotos(@PathVariable int idAtividade) {
        try {
            List<EnviarFotosAtividade> fotos = controller.listarPorAtividade(idAtividade);
            return ResponseEntity.ok(fotos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Object> enviarFoto(
            @PathVariable int idAtividade,
            @RequestParam("foto") MultipartFile file,
            @RequestParam("id_membro") int idMembro
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new MembroErro("Nenhum arquivo de foto enviado."));
        }

        try {
            EnviarFotosAtividade fotoSalva = controller.salvar(file, idMembro, idAtividade);
            return ResponseEntity.status(HttpStatus.CREATED).body(fotoSalva);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new MembroErro("Falha no upload: " + e.getMessage()));
        }
    }
}
