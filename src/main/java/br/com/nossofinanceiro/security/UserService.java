package br.com.nossofinanceiro.security;

// A importação do EmailService não é mais necessária aqui.
// import br.com.nossofinanceiro.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // A injeção do EmailService não é mais necessária.
    // @Autowired
    // private EmailService emailService;

    @Transactional
    public User registrar(User user) {
        // Validações
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Nome de usuário já existe");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        // Criptografa a senha
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ATIVA O USUÁRIO IMEDIATAMENTE
        user.setEmailVerificado(true);

        // A lógica de token e envio de email foi removida.

        // Salva o usuário já ativo no banco de dados
        return userRepository.save(user);
    }

    @Transactional
    public boolean verificarEmail(String token) {
        // Este método não é mais chamado, mas pode ser removido completamente
        // junto com o endpoint no AuthController.
        throw new UnsupportedOperationException("A verificação de email foi desativada.");
    }

    public User buscarPorUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public boolean reenviarEmailVerificacao(String email) {
        // Este método também não é mais chamado e pode ser removido.
        throw new UnsupportedOperationException("A verificação de email foi desativada.");
    }
}