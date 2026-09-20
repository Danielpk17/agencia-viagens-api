package com.senai.agenciaviagens.dto;

import java.time.LocalDateTime;

public record AvaliacaoResponse(
        Long id,
        Integer nota,
        String comentario,
        Long destinoId,
        String destinoNome,
        String autor,
        LocalDateTime criadoEm) {
}
