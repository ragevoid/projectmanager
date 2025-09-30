package com.desafio.projectmanager.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.desafio.projectmanager.dto.response.MembroExternoDTO;

@RestController
@RequestMapping("/mock-api/membros")
public class MembroController {

     private static final Map<UUID, MembroExternoDTO> membrosMock = new HashMap<>();

    @PostMapping
    public MembroExternoDTO criarMembro(@RequestBody MembroExternoDTO novoMembro) {
        UUID Id = UUID.randomUUID();
        novoMembro.setId(Id);
        membrosMock.put(Id, novoMembro);
        System.out.println("Membro criado" + novoMembro.getNome());
        return novoMembro;
    }

    @GetMapping("/{id}")
    public MembroExternoDTO getMembroPorId(@PathVariable UUID Id) {
        System.out.println("Buscando membro com ID" + Id);
        return membrosMock.get(Id);
    }
    
    @GetMapping
    public List<MembroExternoDTO> getAllMembros() {
        return new ArrayList<>(membrosMock.values());
    }

}
