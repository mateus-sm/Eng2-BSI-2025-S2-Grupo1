package com.example.dminfo.dao;

import com.example.dminfo.model.DistribuicaoDeRecursos;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Repository
public class DistribuicaoDeRecursosDAO implements IDAO<DistribuicaoDeRecursos> {

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

    @Override
    public ResultSet readAll(String filtro, Conexao conexao) {
        String sql = "SELECT * FROM distribuicao_de_recursos ORDER BY id_distribuicao";
        return conexao.consultar(sql);
    }

    @Override
    public ResultSet getById(int id, Conexao conexao) {
        String sql = "SELECT * FROM distribuicao_de_recursos WHERE id_distribuicao = " + id;
        ResultSet rs = conexao.consultar(sql);

        try {
            if (rs != null && rs.next()) {
                return rs;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar distribuição por ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public DistribuicaoDeRecursos create(DistribuicaoDeRecursos dist, Conexao conexao) {
        if (dist == null) return null;

        String sql = String.format(
                "INSERT INTO distribuicao_de_recursos (id_admin, data, descricao, instituicaoreceptora, valor) VALUES (%d, '%s', '%s', '%s', " + String.valueOf(dist.getValor()).replace(",", ".") + ") RETURNING id_distribuicao",
                dist.getAdmin(),
                dist.getData(),
                dist.getDescricao(),
                dist.getInstituicaoReceptora()
        );

        ResultSet rs = conexao.consultar(sql);

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

    @Override
    public DistribuicaoDeRecursos update(DistribuicaoDeRecursos dist, Conexao conexao) {
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

            conexao.consultar(sql);
            return dist;
        }

        return null;
    }

    @Override
    public boolean delete(int id, Conexao conexao) {
        String sql = "DELETE FROM distribuicao_de_recursos WHERE id_distribuicao = " + id;
        return conexao.manipular(sql);
    }

    @Override
    public DistribuicaoDeRecursos read(DistribuicaoDeRecursos dist, Conexao conexao) {
        String sql = String.format("SELECT * FROM distribuicao_de_recursos WHERE descricao = '%s'", dist.getDescricao());
        ResultSet rs = conexao.consultar(sql);

        try {
            if (rs != null && rs.next()) {
                dist.setId(rs.getInt("id_distribuicao"));
                dist.setAdmin(rs.getInt("id_admin"));
                dist.setData(rs.getObject("data", LocalDate.class));
                dist.setDescricao(rs.getString("descricao"));
                dist.setInstituicaoReceptora(rs.getString("instituicaoreceptora"));
                dist.setValor(rs.getDouble("valor"));
                return dist;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar distribuição: " + e.getMessage());
        }

        return null;
    }
}