package com.example.dminfo.services;

import com.example.dminfo.model.Membro;
import com.example.dminfo.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

//@Service
public class TesteService {
    //@Autowired
    private UsuarioRepository usuarioRepository;

    //@Autowired
    private MembroRepository membroRepository;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    //@Transactional // Isso garante que ou salva os dois (Usuario e Membro), ou não salva nenhum
    public Membro criarMembro() {

        // Criar o objeto Usuario
        Usuario user = new Usuario(
                "Kaiky",
                "Kaiky@123",
                "KaikyTf",
                "18997544959",
                "kaiky.multiaco@gmail.com",
                "Rua Augusto Cesar Pires",
                "Regente Feijo",
                "Jardim Tenis Clube",
                "19570000",
                "SP",
                "426.443.278-22",
                LocalDate.now(), // dtIni
                null,       // dtFim
                LocalDate.of(2003, 7, 30)  // dtNasc
        );

//        Usuario usuarioSalvo = usuarioRepository.save(user);
//
//        Membro membro = new Membro(
//                262321025,
//                LocalDate.of(2020,12,9), // dtIniMembro
//                null,       // dtFimMembro
//                "Membro ativo",
//                usuarioSalvo
//        );
//
//        return membroRepository.save(membro);
        return null;
    }
}
