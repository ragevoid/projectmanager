package com.desafio.projectmanager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.desafio.projectmanager.model.empresa.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {

    
} 