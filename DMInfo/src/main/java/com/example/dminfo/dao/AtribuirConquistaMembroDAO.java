package com.example.dminfo.dao;

import com.example.dminfo.model.AtribuirConquistaMembro;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

@Repository
public class AtribuirConquistaMembroDAO implements IDAO<AtribuirConquistaMembro> {

    @Override
    public AtribuirConquistaMembro create(AtribuirConquistaMembro acm, Conexao conexao) {
        if (acm == null) return null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String data = (acm.getData() != null) ? sdf.format(acm.getData()) : null;
        String observacao = (acm.getObservacao() != null) ? acm.getObservacao().replace("'", "''") : "";

        String sql = String.format(
                "INSERT INTO atribuir_conquista_membro (id_admin, id_membro, id_conquista, data, observacao) " +
                        "VALUES (%d, %d, %d, %s, '%s') RETURNING id_atribuir_conquista",
                acm.getId_admin(),
                acm.getId_membro(),
                acm.getId_conquista(),
                (data != null ? "'" + data + "'" : "NULL"),
                observacao
        );

        ResultSet rs = conexao.consultar(sql);
        try {
            if (rs != null && rs.next()) {
                acm.setId(rs.getInt("id_atribuir_conquista"));
                return acm;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar atribuição: " + e.getMessage());
        }
        return null;
    }

    @Override
    public AtribuirConquistaMembro read(AtribuirConquistaMembro acm, Conexao conexao) {
        String observacao = acm.getObservacao();
        String sql = String.format("SELECT * FROM atribuir_conquista_membro WHERE observacao = '%s'", observacao);

        ResultSet rs = conexao.consultar(sql);

        try {
            if (rs != null && rs.next()) {
                acm.setId(rs.getInt("id_atribuir_conquista"));
                acm.setId_admin(rs.getInt("id_admin"));
                acm.setId_membro(rs.getInt("id_membro"));
                acm.setId_conquista(rs.getInt("id_conquista"));
                acm.setData(rs.getDate("data"));
                acm.setObservacao(rs.getString("observacao"));
                return acm;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar atribuição: " + e.getMessage());
        }
        return null;
    }

    @Override
    public AtribuirConquistaMembro update(AtribuirConquistaMembro acm, Conexao conexao) {
        if (acm == null) return null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String data = (acm.getData() != null) ? sdf.format(acm.getData()) : null;
        String observacao = (acm.getObservacao() != null) ? acm.getObservacao().replace("'", "''") : "";

        String sql = String.format(
                "UPDATE atribuir_conquista_membro SET " +
                        "id_admin = %d, " +
                        "id_membro = %d, " +
                        "id_conquista = %d, " +
                        "data = %s, " +
                        "observacao = '%s' " +
                        "WHERE id_atribuir_conquista = %d",
                acm.getId_admin(),
                acm.getId_membro(),
                acm.getId_conquista(),
                (data != null ? "'" + data + "'" : "NULL"),
                observacao,
                acm.getId()
        );

        conexao.consultar(sql);
        return acm;
    }

    @Override
    public boolean delete(int id, Conexao conexao) {
        String sql = "DELETE FROM atribuir_conquista_membro WHERE id_atribuir_conquista = " + id;
        return conexao.manipular(sql);
    }

    @Override
    public ResultSet readAll(String filtro, Conexao conexao) {
        String sql = "SELECT * FROM atribuir_conquista_membro";

        if (filtro != null && !filtro.isBlank()) {
            sql += " WHERE id_atribuir_conquista LIKE '%" + filtro.replace("'", "''") + "%'";
        }
        sql += " ORDER BY id_atribuir_conquista";

        return conexao.consultar(sql);
    }

    @Override
    public ResultSet getById(int id, Conexao conexao) {
        String sql = "SELECT * FROM atribuir_conquista_membro WHERE id_atribuir_conquista = " + id;
        return conexao.consultar(sql);
    }
}
