package com.example.dminfo.controller;

import com.example.dminfo.model.Usuario;
import com.example.dminfo.dao.UsuarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsuarioController {

    @Autowired
    private UsuarioDAO usuarioDAO;

    public Map<String, Object> logar(String login, String senha) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuario = usuarioDAO.getUsuario(login); // Busca usuário ativo

        if (usuario != null && usuario.getSenha().equals(senha)) {
            response.put("isLogado", true);
            response.put("usuario", usuario); // Retorna o usuário para a sessão
        } else {
            response.put("isLogado", false);
        }
        return response;
    }

    public List<Usuario> listar() {
        return usuarioDAO.get("");
    }

    public Usuario getById(int id) {
        return usuarioDAO.get(id);
    }



    public Usuario update(int id, Usuario usuarioDetails) {
        Usuario usuarioExistente = usuarioDAO.get(id);
        if (usuarioExistente == null) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        if (!usuarioDetails.getLogin().equals(usuarioExistente.getLogin()) &&
                usuarioDAO.getUsuario(usuarioDetails.getLogin()) != null) {
            throw new RuntimeException("Este 'usuário' (login) já pertence a outra conta.");
        }

        usuarioExistente.setNome(usuarioDetails.getNome());
        usuarioExistente.setSenha(usuarioDetails.getSenha());
        usuarioExistente.setLogin(usuarioDetails.getLogin());
        usuarioExistente.setTelefone(usuarioDetails.getTelefone());
        usuarioExistente.setEmail(usuarioDetails.getEmail());
        usuarioExistente.setRua(usuarioDetails.getRua());
        usuarioExistente.setCidade(usuarioDetails.getCidade());
        usuarioExistente.setBairro(usuarioDetails.getBairro());
        usuarioExistente.setCep(usuarioDetails.getCep());
        usuarioExistente.setUf(usuarioDetails.getUf());
        usuarioExistente.setCpf(usuarioDetails.getCpf());
        usuarioExistente.setDtnasc(usuarioDetails.getDtnasc());

        if (usuarioDAO.alterar(usuarioExistente)) {
            return usuarioExistente;
        }
        throw new RuntimeException("Erro ao atualizar usuário no banco de dados.");
    }

    public boolean excluir(int id) {
        return usuarioDAO.excluir(id);
    }

    public List<Usuario> buscar(String filtro) {
        return usuarioDAO.get(filtro);
    }

    public Usuario salvar(Usuario usuario) {
        usuario.setDtini(LocalDate.now());

        return usuarioDAO.gravar(usuario);
    }
}