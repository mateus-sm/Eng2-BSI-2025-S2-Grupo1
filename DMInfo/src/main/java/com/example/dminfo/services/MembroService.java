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

@Service
public class MembroService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MembroRepository membroRepository;

    public List<Membro> listar() {
        return membroRepository.findAll();
    }

    public Membro salvar(Usuario usuario) {
        //Só cria membro caso exista um usuario para ser associado e esse usuario
        //  não tenha nenhum outro membro
        if (usuario != null) {
            Membro membro = membroRepository.findByUsuario(usuario).orElse(null);

            if (membro == null) {
                membro = new Membro();
                membro.setDtIni(LocalDate.now());
                membro.setUsuario(usuario);
                return membroRepository.save(membro);
            }
        }

        return null;
    }

    public Membro getById(Integer id) {
        Membro membro;
        membro = membroRepository.findById(id).orElse(null);
        return membro;
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