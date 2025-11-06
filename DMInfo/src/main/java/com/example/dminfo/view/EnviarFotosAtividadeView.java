package com.example.dminfo.view;

import com.example.dminfo.controller.EnviarFotosAtividadeController;
import com.example.dminfo.model.Atividade;
import com.example.dminfo.model.EnviarFotosAtividade;
import com.example.dminfo.model.Membro;
import com.example.dminfo.util.MembroErro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("apis/atividade/{idAtividade}/fotos") // Rota aninhada
public class EnviarFotosAtividadeView {

    @Autowired
    private EnviarFotosAtividadeController controller;
    private static final String UPLOAD_DIRECTORY = "src/main/resources/static/uploads/";

    @GetMapping
    public ResponseEntity<Object> listarFotos(@PathVariable int idAtividade) {
        try {
            return ResponseEntity.ok(controller.listarPorAtividade(idAtividade));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Object> enviarFoto(@PathVariable int idAtividade, @RequestParam("foto") MultipartFile file, @RequestParam("id_membro") int idMembro) {

        if (file.isEmpty())
            return ResponseEntity.badRequest().body(new MembroErro("Nenhum arquivo de foto enviado."));

        try {
            String caminhoArquivo = saveFile(file);

            EnviarFotosAtividade foto = new EnviarFotosAtividade();

            foto.setFoto(caminhoArquivo);
            foto.setAtividade(new Atividade() {{ setId(idAtividade); }});
            foto.setMembro(new Membro() {{ setId(idMembro); }});

            EnviarFotosAtividade fotoSalva = controller.salvar(foto);
            return ResponseEntity.status(HttpStatus.CREATED).body(fotoSalva);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro("Falha no upload: " + e.getMessage()));
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        Path physicalPath = Paths.get(UPLOAD_DIRECTORY + uniqueFilename);

        Files.createDirectories(physicalPath.getParent());
        Files.write(physicalPath, file.getBytes());

        return "/uploads/" + uniqueFilename;
    }
}