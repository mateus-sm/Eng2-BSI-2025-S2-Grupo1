package com.example.dminfo.view;

import com.example.dminfo.controller.AdministradorController;
import com.example.dminfo.model.Administrador;
import com.example.dminfo.util.MembroErro; // Reutilizando a classe de Erro
import com.example.dminfo.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("apis/administrador")
public class AdministradorView {

    @Autowired
    private AdministradorController controller;

    // Helper de validação de token
    private ResponseEntity<Object> checkToken(String token) {
        if (!Token.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MembroErro("Acesso não autorizado. Token inválido ou expirado."));
        }
        return null; // Token é válido
    }

    @GetMapping
    public ResponseEntity<Object> listar(@RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null) return tokenError;

        return ResponseEntity.ok(controller.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null) return tokenError;

        Administrador admin = controller.getById(id);
        if (admin == null) {
            return ResponseEntity.badRequest().body(new MembroErro("Administrador não encontrado."));
        }
        return ResponseEntity.ok(admin);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("Authorization") String token, @RequestBody Administrador administrador) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null) return tokenError;

        try {
            Administrador novoAdmin = controller.salvar(administrador);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAdmin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    // Endpoint para "desativar" (setar dtFim)
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader("Authorization") String token, @PathVariable Integer id, @RequestBody Administrador adminDetails) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null) return tokenError;

        try {
            // Espera um JSON simples, ex: {"dtFim": "2025-10-31"}
            Administrador adminAtualizado = controller.update(id, adminDetails);
            return ResponseEntity.ok(adminAtualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    // Endpoint para "excluir" (hard delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null) return tokenError;

        try {
            if (controller.excluir(id)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().body(new MembroErro("Erro ao excluir."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }
}