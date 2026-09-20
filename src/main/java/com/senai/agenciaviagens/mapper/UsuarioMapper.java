package com.senai.agenciaviagens.mapper;

import com.senai.agenciaviagens.domain.Perfil;
import com.senai.agenciaviagens.domain.Usuario;
import com.senai.agenciaviagens.dto.UsuarioResponse;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public UsuarioResponse paraResposta(Usuario usuario) {
        Set<String> perfis = usuario.getPerfis().stream()
                .map(Perfil::getNomeSemPrefixo)
                .collect(Collectors.toCollection(java.util.TreeSet::new));
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.isAtivo(),
                perfis,
                usuario.getCriadoEm());
    }
}
