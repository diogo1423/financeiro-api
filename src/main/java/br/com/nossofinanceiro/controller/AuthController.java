package br.com.nossofinanceiro.controller;

import br.com.nossofinanceiro.security.TokenService;
import br.com.nossofinanceiro.security.User;
import br.com.nossofinanceiro.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    // --- AQUI ESTÁ A CORREÇÃO ---
    // A variável deve ser do tipo UserService.
    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    // DTOs (Data Transfer Objects) para o corpo da requisição/resposta
    public record LoginRequest(String username, String password) {}
    public record AuthResponse(String token) {}
    public record RegisterRequest(String username, String password) {}

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenService.generateToken(authentication);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        User newUser = new User();
        newUser.setUsername(registerRequest.username());
        newUser.setPassword(registerRequest.password());

        // Agora esta linha vai funcionar, pois 'userService' é do tipo correto.
        userService.registrar(newUser);

        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }
}
