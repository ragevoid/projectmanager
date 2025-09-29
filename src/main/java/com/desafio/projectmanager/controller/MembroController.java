package com.desafio.projectmanager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.desafio.projectmanager.dto.response.MembroResponseDTO;
import com.desafio.projectmanager.mapper.MembroMapper;
import com.desafio.projectmanager.model.membro.Membro;
import com.desafio.projectmanager.service.MembroService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/membro")
@RequiredArgsConstructor
public class MembroController {

    private final MembroService membroService;
    private final MembroMapper mapper;

    @GetMapping()
    public ResponseEntity<List<MembroResponseDTO>> getAllOrders() {
    List<Membro> membroList = membroService.listarMembros();
    return ResponseEntity.ok(mapper.toDtoList(membroList));
    }

}
