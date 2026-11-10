package com.login.controller;

import com.login.model.Cliente;
import com.login.repository.ClienteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClienteRepository clienteRepository;

    public ClientController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public ResponseEntity<?> listarClientes(HttpSession session) {
        if (session.getAttribute("usuarioNome") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Faça login para continuar."));
        }
        List<Cliente> clients = clienteRepository.findAll();
        return ResponseEntity.ok(Map.of("clients", clients));
    }

    @PostMapping
    public ResponseEntity<?> cadastrarCliente(@RequestBody Cliente cliente, HttpSession session) {
        if (session.getAttribute("usuarioNome") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Faça login para continuar."));
        }
        clienteRepository.save(cliente);
        return ResponseEntity.ok(Map.of("message", "Cliente cadastrado com sucesso!"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarClientePorId(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("usuarioNome") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Faça login para continuar."));
        }
        return clienteRepository.findById(id)
                .map(cliente -> ResponseEntity.ok(Map.<String, Object>of("client", cliente)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("error", "Cliente não encontrado.")));
    }
}