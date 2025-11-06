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

    @GetMapping
    @ResponseBody
    public ResponseEntity<Object> exibir() {
        try {
            // Chama o service, que chama o DAO
            Parametros p = parametros.exibir();
            if (p != null) {
                return ResponseEntity.ok(p);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum parametro encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar no backend: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> atualizar(@PathVariable Integer id, @RequestBody Parametros dadosAtualizados) {
        try {
            // Define o ID do parâmetro a ser alterado
            dadosAtualizados.setId(id);

            // Reutiliza o método 'salvar' no Controller, que já tem a lógica de UPDATE
            Parametros pAtualizado = parametros.salvar(dadosAtualizados);

            // Retorna o objeto atualizado e o status 200 OK
            return ResponseEntity.ok(pAtualizado);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar no backend: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> excluir(@PathVariable Integer id) {
        try {
            parametros.excluir(id); // Chama o service
            return ResponseEntity.noContent().build(); // Retorna 204 No Content

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao excluir no backend: " + e.getMessage());
        }
    }
}