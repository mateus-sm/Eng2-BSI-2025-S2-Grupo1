package com.example.dminfo.dao;

import com.example.dminfo.model.Usuario;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Repository
public class UsuarioDAO {

    // Helper para construir o objeto Usuario a partir do banco
    private Usuario buildUsuario(ResultSet rs) throws SQLException {
        // Trata datas que podem ser nulas
        LocalDate dtNasc = (rs.getDate("dtnasc") != null) ? rs.getDate("dtnasc").toLocalDate() : null;
        LocalDate dtIni = (rs.getDate("dtini") != null) ? rs.getDate("dtini").toLocalDate() : null;
        LocalDate dtFim = (rs.getDate("dtfim") != null) ? rs.getDate("dtfim").toLocalDate() : null;

        return new Usuario(
                rs.getInt("id_usuario"), // <-- CORRIGIDO
                rs.getString("nome"),
                rs.getString("senha"),
                rs.getString("usuario"), // 'usuario' é o login <-- CORRIGIDO
                rs.getString("telefone"),
                rs.getString("email"),
                rs.getString("rua"),
                rs.getString("cidade"),
                rs.getString("bairro"),
                rs.getString("cep"),
                rs.getString("uf"),
                rs.getString("cpf"),
                dtNasc,
                dtIni,
                dtFim
        );
    }

    // Metodo que o Membro.java precisa (corrigido)
    public Usuario get(int id) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = " + id; // <-- CORRIGIDO
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildUsuario(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Usuário por ID: " + e.getMessage());
        }
        return null;
    }

    // Metodo do exemplo (corrigido)
    public Usuario getUsuario(String login) {
        String sql = "SELECT * FROM usuario WHERE usuario = '" + login + "'"; // <-- CORRIGIDO
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildUsuario(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Usuário por Login: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos os usuários (filtro vazio) ou filtra.
     * @param filtro Cláusula WHERE (ex: "WHERE nome LIKE 'Eduardo%'")
     * @return Lista de usuários
     */
    public List<Usuario> get(String filtro) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario " + filtro;

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null) {
                while (rs.next()) {
                    usuarios.add(buildUsuario(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar Usuários: " + e.getMessage());
        }
        return usuarios;
    }

    /**
     * Grava um novo usuário no banco.
     */
    public Usuario gravar(Usuario usuario) {
        String sql = String.format("INSERT INTO usuario (nome, senha, usuario, telefone, email, rua, cidade, bairro, cep, uf, cpf, dtnasc, dtini) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s') RETURNING id_usuario",
                usuario.getNome(),
                usuario.getSenha(), // Lembre-se: em um projeto real, isso deve ser criptografado
                usuario.getLogin(),
                usuario.getTelefone(),
                usuario.getEmail(),
                usuario.getRua(),
                usuario.getCidade(),
                usuario.getBairro(),
                usuario.getCep(),
                usuario.getUf(),
                usuario.getCpf(),
                usuario.getDtnasc().toString(),
                usuario.getDtini().toString()
        );

        ResultSet rs = SingletonDB.getConexao().consultar(sql); // 'consultar' para pegar o RETURNING
        try {
            if (rs != null && rs.next()) {
                usuario.setId(rs.getInt("id_usuario"));
                return usuario;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar Usuário: " + e.getMessage());
        }
        return null;
    }

    /**
     * Altera um usuário existente no banco.
     */
    public boolean alterar(Usuario usuario) {
        String sql = String.format("UPDATE usuario SET nome = '%s', senha = '%s', usuario = '%s', telefone = '%s', " +
                        "email = '%s', rua = '%s', cidade = '%s', bairro = '%s', cep = '%s', uf = '%s', cpf = '%s', dtnasc = '%s' " +
                        "WHERE id_usuario = %d",
                usuario.getNome(),
                usuario.getSenha(),
                usuario.getLogin(),
                usuario.getTelefone(),
                usuario.getEmail(),
                usuario.getRua(),
                usuario.getCidade(),
                usuario.getBairro(),
                usuario.getCep(),
                usuario.getUf(),
                usuario.getCpf(),
                usuario.getDtnasc().toString(),
                usuario.getId()
        );
        return SingletonDB.getConexao().executar(sql); // 'executar' para UPDATE
    }

    /**
     * Exclusão LÓGICA: Define a data de fim (dtfim) para o usuário.
     */
    public boolean excluir(int id) {
        String sql = String.format("UPDATE usuario SET dtfim = '%s' WHERE id_usuario = %d",
                LocalDate.now().toString(),
                id
        );
        return SingletonDB.getConexao().executar(sql);
    }
}