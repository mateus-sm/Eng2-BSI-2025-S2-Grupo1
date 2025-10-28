package com.example.dminfo.controller;

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
    public ResponseEntity<Object> listarAll(){
        return ResponseEntity.ok(LPMservice.exibirAll());
    }

    @GetMapping
    public ResponseEntity<Object> listarMesAno(@RequestParam int mes, int ano){
        return ResponseEntity.ok(LPMservice.exibirMesAndAno(mes, ano));
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody LancarPagamentoMensalidade LPM){
        if(LPM != null){
            Optional<LancarPagamentoMensalidade> mensal = LPMservice
        }
        return ResponseEntity.status(HttpStatus.CREATED).body((LPMservice.salvar(LPM)));
    }
}
