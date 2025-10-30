package com.example.dminfo.services;

import com.example.dminfo.model.Administrador;
import com.example.dminfo.model.Usuario;
import com.example.dminfo.repositories.AdministradorRepository;
import com.example.dminfo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdministradorService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    public Administrador getById(Integer id) {
        Administrador administrador;
        administrador = administradorRepository.findById(id).orElse(null);
        return administrador;
    }

    public List<Administrador> listar() {
        return administradorRepository.findAll();
    }

    public Administrador salvar(Administrador administrador) {

        // Busca o usuário pelo ID fornecido no JSON
        Usuario usuario = usuarioRepository.findById(administrador.getUsuario().getId()).orElse(null);
        if(usuario == null)
            throw new RuntimeException("Usuário não encontrado para o ID fornecido.");

        // Verifica se o usuário já é um administrador
        Administrador administradorExistente = administradorRepository.findByUsuario(usuario).orElse(null);
        if(administradorExistente != null) {
            throw new RuntimeException("Este usuário já está associado a um administrador.");
        }

        // Define os dados do novo membro
        administrador.setUsuario(usuario);
        administrador.setDtIni(LocalDate.now());
        return administradorRepository.save(administrador);
    }

    public Administrador update(Integer id, Administrador adminDetails) {

        Administrador adminBanco = getById(id);
        if (adminBanco == null) {
            throw new RuntimeException("Administrador não encontrado para o ID: " + id);
        }
        adminBanco.setDtFim(adminDetails.getDtFim());

        return administradorRepository.save(adminBanco);
    }

    public boolean excluir(Integer id) {
        Administrador administrador;

        administrador = administradorRepository.findById(id).orElse(null);
        if  (administrador != null) {
            administradorRepository.deleteById(id);
            return true;
        }

        return false;
    }
}