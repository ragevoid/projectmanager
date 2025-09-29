package com.desafio.projectmanager.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.desafio.projectmanager.model.membro.Membro;
import com.desafio.projectmanager.model.projeto.Projeto;

@Repository
public interface MembroRepository extends JpaRepository<Membro, UUID> {

    List<Membro> findAllByDeletedFalse();

}
