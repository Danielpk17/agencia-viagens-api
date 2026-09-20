package com.senai.agenciaviagens.repository;

import com.senai.agenciaviagens.domain.Destino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DestinoRepository extends JpaRepository<Destino, Long> {

    Optional<Destino> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);

    List<Destino> findAllByOrderByNomeAsc();

    List<Destino> findByAtivoTrueOrderByNomeAsc();

    List<Destino> findByPaisIgnoreCaseOrderByNomeAsc(String pais);

    @Query("select d from Destino d where lower(d.nome) like lower(concat('%', :termo, '%')) "
            + "or lower(d.cidade) like lower(concat('%', :termo, '%')) "
            + "or lower(d.pais) like lower(concat('%', :termo, '%')) order by d.nome asc")
    List<Destino> buscarPorTermo(@Param("termo") String termo);
}
