package com.example.dminfo.dao;

import com.example.dminfo.model.Administrador;
import com.example.dminfo.model.Doacao;
import com.example.dminfo.model.Doador;
import com.example.dminfo.model.Usuario;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class DoacaoDAO {

    private Doacao buildDoacao(ResultSet rs) throws SQLException {
        Doacao doacao = new Doacao();
        doacao.setId_doacao(rs.getInt("id_doacao"));
        doacao.setData(rs.getDate("data").toLocalDate());
        doacao.setValor(rs.getDouble("valor"));
        doacao.setObservacao(rs.getString("observacao"));

        Doador doador = new Doador();
        doador.setId(rs.getInt("id_doador"));
        doador.setNome(rs.getString("doador_nome"));
        doacao.setId_doador(doador);

        Administrador admin = new Administrador();
        admin.setId(rs.getInt("id_admin"));

        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id_usuario"));
        usuario.setNome(rs.getString("usuario_nome"));
        admin.setUsuario(usuario);

        doacao.setId_admin(admin);
        return doacao;
    }

    public Doacao get(int id, Conexao conexao) {
        String sql = "SELECT d.*, don.nome AS doador_nome, a.id_usuario, u.nome AS usuario_nome " +
                "FROM doacao d " +
                "JOIN doador don ON d.id_doador = don.id_doador " +
                "JOIN administrador a ON d.id_admin = a.id_admin " +
                "JOIN usuario u ON a.id_usuario = u.id_usuario " +
                "WHERE d.id_doacao = " + id;

        ResultSet rs = conexao.consultar(sql);
        try {
            if (rs != null && rs.next())
                return buildDoacao(rs);
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Doação por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Doacao> readAll(String filtro, Conexao conexao) {
        List<Doacao> doacoes = new ArrayList<>();
        // O filtro pode ser adaptado conforme necessidade, aqui mantive vazio ou genérico
        String whereClause = (filtro != null && !filtro.isEmpty()) ? filtro : "";

        String sql = "SELECT d.*, don.nome AS doador_nome, a.id_usuario, u.nome AS usuario_nome " +
                "FROM doacao d " +
                "JOIN doador don ON d.id_doador = don.id_doador " +
                "JOIN administrador a ON d.id_admin = a.id_admin " +
                "JOIN usuario u ON a.id_usuario = u.id_usuario " +
                whereClause + " ORDER BY d.data DESC";

        ResultSet rs = conexao.consultar(sql);
        try {
            if(rs != null)
                while(rs.next())
                    doacoes.add(buildDoacao(rs));
        }catch(SQLException e){
            System.out.println("Erro ao listar Doações: " + e.getMessage());
        }
        return doacoes;
    }

    public Doacao gravar(Doacao doacao, Conexao conexao) {
        if (doacao == null) return null;

        String obsOriginal = doacao.getObservacao() != null ? doacao.getObservacao() : "";
        String obsEscapada = obsOriginal.replace("'", "''");

        String sql = String.format(Locale.US,
                "INSERT INTO doacao (id_doador, id_admin, data, valor, observacao) " +
                        "VALUES (%d, %d, '%s', %f, '%s') RETURNING id_doacao",
                doacao.getId_doador().getId(),
                doacao.getId_admin().getId(),
                doacao.getData().toString(),
                doacao.getValor(),
                obsEscapada
        );

        ResultSet rs = conexao.consultar(sql);
        try{
            if(rs != null && rs.next()){
                doacao.setId_doacao(rs.getInt("id_doacao"));
                return doacao;
            }
        }catch(SQLException e){
            System.out.println("Erro ao gravar Doação (SQL): " + e.getMessage());
        }
        return null;
    }

    public Doacao atualizar(Doacao doacao, Conexao conexao) {
        if (doacao == null) return null;

        String obsOriginal = doacao.getObservacao() != null ? doacao.getObservacao() : "";
        String obsEscapada = obsOriginal.replace("'", "''");

        String sql = String.format(Locale.US,
                "UPDATE doacao SET " +
                        "id_doador = %d, " +
                        "id_admin = %d, " +
                        "valor = %f, " +
                        "observacao = '%s' " +
                        "WHERE id_doacao = %d",
                doacao.getId_doador().getId(),
                doacao.getId_admin().getId(),
                doacao.getValor(),
                obsEscapada,
                doacao.getId_doacao()
        );

        conexao.consultar(sql);
        return doacao;
    }

    public boolean excluir(int id, Conexao conexao) {
        String sql = String.format("DELETE FROM doacao WHERE id_doacao = %d", id);
        return conexao.manipular(sql);
    }
}