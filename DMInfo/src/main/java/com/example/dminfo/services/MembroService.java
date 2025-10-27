package com.example.dminfo.services;

import com.example.dminfo.model.Membro;
import com.example.dminfo.model.Usuario;
import com.example.dminfo.repositories.MembroRepository;
import com.example.dminfo.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MembroService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MembroRepository membroRepository;

    public Membro getById(Integer id) {
        Membro membro;
        membro = membroRepository.findById(id).orElse(null);
        return membro;
    }

    public List<Membro> listar() {
        return membroRepository.findAll();
    }

    public Membro salvar(Membro membro) {

        // Busca o usuário pelo ID fornecido no JSON
        Usuario usuario = usuarioRepository.findById(membro.getUsuario().getId()).orElse(null);
        if (usuario == null)
            throw new RuntimeException("Usuário não encontrado para o ID fornecido.");

        // Verifica se o usuário já é um membro
        Membro membroExistente = membroRepository.findByUsuario(usuario).orElse(null);
        if (membroExistente != null) {
            throw new RuntimeException("Este usuário já está associado a um membro.");
        }

        // Define os dados do novo membro
        membro.setUsuario(usuario);
        membro.setDtIni(LocalDate.now());
        return membroRepository.save(membro);
    }

    public Membro update(Integer id, Membro membroDetails) {
        Membro membro = membroRepository.findById(id).orElse(null);
        if (membro == null) {
            throw new RuntimeException("Membro não encontrado com ID: " + id);
        }

        // Atualiza os campos
        membro.setObservacao(membroDetails.getObservacao());
        membro.setDtFim(membroDetails.getDtFim());

        return membroRepository.save(membro);
    }

    public boolean excluir(Integer id) {
        Membro membro;

        membro = membroRepository.findById(id).orElse(null);
        if  (membro != null) {
            membroRepository.deleteById(id);
            return true;
        }

        return false;
    }
}