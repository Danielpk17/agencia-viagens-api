package com.senai.agenciaviagens.controller;

import com.senai.agenciaviagens.dto.AvaliacaoRequest;
import com.senai.agenciaviagens.dto.AvaliacaoResponse;
import com.senai.agenciaviagens.service.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping("/destinos/{destinoId}/avaliacoes")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<AvaliacaoResponse>> listarPorDestino(@PathVariable Long destinoId) {
        return ResponseEntity.ok(avaliacaoService.listarPorDestino(destinoId));
    }

    @PostMapping("/destinos/{destinoId}/avaliacoes")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AvaliacaoResponse> avaliar(@PathVariable Long destinoId,
                                                     @Valid @RequestBody AvaliacaoRequest requisicao,
                                                     Authentication autenticacao,
                                                     UriComponentsBuilder uriBuilder) {
        AvaliacaoResponse criada = avaliacaoService.avaliar(destinoId, requisicao, autenticacao.getName());
        URI endereco = uriBuilder.path("/api/destinos/{destinoId}/avaliacoes").buildAndExpand(destinoId).toUri();
        return ResponseEntity.created(endereco).body(criada);
    }

    @DeleteMapping("/avaliacoes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        avaliacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
