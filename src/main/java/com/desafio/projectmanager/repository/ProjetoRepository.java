package com.desafio.projectmanager.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.desafio.projectmanager.model.projeto.Projeto;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, UUID> {

    List<Projeto> findAllByDeletedFalse();

    Optional<Projeto> findByIdAndDeletedFalse(UUID Id);

    // @Query("SELECT p FROM Projeto p WHERE p.membros.id = :membroId AND p.status <> 6 AND p.status <> 7")
    // List<Projeto> findAllByMembroId(UUID membroId);

}
