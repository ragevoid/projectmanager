package com.desafio.projectmanager.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @Value("${credencial.api.user}")
    private String user;
    @Value("${credencial.api.password}")
    private String password; 

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String plainCredentials = user + ":" + password;
        String base64Credentials = Base64.getEncoder().encodeToString(plainCredentials.getBytes(StandardCharsets.UTF_8));
        headers.add("Authorization", "Basic " + base64Credentials);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public Membro criarMembro(Membro membro) {
        HttpHeaders headers = createAuthHeaders();
        MembroMockadoDTO dtoRequest = membroMapper.toDTO(membro);
        HttpEntity<MembroMockadoDTO> request = new HttpEntity<>(dtoRequest, headers);

        ResponseEntity<MembroMockadoDTO> response =
                restTemplate.exchange(membroApiUrl, HttpMethod.POST, request, MembroMockadoDTO.class);

        return membroMapper.toEntity(response.getBody());
    }

    public Membro buscarMembroPorID(UUID id) {
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        String url = membroApiUrl + "/" + id;

        try {
            ResponseEntity<MembroMockadoDTO> response = restTemplate.exchange(url, HttpMethod.GET, request, MembroMockadoDTO.class);
            return membroMapper.toEntity(response.getBody());
        } catch (Exception e) {
            throw new NotFoundException("Membro não encontrado: ", e);
        }
    }

    public List<Membro> ListarTodosMembros() {
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<MembroMockadoDTO[]> response =
                restTemplate.exchange(membroApiUrl, HttpMethod.GET, request, MembroMockadoDTO[].class);

        MembroMockadoDTO[] dtoArray = response.getBody();
        if (dtoArray == null) return List.of();

        return Arrays.stream(dtoArray)
                .map(membroMapper::toEntity)
                .toList();
    }
}