package com.example.dminfo.services;

import com.example.dminfo.model.LancarPagamentoMensalidade;
import com.example.dminfo.repositories.LancarPagamentoMensalidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LancarPagamentoMensalidadeService {
    @Autowired
    private LancarPagamentoMensalidadeRepository repository;

    public List<LancarPagamentoMensalidade> exibirAll(){
        return repository.findAll();
    }

    public List<LancarPagamentoMensalidade> exibirMesAndAno(int mes, int ano){
        return repository.findByMesAndAno(mes, ano);
    }
    
    public List<LancarPagamentoMensalidade> exibirAno(int ano){
        return repository.findByAno(ano);
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
