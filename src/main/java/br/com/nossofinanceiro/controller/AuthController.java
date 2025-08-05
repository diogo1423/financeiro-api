package br.com.nossofinanceiro.controller;

import br.com.nossofinanceiro.security.TokenService;
import br.com.nossofinanceiro.security.User;
import br.com.nossofinanceiro.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Value("${app.frontend-url}") // Injeta a URL do frontend a partir do application.properties
    private String frontendUrl;

    // DTOs com validação
    public record LoginRequest(
            @NotBlank(message = "Usuário é obrigatório")
            String username,

            @NotBlank(message = "Senha é obrigatória")
            String password
    ) {}

    public record RegisterRequest(
            @NotBlank(message = "Nome é obrigatório")
            String nome,

            @NotBlank(message = "Email é obrigatório")
            @Email(message = "Email inválido")
            String email,

            @NotBlank(message = "Telefone é obrigatório")
            String telefone,

            @NotBlank(message = "Usuário é obrigatório")
            @Size(min = 3, max = 20, message = "Usuário deve ter entre 3 e 20 caracteres")
            String username,

            @NotBlank(message = "Senha é obrigatória")
            @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
            String password
    ) {}

    public record AuthResponse(String token, String username, String nome, String email) {}

    public record EmailRequest(
            @NotBlank @Email String email
    ) {}

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Busca o usuário para verificar se o email foi verificado
            User user = userService.buscarPorUsername(loginRequest.username());

            if (!user.isEmailVerificado()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "EMAIL_NAO_VERIFICADO");
                response.put("message", "Por favor, verifique seu email antes de fazer login");
                response.put("email", user.getEmail());
                return ResponseEntity.status(403).body(response);
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenService.generateToken(authentication);

            return ResponseEntity.ok(new AuthResponse(
                    jwt,
                    user.getUsername(),
                    user.getNome(),
                    user.getEmail()
            ));
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "CREDENCIAIS_INVALIDAS");
            response.put("message", "Usuário ou senha incorretos");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User newUser = new User();
            newUser.setNome(registerRequest.nome());
            newUser.setEmail(registerRequest.email());
            newUser.setTelefone(registerRequest.telefone());
            newUser.setUsername(registerRequest.username());
            newUser.setPassword(registerRequest.password());

            userService.registrar(newUser);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuário registrado com sucesso! Verifique seu email para ativar sua conta.");
            response.put("email", registerRequest.email());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "ERRO_REGISTRO");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/verificar-email")
    public ResponseEntity<?> verificarEmail(@RequestParam String token) {
        try {
            userService.verificarEmail(token);

            // Redireciona para a URL do frontend com mensagem de sucesso
            return ResponseEntity.status(302)
                    .header("Location", frontendUrl + "/#/login?emailVerificado=true")
                    .build();

        } catch (RuntimeException e) {
            // Redireciona para página de erro com a mensagem de erro
            return ResponseEntity.status(302)
                    .header("Location", frontendUrl + "/#/login?erro=" + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/reenviar-verificacao")
    public ResponseEntity<?> reenviarVerificacao(@Valid @RequestBody EmailRequest request) {
        try {
            userService.reenviarEmailVerificacao(request.email());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Email de verificação reenviado com sucesso!");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "ERRO_REENVIO");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> obterPerfil(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Não autenticado");
        }

        User user = userService.buscarPorUsername(authentication.getName());

        Map<String, Object> perfil = new HashMap<>();
        perfil.put("id", user.getId());
        perfil.put("nome", user.getNome());
        perfil.put("email", user.getEmail());
        perfil.put("telefone", user.getTelefone());
        perfil.put("username", user.getUsername());
        perfil.put("emailVerificado", user.isEmailVerificado());
        perfil.put("dataCriacao", user.getDataCriacao());

        return ResponseEntity.ok(perfil);
    }
}