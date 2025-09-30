package com.desafio.projectmanager.service;


import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.desafio.projectmanager.mapper.MembroMapper;
import com.desafio.projectmanager.model.membro.Membro;
import com.desafio.projectmanager.repository.MembroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembroService {

    private final MembroRepository membroRepository;
    private final MembroApiAdapter membroApiAdapter; 
    private final MembroMapper membroMapper;

    public Membro  encontrarPorId(UUID id){
        return membroRepository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new IllegalArgumentException("Membro não encontrado com ID: " + id)); 
    }

     public List<Membro> encontrarTodosPorId(Set<UUID>  ids){
        return membroRepository.findAllById(ids);
    }

}