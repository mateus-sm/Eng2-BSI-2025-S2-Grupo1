//package com.example.dminfo.view;
//
//import com.example.dminfo.controller.FrequenciaAtividadeController;
//import com.example.dminfo.model.CriarRealizacaoAtividades;
//import com.example.dminfo.model.FrequenciaAtividade;
//import com.example.dminfo.model.Membro;
//import com.example.dminfo.util.MembroErro;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@CrossOrigin
//@RestController
//@RequestMapping("apis/frequencia")
//public class FrequenciaAtividadeView {
//
//    @Autowired
//    private FrequenciaAtividadeController controller;
//
//    @GetMapping("/atividade/{id}")
//    public ResponseEntity<Object> getFrequenciasPorAtividade(@PathVariable int id) {
//        try {
//            List<FrequenciaAtividade> frequencias = controller.buscarFrequenciasPorAtividade(id);
//            return ResponseEntity.ok(frequencias);
//        } catch (RuntimeException e) {
//            // Captura erros do DAO
//            return ResponseEntity.internalServerError().body(new MembroErro("Erro ao buscar frequências: " + e.getMessage()));
//        }
//    }
//
//    @GetMapping("/membros")
//    public ResponseEntity<Object> listarMembros(
//            @RequestParam(value = "busca", required = false) String termoBusca) {
//        try {
//            /*
//            // --- ATIVAR FUNÇÃO DE TESTE AQUI ---
//            List<Membro> membrosTeste = controller.getMembrosTeste();
//            return ResponseEntity.ok(membrosTeste);
//            // ------------------------------------
//            */
//         // Descomente esta parte para retornar ao método original APÓS o teste
//        List<Membro> membros = controller.listarMembros(termoBusca);
//        return ResponseEntity.ok(membros);
//
//
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(new MembroErro("Erro ao listar membros: " + e.getMessage()));
//        }
//    }
//
//    /*
//    @GetMapping("/membros")
//    public ResponseEntity<Object> listarMembros(
//            @RequestParam(value = "busca", required = false) String termoBusca) {
//        try {
//            List<Membro> membros = controller.listarMembros(termoBusca);
//            return ResponseEntity.ok(membros);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(new MembroErro("Erro ao listar membros: " + e.getMessage()));
//        }
//    }
//
//    */
//
//    /** 1. GET: Buscar Atividades pela Data (DT. INÍCIO) */
//    // Endpoint: /apis/frequencia/atividades?data=YYYY-MM-DD
//    @GetMapping("/atividades")
//    public ResponseEntity<Object> buscarAtividadesPorData(
//            @RequestParam("data") String dataStr) {
//        try {
//            LocalDate data = LocalDate.parse(dataStr);
//            List<CriarRealizacaoAtividades> atividades = controller.buscarAtividadesPorData(data);
//            return ResponseEntity.ok(atividades);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(new MembroErro("Formato de data inválido ou erro: " + e.getMessage()));
//        }
//    }
//
//    /** 2. GET: Buscar Membro pelo ID */
//    // Endpoint: /apis/frequencia/membros/{id}
//    @GetMapping("/membros/{id}")
//    public ResponseEntity<Object> buscarMembro(@PathVariable int id) {
//        try {
//            Membro membro = controller.buscarMembroPorId(id);
//            if (membro != null) {
//                return ResponseEntity.ok(membro);
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MembroErro("Membro não encontrado."));
//            }
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(new MembroErro("Erro ao buscar membro: " + e.getMessage()));
//        }
//    }
//
//    /** 3. POST: Lançar Frequência */
//    // Endpoint: /apis/frequencia?idAtividade={id_criacao}&idMembro={id_membro}
//    @PostMapping
//    public ResponseEntity<Object> lancarFrequencia(
//            @RequestParam("idAtividade") int idAtividade,
//            @RequestParam("idMembro") int idMembro) {
//        try {
//            FrequenciaAtividade novaFrequencia = controller.lancarFrequencia(idAtividade, idMembro);
//            return ResponseEntity.status(HttpStatus.CREATED).body(novaFrequencia);
//        } catch (RuntimeException e) {
//            // Captura erros de validação (Membro não encontrado, já registrado, etc.)
//            return ResponseEntity.badRequest().body(new MembroErro(e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(new MembroErro("Erro ao registrar frequência: " + e.getMessage()));
//        }
//    }
//}