package com.desafio.projectmanager.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.desafio.projectmanager.model.membro.Membro;
import com.desafio.projectmanager.model.projeto.StatusProjeto;

@Repository
public interface MembroRepository extends JpaRepository<Membro, UUID> {

    List<Membro> findAll();

    Optional<Membro> findById(UUID id);

    @Query("SELECT COUNT(p) FROM Projeto p JOIN p.membros m WHERE m.id = :membroId AND p.status NOT IN :statusExcluidos")
    Long countProjetosAtivosPorMembro(@Param("membroId") UUID membroId,
            @Param("statusExcluidos") List<StatusProjeto> statusExcluidos);

    Optional<Membro> findByIdMockado(UUID idMockado);

}
