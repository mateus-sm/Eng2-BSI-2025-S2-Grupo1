package com.example.dminfo.model;

import com.example.dminfo.dao.ParametrosDAO;
import com.example.dminfo.model.template.SalvarParametrosTemplate;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class Parametros {
    private int id;
    private String razaoSocial;
    private String nomeFantasia;
    private String descricao;
    private String rua;
    private String bairro;
    private String cidade;
    private String cep;
    private String uf;
    private String telefone;
    private String site;
    private String email;
    private String cnpj;
    private String logoGrande;
    private String logoPequeno;

    @Autowired
    private ParametrosDAO dao;

    public Parametros() {}

    public Parametros(int id, String razaoSocial, String nomeFantasia, String descricao, String rua, String bairro, String cidade, String cep, String uf, String telefone, String site, String email, String cnpj, String logoGrande, String logoPequeno) {
        this.id = id;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.descricao = descricao;
        this.rua = rua;
        this.bairro = bairro;
        this.cidade = cidade;
        this.cep = cep;
        this.uf = uf;
        this.telefone = telefone;
        this.site = site;
        this.email = email;
        this.cnpj = cnpj;
        this.logoGrande = logoGrande;
        this.logoPequeno = logoPequeno;
    }

    private Parametros montarParametros(ResultSet rs) throws SQLException {
        return new Parametros(
                rs.getInt("id_parametro"),
                rs.getString("razao_social"),
                rs.getString("nome_fantasia"),
                rs.getString("descricao"),
                rs.getString("rua"),
                rs.getString("bairro"),
                rs.getString("cidade"),
                rs.getString("cep"),
                rs.getString("uf"),
                rs.getString("telefone"),
                rs.getString("site"),
                rs.getString("email"),
                rs.getString("cnpj"),
                rs.getString("logotipogrande"),
                rs.getString("logotipopequeno")
        );
    }

    public Parametros exibir(Conexao conexao) {
        ResultSet rs = dao.get(conexao);
        try {
            if (rs != null && rs.next()) {
                return montarParametros(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao exibir parâmetros: " + e.getMessage());
        }
        return null;
    }

    public Parametros salvar(Parametros parametro, Conexao conexao) {

        SalvarParametrosTemplate template = new SalvarParametrosTemplate() {
            @Override
            protected void validarRegrasDeNegocio(Parametros p) {
                if (p.getRazaoSocial() == null || p.getRazaoSocial().trim().isEmpty())
                    throw new RuntimeException("Razão Social é obrigatória.");
                if (p.getRua() == null || p.getRua().trim().isEmpty())
                    throw new RuntimeException("Rua é obrigatória.");
                if (p.getCidade() == null || p.getCidade().trim().isEmpty())
                    throw new RuntimeException("Cidade é obrigatória.");
                if (p.getTelefone() == null || p.getTelefone().trim().isEmpty())
                    throw new RuntimeException("Telefone é obrigatório.");
                if (p.getLogoPequeno() == null || p.getLogoPequeno().trim().isEmpty())
                    throw new RuntimeException("Logo Pequeno é obrigatório.");
            }

            @Override
            protected Parametros buscarExistente(Conexao conn) {
                return exibir(conn);
            }

            @Override
            protected Parametros inserirNovo(Parametros p, Conexao conn) {
                return dao.create(p, conn);
            }

            @Override
            protected Parametros atualizarExistente(Parametros p, Conexao conn) {
                Parametros atualizado = dao.update(p, conn);
                if (atualizado == null) {
                    throw new RuntimeException("Erro ao salvar parâmetros.");
                }
                return atualizado;
            }
        };

        return template.executarSalvamento(parametro, conexao);
    }

    public void excluir(Integer id, Conexao conexao) {
        dao.delete(id, conexao);
    }

    public boolean existeParametro(Conexao conexao) {
        return dao.count(conexao) > 0;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getRazaoSocial() {
        return razaoSocial;
    }
    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }
    public String getNomeFantasia() {
        return nomeFantasia;
    }
    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public String getRua() {
        return rua;
    }
    public void setRua(String rua) {
        this.rua = rua;
    }
    public String getBairro() {
        return bairro;
    }
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
    public String getCidade() {
        return cidade;
    }
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
    public String getCep() {
        return cep;
    }
    public void setCep(String cep) {
        this.cep = cep;
    }
    public String getUf() {
        return uf;
    }
    public void setUf(String uf) {
        this.uf = uf;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public String getSite() {
        return site;
    }
    public void setSite(String site) {
        this.site = site;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getCnpj() {
        return cnpj;
    }
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
    public String getLogoGrande() {
        return logoGrande;
    }
    public void setLogoGrande(String logoGrande) {
        this.logoGrande = logoGrande;
    }
    public String getLogoPequeno() {
        return logoPequeno;
    }
    public void setLogoPequeno(String logoPequeno) {
        this.logoPequeno = logoPequeno;
    }
}