package br.com.nossofinanceiro.controller;

import br.com.nossofinanceiro.Transacao;
import br.com.nossofinanceiro.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    // DTO atualizado com tags
    public record LancamentoRequest(
            String descricao, double valor, String categoria,
            LocalDate data, String tipo, String tags,
            Boolean parcelado, Integer numeroParcelas
    ) {}

    @PostMapping
    public ResponseEntity<List<Transacao>> criarLancamento(@RequestBody LancamentoRequest request) {
        List<Transacao> transacoesCriadas = transacaoService.criarLancamento(request);
        return ResponseEntity.ok(transacoesCriadas);
    }

    @GetMapping
    public ResponseEntity<List<Transacao>> listarPorMes(
            @RequestParam int ano,
            @RequestParam int mes) {
        return ResponseEntity.ok(transacaoService.listarPorMesAno(ano, mes));
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<Transacao> marcarComoPaga(@PathVariable Long id) {
        return ResponseEntity.ok(transacaoService.marcarComoPaga(id));
    }

    @PutMapping("/{id}/desmarcar-pago")
    public ResponseEntity<Transacao> desmarcarComoPaga(@PathVariable Long id) {
        return ResponseEntity.ok(transacaoService.desmarcarComoPaga(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transacao> getTransacaoPorId(@PathVariable Long id) {
        Transacao transacao = transacaoService.getTransacaoPorId(id);
        return ResponseEntity.ok(transacao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transacao> atualizarTransacao(@PathVariable Long id, @RequestBody LancamentoRequest request) {
        Transacao transacaoAtualizada = transacaoService.atualizarLancamento(id, request);
        return ResponseEntity.ok(transacaoAtualizada);
    }

    @GetMapping("/saldo")
    public ResponseEntity<Double> getSaldo() {
        return ResponseEntity.ok(transacaoService.calcularSaldo());
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<TransacaoService.DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(transacaoService.getDashboardStats());
    }

    @GetMapping("/gastos-por-categoria")
    public ResponseEntity<Map<String, Double>> getGastosPorCategoria() {
        return ResponseEntity.ok(transacaoService.getGastosPorCategoria());
    }
}