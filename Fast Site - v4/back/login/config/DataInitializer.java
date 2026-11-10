package com.login.config;

import com.login.model.Usuario;
import com.login.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String usuarioPadrao = "admin@email.com";

            if (usuarioRepository.findByUsuario(usuarioPadrao).isEmpty()) {
                Usuario admin = new Usuario(
                        usuarioPadrao,
                        passwordEncoder.encode("12345678"),
                        "Administrador"
                );

                usuarioRepository.save(admin);
                System.out.println(">>> Utilizador padrão criado com sucesso!");
            }
        };
    }
}