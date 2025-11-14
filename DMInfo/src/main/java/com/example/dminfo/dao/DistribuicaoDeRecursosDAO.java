package com.example.dminfo.dao;

import com.example.dminfo.model.Administrador;
import com.example.dminfo.model.DistribuicaoDeRecursos;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DistribuicaoDeRecursosDAO {

//    CREATE TABLE IF NOT EXISTS distribuicao_de_recursos (
//            id_distribuicao SERIAL PRIMARY KEY,
//            id_admin INT NULL,
//            data DATE NULL,
//            descricao VARCHAR(50) NULL,
//    instituicaoreceptora VARCHAR(100) NULL,
//    valor double precision NULL,
//    CONSTRAINT fk_distribuicao_de_recursos_administrador1
//    FOREIGN KEY (id_admin)
//    REFERENCES administrador (id_admin)
//    ON DELETE NO ACTION
//    ON UPDATE NO ACTION
//    );

    private DistribuicaoDeRecursos buildDistribuicao(ResultSet rs) throws SQLException {
        DistribuicaoDeRecursos d = new DistribuicaoDeRecursos();

        d.setId(rs.getInt("id_distribuicao"));
        d.setAdmin(rs.getInt("id_admin"));
        d.setData(rs.getObject("data", LocalDate.class));
        d.setDescricao(rs.getString("descricao"));
        d.setInstituicaoReceptora(rs.getString("instituicaoreceptora"));
        d.setValor(rs.getDouble("valor"));

        return d;
    }

    public List<DistribuicaoDeRecursos> listar() {
        List<DistribuicaoDeRecursos> lista = new ArrayList<>();
        String sql = "SELECT * FROM distribuicao_de_recursos ORDER BY id_distribuicao";
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            while (rs != null && rs.next()) {
                lista.add(buildDistribuicao(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar distribuições de recursos: " + e.getMessage());
        }
        return lista;
    }

    public DistribuicaoDeRecursos getById(int id) {
        String sql = "SELECT * FROM distribuicao_de_recursos WHERE id_distribuicao = " + id;
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildDistribuicao(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar distribuição por ID: " + e.getMessage());
        }
        return null;
    }

    public DistribuicaoDeRecursos gravar(DistribuicaoDeRecursos dist) {
        if (dist == null)
            return null;

        String sql = String.format(
                "INSERT INTO distribuicao_de_recursos (id_admin, data, descricao, instituicaoreceptora, valor) VALUES (%d, '%s', '%s', '%s', " + String.valueOf(dist.getValor()).replace(",", ".") + ") RETURNING id_distribuicao",
                dist.getAdmin(),
                dist.getData(),
                dist.getDescricao(),
                dist.getInstituicaoReceptora()
        );

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                dist.setId(rs.getInt("id_distribuicao"));
                return dist;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar distribuição: " + e.getMessage());
        }
        return null;
    }

    public boolean alterar(DistribuicaoDeRecursos dist) {
        if (dist != null) {
            String sql = String.format(
                    "UPDATE distribuicao_de_recursos SET id_admin = %d, data = '%s', descricao = '%s', " +
                            "instituicaoreceptora = '%s', valor = " + String.valueOf(dist.getValor()).replace(",", ".") + " WHERE id_distribuicao = %d",
                    dist.getAdmin(),
                    dist.getData(),
                    dist.getDescricao(),
                    dist.getInstituicaoReceptora(),
                    dist.getId()
            );

            return SingletonDB.getConexao().manipular(sql);
        }
        return false;
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM distribuicao_de_recursos WHERE id_distribuicao = " + id;
        return SingletonDB.getConexao().manipular(sql);
    }

    public DistribuicaoDeRecursos consultar(String descricao) {
        String sql = String.format("SELECT * FROM distribuicao_de_recursos WHERE descricao = '%s'", descricao);
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildDistribuicao(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar distribuição: " + e.getMessage());
        }
        return null;
    }
}