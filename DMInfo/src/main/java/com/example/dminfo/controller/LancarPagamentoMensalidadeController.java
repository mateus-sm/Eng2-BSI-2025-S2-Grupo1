package com.example.dminfo.controller;

import com.example.dminfo.model.Erro;
import com.example.dminfo.model.LancarPagamentoMensalidade;
import com.example.dminfo.services.LancarPagamentoMensalidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/lancar-pagamento-mensalidade")
public class LancarPagamentoMensalidadeController {
    @Autowired LancarPagamentoMensalidadeService LPMservice;

    @GetMapping
    ResponseEntity<Object> listarAll(){
        return ResponseEntity.ok(LPMservice.exibirAll());
    }

    @GetMapping
    ResponseEntity<Object> listarMesAno(@RequestParam int mes, int ano){
        return ResponseEntity.ok(LPMservice.consultaMesAndAno(mes, ano));
    }

    @GetMapping
    ResponseEntity<Object> listarMembros(@RequestParam int idMembro){
        return ResponseEntity.ok(LPMservice.consultaMembro(idMembro));
    }

    @PostMapping
    ResponseEntity<Object> create(@RequestBody LancarPagamentoMensalidade LPM){
        if(LPM != null){
            Optional<LancarPagamentoMensalidade> mensal = LPMservice.consultaMembro(LPM.getIdMembro());
            if(mensal.isPresent()){
                return ResponseEntity.badRequest().body(new Erro("Erro de Objeto", "Conquista ja existe"));
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body((LPMservice.salvar(LPM)));
    }
}
