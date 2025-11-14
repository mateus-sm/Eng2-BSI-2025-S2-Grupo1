package com.example.dminfo.view;

import com.example.dminfo.controller.CriarRealizacaoAtividadesController;
import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.util.MembroErro;
import com.example.dminfo.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("apis/realizacao-atividades")
public class CriarRealizacaoAtividadesView {

    @Autowired
    private CriarRealizacaoAtividadesController controller;

    private ResponseEntity<Object> checkToken(String token) {
        // Lógica de validação de token omitida para simplificação
        // if (token == null || !Token.validarToken(token))
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MembroErro("Acesso não autorizado ou Token inválido."));
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Integer id) {
        /* checkToken(token); */
        try {
            CriarRealizacaoAtividades atividade = controller.getById(id);

            if (atividade != null) {
                // Sucesso: retorna o objeto encontrado (código 200 OK)
                return ResponseEntity.ok(atividade);
            } else {
                // Não encontrado (código 404 NOT FOUND)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MembroErro("Atividade não encontrada."));
            }
        } catch (Exception e) {
            // Erro interno (código 500 INTERNAL SERVER ERROR)
            return ResponseEntity.internalServerError().body(new MembroErro("Erro ao buscar atividade: " + e.getMessage()));
        }
    }
    // LISTAR (GET /apis/realizacao-atividades)
    @GetMapping
    public ResponseEntity<Object> listar(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) String ordenarPor) {
        /* checkToken(token); */
        try {
            List<CriarRealizacaoAtividades> atividades = controller.listar(descricao, ordenarPor);
            return ResponseEntity.ok(atividades);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MembroErro("Erro ao listar atividades: " + e.getMessage()));
        }
    }

    // SALVAR (POST /apis/realizacao-atividades) - A ser implementado
    @PostMapping
    public ResponseEntity<Object> salvar(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody CriarRealizacaoAtividades atividade) {
        /* checkToken(token); */
        try {
            CriarRealizacaoAtividades novaAtividade = controller.salvar(atividade);
            if (novaAtividade == null) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new MembroErro("Método salvar não implementado."));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(novaAtividade);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    // FINALIZAR ATIVIDADE (PUT /apis/realizacao-atividades/{id}/finalizar)
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Object> finalizarAtividade(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Integer id,
            @RequestBody CriarRealizacaoAtividades atividade) {
        /* checkToken(token); */
        try {
            // Garante que o ID do path seja usado
            atividade.setId(id);

            if (controller.finalizar(atividade))
                return ResponseEntity.ok().build();
            else
                return ResponseEntity.badRequest().body(new MembroErro("Erro ao finalizar atividade ou atividade não encontrada."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MembroErro("Falha interna ao finalizar: " + e.getMessage()));
        }
    }

    // ATUALIZAR (PUT /apis/realizacao-atividades/{id}) - A ser implementado
    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Integer id,
            @RequestBody CriarRealizacaoAtividades atividade) {
        /* checkToken(token); */
        try {
            CriarRealizacaoAtividades atualizada = controller.atualizar(id, atividade);
            if (atualizada == null) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new MembroErro("Método atualizar não implementado."));
            }
            return ResponseEntity.ok(atualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    // EXCLUIR (DELETE /apis/realizacao-atividades/{id}) - A ser implementado
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Integer id) {
        /* checkToken(token); */
        try {
            if (controller.excluir(id))
                return ResponseEntity.noContent().build();
            else
                return ResponseEntity.badRequest().body(new MembroErro("Erro ao excluir atividade."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }
}