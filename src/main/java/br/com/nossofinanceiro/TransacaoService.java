package br.com.nossofinanceiro;

import br.com.nossofinanceiro.controller.TransacaoController.LancamentoRequest;
import br.com.nossofinanceiro.security.User;
import br.com.nossofinanceiro.security.UserRepository; // Importação que faltava
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private UserRepository userRepository; // Agora o compilador sabe onde encontrar esta classe

    public record DashboardStats(double receitasMes, double despesasPagasMes, double balancoMes) {}

    private User getUsuarioLogado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    public List<Transacao> criarLancamento(LancamentoRequest request) {
        User usuarioLogado = getUsuarioLogado();
        List<Transacao> transacoesSalvas = new ArrayList<>();
        boolean eReceita = request.tipo().equalsIgnoreCase("RECEITA");

        if (request.parcelado() != null && request.parcelado() && request.numeroParcelas() != null && request.numeroParcelas() > 1) {
            for (int i = 1; i <= request.numeroParcelas(); i++) {
                Transacao t = new Transacao();
                t.setDescricao(request.descricao() + " (" + i + "/" + request.numeroParcelas() + ")");
                t.setValor(eReceita ? request.valor() : request.valor() / request.numeroParcelas());
                t.setCategoria(request.categoria());
                t.setData(request.data().plusMonths(i - 1));
                t.setTipo(TipoTransacao.valueOf(request.tipo()));
                t.setTags(request.tags());
                t.setParcelaAtual(i);
                t.setTotalParcelas(request.numeroParcelas());
                t.setUser(usuarioLogado);
                t.setPago(eReceita);
                transacoesSalvas.add(transacaoRepository.save(t));
            }
        } else {
            Transacao t = new Transacao();
            t.setDescricao(request.descricao());
            t.setValor(request.valor());
            t.setCategoria(request.categoria());
            t.setData(request.data());
            t.setTipo(TipoTransacao.valueOf(request.tipo()));
            t.setTags(request.tags());
            t.setPago(eReceita);
            t.setUser(usuarioLogado);
            transacoesSalvas.add(transacaoRepository.save(t));
        }
        return transacoesSalvas;
    }

    public List<Transacao> listarPorMesAno(int ano, int mes) {
        User usuarioLogado = getUsuarioLogado();
        LocalDate inicioMes = LocalDate.of(ano, mes, 1);
        LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());
        return transacaoRepository.findAllByUserAndDataBetweenOrderByDataDesc(usuarioLogado, inicioMes, fimMes);
    }

    public Transacao marcarComoPaga(Long transacaoId) {
        User usuarioLogado = getUsuarioLogado();
        Transacao transacao = transacaoRepository.findByIdAndUser(transacaoId, usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada ou não pertence ao usuário"));

        transacao.setPago(true);
        return transacaoRepository.save(transacao);
    }

    public Transacao desmarcarComoPaga(Long transacaoId) {
        User usuarioLogado = getUsuarioLogado();
        Transacao transacao = transacaoRepository.findByIdAndUser(transacaoId, usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada ou não pertence ao usuário"));

        transacao.setPago(false);
        return transacaoRepository.save(transacao);
    }

    public Transacao getTransacaoPorId(Long id) {
        User usuarioLogado = getUsuarioLogado();
        return transacaoRepository.findByIdAndUser(id, usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada ou não pertence ao usuário."));
    }

    public Transacao atualizarLancamento(Long id, LancamentoRequest request) {
        Transacao transacaoExistente = getTransacaoPorId(id);

        transacaoExistente.setDescricao(request.descricao());
        transacaoExistente.setValor(request.valor());
        transacaoExistente.setCategoria(request.categoria());
        transacaoExistente.setData(request.data());
        transacaoExistente.setTipo(TipoTransacao.valueOf(request.tipo()));
        transacaoExistente.setTags(request.tags());

        return transacaoRepository.save(transacaoExistente);
    }

    public List<Transacao> gerarRelatorio(LocalDate dataInicio, LocalDate dataFim, String categoria) {
        User usuarioLogado = getUsuarioLogado();
        if (!StringUtils.hasText(categoria) || categoria.equalsIgnoreCase("TODAS")) {
            return transacaoRepository.findAllByUserAndDataBetweenOrderByDataDesc(usuarioLogado, dataInicio, dataFim);
        } else {
            return transacaoRepository.findAllByUserAndDataBetweenAndCategoriaOrderByDataDesc(usuarioLogado, dataInicio, dataFim, categoria);
        }
    }

    public double calcularSaldo() {
        return transacaoRepository.findAllByUser(getUsuarioLogado()).stream()
                .mapToDouble(t -> {
                    if (t.getTipo() == TipoTransacao.RECEITA) {
                        return t.getValor();
                    } else {
                        return t.isPago() ? -t.getValor() : 0;
                    }
                })
                .sum();
    }

    public DashboardStats getDashboardStats() {
        User usuarioLogado = getUsuarioLogado();
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

        List<Transacao> transacoesDoMes = transacaoRepository.findAllByUserAndDataBetweenOrderByDataDesc(usuarioLogado, inicioMes, fimMes);

        double receitasMes = transacoesDoMes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.RECEITA)
                .mapToDouble(Transacao::getValor)
                .sum();

        double despesasPagasMes = transacoesDoMes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA && t.isPago())
                .mapToDouble(Transacao::getValor)
                .sum();

        double balancoMes = receitasMes - despesasPagasMes;

        return new DashboardStats(receitasMes, despesasPagasMes, balancoMes);
    }

    public Map<String, Double> getGastosPorCategoria() {
        return transacaoRepository.findAllByUser(getUsuarioLogado()).stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA && t.isPago())
                .collect(Collectors.groupingBy(Transacao::getCategoria, Collectors.summingDouble(Transacao::getValor)));
    }
}
