package com.example.dminfo.view;

import com.example.dminfo.controller.ParametrosController;
import com.example.dminfo.model.Parametros;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequestMapping("apis/parametrizacao")
public class ParametrosView {

    @Autowired
    private ParametrosController parametros;

    @PostMapping
    @ResponseBody
    public ResponseEntity<Object> salvar(@RequestBody Parametros dadosDoForm) {
        try {
            Parametros pSalvo = parametros.salvar(dadosDoForm);
            return ResponseEntity.status(HttpStatus.CREATED).body(pSalvo);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar no backend: " + e.getMessage());
        }
    }
}