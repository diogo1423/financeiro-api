package br.com.nossofinanceiro.config;

import br.com.nossofinanceiro.security.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Autowired
    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Define como o Spring Security deve carregar os detalhes de um usuário.
     * Ele usa o seu UserRepository para buscar um usuário pelo nome de usuário.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    /**
     * Configura o provedor de autenticação.
     * Ele conecta o UserDetailsService (que busca o usuário) com o PasswordEncoder (que verifica a senha).
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Expõe o AuthenticationManager do Spring como um Bean para que possamos usá-lo
     * no AuthController para autenticar os usuários.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define o algoritmo de criptografia de senhas.
     * Usamos BCrypt, que é o padrão e muito seguro.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura o CORS (Cross-Origin Resource Sharing) via código.
     * Isso permite que seu frontend (rodando em localhost ou no Vercel)
     * faça requisições para sua API (rodando no Render).
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite que estas origens acessem a API
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:8000",
                "http://localhost:3000",
                "https://frontend-git-main-diogo-morais-projects.vercel.app",
                "https://frontend-three-wine-78.vercel.app", // URL ADICIONADA
                "file://"
        ));
        // Permite os métodos HTTP mais comuns
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "OPTIONS"));
        // Permite todos os cabeçalhos
        configuration.setAllowedHeaders(List.of("*"));
        // Permite o envio de credenciais (como cookies ou tokens de autorização)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica a configuração de CORS para todos os endpoints que começam com /api/
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}