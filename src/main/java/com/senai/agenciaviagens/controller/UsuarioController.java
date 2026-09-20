package com.senai.agenciaviagens.controller;

import com.senai.agenciaviagens.dto.UsuarioRequest;
import com.senai.agenciaviagens.dto.UsuarioResponse;
import com.senai.agenciaviagens.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest requisicao,
                                                 UriComponentsBuilder uriBuilder) {
        UsuarioResponse criado = usuarioService.criar(requisicao);
        URI endereco = uriBuilder.path("/api/usuarios/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(endereco).body(criado);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UsuarioResponse> alterarStatus(@PathVariable Long id,
                                                         @RequestParam boolean ativo) {
        return ResponseEntity.ok(usuarioService.alterarStatus(id, ativo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
