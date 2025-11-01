package com.example.dminfo.view;

import com.example.dminfo.util.MembroErro;
import com.example.dminfo.model.Membro;
import com.example.dminfo.controller.MembroController;
import com.example.dminfo.util.Token; // <-- 1. Importe o Token
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("apis/membro")
public class MembroView {

    @Autowired
    private MembroController controller;

    /**
     * Helper (função de ajuda) para verificar o token
     */
    private ResponseEntity<Object> checkToken(String token) {
        if (!Token.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MembroErro("Acesso não autorizado. Token inválido ou expirado."));
        }
        return null; // Token é válido
    }

    @GetMapping
    public ResponseEntity<Object> listar(@RequestHeader("Authorization") String token) {
        // 2. Valida o token
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null) return tokenError;

        // 3. Se o token for válido, executa a lógica normal
        return ResponseEntity.ok(controller.listar());
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("Authorization") String token, @RequestBody Membro membro) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null) return tokenError;

        try {
            Membro novoMembro = controller.salvar(membro);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoMembro);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @GetMapping(value="/get-by-id/{id}")
    public ResponseEntity<Object> read(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null) return tokenError;

        Membro membro = controller.getById(id);
        if (membro == null) {
            return ResponseEntity.badRequest().body(new MembroErro("Membro não encontrado"));
        }
        return ResponseEntity.ok(membro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader("Authorization") String token, @PathVariable Integer id, @RequestBody Membro membroDetails) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null) return tokenError;

        try {
            Membro membroAtualizado = controller.update(id, membroDetails);
            return ResponseEntity.ok(membroAtualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

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