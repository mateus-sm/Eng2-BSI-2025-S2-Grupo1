package com.example.dminfo.view;

import com.example.dminfo.controller.EnviarFotosAtividadeController;
import com.example.dminfo.model.EnviarFotosAtividade;
import com.example.dminfo.model.MembroErro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/atividade/{idAtividade}/fotos")
public class EnviarFotosAtividadeView {

    @Autowired
    private EnviarFotosAtividadeController controller;

    // GET /apis/atividade/{idAtividade}/fotos
    @GetMapping
    public ResponseEntity<Object> listarFotos(@PathVariable int idAtividade) {
        try {
            List<EnviarFotosAtividade> fotos = controller.listarPorAtividade(idAtividade);
            return ResponseEntity.ok(fotos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    // GET /apis/eventos
    @GetMapping("/eventos")
    public ResponseEntity<Object> listarTodosEventos() {
        try {
            List<Map<String, Object>> eventos = controller.listarTodosEventos();
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MembroErro("Erro ao buscar eventos: " + e.getMessage()));
        }
    }

    // GET /apis/atividade/{idAtividade}/fotos/{idFoto}
    @GetMapping("/{idFoto}")
    public ResponseEntity<Object> buscarFotoPorId(@PathVariable int idFoto) {
        Map<String, Object> resultado = controller.getById(idFoto);

        if (resultado.containsKey("erro"))
            return ResponseEntity.badRequest().body(new MembroErro(resultado.get("erro").toString()));
        return ResponseEntity.ok(resultado);
    }

    // POST /apis/atividade/{idAtividade}/fotos
    @PostMapping
    public ResponseEntity<Object> enviarFoto(@PathVariable int idAtividade, @RequestParam("foto") MultipartFile file, @RequestParam("id_membro") int idMembro) {
        Map<String, Object> resultado = controller.salvar(file, idMembro, idAtividade);

        if (resultado.containsKey("erro"))
            return ResponseEntity.badRequest().body(new MembroErro(resultado.get("erro").toString()));
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // DELETE /apis/atividade/{idAtividade}/fotos/{idFoto}
    @DeleteMapping("/{idFoto}")
    public ResponseEntity<Object> excluirFoto(@PathVariable int idFoto) {
        Map<String, Object> resultado = controller.excluirFoto(idFoto);

        if (resultado.containsKey("erro") || resultado.containsKey("alerta"))
            return ResponseEntity.badRequest().body(new MembroErro(resultado.containsKey("erro") ? resultado.get("erro").toString() : resultado.get("alerta").toString()));

        return ResponseEntity.ok(new MembroErro(resultado.get("mensagem").toString()));
    }

    // PUT /apis/atividade/{idAtividade}/fotos/{idFoto}
    @PutMapping("/{idFoto}")
    public ResponseEntity<Object> alterarFoto(@PathVariable int idFoto, @RequestBody EnviarFotosAtividade foto) {
        Map<String, Object> resultado = controller.alterar(idFoto, foto.getMembro().getId(), foto.getAtividade().getId());

        if (resultado.containsKey("erro"))
            return ResponseEntity.badRequest().body(new MembroErro(resultado.get("erro").toString()));

        return ResponseEntity.ok(new MembroErro(resultado.get("mensagem").toString()));
    }
}