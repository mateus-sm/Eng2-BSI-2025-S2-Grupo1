package com.example.dminfo.model;

import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.dao.UsuarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class Membro {
    // Campos do modelo (POJO)
    private int id;
    private int codigo;
    private LocalDate dtIni;
    private LocalDate dtFim;
    private String observacao;
    private Usuario usuario; // O modelo Usuario, não a entidade

    // Injeção dos DAOs (padrão "Fat Model")
    @Autowired
    private MembroDAO dao;
    @Autowired
    private UsuarioDAO usuarioDAO; // Necessário para a lógica de 'salvar'

    // Construtores
    public Membro() {}

    // Getters e Setters (necessários)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }
    public LocalDate getDtIni() { return dtIni; }
    public void setDtIni(LocalDate dtIni) { this.dtIni = dtIni; }
    public LocalDate getDtFim() { return dtFim; }
    public void setDtFim(LocalDate dtFim) { this.dtFim = dtFim; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    // --- LÓGICA DE NEGÓCIOS (antigo MembroService) ---

    public List<Membro> listar() {
        return dao.get(""); // Filtro vazio
    }

    public Membro getById(Integer id) {
        return dao.get(id);
    }

    public Membro salvar(Membro membro) {
        // Validação 1: O código do membro deve ser positivo
        if (membro.getCodigo() <= 0) {
            throw new RuntimeException("O Código do Membro deve ser um número positivo.");
        }

        // Validação 2: Verifica se o ID do usuário foi fornecido
        if (membro.getUsuario() == null || membro.getUsuario().getId() == 0) {
            throw new RuntimeException("ID do Usuário é obrigatório.");
        }

        // Validação 3: Busca o usuário (seguindo o padrão do exemplo)
        Usuario usuario = usuarioDAO.get(membro.getUsuario().getId());
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado para o ID fornecido.");
        }

        // Validação 4: Verifica se o usuário já é um membro
        if (dao.existsByUsuarioId(usuario.getId())) {
            throw new RuntimeException("Este usuário já está associado a um membro.");
        }

        // Define os dados do novo membro
        membro.setUsuario(usuario); // Anexa o usuário completo
        membro.setDtIni(LocalDate.now()); // Define a data de início

        return dao.gravar(membro);
    }

    public Membro update(Integer id, Membro membroDetails) {
        // Validação 1: Código positivo
        if (membroDetails.getCodigo() <= 0) {
            throw new RuntimeException("O Código do Membro deve ser um número positivo.");
        }

        // Busca o membro original
        Membro membroExistente = dao.get(id);
        if (membroExistente == null) {
            throw new RuntimeException("Membro não encontrado com ID: " + id);
        }

        // Atualiza os campos
        membroExistente.setCodigo(membroDetails.getCodigo());
        membroExistente.setObservacao(membroDetails.getObservacao());
        membroExistente.setDtFim(membroDetails.getDtFim());

        // Salva as alterações
        if (dao.alterar(membroExistente)) {
            return membroExistente;
        }
        throw new RuntimeException("Erro ao atualizar membro no banco de dados.");
    }

    public boolean excluir(Integer id) {
        Membro membro = dao.get(id);
        if (membro == null) {
            throw new RuntimeException("Membro não encontrado com ID: " + id);
        }
        return dao.excluir(id);
    }
}