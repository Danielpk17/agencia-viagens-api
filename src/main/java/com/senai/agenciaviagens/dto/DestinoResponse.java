package com.senai.agenciaviagens.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DestinoResponse(
        Long id,
        String nome,
        String pais,
        String cidade,
        String descricao,
        BigDecimal precoMedio,
        Integer duracaoDias,
        boolean ativo,
        BigDecimal notaMedia,
        long totalAvaliacoes,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm) {
}
