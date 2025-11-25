package com.example.dminfo.view;

import com.example.dminfo.controller.AdministradorController;
import com.example.dminfo.model.Administrador;
import com.example.dminfo.model.MembroErro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("apis/administrador")
public class AdministradorView {

    @Autowired
    private AdministradorController controller;

    @GetMapping
    public ResponseEntity<Object> listar() {
        return ResponseEntity.ok(controller.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {

        Administrador admin = controller.buscar(id);

        if (admin == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MembroErro("Administrador não encontrado."));
        }

        return ResponseEntity.ok(admin);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Administrador administrador) {

        try {
            Administrador novoAdmin = controller.salvar(administrador);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAdmin);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MembroErro(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id,
                                         @RequestBody Administrador dados) {

        try {
            Administrador atualizado = controller.update(id, dados);
            return ResponseEntity.ok(atualizado);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MembroErro(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {

        try {
            if (controller.excluir(id)) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MembroErro("Erro ao excluir."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MembroErro(e.getMessage()));
        }
    }
}
