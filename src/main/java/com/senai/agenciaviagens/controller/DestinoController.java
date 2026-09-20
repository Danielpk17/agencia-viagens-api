package com.senai.agenciaviagens.controller;

import com.senai.agenciaviagens.dto.DestinoRequest;
import com.senai.agenciaviagens.dto.DestinoResponse;
import com.senai.agenciaviagens.service.DestinoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/destinos")
public class DestinoController {

    private final DestinoService destinoService;

    public DestinoController(DestinoService destinoService) {
        this.destinoService = destinoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<DestinoResponse>> listar(@RequestParam(required = false) String pais,
                                                        @RequestParam(required = false) String busca) {
        if (pais != null && !pais.isBlank()) {
            return ResponseEntity.ok(destinoService.listarPorPais(pais));
        }
        if (busca != null && !busca.isBlank()) {
            return ResponseEntity.ok(destinoService.buscarPorTermo(busca));
        }
        return ResponseEntity.ok(destinoService.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DestinoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(destinoService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DestinoResponse> criar(@Valid @RequestBody DestinoRequest requisicao,
                                                 UriComponentsBuilder uriBuilder) {
        DestinoResponse criado = destinoService.criar(requisicao);
        URI endereco = uriBuilder.path("/api/destinos/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(endereco).body(criado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DestinoResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody DestinoRequest requisicao) {
        return ResponseEntity.ok(destinoService.atualizar(id, requisicao));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        destinoService.excluir(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
