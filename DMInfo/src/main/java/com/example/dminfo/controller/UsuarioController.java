package com.example.dminfo.controller;

import com.example.dminfo.model.Usuario;
import com.example.dminfo.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsuarioController {

    // Injeta o "Fat Model"
    @Autowired
    private Usuario usuarioModel;

    /**
     * Tenta logar um usuário.
     * Segue o padrão do exemplo proj-bga.
     */
    public Map<String, Object> logar(String login, String senha) {
        Map<String, Object> response = new HashMap<>();

        // A lógica de negócios está DENTRO do 'usuarioModel'
        if (usuarioModel.logar(login, senha)) {
            response.put("isLogado", true);
            // Assumindo que você tem uma classe Token.java no pacote util
            response.put("token", Token.gerarToken(login));

            // Você pode adicionar outros dados do usuário se precisar
            // response.put("nome", usuarioModel.getNome()); // Cuidado, 'usuarioModel' é um bean
        } else {
            response.put("isLogado", false);
            response.put("token", "");
        }

        return response;
    }

    // --- NOVOS MÉTODOS CRUD ABAIXO ---

    public List<Usuario> listar() {
        return usuarioModel.listar();
    }

    public Usuario getById(int id) {
        return usuarioModel.getById(id);
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioModel.salvar(usuario);
    }

    public Usuario update(int id, Usuario usuarioDetails) {
        return usuarioModel.update(id, usuarioDetails);
    }

    public boolean excluir(int id) {
        return usuarioModel.excluir(id);
    }
}