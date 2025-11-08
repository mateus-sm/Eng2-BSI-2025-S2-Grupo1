package com.example.dminfo.view;

import com.example.dminfo.controller.DoacaoController;
import com.example.dminfo.model.Doacao;
import com.example.dminfo.util.MembroErro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("apis/doacao")
public class DoacaoView {

    @Autowired
    private DoacaoController controller;

    @GetMapping
    public ResponseEntity<Object> listar() {
        return ResponseEntity.ok(controller.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscar(@PathVariable int id) {
        Doacao doacao = controller.buscar(id);
        if (doacao == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(doacao);
    }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody Doacao doacao) {
        try {
            Doacao salva = controller.salvar(doacao);
            if (salva == null)
                return ResponseEntity.badRequest().body(new MembroErro("Não foi possível gravar a doação. Verifique os dados."));
            return ResponseEntity.status(HttpStatus.CREATED).body(salva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable int id, @RequestBody Doacao doacao) {
        try {
            doacao.setId_doacao(id);
            Doacao atualizada = controller.atualizar(doacao);

            if (atualizada == null)
                return ResponseEntity.badRequest().body(new MembroErro("Não foi possível atualizar a doação. Verifique os dados."));

            return ResponseEntity.ok(atualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> excluir(@PathVariable int id) {
        try {
            if (controller.excluir(id))
                return ResponseEntity.noContent().build();

            return ResponseEntity.badRequest().body(new MembroErro("Não foi possível excluir a doação."));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }
}