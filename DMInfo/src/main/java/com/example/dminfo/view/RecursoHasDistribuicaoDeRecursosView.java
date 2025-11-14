package com.example.dminfo.view;

import com.example.dminfo.controller.RecursoHasDistribuicaoDeRecursosController;
import com.example.dminfo.model.RecursoHasDistribuicaoDeRecursos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequestMapping("apis/distribuicao-itens")
public class RecursoHasDistribuicaoDeRecursosView {

    @Autowired
    private RecursoHasDistribuicaoDeRecursosController itemController;

    @GetMapping
    @ResponseBody
    public ResponseEntity<Object> listar() {
        try {
            return ResponseEntity.ok(itemController.listar());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar itens de distribuição: " + e.getMessage());
        }
    }

    @GetMapping("/por-distribuicao/{idDistribuicao}")
    @ResponseBody
    public ResponseEntity<Object> listarPorDistribuicao(@PathVariable Integer idDistribuicao) {
        try {
            return ResponseEntity.ok(itemController.listarPorDistribuicao(idDistribuicao));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar itens da distribuição: " + e.getMessage());
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Object> criar(@RequestBody RecursoHasDistribuicaoDeRecursos item) {
        try {
            RecursoHasDistribuicaoDeRecursos salvo = itemController.salvar(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar item da distribuição: " + e.getMessage());
        }
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Object> atualizar(@RequestBody RecursoHasDistribuicaoDeRecursos item) {
        try {
            itemController.atualizar(item);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar item da distribuição: " + e.getMessage());
        }
    }

    //CHAVE COMPOSTA
    //Exemplo de chamada: GET .../apis/distribuicao-itens/item?idRecurso=1&idDistribuicao=2
    @GetMapping("/item")
    @ResponseBody
    public ResponseEntity<Object> buscarPorId(
            @RequestParam Integer idRecurso,
            @RequestParam Integer idDistribuicao) {
        try {
            return ResponseEntity.ok(itemController.getById(idRecurso, idDistribuicao));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        }
    }


    @DeleteMapping
    @ResponseBody
    public ResponseEntity<Object> excluir(
            @RequestParam Integer idRecurso,
            @RequestParam Integer idDistribuicao) {
        try {
            itemController.excluir(idRecurso, idDistribuicao);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao excluir item da distribuição: " + e.getMessage());
        }
    }
}
