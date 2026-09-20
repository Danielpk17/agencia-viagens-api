package com.senai.agenciaviagens.service;

import com.senai.agenciaviagens.domain.Avaliacao;
import com.senai.agenciaviagens.domain.Destino;
import com.senai.agenciaviagens.domain.Usuario;
import com.senai.agenciaviagens.dto.AvaliacaoRequest;
import com.senai.agenciaviagens.dto.AvaliacaoResponse;
import com.senai.agenciaviagens.exception.RecursoNaoEncontradoException;
import com.senai.agenciaviagens.exception.RegraNegocioException;
import com.senai.agenciaviagens.mapper.AvaliacaoMapper;
import com.senai.agenciaviagens.repository.AvaliacaoRepository;
import com.senai.agenciaviagens.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DestinoService destinoService;
    private final AvaliacaoMapper avaliacaoMapper;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            UsuarioRepository usuarioRepository,
                            DestinoService destinoService,
                            AvaliacaoMapper avaliacaoMapper) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.destinoService = destinoService;
        this.avaliacaoMapper = avaliacaoMapper;
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponse> listarPorDestino(Long destinoId) {
        destinoService.obterEntidade(destinoId);
        return avaliacaoRepository.findByDestinoIdOrderByCriadoEmDesc(destinoId).stream()
                .map(avaliacaoMapper::paraResposta)
                .toList();
    }

    @Transactional
    public AvaliacaoResponse avaliar(Long destinoId, AvaliacaoRequest requisicao, String username) {
        Destino destino = destinoService.obterEntidade(destinoId);
        if (!destino.isAtivo()) {
            throw new RegraNegocioException("Nao e possivel avaliar um destino inativo");
        }
        Usuario usuario = obterUsuario(username);
        if (avaliacaoRepository.existsByDestinoIdAndUsuarioId(destinoId, usuario.getId())) {
            throw new RegraNegocioException("Este usuario ja avaliou o destino informado");
        }
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(requisicao.nota());
        avaliacao.setComentario(requisicao.comentario());
        avaliacao.setDestino(destino);
        avaliacao.setUsuario(usuario);
        return avaliacaoMapper.paraResposta(avaliacaoRepository.save(avaliacao));
    }

    @Transactional
    public void excluir(Long avaliacaoId) {
        Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Avaliacao nao encontrada para o id " + avaliacaoId));
        avaliacaoRepository.delete(avaliacao);
    }

    private Usuario obterUsuario(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario autenticado nao encontrado no banco de dados"));
    }
}
