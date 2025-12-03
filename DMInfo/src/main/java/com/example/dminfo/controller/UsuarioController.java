package com.example.dminfo.controller;

import com.example.dminfo.model.Usuario;
import com.example.dminfo.dao.UsuarioDAO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsuarioController {

    public Map<String, Object> logar(String login, String senha) {
        Map<String, Object> response = new HashMap<>();
        UsuarioDAO dao = new UsuarioDAO();

        Usuario usuario = dao.getUsuario(login);

        if (usuario != null && usuario.getSenha().equals(senha)) {
            response.put("isLogado", true);
            response.put("usuario", usuario);
        } else {
            response.put("isLogado", false);
        }
        return response;
    }

    public List<Usuario> listar() {
        return new UsuarioDAO().get("");
    }

    public Usuario getById(int id) {
        return new UsuarioDAO().get(id);
    }

    public Usuario salvar(Usuario usuario) {
        return usuario.salvar();
    }

    public Usuario update(int id, Usuario usuarioDetails) {
        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuarioExistente = dao.get(id);

        if (usuarioExistente == null) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        if (!usuarioDetails.getLogin().equals(usuarioExistente.getLogin()) &&
                dao.getUsuario(usuarioDetails.getLogin()) != null) {
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

        if(usuarioDetails.getDtnasc() != null) {
            usuarioExistente.setDtnasc(usuarioDetails.getDtnasc());
        }

        if (dao.alterar(usuarioExistente)) {
            return usuarioExistente;
        }
        throw new RuntimeException("Erro ao atualizar usuário no banco de dados.");
    }

    public boolean excluir(int id) {
        return new UsuarioDAO().excluir(id);
    }

    public List<Usuario> buscar(String filtro) {
        return new UsuarioDAO().get(filtro);
    }
}