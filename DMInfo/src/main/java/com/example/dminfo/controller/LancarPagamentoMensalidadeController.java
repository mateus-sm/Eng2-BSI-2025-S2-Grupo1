package com.example.dminfo.controller;

import com.example.dminfo.model.Erro;
import com.example.dminfo.model.LancarPagamentoMensalidade;
import com.example.dminfo.services.LancarPagamentoMensalidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
    @RequestMapping("/lancar-pagamento-mensalidade")
public class LancarPagamentoMensalidadeController {
    @Autowired LancarPagamentoMensalidadeService LPMservice;

    @GetMapping
    ResponseEntity<Object> listarAll(){
        return ResponseEntity.ok(LPMservice.exibirAll());
    }

    @GetMapping("/por-mes-ano")
    ResponseEntity<Object> listarMesAno(@RequestParam int mes, @RequestParam int ano){
        return ResponseEntity.ok(LPMservice.consultaMesAndAno(mes, ano));
    }

    @GetMapping("/por-membro")
    ResponseEntity<Object> listarMembros(@RequestParam int idMembro){
        return ResponseEntity.ok(LPMservice.consultaMembro(idMembro));
    }

    @GetMapping("/consulta-unica")
    public ResponseEntity<Object> buscarPagamentoUnico(@RequestParam int idMembro, @RequestParam int mes, @RequestParam int ano)
    {
        Optional<LancarPagamentoMensalidade> pagamentoOptional = LPMservice.consultarPagamentoUnico(idMembro, mes, ano);

        if (pagamentoOptional.isPresent()) {
            return ResponseEntity.ok(pagamentoOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Erro("Não Encontrado", "Nenhum pagamento localizado para os dados informados."));
        }
    }

    @PostMapping
    ResponseEntity<Object> create(@RequestBody LancarPagamentoMensalidade LPM){

        if (LPM == null) {
            return ResponseEntity.badRequest().body(new Erro("Erro de Objeto", "Dados do pagamento estão nulos."));
        }

        int idMembro = LPM.getIdMembro();
        int mes = LPM.getMes();
        int ano = LPM.getAno();

        Optional<LancarPagamentoMensalidade> mensalJaExiste = LPMservice.consultarPagamentoUnico(idMembro, mes, ano);

        if(mensalJaExiste.isPresent()){
            return ResponseEntity.badRequest().body(new Erro("Erro de Negócio", "Pagamento já cadastrado para este membro no mês/ano informado."));
        }

        LancarPagamentoMensalidade novoPagamento = LPMservice.salvar(LPM);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPagamento);
    }
}
