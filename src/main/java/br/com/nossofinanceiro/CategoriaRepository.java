package br.com.nossofinanceiro;

import br.com.nossofinanceiro.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findAllByUser(User user);

    // --- NOVO MÉTODO ---
    // Busca todas as categorias de um usuário e de um tipo específico
    List<Categoria> findAllByUserAndTipo(User user, TipoTransacao tipo);
}
