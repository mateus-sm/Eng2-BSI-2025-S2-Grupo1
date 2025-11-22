// Local: view/MensalidadeView.java
package com.example.dminfo.view;

import com.example.dminfo.controller.MensalidadeController;
import com.example.dminfo.model.Mensalidade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("apis/mensalidade")
public class MensalidadeView {

    @Autowired
    private MensalidadeController controller;

    @GetMapping("/listar")
    @ResponseBody
    public ResponseEntity<Object> listar() {
        try {
            return ResponseEntity.ok(controller.listar("")); // Lista todos
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<Object> listarComFiltro(@RequestParam(required = false, defaultValue = "") String nome) {
        try {
            return ResponseEntity.ok(controller.listar(nome));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @PostMapping("/salvar")
    @ResponseBody
    public ResponseEntity<Object> salvar(@RequestBody Mensalidade mensalidade) {
        try {
            Mensalidade salva = controller.salvar(mensalidade);
            return ResponseEntity.status(HttpStatus.CREATED).body(salva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/buscar/{id}")
    @ResponseBody
    public ResponseEntity<Object> buscarPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(controller.getById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        }
    }

    @DeleteMapping("/excluir/{id}")
    @ResponseBody
    public ResponseEntity<Object> excluir(@PathVariable Integer id) {
        try {
            controller.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/mesano")
    @ResponseBody
    public ResponseEntity<Object> listarMesAno(@RequestParam int mes, @RequestParam int ano) {
        try {
            return ResponseEntity.ok(controller.listarMesAno(mes, ano));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar por mês/ano: " + e.getMessage());
        }
    }

    @GetMapping("/membro/{id}")
    @ResponseBody
    public ResponseEntity<Object> listarMembro(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(controller.listarMembro(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar por membro: " + e.getMessage());
        }
    }
}