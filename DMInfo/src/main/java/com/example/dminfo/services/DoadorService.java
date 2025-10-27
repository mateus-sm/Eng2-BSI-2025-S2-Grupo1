package com.example.dminfo.services;

import com.example.dminfo.model.Doador;
import com.example.dminfo.repositories.DoadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DoadorService {
    @Autowired
    private DoadorRepository doadorRepository;

    public List<Doador> exibir() { return doadorRepository.findAll(); }

    public Optional<Doador> buscarPorId(Integer id) {
        return doadorRepository.findById(id);
    }

    public Doador salvar(Doador doador){
        return doadorRepository.save(doador);
    }

    public Doador atualizar(Integer id, Doador doadorDetalhes){
        //Busca o doador
        Doador doador = buscarPorId(id).orElseThrow(() -> new RuntimeException("Doador não encontrado com id: " + id));

        //Atualiza os campos
        doador.setNome(doadorDetalhes.getNome());
        doador.setDocumento(doadorDetalhes.getDocumento());
        doador.setRua(doadorDetalhes.getRua());
        doador.setBairro(doadorDetalhes.getBairro());
        doador.setCidade(doadorDetalhes.getCidade());
        doador.setUf(doadorDetalhes.getUf());
        doador.setCep(doadorDetalhes.getCep());
        doador.setEmail(doadorDetalhes.getEmail());
        doador.setTelefone(doadorDetalhes.getTelefone());
        doador.setContato(doadorDetalhes.getContato());

        //Salva
        return salvar(doador);
    }

    public void excluir(Integer id) {
        doadorRepository.deleteById(id);
    }
}