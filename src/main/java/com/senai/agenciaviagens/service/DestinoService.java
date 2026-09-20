package com.senai.agenciaviagens.service;

import com.senai.agenciaviagens.domain.Destino;
import com.senai.agenciaviagens.dto.DestinoRequest;
import com.senai.agenciaviagens.dto.DestinoResponse;
import com.senai.agenciaviagens.exception.RecursoNaoEncontradoException;
import com.senai.agenciaviagens.exception.RegraNegocioException;
import com.senai.agenciaviagens.mapper.DestinoMapper;
import com.senai.agenciaviagens.repository.DestinoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DestinoService {

    private final DestinoRepository destinoRepository;
    private final DestinoMapper destinoMapper;

    public DestinoService(DestinoRepository destinoRepository, DestinoMapper destinoMapper) {
        this.destinoRepository = destinoRepository;
        this.destinoMapper = destinoMapper;
    }

    @Transactional(readOnly = true)
    public List<DestinoResponse> listar() {
        return destinoRepository.findAllByOrderByNomeAsc().stream()
                .map(destinoMapper::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DestinoResponse> listarAtivos() {
        return destinoRepository.findByAtivoTrueOrderByNomeAsc().stream()
                .map(destinoMapper::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DestinoResponse> listarPorPais(String pais) {
        return destinoRepository.findByPaisIgnoreCaseOrderByNomeAsc(pais).stream()
                .map(destinoMapper::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DestinoResponse> buscarPorTermo(String termo) {
        return destinoRepository.buscarPorTermo(termo).stream()
                .map(destinoMapper::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public DestinoResponse buscarPorId(Long id) {
        return destinoMapper.paraResposta(obterEntidade(id));
    }

    @Transactional(readOnly = true)
    public DestinoResponse buscarAtivoPorId(Long id) {
        Destino destino = obterEntidade(id);
        if (!destino.isAtivo()) {
            throw new RecursoNaoEncontradoException("Destino nao encontrado para o id " + id);
        }
        return destinoMapper.paraResposta(destino);
    }

    @Transactional
    public DestinoResponse criar(DestinoRequest requisicao) {
        if (destinoRepository.existsByNomeIgnoreCase(requisicao.nome().trim())) {
            throw new RegraNegocioException("Ja existe um destino cadastrado com o nome " + requisicao.nome());
        }
        Destino destino = destinoMapper.paraEntidade(requisicao);
        return destinoMapper.paraResposta(destinoRepository.save(destino));
    }

    @Transactional
    public DestinoResponse atualizar(Long id, DestinoRequest requisicao) {
        Destino destino = obterEntidade(id);
        destinoRepository.findByNomeIgnoreCase(requisicao.nome().trim())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new RegraNegocioException("Ja existe outro destino cadastrado com o nome " + requisicao.nome());
                });
        destinoMapper.aplicar(requisicao, destino);
        return destinoMapper.paraResposta(destinoRepository.save(destino));
    }

    @Transactional
    public void excluir(Long id) {
        Destino destino = obterEntidade(id);
        destinoRepository.delete(destino);
    }

    @Transactional(readOnly = true)
    public Destino obterEntidade(Long id) {
        return destinoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Destino nao encontrado para o id " + id));
    }
}
