package com.desafio.projectmanager.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.desafio.projectmanager.model.membro.Membro;
import com.desafio.projectmanager.model.projeto.Projeto;
import com.desafio.projectmanager.repository.MembroRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembroService {
    private final MembroRepository membroRepository;
    private final ProjetoService projetoService;

    public List<Membro> listarMembros() {
        return membroRepository.findAll().stream()
                .collect(Collectors.toList());
    }

    public Membro salvarMembro(Membro membro) {
        return membroRepository.save(membro);
    }

}
