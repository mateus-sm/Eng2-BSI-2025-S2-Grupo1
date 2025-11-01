package com.example.dminfo.model;

import com.example.dminfo.dao.UsuarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.time.LocalDate;

@Repository
public class Usuario {

    @Autowired
    private UsuarioDAO dao;

    // Campos baseados no seu print da tabela 'usuario'
    private int id; // id_usuario
    private String nome;
    private String senha;
    private String login; // 'usuario' no banco
    private String telefone;
    private String email;
    private String rua;
    private String cidade;
    private String bairro;
    private String cep;
    private String uf;
    private String cpf;
    private LocalDate dtnasc;
    private LocalDate dtini;
    private LocalDate dtfim;

    // Construtor vazio
    public Usuario() {}

    // Construtor completo para o DAO usar
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

    // --- Getters e Setters para todos os campos ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
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
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public LocalDate getDtnasc() { return dtnasc; }
    public void setDtnasc(LocalDate dtnasc) { this.dtnasc = dtnasc; }
    public LocalDate getDtini() { return dtini; }
    public void setDtini(LocalDate dtini) { this.dtini = dtini; }
    public LocalDate getDtfim() { return dtfim; }
    public void setDtfim(LocalDate dtfim) { this.dtfim = dtfim; }

    // --- Lógica de Negócios ---

    public boolean logar(String login, String senha) {
        Usuario usuario = dao.getUsuario(login);
        if (usuario == null)
            return false;

        // Lógica de login simplificada (sem 'isAtivo' pois não existe no seu banco)
        if (usuario.getSenha().equals(senha))
            return true;

        return false;
    }

    public Usuario getById(int id) {
        return dao.get(id);
    }

    public List<Usuario> listar() {
        return dao.get(""); // Filtro vazio
    }

    public Usuario salvar(Usuario usuario) {
        // Validação 1: Login (usuário) não pode ser nulo ou vazio
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            throw new RuntimeException("O campo 'usuário' (login) é obrigatório.");
        }

        // Validação 2: Verifica se o login (usuário) já existe
        if (dao.getUsuario(usuario.getLogin()) != null) {
            throw new RuntimeException("Este 'usuário' (login) já está cadastrado.");
        }

        // Validação 3: (Exemplo) CPF deve ter 14 caracteres (ex: 111.222.333-44)
        if (usuario.getCpf() == null || usuario.getCpf().length() != 14) {
            throw new RuntimeException("CPF inválido. Deve estar no formato 000.000.000-00.");
        }

        // Define a data de início
        usuario.setDtini(LocalDate.now());

        return dao.gravar(usuario);
    }

    public Usuario update(int id, Usuario usuarioDetails) {
        // 1. Busca o usuário original
        Usuario usuarioExistente = dao.get(id);
        if (usuarioExistente == null) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        // 2. Validações (ex: login não pode mudar para um que já existe)
        if (!usuarioDetails.getLogin().equals(usuarioExistente.getLogin()) && dao.getUsuario(usuarioDetails.getLogin()) != null) {
            throw new RuntimeException("Este 'usuário' (login) já pertence a outra conta.");
        }

        // 3. Atualiza os campos do usuário existente com os novos detalhes
        usuarioExistente.setNome(usuarioDetails.getNome());
        usuarioExistente.setSenha(usuarioDetails.getSenha());
        usuarioExistente.setLogin(usuarioDetails.getLogin());
        usuarioExistente.setTelefone(usuarioDetails.getTelefone());
        usuarioExistente.setEmail(usuarioDetails.getEmail());
        usuarioExistente.setRua(usuarioDetails.getRua());
        usuarioExistente.setCidade(usuarioDetails.getCidade());
        usuarioExistente.setBairro(usuarioDetails.getBairro());
        usuarioExistente.setCep(usuarioDetails.getCep());
        usuarioExistente.setUf(usuarioDetails.getUf());
        usuarioExistente.setCpf(usuarioDetails.getCpf());
        usuarioExistente.setDtnasc(usuarioDetails.getDtnasc());
        // Não alteramos dtini e dtfim aqui

        // 4. Salva as alterações
        if (dao.alterar(usuarioExistente)) {
            return usuarioExistente;
        }
        throw new RuntimeException("Erro ao atualizar usuário no banco de dados.");
    }

    public boolean excluir(int id) {
        if (dao.get(id) == null) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }
        // (Em um sistema real, você verificaria se este usuário
        // pode ser excluído, ex: se ele não é um 'membro' ativo)

        return dao.excluir(id); // Exclusão lógica
    }
}