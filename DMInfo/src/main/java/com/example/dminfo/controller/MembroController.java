package com.example.dminfo.controller;

import com.example.dminfo.model.Erro;
import com.example.dminfo.model.Membro;
import com.example.dminfo.model.Usuario;
import com.example.dminfo.services.MembroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/membro")
public class MembroController {
    @Autowired
    private MembroService service;

    @GetMapping
    public ResponseEntity<Object> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Membro membro) {

        if (membro.getUsuario() == null || membro.getUsuario().getId() == 0)
            return ResponseEntity.badRequest().body(new Erro("Erro de Objeto", "ID do Usuário é obrigatório."));

        try {
            // O 'codigo' deve ser enviado no corpo do objeto 'membro'
            Membro novoMembro = service.salvar(membro);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoMembro);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Erro("Erro de Negócio", e.getMessage()));
        }
    }

    @GetMapping(value="get-by-id/{id}")
    ResponseEntity<Object> read(@PathVariable Integer id) {
        Membro membro = service.getById(id);

        if (membro == null) {
            return ResponseEntity.badRequest().body(new Erro("Erro de Banco", "Usuario não encontrado"));
        }

        return ResponseEntity.ok(membro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody Membro membroDetails) {
        try {
            Membro membroAtualizado = service.update(id, membroDetails);
            return ResponseEntity.ok(membroAtualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Erro("Erro de Banco", "Não foi possível atualizar"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().body(new Erro("Erro de busca", "Usuario não encontrado"));
        }
    }
}
