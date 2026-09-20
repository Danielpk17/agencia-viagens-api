package com.senai.agenciaviagens.controller;

import com.senai.agenciaviagens.dto.DestinoResponse;
import com.senai.agenciaviagens.service.DestinoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/publico/destinos")
public class DestinoPublicoController {

    private final DestinoService destinoService;

    public DestinoPublicoController(DestinoService destinoService) {
        this.destinoService = destinoService;
    }

    @GetMapping
    public ResponseEntity<List<DestinoResponse>> listarVitrine() {
        return ResponseEntity.ok(destinoService.listarAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DestinoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(destinoService.buscarAtivoPorId(id));
    }
}
