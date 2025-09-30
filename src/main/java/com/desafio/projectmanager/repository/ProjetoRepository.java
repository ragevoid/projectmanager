package com.desafio.projectmanager.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.desafio.projectmanager.model.projeto.Projeto;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, UUID>, JpaSpecificationExecutor<Projeto> {

    List<Projeto> findAllByDeletedFalse();

    Optional<Projeto> findByIdAndDeletedFalse(UUID Id);

}
