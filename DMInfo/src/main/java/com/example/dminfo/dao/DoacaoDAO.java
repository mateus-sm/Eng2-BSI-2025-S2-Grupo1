package com.example.dminfo.dao;

import com.example.dminfo.model.Administrador;
import com.example.dminfo.model.Doacao;
import com.example.dminfo.model.Doador;
import com.example.dminfo.model.Usuario;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DoacaoDAO {

    // Helper para construir a Doacao com Doador e Admin (e Usuário do Admin)
    private Doacao buildDoacao(ResultSet rs) throws SQLException {
        Doacao doacao = new Doacao();
        doacao.setId_doacao(rs.getInt("id_doacao"));
        doacao.setData(rs.getDate("data").toLocalDate());
        doacao.setValor(rs.getDouble("valor"));
        doacao.setObservacao(rs.getString("observacao"));

        // Monta Doador
        Doador doador = new Doador();
        doador.setId(rs.getInt("id_doador"));
        doador.setNome(rs.getString("doador_nome"));
        doacao.setId_doador(doador);

        // Monta Admin
        Administrador admin = new Administrador();
        admin.setId(rs.getInt("id_admin"));

        // Monta Usuário do Admin
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id_usuario"));
        usuario.setNome(rs.getString("usuario_nome"));
        admin.setUsuario(usuario);
        doacao.setId_admin(admin);

        return doacao;
    }

    public List<Doacao> get(String filtro) {
        List<Doacao> doacoes = new ArrayList<>();
        String sql = "SELECT d.*, " +
                "   don.nome AS doador_nome, " +
                "   a.id_admin, u.id_usuario, u.nome AS usuario_nome " +
                "FROM doacao d " +
                "JOIN doador don ON d.id_doador = don.id_doador " +
                "JOIN administrador a ON d.id_admin = a.id_admin " +
                "JOIN usuario u ON a.id_usuario = u.id_usuario " +
                filtro + " ORDER BY d.data DESC";

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null) {
                while (rs.next()) {
                    doacoes.add(buildDoacao(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar Doações: " + e.getMessage());
        }
        return doacoes;
    }

    public Doacao gravar(Doacao doacao) {
        String sql = String.format("INSERT INTO doacao (id_doador, id_admin, data, valor, observacao) " +
                        "VALUES (%d, %d, '%s', %f, '%s') RETURNING id_doacao",
                doacao.getId_doador().getId(),
                doacao.getId_admin().getId(),
                doacao.getData().toString(),
                doacao.getValor(),
                doacao.getObservacao()
        );

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                doacao.setId_doacao(rs.getInt("id_doacao"));
                return doacao;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar Doação: " + e.getMessage());
        }
        return null;
    }
}