package com.example.dminfo.view;

import com.example.dminfo.controller.ParametrosController;
import com.example.dminfo.model.Parametros;
import com.example.dminfo.util.MembroErro;
import com.example.dminfo.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("apis/parametros")
public class ParametrosView {

    @Autowired
    private ParametrosController controller; // O @Service

    private ResponseEntity<Object> checkToken(String token) {
        if (!Token.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MembroErro("Acesso não autorizado."));
        }
        return null; // Token é válido
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null) return tokenError;

        Parametros p = controller.exibir();
        if (p == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MembroErro("Nenhum parâmetro cadastrado."));
        }
        return ResponseEntity.ok(p);
    }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestHeader("Authorization") String token, @RequestBody Parametros parametro) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null) return tokenError;

        try {
            // Este endpoint único lida com CREATE e UPDATE
            Parametros salvo = controller.salvar(parametro);
            return ResponseEntity.ok(salvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }
}