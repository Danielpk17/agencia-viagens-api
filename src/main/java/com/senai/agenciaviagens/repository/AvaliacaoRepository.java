package com.senai.agenciaviagens.repository;

import com.senai.agenciaviagens.domain.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByDestinoIdOrderByCriadoEmDesc(Long destinoId);

    boolean existsByDestinoIdAndUsuarioId(Long destinoId, Long usuarioId);

    long countByDestinoId(Long destinoId);
}
