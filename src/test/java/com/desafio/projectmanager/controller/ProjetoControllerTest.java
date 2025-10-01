package com.desafio.projectmanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import com.desafio.projectmanager.dto.request.AdicionarMembrosRequestDTO;
import com.desafio.projectmanager.dto.request.AtualizarProjetoDTO;
import com.desafio.projectmanager.dto.request.ProjetoFiltroDTO;
import com.desafio.projectmanager.dto.request.ProjetoRequestDTO;
import com.desafio.projectmanager.dto.response.ProjetoDetalhesDTO;
import com.desafio.projectmanager.handler.exceptions.BusinessException;
import com.desafio.projectmanager.handler.exceptions.NotFoundException;
import com.desafio.projectmanager.model.projeto.StatusProjeto;
import com.desafio.projectmanager.service.ProjetoService;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes para ProjetoController")
class ProjetoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjetoService projetoService;

    private UUID projetoId;
    private ProjetoRequestDTO projetoRequestDTO;
    private ProjetoDetalhesDTO projetoDetalhesDTO;
    private AtualizarProjetoDTO atualizarProjetoDTO;

    @BeforeEach
    void setUp() {
        projetoId = UUID.randomUUID();

        projetoRequestDTO = new ProjetoRequestDTO();
        projetoRequestDTO.setNome("Novo Projeto via Controller");
        
        projetoDetalhesDTO = new ProjetoDetalhesDTO();
        projetoDetalhesDTO.setId(projetoId);
        projetoDetalhesDTO.setNome("Projeto Detalhes DTO");
        
        atualizarProjetoDTO = new AtualizarProjetoDTO();
        atualizarProjetoDTO.setNome("Projeto com nome atualizado");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("criarProjeto deveria retornar Status 200 e o DTO do projeto quando dados forem válidos")
    void criarProjeto_deveriaRetornarStatus200EOProjetoCriado_quandoDadosForemValidos() throws Exception {
        when(projetoService.criarProjeto(any(ProjetoRequestDTO.class))).thenReturn(projetoDetalhesDTO);

        mockMvc.perform(post("/projeto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projetoRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(projetoDetalhesDTO.getId().toString())))
                .andExpect(jsonPath("$.nome", is(projetoDetalhesDTO.getNome())));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("listarProjetosPorFiltro deveria retornar Status 200 e uma página de projetos")
    void listarProjetosPorFiltro_deveriaRetornarStatus200EUmaPaginaDeProjetos_quandoFiltrosValidos() throws Exception {
        Page<ProjetoDetalhesDTO> paginaDeProjetos = new PageImpl<>(List.of(new ProjetoDetalhesDTO()));
        when(projetoService.listarProjetosPorFiltro(any(ProjetoFiltroDTO.class), any(Pageable.class))).thenReturn(paginaDeProjetos);

        mockMvc.perform(post("/projeto/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ProjetoFiltroDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements", is(1)));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("eliminarProjeto deveria retornar Status 204 quando exclusão for bem-sucedida")
    void eliminarProjeto_deveriaRetornarStatus204_quandoExclusaoForBemSucedida() throws Exception {
        doNothing().when(projetoService).eliminarProjeto(projetoId);
        
        mockMvc.perform(delete("/projeto/{id}", projetoId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("eliminarProjeto deveria retornar Status 400 quando exclusão não for permitida")
    void eliminarProjeto_deveriaRetornarStatus400_quandoExclusaoNaoForPermitida() throws Exception {
        doThrow(new BusinessException("Exclusão não permitida")).when(projetoService).eliminarProjeto(projetoId);

        mockMvc.perform(delete("/projeto/{id}", projetoId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("alterarStatus deveria retornar Status 200 e o projeto atualizado quando transição for válida")
    void alterarStatus_deveriaRetornarStatus200EOProjetoAtualizado_quandoTransicaoDeStatusForValida() throws Exception {
        StatusProjeto novoStatus = StatusProjeto.EM_ANDAMENTO;
        when(projetoService.alterarStatus(projetoId, novoStatus)).thenReturn(projetoDetalhesDTO);

        mockMvc.perform(patch("/projeto/{id}/status/{novoStatus}", projetoId, novoStatus))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(projetoId.toString())));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("alterarStatus deveria retornar Status 400 quando transição de status for inválida")
    void alterarStatus_deveriaRetornarStatus400_quandoTransicaoDeStatusForInvalida() throws Exception {
        StatusProjeto novoStatus = StatusProjeto.CANCELADO;
        when(projetoService.alterarStatus(projetoId, novoStatus)).thenThrow(new BusinessException("Transição inválida"));

        mockMvc.perform(patch("/projeto/{id}/status/{novoStatus}", projetoId, novoStatus))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("adicionarMembrosAoProjeto deveria retornar Status 200 e o projeto atualizado")
    void adicionarMembrosAoProjeto_deveriaRetornarStatus200EOProjetoAtualizado_quandoAdicaoForBemSucedida() throws Exception {
        AdicionarMembrosRequestDTO requestDTO = new AdicionarMembrosRequestDTO();
        when(projetoService.adicionarMembros(any(AdicionarMembrosRequestDTO.class), any(UUID.class))).thenReturn(projetoDetalhesDTO);

        mockMvc.perform(post("/projeto/{projetoId}/membros", projetoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(projetoId.toString())));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("adicionarMembrosAoProjeto deveria retornar Status 400 quando regra de negócio for violada")
    void adicionarMembrosAoProjeto_deveriaRetornarStatus400_quandoRegraDeNegocioForViolada() throws Exception {
        AdicionarMembrosRequestDTO requestDTO = new AdicionarMembrosRequestDTO();
        when(projetoService.adicionarMembros(any(AdicionarMembrosRequestDTO.class), any(UUID.class)))
                .thenThrow(new BusinessException("Membro inválido"));

        mockMvc.perform(post("/projeto/{projetoId}/membros", projetoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("atualizarProjeto deveria retornar Status 200 e o projeto atualizado quando dados forem válidos")
    void atualizarProjeto_deveriaRetornarStatus200EOProjetoAtualizado_quandoDadosForemValidos() throws Exception {
        when(projetoService.atualizarProjeto(any(UUID.class), any(AtualizarProjetoDTO.class))).thenReturn(projetoDetalhesDTO);
        
        mockMvc.perform(patch("/projeto/{id}", projetoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizarProjetoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(projetoId.toString())));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("atualizarProjeto deveria retornar Status 404 quando projeto não for encontrado")
    void atualizarProjeto_deveriaRetornarStatus404_quandoProjetoNaoEncontrado() throws Exception {
        when(projetoService.atualizarProjeto(any(UUID.class), any(AtualizarProjetoDTO.class)))
                .thenThrow(new NotFoundException("Projeto não encontrado"));
        
        mockMvc.perform(patch("/projeto/{id}", projetoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizarProjetoDTO)))
                .andExpect(status().isNotFound());
    }
}