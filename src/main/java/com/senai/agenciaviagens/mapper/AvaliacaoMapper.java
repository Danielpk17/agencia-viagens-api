package com.senai.agenciaviagens.mapper;

import com.senai.agenciaviagens.domain.Avaliacao;
import com.senai.agenciaviagens.dto.AvaliacaoResponse;
import org.springframework.stereotype.Component;

@Component
public class AvaliacaoMapper {

    public AvaliacaoResponse paraResposta(Avaliacao avaliacao) {
        return new AvaliacaoResponse(
                avaliacao.getId(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getDestino().getId(),
                avaliacao.getDestino().getNome(),
                avaliacao.getUsuario().getUsername(),
                avaliacao.getCriadoEm());
    }
}
