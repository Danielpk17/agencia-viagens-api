package com.senai.agenciaviagens.mapper;

import com.senai.agenciaviagens.domain.Avaliacao;
import com.senai.agenciaviagens.domain.Destino;
import com.senai.agenciaviagens.dto.DestinoRequest;
import com.senai.agenciaviagens.dto.DestinoResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DestinoMapperTest {

    private final DestinoMapper destinoMapper = new DestinoMapper();

    @Test
    void deveConverterRequisicaoEmEntidadeAtivaPorPadrao() {
        DestinoRequest requisicao = new DestinoRequest("Gramado", "Brasil", "Gramado",
                "Serra gaucha", new BigDecimal("3200.00"), 4, null);

        Destino destino = destinoMapper.paraEntidade(requisicao);

        assertEquals("Gramado", destino.getNome());
        assertEquals("Brasil", destino.getPais());
        assertTrue(destino.isAtivo());
    }

    @Test
    void deveCalcularNotaMediaDasAvaliacoes() {
        Destino destino = new Destino();
        destino.setId(1L);
        destino.setNome("Bariloche");
        destino.setPais("Argentina");
        destino.setCidade("Bariloche");
        destino.setPrecoMedio(new BigDecimal("5400.00"));
        destino.setDuracaoDias(7);
        destino.setAvaliacoes(List.of(criarAvaliacao(5), criarAvaliacao(4), criarAvaliacao(3)));

        DestinoResponse resposta = destinoMapper.paraResposta(destino);

        assertEquals(3, resposta.totalAvaliacoes());
        assertEquals(new BigDecimal("4.00"), resposta.notaMedia());
    }

    private Avaliacao criarAvaliacao(int nota) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(nota);
        return avaliacao;
    }
}
