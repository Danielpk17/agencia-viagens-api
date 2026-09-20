package com.senai.agenciaviagens.mapper;

import com.senai.agenciaviagens.domain.Avaliacao;
import com.senai.agenciaviagens.domain.Destino;
import com.senai.agenciaviagens.dto.DestinoRequest;
import com.senai.agenciaviagens.dto.DestinoResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class DestinoMapper {

    public Destino paraEntidade(DestinoRequest requisicao) {
        Destino destino = new Destino();
        aplicar(requisicao, destino);
        return destino;
    }

    public void aplicar(DestinoRequest requisicao, Destino destino) {
        destino.setNome(requisicao.nome().trim());
        destino.setPais(requisicao.pais().trim());
        destino.setCidade(requisicao.cidade().trim());
        destino.setDescricao(requisicao.descricao());
        destino.setPrecoMedio(requisicao.precoMedio());
        destino.setDuracaoDias(requisicao.duracaoDias());
        destino.setAtivo(requisicao.ativo() == null || requisicao.ativo());
    }

    public DestinoResponse paraResposta(Destino destino) {
        List<Avaliacao> avaliacoes = destino.getAvaliacoes();
        long total = avaliacoes == null ? 0L : avaliacoes.size();
        BigDecimal media = BigDecimal.ZERO;
        if (total > 0) {
            double soma = avaliacoes.stream().mapToInt(Avaliacao::getNota).average().orElse(0.0);
            media = BigDecimal.valueOf(soma).setScale(2, RoundingMode.HALF_UP);
        }
        return new DestinoResponse(
                destino.getId(),
                destino.getNome(),
                destino.getPais(),
                destino.getCidade(),
                destino.getDescricao(),
                destino.getPrecoMedio(),
                destino.getDuracaoDias(),
                destino.isAtivo(),
                media,
                total,
                destino.getCriadoEm(),
                destino.getAtualizadoEm());
    }
}
