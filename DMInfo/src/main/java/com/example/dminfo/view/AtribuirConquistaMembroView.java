package com.example.dminfo.view;

import com.example.dminfo.dao.AtribuirConquistaMembroDAO;
import com.example.dminfo.model.AtribuirConquistaMembro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequestMapping("apis/atribuirconquistamembro")
public class AtribuirConquistaMembroView {

    @Autowired
    private AtribuirConquistaMembroDAO dao;

    @GetMapping
    @ResponseBody
    public ResponseEntity<Object> listar() {
        try {
            return ResponseEntity.ok(dao.listar());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar atribuições: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> gravar(@RequestBody AtribuirConquistaMembro acm) {
        try {
            AtribuirConquistaMembro salvo = dao.gravar(acm);
            if (salvo != null)
                return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
            else
                return ResponseEntity.badRequest().body("Erro ao gravar atribuição");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Object> atualizar(@RequestBody AtribuirConquistaMembro atribuirConquistaMembro) {
        try {
            dao.alterar(atribuirConquistaMembro);
            return ResponseEntity.ok(atribuirConquistaMembro);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar atribuição: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> buscarPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(dao.getById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> excluir(@PathVariable Integer id) {
        try {
            dao.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao excluir atribuição: " + e.getMessage());
        }
    }
}
