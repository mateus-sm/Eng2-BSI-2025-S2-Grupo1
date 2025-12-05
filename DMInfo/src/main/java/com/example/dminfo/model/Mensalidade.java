package com.example.dminfo.model;

import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.dao.MensalidadeDAO;
import com.example.dminfo.util.Conexao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component; // Usamos Component para o Spring gerenciar, similar ao Repository do seu amigo

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class Mensalidade {

    private int id_mensalidade;
    private int id_membro;
    private int mes;
    private int ano;
    private Double valor;
    private LocalDate dataPagamento;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String nome_membro;

    @Autowired
    @JsonIgnore
    private MensalidadeDAO dao = new MensalidadeDAO();

    @Autowired
    @JsonIgnore
    private MembroDAO membroDAO = new MembroDAO();

    public Mensalidade() {}

    public Mensalidade(int id_mensalidade, int id_membro, int mes, int ano, Double valor, LocalDate dataPagamento) {
        this.id_mensalidade = id_mensalidade;
        this.id_membro = id_membro;
        this.mes = mes;
        this.ano = ano;
        this.valor = valor;
        this.dataPagamento = dataPagamento;
    }

    private Mensalidade montarMensalidade(ResultSet rs) throws SQLException {
        Mensalidade m = new Mensalidade();
        m.setId_mensalidade(rs.getInt("id_mensalidade"));
        m.setId_membro(rs.getInt("id_membro"));
        m.setMes(rs.getInt("mes"));
        m.setAno(rs.getInt("ano"));
        m.setValor(rs.getDouble("valor"));
        m.setDataPagamento(rs.getObject("datapagamento", LocalDate.class));
        try {
            m.setNome_membro(rs.getString("nome_membro"));
        } catch (SQLException ignore) {}
        return m;
    }

    public Mensalidade salvar(Conexao conexao) {
        if (this.valor == null || this.valor <= 0) {
            throw new RuntimeException("Valor precisa ser maior que zero");
        }

        if (this.ano <= 0 || this.mes <= 0 || this.dataPagamento == null) {
            throw new RuntimeException("Data da mensalidade inválida");
        }

        if (membroDAO.get(this.id_membro, conexao) == null) {
            throw new RuntimeException("Membro não encontrado no banco");
        }

        if (this.dataPagamento.isAfter(LocalDate.now())) {
            throw new RuntimeException("Data do pagamento não pode ser no futuro");
        }

        try {
            ResultSet rsDup = dao.buscarPorMembroMesAno(this.id_membro, this.mes, this.ano, conexao);
            if (rsDup != null && rsDup.next()) {
                Mensalidade duplicada = montarMensalidade(rsDup);
                if (this.id_mensalidade == 0 || this.id_mensalidade != duplicada.getId_mensalidade()) {
                    throw new RuntimeException("Este membro já possui uma mensalidade registrada para o Mês " + this.mes + "/" + this.ano);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar duplicidade: " + e.getMessage());
        }

        boolean existe = false;
        if (this.id_mensalidade > 0) {
            try {
                ResultSet rsExist = dao.buscarPorId(this.id_mensalidade, conexao);
                if (rsExist != null && rsExist.next()) {
                    existe = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (existe) {
            boolean ok = dao.alterar(this, conexao);
            if (!ok) throw new RuntimeException("Erro ao atualizar mensalidade");
            return this;
        } else {
            return dao.gravar(this, conexao);
        }
    }

    public boolean excluir(Conexao conexao) {
        return dao.excluir(this.id_mensalidade, conexao);
    }

    // --- MÉTODOS DE BUSCA (PROCESSAM O RESULTSET DA DAO) ---

    public Mensalidade buscarPorId(Integer id, Conexao conexao) {
        ResultSet rs = dao.buscarPorId(id, conexao);
        try {
            if (rs != null && rs.next()) {
                return montarMensalidade(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar mensalidade por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Mensalidade> listarTodos(String filtroNome, Conexao conexao) {
        List<Mensalidade> lista = new ArrayList<>();
        ResultSet rs = dao.listar(filtroNome, conexao);
        try {
            if (rs != null) {
                while (rs.next()) {
                    lista.add(montarMensalidade(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar mensalidades: " + e.getMessage());
        }
        return lista;
    }

    public List<Mensalidade> listarPorMesAno(int mes, int ano, Conexao conexao) {
        List<Mensalidade> lista = new ArrayList<>();
        ResultSet rs = dao.listarMesAno(mes, ano, conexao);
        try {
            if (rs != null) {
                while (rs.next()) {
                    lista.add(montarMensalidade(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar mensalidades por Mês/Ano: " + e.getMessage());
        }
        return lista;
    }

    public List<Mensalidade> listarPorMembro(Integer idMembro, Conexao conexao) {
        List<Mensalidade> lista = new ArrayList<>();
        ResultSet rs = dao.listarMembro(idMembro, conexao);
        try {
            if (rs != null) {
                while (rs.next()) {
                    lista.add(montarMensalidade(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar mensalidades por membro: " + e.getMessage());
        }
        return lista;
    }

    public List<Mensalidade> filtrarAvancado(String nome, String dataIni, String dataFim, Conexao conexao) {
        List<Mensalidade> lista = new ArrayList<>();
        ResultSet rs = dao.filtrar(nome, dataIni, dataFim, conexao);
        try {
            if (rs != null) {
                while (rs.next()) {
                    lista.add(montarMensalidade(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao filtrar mensalidades: " + e.getMessage());
        }
        return lista;
    }

    // Getters e Setters
    public int getId_mensalidade() { return id_mensalidade; }
    public void setId_mensalidade(int id_mensalidade) { this.id_mensalidade = id_mensalidade; }
    public int getId_membro() { return id_membro; }
    public void setId_membro(int id_membro) { this.id_membro = id_membro; }
    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }
    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }
    public String getNome_membro() {return nome_membro;}
    public void setNome_membro(String nome_membro) {this.nome_membro = nome_membro;}
}