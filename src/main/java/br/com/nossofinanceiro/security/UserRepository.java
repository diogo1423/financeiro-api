package br.com.nossofinanceiro.security;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    // Optional<User> findByTokenVerificacao(String token); // REMOVA ESTA LINHA
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}