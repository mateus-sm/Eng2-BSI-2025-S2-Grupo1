package com.example.dminfo.model;

import com.example.dminfo.dao.DistribuicaoDeRecursosDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DistribuicaoDeRecursos {
    private int id;
    private int admin;
    private LocalDate data;
    private String descricao;
    private String instituicaoReceptora;
    private double valor;

    private List<ItemDistribuido> itens;

    @Autowired
    private DistribuicaoDeRecursosDAO dao;

    public DistribuicaoDeRecursos() { }

    public DistribuicaoDeRecursos(int admin, LocalDate data, String descricao, int id, String instituicaoReceptora, double valor) {
        this.admin = admin;
        this.data = data;
        this.descricao = descricao;
        this.id = id;
        this.instituicaoReceptora = instituicaoReceptora;
        this.valor = valor;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInstituicaoReceptora() {
        return instituicaoReceptora;
    }

    public void setInstituicaoReceptora(String instituicaoReceptora) {
        this.instituicaoReceptora = instituicaoReceptora;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public List<ItemDistribuido> getItens() {
        return itens;
    }

    public void setItens(List<ItemDistribuido> itens) {
        this.itens = itens;
    }

    //Metodos
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

    public DistribuicaoDeRecursos consultar(DistribuicaoDeRecursos dist, Conexao conexao) {
        return dao.read(dist, conexao);
    }

    public List<DistribuicaoDeRecursos> listar(Conexao conexao) {
        List<DistribuicaoDeRecursos> lista = new ArrayList<>();
        ResultSet rs = dao.readAll("", conexao);

        try {
            while (rs.next()) {
                lista.add(buildDistribuicao(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao construir lista: " + e.getMessage());
        }

        return lista;
    }

    public DistribuicaoDeRecursos gravar(DistribuicaoDeRecursos dist, Conexao conexao) {
        if (dist == null) {
            throw new RuntimeException("Atribuição nula");
        }

        DistribuicaoDeRecursos existente = dao.read(dist, conexao);
        if (existente != null) {
            throw new RuntimeException("Já existe uma atribuição com essa descrição.");
        }

        return dao.create(dist, conexao);
    }

    public DistribuicaoDeRecursos alterar(DistribuicaoDeRecursos dist, Conexao conexao) {
        DistribuicaoDeRecursos d = dao.update(dist, conexao);
        if (d != null) {
            return d;
        }
        throw new RuntimeException("Erro ao atualizar Distribuição");
    }

    public DistribuicaoDeRecursos getById(Integer id, Conexao conexao) {
        DistribuicaoDeRecursos dist = new DistribuicaoDeRecursos();
        ResultSet rs = dao.getById(id, conexao);

        try {
            if (rs != null) {
                return buildDistribuicao(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao construir objeto na Model: " + e.getMessage());
        }

        return dist;
    }

    public boolean excluir(int id, Conexao conexao) {
        return dao.delete(id, conexao);
    }
}
