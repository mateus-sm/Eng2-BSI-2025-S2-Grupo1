package com.example.dminfo.services;

import com.example.dminfo.model.LancarPagamentoMensalidade;
import com.example.dminfo.repositories.LancarPagamentoMensalidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LancarPagamentoMensalidadeService {
    @Autowired
    private LancarPagamentoMensalidadeRepository repository;

    public List<LancarPagamentoMensalidade> exibirAll(){
        return repository.findAll();
    }

    public List<LancarPagamentoMensalidade> consultaMesAndAno(int mes, int ano){
        return repository.findByMesAndAno(mes, ano);
    }
    
    public List<LancarPagamentoMensalidade> consultaAno(int ano){
        return repository.findByAno(ano);
    }

    public List<LancarPagamentoMensalidade> consultaMembro(int idMembro){
        return repository.findByMembro(idMembro);
    }

    public boolean salvar(LancarPagamentoMensalidade Lpm){
        boolean retorno = false;

        if(repository.existsById(Lpm.getIdMensalidade())){
            repository.save(Lpm);
            retorno =  true;
        }
        return retorno;
    }

    public boolean excluir(Integer id){
        boolean retorno = false;

        if(repository.existsById(id)){
            repository.deleteById(id);
            retorno =  true;
        }
        return retorno;
    }

}
