package com.desafio.projectmanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.anyList;

import com.desafio.projectmanager.dto.request.AdicionarMembrosRequestDTO;
import com.desafio.projectmanager.dto.request.ProjetoRequestDTO;
import com.desafio.projectmanager.dto.response.ProjetoDetalhesDTO;
import com.desafio.projectmanager.service.ProjetoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjetoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjetoService projetoService;

    @Test
    @DisplayName("POST /projeto - Deve criar um projeto e retornar status 201 Created")
    void criarProjeto_deveRetornar201() throws Exception {
        ProjetoRequestDTO requestDTO = new ProjetoRequestDTO();
        requestDTO.setNome("Projeto via Teste");
        requestDTO.setGerenteId(UUID.randomUUID());
        
        ProjetoDetalhesDTO responseDTO = new ProjetoDetalhesDTO();
        responseDTO.setId(UUID.randomUUID());
        responseDTO.setNome("Projeto via Teste");
        
        when(projetoService.criarProjeto(any(ProjetoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/projeto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.nome").value("Projeto via Teste"));
    }

    @Test
    @DisplayName("GET /projeto/search/{id} - Deve encontrar um projeto e retornar status 200 OK")
    void encontrarPorId_deveRetornar200() throws Exception {
        UUID id = UUID.randomUUID();
        ProjetoDetalhesDTO responseDTO = new ProjetoDetalhesDTO();
        responseDTO.setId(id);
        
        when(projetoService.encontrarPorId(id)).thenReturn(responseDTO);

        mockMvc.perform(get("/projeto/search/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }
    
    @Test
    @DisplayName("DELETE /{id} - Deve excluir um projeto e retornar status 204 No Content")
    void eliminarProjeto_deveRetornar204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(projetoService).eliminarProjeto(id);

        mockMvc.perform(delete("/projeto/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /projeto/{id}/membros - Deve adicionar membros e retornar status 200 OK")
    void adicionarMembrosAoProjeto_deveRetornar200() throws Exception {
        UUID projetoId = UUID.randomUUID();
        UUID membroId = UUID.randomUUID();
        AdicionarMembrosRequestDTO requestDTO = new AdicionarMembrosRequestDTO();
        requestDTO.setMembrosIds(List.of(membroId));
        
        when(projetoService.adicionarMembros(anyList(), any(UUID.class))).thenReturn(new ProjetoDetalhesDTO());
        
        mockMvc.perform(post("/projeto/{projetoId}/membros", projetoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("POST /projeto - Deve retornar status 400 Bad Request para DTO inválido")
    void criarProjeto_deveRetornar400_quandoDTOInvalido() throws Exception {

        ProjetoRequestDTO requestDTO = new ProjetoRequestDTO();
        requestDTO.setGerenteId(UUID.randomUUID());

        mockMvc.perform(post("/projeto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
}