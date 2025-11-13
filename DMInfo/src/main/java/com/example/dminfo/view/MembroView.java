package com.example.dminfo.view;

import com.example.dminfo.model.MembroErro;
import com.example.dminfo.model.Membro;
import com.example.dminfo.controller.MembroController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("apis/membro")
public class MembroView {

    @Autowired
    private MembroController controller;

    @GetMapping
    public ResponseEntity<Object> listar(@RequestParam(required = false) String filtro) {
        return ResponseEntity.ok(controller.listar(filtro));
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Membro membro) {
        try {
            Membro novoMembro = controller.salvar(membro);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoMembro);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @GetMapping(value="/get-by-id/{id}")
    public ResponseEntity<Object> read(@PathVariable Integer id) {
        Membro membro = controller.getById(id);
        if (membro == null) {
            return ResponseEntity.badRequest().body(new MembroErro("Membro não encontrado"));
        }
        return ResponseEntity.ok(membro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody Membro membroDetails) {
        try {
            Membro membroAtualizado = controller.update(id, membroDetails);
            return ResponseEntity.ok(membroAtualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
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

    //comunicação membro_atividade
    @GetMapping("/atividade/{idCriacao}")
    public ResponseEntity<Object> listarMembrosAtividade(@PathVariable Integer idCriacao) {
        try {
            List<Integer> idsMembros = controller.listarMembrosPorAtividade(idCriacao);
            return ResponseEntity.ok(idsMembros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MembroErro("Erro ao listar membros da atividade: " + e.getMessage()));
        }
    }

    // NOVO: Endpoint para adicionar um membro a uma atividade
    @PostMapping("/atividade/{idCriacao}/{idMembro}")
    public ResponseEntity<Object> adicionarMembroAtividade(@PathVariable Integer idCriacao, @PathVariable Integer idMembro) {
        try {
            if (controller.adicionarMembroAtividade(idCriacao, idMembro)) {
                return ResponseEntity.ok("Membro associado à atividade com sucesso.");
            }
            return ResponseEntity.badRequest().body(new MembroErro("Falha ao associar membro."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MembroErro("Erro ao salvar associação: " + e.getMessage()));
        }
    }

    // NOVO: Endpoint para remover um membro de uma atividade
    @DeleteMapping("/atividade/{idCriacao}/{idMembro}")
    public ResponseEntity<Object> removerMembroAtividade(@PathVariable Integer idCriacao, @PathVariable Integer idMembro) {
        try {
            if (controller.removerMembroAtividade(idCriacao, idMembro)) {
                return ResponseEntity.ok("Associação removida com sucesso.");
            }
            return ResponseEntity.badRequest().body(new MembroErro("Falha ao remover associação."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MembroErro("Erro ao remover associação: " + e.getMessage()));
        }
    }

}