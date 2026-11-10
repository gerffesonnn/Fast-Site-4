package com.login.controller;

import com.login.model.Usuario;
import com.login.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping({"/api/login", "/api/auth/login"})
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpSession session) {
        String usuarioInformado = firstNonBlank(payload.get("usuario"), payload.get("email"));
        String senhaInformada = payload.get("senha");
        if (isBlank(usuarioInformado) || isBlank(senhaInformada)) {
            return error(HttpStatus.BAD_REQUEST, "Usuário e senha são obrigatórios.");
        }

        Usuario usuario = usuarioRepository.findByUsuario(usuarioInformado.trim())
                .filter(candidato -> passwordEncoder.matches(senhaInformada, candidato.getSenha()))
                .orElse(null);
        if (usuario == null) {
            return error(HttpStatus.UNAUTHORIZED, "Usuário ou senha incorretos.");
        }

        session.setAttribute("usuarioNome", usuario.getNome());
        return ResponseEntity.ok(Map.of(
                "message", "Login efetuado com sucesso!",
                "usuario", usuario.getUsuario()
        ));
    }

    @PostMapping({"/api/usuarios", "/api/auth/signup", "/api/auth/register"})
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload, HttpSession session) {
        String usuarioInformado = firstNonBlank(payload.get("usuario"), payload.get("email"));
        String senhaInformada = payload.get("senha");
        String nomeInformado = firstNonBlank(payload.get("nome"), usuarioInformado);
        if (isBlank(usuarioInformado) || isBlank(senhaInformada) || isBlank(nomeInformado)) {
            return error(HttpStatus.BAD_REQUEST, "Usuário, senha e nome são obrigatórios.");
        }
        if (senhaInformada.length() < 8) {
            return error(HttpStatus.BAD_REQUEST, "A senha deve ter pelo menos 8 caracteres.");
        }

        String usuarioNormalizado = usuarioInformado.trim();
        if (usuarioRepository.findByUsuario(usuarioNormalizado).isPresent()) {
            return error(HttpStatus.BAD_REQUEST, "Usuário já existente!");
        }

        Usuario usuario = new Usuario(
                usuarioNormalizado,
                passwordEncoder.encode(senhaInformada),
                nomeInformado.trim()
        );
        try {
            usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException exception) {
            return error(HttpStatus.CONFLICT, "Usuário já existente!");
        }
        session.setAttribute("usuarioNome", usuario.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Login cadastrado com sucesso!",
                "usuario", usuario.getUsuario()
        ));
    }

    @GetMapping("/session")
    public ResponseEntity<?> obterSessao(HttpSession session) {
        String nome = (String) session.getAttribute("usuarioNome");
        if (nome == null) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "name", nome
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Sessão encerrada com sucesso."));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String firstNonBlank(String first, String fallback) {
        return isBlank(first) ? fallback : first;
    }

    private static ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}