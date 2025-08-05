package br.com.nossofinanceiro.controller;

import br.com.nossofinanceiro.security.TokenService;
import br.com.nossofinanceiro.security.User;
import br.com.nossofinanceiro.security.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    // DTOs (não mudam)
    public record LoginRequest(String username, String password) {}
    public record RegisterRequest(String nome, String email, String telefone, String username, String password) {}
    public record AuthResponse(String token, String username, String nome, String email) {}

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // LÓGICA DE VERIFICAÇÃO DE EMAIL REMOVIDA DAQUI

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenService.generateToken(authentication);

            User user = userService.buscarPorUsername(loginRequest.username());

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

            // MENSAGEM DE SUCESSO ATUALIZADA
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuário registrado com sucesso! Você já pode fazer login.");
            response.put("email", registerRequest.email());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "ERRO_REGISTRO");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // OS ENDPOINTS /verificar-email e /reenviar-verificacao FORAM REMOVIDOS

    // O endpoint de perfil continua o mesmo
    @GetMapping("/perfil")
    public ResponseEntity<?> obterPerfil(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Não autenticado");
        }

        User user = userService.buscarPorUsername(authentication.getName());
        Map<String, Object> perfil = new HashMap<>();
        perfil.put("id", user.getId());
        perfil.put("nome", user.getNome());
        //... (etc)
        return ResponseEntity.ok(perfil);
    }
}