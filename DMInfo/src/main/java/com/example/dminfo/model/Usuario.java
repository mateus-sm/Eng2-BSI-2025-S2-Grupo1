package com.example.dminfo.model;

import com.example.dminfo.dao.UsuarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

@Repository
public class Usuario {

    @Autowired
    private UsuarioDAO dao;

    // --- CAMPOS ---
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
    private String foto; // Este campo existe no seu Model, mas não no DAO

    // --- CAMPO 'dtfim' (Necessário pelo seu DAO) ---
    private LocalDate dtfim;

    // --- CONSTRUTORES ---

    // Construtor vazio (obrigatório pelo Spring)
    public Usuario() {}

    // Construtor completo (obrigatório pelo buildUsuario do DAO)
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

    // --- GETTERS E SETTERS ---
    // (Cole os seus Getters e Setters aqui)
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
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public LocalDate getDtfim() { return dtfim; }
    public void setDtfim(LocalDate dtfim) { this.dtfim = dtfim; }


    // --- LÓGICA DE NEGÓCIOS ---

    public boolean logar(String login, String senha) {
        if(login == null || login.isEmpty() || senha == null || senha.isEmpty())
            return false;

        Usuario user = dao.getUsuario(login);
        if(user == null)
            return false;

        return user.getSenha().equals(senha);
    }

    public Map<String, String> validar(Usuario usuario) {
        Map<String, String> errors = new HashMap<>();

        // Validação 1: Login (usuário)
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            errors.put("usuario_error", "O campo 'usuário' (login) é obrigatório.");
        } else if (dao.getUsuario(usuario.getLogin()) != null) {
            errors.put("usuario_error", "Este 'usuário' (login) já está cadastrado.");
        }

        // Validação 2: E-mail
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            errors.put("email_error", "O campo 'e-mail' é obrigatório.");
        } else if (dao.getUsuarioByEmail(usuario.getEmail()) != null) {
            errors.put("email_error", "Este e-mail já está cadastrado.");
        }

        // Validação 3: CPF
        if (usuario.getCpf() == null || usuario.getCpf().length() != 14) {
            errors.put("cpf_error", "CPF inválido. Deve estar no formato 000.000.000-00.");
        } else if (dao.getUsuarioByCpf(usuario.getCpf()) != null) {
            errors.put("cpf_error", "Este CPF já está cadastrado.");
        }

        // Validação 4: Nome
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            errors.put("nome_error", "O nome é obrigatório.");
        }

        // Validação 5: Data de Nascimento
        if (usuario.getDtnasc() == null) {
            errors.put("dtnasc_error", "A data de nascimento é obrigatória.");
        }

        return errors;
    }

    public Usuario salvar(Usuario usuario) {
        // Define a data de início
        usuario.setDtini(LocalDate.now());

        // Grava no banco
        return dao.gravar(usuario);
    }

    // ---
    // --- MÉTODOS CRUD CORRIGIDOS ---
    // ---

    public Usuario getById(int id){
        // CORRIGIDO: de dao.getById(id) para dao.get(id)
        return dao.get(id);
    }

    public List<Usuario> listar(){
        // CORRIGIDO: de dao.listar() para dao.get("")
        return dao.get(""); // Filtro vazio
    }

    /**
     * MÉTODO UPDATE CORRIGIDO
     * 1. A assinatura agora bate com o Controller: update(int id, Usuario usuarioDetails)
     * 2. A lógica interna busca, atualiza e chama o dao.alterar()
     */
    public Usuario update(int id, Usuario usuarioDetails) {
        // 1. Busca o usuário original
        Usuario usuarioExistente = dao.get(id); // Usa o 'get(id)' do DAO
        if (usuarioExistente == null) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        // 2. Validações (ex: login não pode mudar para um que já existe)
        if (!usuarioDetails.getLogin().equals(usuarioExistente.getLogin()) && dao.getUsuario(usuarioDetails.getLogin()) != null) {
            throw new RuntimeException("Este 'usuário' (login) já pertence a outra conta.");
        }
        // (Você pode adicionar mais validações de CPF/Email aqui se desejar)

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
        if (dao.alterar(usuarioExistente)) { // Chama 'alterar' do DAO
            return usuarioExistente;
        }
        throw new RuntimeException("Erro ao atualizar usuário no banco de dados.");
    }

    public boolean excluir(int id)
    {
        // CORRIGIDO: de void para boolean
        return dao.excluir(id);
    }
}