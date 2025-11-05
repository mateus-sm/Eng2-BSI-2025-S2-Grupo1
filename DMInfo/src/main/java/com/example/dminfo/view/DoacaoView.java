package com.example.dminfo.view;

import com.example.dminfo.controller.DoacaoController;
import com.example.dminfo.model.Doacao;
import com.example.dminfo.util.MembroErro;
import com.example.dminfo.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("apis/doacao")
public class DoacaoView {

    @Autowired
    private DoacaoController controller;

    private ResponseEntity<Object> checkToken(String token) {
        if (!Token.validarToken(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MembroErro("Acesso não autorizado."));
        return null; //Token é válido
    }

    @GetMapping
    public ResponseEntity<Object> listar(@RequestHeader("Authorization") String token) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;
        return ResponseEntity.ok(controller.listar());
    }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestHeader("Authorization") String token, @RequestBody Doacao doacao) {
        ResponseEntity<Object> tokenError = checkToken(token);
        if (tokenError != null)
            return tokenError;

        try{
            //O JSON de entrada deve ser {"id_doador": {"id": 1}, "id_admin": {"id": 1}, "valor": 100.0, "observacao": "..."}
            Doacao salva = controller.salvar(doacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(salva);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }
}