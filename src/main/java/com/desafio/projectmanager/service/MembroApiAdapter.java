package com.desafio.projectmanager.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.desafio.projectmanager.dto.response.MembroExternoDTO;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MembroApiAdapter {

    private final RestTemplate restTemplate;

    @Value("${membro.api.url}")
    private String membroApiUrl;

    public Optional<MembroExternoDTO> buscarMembroPorId(UUID idExterno) {
        try {
            String url = membroApiUrl + "/" + idExterno;
            MembroExternoDTO membro = restTemplate.getForObject(url, MembroExternoDTO.class);
            return Optional.ofNullable(membro);
        } catch (Exception e) {
            throw new IllegalArgumentException("Membro não encontrado");
        }
    }

}
