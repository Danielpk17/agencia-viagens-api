package com.senai.agenciaviagens.dto;

import java.util.Set;

public record UsuarioAutenticadoResponse(
        String username,
        String nome,
        Set<String> perfis,
        boolean autenticado) {
}
