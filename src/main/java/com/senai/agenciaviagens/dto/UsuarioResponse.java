package com.senai.agenciaviagens.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UsuarioResponse(
        Long id,
        String username,
        String nome,
        String email,
        boolean ativo,
        Set<String> perfis,
        LocalDateTime criadoEm) {
}
