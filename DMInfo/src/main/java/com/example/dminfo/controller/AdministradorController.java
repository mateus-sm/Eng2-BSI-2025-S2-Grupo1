package com.example.dminfo.controller;

import com.example.dminfo.model.Erro;
import com.example.dminfo.model.Administrador;
import com.example.dminfo.services.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/administrador")
public class AdministradorController {
    @Autowired
    private AdministradorService service;

    @GetMapping
    public ResponseEntity<Object> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Administrador administrador) {

        if (administrador.getUsuario() == null || administrador.getUsuario().getId() == 0)
            return ResponseEntity.badRequest().body(new Erro("Erro de Objeto", "ID do Usuário é obrigatório."));

        try {
            // O 'codigo' deve ser enviado no corpo do objeto 'membro'
            Administrador novoAdministrador = service.salvar(administrador);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAdministrador);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Erro("Erro de Negócio", e.getMessage()));
        }
    }

    @GetMapping(value="get-by-id/{id}")
    ResponseEntity<Object> read(@PathVariable Integer id) {
        Administrador administrador = service.getById(id);

        if (administrador == null) {
            return ResponseEntity.badRequest().body(new Erro("Erro de Banco", "Usuario não encontrado"));
        }

        return ResponseEntity.ok(administrador);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody Administrador administradorDetails) {
        try {
            Administrador administradorAtualizado = service.update(id, administradorDetails);
            return ResponseEntity.ok(administradorAtualizado);
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
