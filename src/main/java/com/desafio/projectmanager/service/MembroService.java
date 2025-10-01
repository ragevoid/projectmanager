package com.desafio.projectmanager.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.desafio.projectmanager.dto.response.MembroMockadoDTO;
import com.desafio.projectmanager.handler.exceptions.NotFoundException;
import com.desafio.projectmanager.mapper.MembroMapper;
import com.desafio.projectmanager.model.membro.Membro;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembroService {

    private final RestTemplate restTemplate;
    private final MembroMapper membroMapper;

     @Value("${membro.api.url}")
    private String membroApiUrl;

    public Membro criarMembro(Membro membro) {
        MembroMockadoDTO dtoRequest = membroMapper.toDTO(membro);
        MembroMockadoDTO dtoResponse =
                restTemplate.postForObject(membroApiUrl, dtoRequest, MembroMockadoDTO.class);

        return membroMapper.toEntity(dtoResponse);
    }

    public Membro buscarMembroPorID(UUID id) {
        try {
            String url = membroApiUrl + "/" + id;
            MembroMockadoDTO membro = restTemplate.getForObject(url, MembroMockadoDTO.class);
            return membroMapper.toEntity(membro);

        } catch (Exception e) {
            throw new NotFoundException("Membro não encontrado");
        }
    }
    public List<Membro> ListarTodosMembros() {
        MembroMockadoDTO[] dtoArray =
                restTemplate.getForObject(membroApiUrl, MembroMockadoDTO[].class);

        if (dtoArray == null) return List.of();

        return Arrays.stream(dtoArray)
                .map(membroMapper::toEntity)
                .toList();
    }

}
