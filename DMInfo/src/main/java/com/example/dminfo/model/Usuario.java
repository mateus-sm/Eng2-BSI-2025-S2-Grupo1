package com.example.dminfo.model;

import com.example.dminfo.dao.UsuarioDAO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Usuario {

    private int id;
    private String nome;
    private String cpf;
    private String login;
    private String senha;
    private String telefone;
    private String email;
    private LocalDate dtnasc;
    private LocalDate dtini;
    private String rua;
    private String cidade;
    private String bairro;
    private String cep;
    private String uf;
    private String foto;
    private LocalDate dtfim;

    // --- IMPORTANTE: Instância da DAO com @JsonIgnore para evitar erro 500 ---
    @JsonIgnore
    private UsuarioDAO dao = new UsuarioDAO();

    public Usuario() {}

    public Usuario(int id, String nome, String senha, String login, String telefone,
                   String email, String rua, String cidade, String bairro, String cep,
                   String uf, String cpf, LocalDate dtnasc, LocalDate dtini, LocalDate dtfim) {
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        this.login = login;
        this.telefone = telefone;
        this.email = email;
        this.rua = rua;
        this.cidade = cidade;
        this.bairro = bairro;
        this.cep = cep;
        this.uf = uf;
        this.cpf = cpf;
        this.dtnasc = dtnasc;
        this.dtini = dtini;
        this.dtfim = dtfim;
    }

    // --- MÉTODO DE NEGÓCIO (VALIDAÇÃO E SALVAMENTO) ---
    public Usuario salvar() {
        validar(); // Executa as verificações

        // Define data de início se não tiver
        if (this.dtini == null) {
            this.dtini = LocalDate.now();
        }

        // Chama a DAO para gravar
        return dao.gravar(this);
    }

    private void validar() {
        if (nome == null || nome.trim().isEmpty()) throw new RuntimeException("Nome é obrigatório.");
        if (senha == null || senha.trim().isEmpty()) throw new RuntimeException("Senha é obrigatória.");
        if (login == null || login.trim().isEmpty()) throw new RuntimeException("Login (usuário) é obrigatório.");
        if (dtnasc == null) throw new RuntimeException("Data de nascimento é obrigatória.");

        if (this.dtnasc.isAfter(LocalDate.now())) {
            throw new RuntimeException("A data de nascimento não pode ser maior que a data atual.");
        }

        if (cpf == null || !cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new RuntimeException("Formato de CPF inválido (esperado: 000.000.000-00).");
        }
        if (dao.getUsuarioByCpf(this.cpf) != null) {
            throw new RuntimeException("Este CPF já está em uso.");
        }

        if (telefone == null || telefone.trim().isEmpty()) {
            throw new RuntimeException("Telefone é obrigatório.");
        }
        if (dao.getUsuarioByTelefone(this.telefone) != null) {
            throw new RuntimeException("Este Telefone já está em uso.");
        }

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (email == null || !Pattern.matches(emailRegex, email)) {
            throw new RuntimeException("Formato de E-mail inválido.");
        }
        if (dao.getUsuarioByEmail(this.email) != null) {
            throw new RuntimeException("Este E-mail já está em uso.");
        }

        if (dao.getUsuario(this.login) != null) {
            throw new RuntimeException("Este nome de usuário (login) já está em uso.");
        }
    }

    // --- Getters e Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getDtnasc() { return dtnasc; }
    public void setDtnasc(LocalDate dtnasc) { this.dtnasc = dtnasc; }
    public LocalDate getDtini() { return dtini; }
    public void setDtini(LocalDate dtini) { this.dtini = dtini; }
    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    public LocalDate getDtfim() { return dtfim; }
    public void setDtfim(LocalDate dtfim) { this.dtfim = dtfim; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}