package br.com.nossofinanceiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Esta anotação mágica transforma a classe no coração da nossa aplicação web.
@SpringBootApplication
public class ControleFinanceiroApplication {

    public static void main(String[] args) {
        // Esta linha agora inicia um servidor web completo, em vez de só rodar o código.
        SpringApplication.run(ControleFinanceiroApplication.class, args);
    }

}
