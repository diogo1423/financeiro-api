package br.com.nossofinanceiro.controller;

import br.com.nossofinanceiro.Transacao;
import br.com.nossofinanceiro.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private TransacaoService transacaoService;

    @GetMapping("/transacoes")
    public ResponseEntity<List<Transacao>> getTransacoesPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String categoria) {

        List<Transacao> relatorio = transacaoService.gerarRelatorio(dataInicio, dataFim, categoria);
        return ResponseEntity.ok(relatorio);
    }
}
