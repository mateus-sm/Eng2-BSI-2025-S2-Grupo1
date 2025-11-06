package com.example.dminfo.dao;

import com.example.dminfo.model.AtribuirConquistaMembro;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AtribuirConquistaMembroDAO {

    private AtribuirConquistaMembro buildACM(ResultSet rs) throws SQLException {
        AtribuirConquistaMembro acm = new AtribuirConquistaMembro();
        acm.setId(rs.getInt("id_atribuir_conquista"));
        acm.setId_admin(rs.getInt("id_admin"));
        acm.setId_membro(rs.getInt("id_membro"));
        acm.setId_conquista(rs.getInt("id_conquista"));
        acm.setData(rs.getDate("data"));
        acm.setObservacao(rs.getString("observacao"));
        return acm;
    }

    public List<AtribuirConquistaMembro> listar() {
        List<AtribuirConquistaMembro> acm = new ArrayList<>();
        String sql = "SELECT * FROM atribuir_conquista_membro ORDER BY id_atribuir_conquista";
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            while (rs != null && rs.next()) {
                acm.add(buildACM(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar atribuições: " + e.getMessage());
        }
        return acm;
    }

    public AtribuirConquistaMembro getById(int id) {
        String sql = "SELECT * FROM atribuir_conquista_membro WHERE id_atribuir_conquista = " + id;
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildACM(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar atribuição por ID: " + e.getMessage());
        }
        return null;
    }

    public AtribuirConquistaMembro gravar(AtribuirConquistaMembro acm) {
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

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
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

    public boolean alterar(AtribuirConquistaMembro acm) {
        if (acm != null) {
            String sql = String.format(
                    "UPDATE atribuir_conquista_membro SET (id_admin, id_membro, id_conquista, data, observacao)" +
                            " VALUES ('%d', '%d', '%d', '%s', '%s') RETURNING id_atribuir_conquista",
                    acm.getId_admin(), acm.getId_membro(), acm.getId_conquista(), acm.getData(), acm.getObservacao()
            );

            return SingletonDB.getConexao().manipular(sql);
        }
        return false;
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM atribuir_conquista_membro WHERE id_atribuir_conquista = " + id;
        return SingletonDB.getConexao().manipular(sql);
    }

    public AtribuirConquistaMembro consultar(String descricao) {
        String sql = String.format("SELECT * FROM atribuir_conquista_membro WHERE observacao = '%s'", descricao);
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildACM(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar atribuição: " + e.getMessage());
        }
        return null;
    }


}
