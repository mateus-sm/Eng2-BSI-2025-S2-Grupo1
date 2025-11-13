package com.example.dminfo.view;

import com.example.dminfo.model.MembroErro;
import com.example.dminfo.controller.UsuarioController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.dminfo.model.Usuario;
import org.springframework.http.HttpStatus;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("apis/usuario")
public class UsuarioView {

    @Autowired
    private UsuarioController controller;



    @PostMapping("/login")
    public ResponseEntity<Object> logar(@RequestBody Map<String, String> dados) {
        String login = dados.get("usuario"); // 'usuario' é o campo de login
        String senha = dados.get("senha");

        if (login == null || login.isEmpty() || senha == null || senha.isEmpty()) {
            return ResponseEntity.badRequest().body(new MembroErro("Usuário e senha são obrigatórios."));
        }

        try {

            Map<String, Object> json = controller.logar(login, senha);

            if ((boolean) json.get("isLogado")) {
                return ResponseEntity.ok(json);
            } else {
                return ResponseEntity.badRequest().body(new MembroErro("Login ou senha inválidos."));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MembroErro("Erro interno: " + e.getMessage()));
        }
    }


    @GetMapping
    public ResponseEntity<Object> listar() {
        try {
            return ResponseEntity.ok(controller.listar());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable int id) {
        try {
            Usuario usuario = controller.getById(id);
            if (usuario == null) {
                return ResponseEntity.badRequest().body(new MembroErro("Usuário não encontrado."));
            }
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = controller.salvar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable int id, @RequestBody Usuario usuarioDetails) {
        try {
            Usuario usuarioAtualizado = controller.update(id, usuarioDetails);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id) {
        try {
            if (controller.excluir(id)) {
                // Retorna 200 OK com uma mensagem, pois foi exclusão lógica
                return ResponseEntity.ok(Map.of("mensagem", "Usuário desativado com sucesso."));
            } else {
                return ResponseEntity.badRequest().body(new MembroErro("Erro ao desativar usuário."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
        }
    }
}