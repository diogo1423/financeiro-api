package br.com.nossofinanceiro.security;

import br.com.nossofinanceiro.service.EmailService;
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

    @Autowired
    private EmailService emailService;

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

        // Gera token de verificação
        String token = UUID.randomUUID().toString();
        user.setTokenVerificacao(token);
        user.setDataTokenVerificacao(LocalDateTime.now());
        user.setEmailVerificado(false);

        // Salva o usuário
        User novoUsuario = userRepository.save(user);

        // Envia email de verificação
        emailService.enviarEmailVerificacao(user.getEmail(), user.getNome(), token);

        return novoUsuario;
    }

    @Transactional
    public boolean verificarEmail(String token) {
        User user = userRepository.findByTokenVerificacao(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        // Verifica se o token não expirou (24 horas)
        if (user.getDataTokenVerificacao().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        // Ativa o usuário
        user.setEmailVerificado(true);
        user.setTokenVerificacao(null);
        user.setDataTokenVerificacao(null);
        userRepository.save(user);

        // Envia email de boas-vindas
        emailService.enviarEmailBoasVindas(user.getEmail(), user.getNome());

        return true;
    }

    public User buscarPorUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public boolean reenviarEmailVerificacao(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));

        if (user.isEmailVerificado()) {
            throw new RuntimeException("Email já verificado");
        }

        // Gera novo token
        String token = UUID.randomUUID().toString();
        user.setTokenVerificacao(token);
        user.setDataTokenVerificacao(LocalDateTime.now());
        userRepository.save(user);

        // Reenvia email
        emailService.enviarEmailVerificacao(user.getEmail(), user.getNome(), token);

        return true;
    }
}