package com.desafio.projectmanager.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.desafio.projectmanager.model.projeto.Projeto;
import com.desafio.projectmanager.model.projeto.StatusProjeto;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, UUID>, JpaSpecificationExecutor<Projeto> {

    List<Projeto> findAllByDeletedFalse();

    Optional<Projeto> findByIdAndDeletedFalse(UUID Id);

    @Query("SELECT p FROM Projeto p WHERE :membroId MEMBER OF p.membrosIds AND p.status NOT IN ('CANCELADO', 'ENCERRADO') AND p.deleted = false")
    List<Projeto> findProjetosAtivosPorMembro(UUID membroId);

    @Query("SELECT p FROM Projeto p WHERE p.status = :status AND p.deleted = false")
    List<Projeto> findByStatus(@Param("status") StatusProjeto status);

    @Query("SELECT p.membrosIds FROM Projeto p WHERE p.deleted = false")
    List<Set<UUID>> findAllMembrosIds();


}
