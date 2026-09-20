package com.senai.agenciaviagens.controller;

import com.senai.agenciaviagens.dto.UsuarioAutenticadoResponse;
import com.senai.agenciaviagens.dto.UsuarioResponse;
import com.senai.agenciaviagens.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioAutenticadoResponse> login(Authentication autenticacao) {
        return ResponseEntity.ok(montarResposta(autenticacao));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioAutenticadoResponse> usuarioAtual(Authentication autenticacao) {
        return ResponseEntity.ok(montarResposta(autenticacao));
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioResponse> perfilCompleto(Authentication autenticacao) {
        return ResponseEntity.ok(usuarioService.buscarPorUsername(autenticacao.getName()));
    }

    private UsuarioAutenticadoResponse montarResposta(Authentication autenticacao) {
        Set<String> perfis = autenticacao.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(perfil -> perfil.startsWith("ROLE_") ? perfil.substring(5) : perfil)
                .collect(Collectors.toCollection(TreeSet::new));
        UsuarioResponse usuario = usuarioService.buscarPorUsername(autenticacao.getName());
        return new UsuarioAutenticadoResponse(usuario.username(), usuario.nome(), perfis, true);
    }
}
