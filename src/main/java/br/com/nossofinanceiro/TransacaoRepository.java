package br.com.nossofinanceiro;

import br.com.nossofinanceiro.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // Importe o Optional

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findAllByUser(User user);
    List<Transacao> findAllByUserAndDataBetweenOrderByDataDesc(User user, LocalDate inicio, LocalDate fim);
    List<Transacao> findAllByUserAndDataBetweenAndCategoriaOrderByDataDesc(User user, LocalDate inicio, LocalDate fim, String categoria);

    // --- NOVO MÉTODO ---
    // Busca uma transação pelo seu ID e pelo seu dono (usuário)
    Optional<Transacao> findByIdAndUser(Long id, User user);
}
