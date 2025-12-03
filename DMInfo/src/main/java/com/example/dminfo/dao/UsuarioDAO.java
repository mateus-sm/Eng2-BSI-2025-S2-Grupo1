package com.example.dminfo.dao;

import com.example.dminfo.model.Usuario;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioDAO {

    private Usuario buildUsuario(ResultSet rs) throws SQLException {
        LocalDate dtNasc = (rs.getDate("dtnasc") != null) ? rs.getDate("dtnasc").toLocalDate() : null;
        LocalDate dtIni = (rs.getDate("dtini") != null) ? rs.getDate("dtini").toLocalDate() : null;
        LocalDate dtFim = (rs.getDate("dtfim") != null) ? rs.getDate("dtfim").toLocalDate() : null;

        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nome"),
                rs.getString("senha"),
                rs.getString("usuario"),
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

    public Usuario get(int id) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        try (PreparedStatement stmt = SingletonDB.getConexao().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return buildUsuario(rs); // Usa o helper
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Usuario por ID: " + e.getMessage());
        }
        return null;
    }

    public Usuario getUsuario(String login) {
        String sql = "SELECT * FROM usuario WHERE usuario = ? AND dtfim IS NULL";
        try (PreparedStatement stmt = SingletonDB.getConexao().getConnection().prepareStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return buildUsuario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Usuario por login: " + e.getMessage());
        }
        return null;
    }

    public Usuario getUsuarioByTelefone(String telefone) {
        // Atenção: O telefone no banco deve estar limpo (apenas números) se o front mandar limpo
        String sql = String.format("SELECT * FROM usuario WHERE telefone = '%s'", telefone);

        try (ResultSet rs = SingletonDB.getConexao().consultar(sql)) {
            if (rs != null && rs.next()) {
                return buildUsuario(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuário por telefone: " + e.getMessage());
        }
        return null;
    }

    public Usuario getUsuarioByEmail(String email) {
        String sql = "SELECT * FROM usuario WHERE email = ? AND dtfim IS NULL";
        try (PreparedStatement stmt = SingletonDB.getConexao().getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return buildUsuario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Usuario por email: " + e.getMessage());
        }
        return null;
    }

    public Usuario getUsuarioByCpf(String cpf) {
        String sql = "SELECT * FROM usuario WHERE cpf = ? AND dtfim IS NULL";
        try (PreparedStatement stmt = SingletonDB.getConexao().getConnection().prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return buildUsuario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Usuario por CPF: " + e.getMessage());
        }
        return null;
    }

    public List<Usuario> get(String filtro) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE nome ILIKE ? AND dtfim IS NULL ORDER BY nome";

        try (PreparedStatement stmt = SingletonDB.getConexao().getConnection().prepareStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                usuarios.add(buildUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar Usuarios: " + e.getMessage());
        }
        return usuarios;
    }


    public Usuario gravar(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, senha, usuario, telefone, email, rua, cidade, " +
                "bairro, cep, uf, cpf, dtnasc, dtini) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id_usuario";

        try (PreparedStatement stmt = SingletonDB.getConexao().getConnection().prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getLogin());
            stmt.setString(4, usuario.getTelefone());
            stmt.setString(5, usuario.getEmail());
            stmt.setString(6, usuario.getRua());
            stmt.setString(7, usuario.getCidade());
            stmt.setString(8, usuario.getBairro());
            stmt.setString(9, usuario.getCep());
            stmt.setString(10, usuario.getUf());
            stmt.setString(11, usuario.getCpf());

            if (usuario.getDtnasc() != null) {
                stmt.setDate(12, Date.valueOf(usuario.getDtnasc()));
            } else {
                stmt.setNull(12, java.sql.Types.DATE);
            }

            if (usuario.getDtini() != null) {
                stmt.setDate(13, Date.valueOf(usuario.getDtini()));
            } else {
                stmt.setNull(13, java.sql.Types.DATE);
            }

            ResultSet rs = stmt.executeQuery();

            if (rs != null && rs.next()) {
                usuario.setId(rs.getInt("id_usuario"));
                return usuario;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar Usuário (DAO): " + e.getMessage());
        }
        return null;
    }

    public boolean alterar(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, senha = ?, usuario = ?, telefone = ?, " +
                "email = ?, rua = ?, cidade = ?, bairro = ?, cep = ?, uf = ?, cpf = ?, dtnasc = ? " +
                "WHERE id_usuario = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getConnection().prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getLogin());
            stmt.setString(4, usuario.getTelefone());
            stmt.setString(5, usuario.getEmail());
            stmt.setString(6, usuario.getRua());
            stmt.setString(7, usuario.getCidade());
            stmt.setString(8, usuario.getBairro());
            stmt.setString(9, usuario.getCep());
            stmt.setString(10, usuario.getUf());
            stmt.setString(11, usuario.getCpf());

            if (usuario.getDtnasc() != null) {
                stmt.setDate(12, Date.valueOf(usuario.getDtnasc()));
            } else {
                stmt.setNull(12, java.sql.Types.DATE);
            }

            stmt.setInt(13, usuario.getId());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao alterar Usuário (DAO): " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        // 1. Query com placeholders '?'
        String sql = "UPDATE usuario SET dtfim = ? WHERE id_usuario = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getConnection().prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, id);

            int linhasAfetadas = stmt.executeUpdate();


            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao excluir Usuário (DAO): " + e.getMessage());
            return false;
        }
    }
}