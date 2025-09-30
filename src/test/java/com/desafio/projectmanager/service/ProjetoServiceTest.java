package com.desafio.projectmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.desafio.projectmanager.dto.request.AtualizarProjetoDTO;
import com.desafio.projectmanager.dto.request.ProjetoRequestDTO;
import com.desafio.projectmanager.dto.response.ProjetoDetalhesDTO;
import com.desafio.projectmanager.handler.exceptions.BusinessException;
import com.desafio.projectmanager.mapper.ProjetoMapper;
import com.desafio.projectmanager.model.membro.Atribuicao;
import com.desafio.projectmanager.model.membro.Membro;
import com.desafio.projectmanager.model.projeto.Projeto;
import com.desafio.projectmanager.model.projeto.Risco;
import com.desafio.projectmanager.model.projeto.StatusProjeto;
import com.desafio.projectmanager.repository.ProjetoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private ProjetoMapper projetoMapper;

    @Mock
    private MembroService membroService;

    @InjectMocks
    private ProjetoService projetoService;

    private Projeto projetoPadrao;
    private Membro gerentePadrao;

    @BeforeEach
    void setUp() {
        gerentePadrao = new Membro();
        gerentePadrao.setId(UUID.randomUUID());
        gerentePadrao.setNome("Gerente Teste");
        gerentePadrao.setAtribuicao(Atribuicao.FUNCIONARIO);

        projetoPadrao = new Projeto();
        projetoPadrao.setId(UUID.randomUUID());
        projetoPadrao.setNome("Projeto Teste");
        projetoPadrao.setStatus(StatusProjeto.EM_ANALISE);
        projetoPadrao.setDeleted(false);
        projetoPadrao.setGerente(gerentePadrao);
        projetoPadrao.setMembros(new HashSet<>(Set.of(gerentePadrao)));
        projetoPadrao.setOrcamento(new BigDecimal("50000"));
        projetoPadrao.setDataInicio(LocalDate.now());
        projetoPadrao.setDataFinalPrevisao(LocalDate.now().plusMonths(1));
    }

    @Test
    @DisplayName("Deve criar projeto com sucesso")
    void criarProjeto_deveRetornarProjetoDetalhesDTO() {
        ProjetoRequestDTO requestDTO = new ProjetoRequestDTO();
        requestDTO.setGerenteId(gerentePadrao.getId());

        when(membroService.buscarMembroAPIPorId(any(UUID.class))).thenReturn(gerentePadrao);
        when(projetoMapper.toEntity(any(ProjetoRequestDTO.class))).thenReturn(projetoPadrao);
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projetoPadrao);
        when(projetoMapper.toDetalhesDTO(any(Projeto.class), any(Risco.class))).thenReturn(new ProjetoDetalhesDTO());

        projetoService.criarProjeto(requestDTO);

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository).save(projetoCaptor.capture());
        Projeto projetoSalvo = projetoCaptor.getValue();

        assertNotNull(projetoSalvo.getGerente());
        assertEquals("Gerente Teste", projetoSalvo.getGerente().getNome());
        assertTrue(projetoSalvo.getMembros().contains(gerentePadrao));
    }

    @Test
    @DisplayName("Deve encontrar projeto por ID e retornar DTO de detalhes")
    void encontrarPorId_deveRetornarDTO_quandoEncontrado() {
        when(projetoRepository.findByIdAndDeletedFalse(projetoPadrao.getId())).thenReturn(Optional.of(projetoPadrao));
        when(projetoMapper.toDetalhesDTO(any(Projeto.class), any(Risco.class))).thenReturn(new ProjetoDetalhesDTO());

        ProjetoDetalhesDTO resultado = projetoService.encontrarPorId(projetoPadrao.getId());

        assertNotNull(resultado);
        verify(projetoRepository).findByIdAndDeletedFalse(projetoPadrao.getId());
        verify(projetoMapper).toDetalhesDTO(projetoPadrao, Risco.BAIXO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar por ID inexistente")
    void encontrarPorId_deveLancarExcecao_quandoNaoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        when(projetoRepository.findByIdAndDeletedFalse(idInexistente)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            projetoService.encontrarPorId(idInexistente);
        });
    }

    @Test
    @DisplayName("Deve excluir projeto (soft delete) com status permitido")
    void eliminarProjeto_deveMarcarComoExcluido_quandoStatusPermitido() {

        when(projetoRepository.findByIdAndDeletedFalse(projetoPadrao.getId())).thenReturn(Optional.of(projetoPadrao));

        projetoService.eliminarProjeto(projetoPadrao.getId());

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository).save(projetoCaptor.capture());
        assertTrue(projetoCaptor.getValue().getDeleted());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar excluir projeto com status INICIADO")
    void eliminarProjeto_deveLancarExcecao_quandoStatusNaoPermitido() {

        projetoPadrao.setStatus(StatusProjeto.INICIADO);
        when(projetoRepository.findByIdAndDeletedFalse(projetoPadrao.getId())).thenReturn(Optional.of(projetoPadrao));

        assertThrows(BusinessException.class, () -> {
            projetoService.eliminarProjeto(projetoPadrao.getId());
        });
        verify(projetoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar campos do projeto com sucesso")
    void atualizarProjeto_deveAtualizarCamposCorretamente() {

        AtualizarProjetoDTO dto = new AtualizarProjetoDTO();
        dto.setNome("Novo Nome do Projeto");
        dto.setOrcamento(new BigDecimal("99999"));

        when(projetoRepository.findByIdAndDeletedFalse(projetoPadrao.getId())).thenReturn(Optional.of(projetoPadrao));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projetoPadrao);
        when(projetoMapper.toDetalhesDTO(any(Projeto.class), any(Risco.class))).thenReturn(new ProjetoDetalhesDTO());

        projetoService.atualizarProjeto(projetoPadrao.getId(), dto);

        ArgumentCaptor<Projeto> captor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository).save(captor.capture());

        assertEquals("Novo Nome do Projeto", captor.getValue().getNome());
        assertEquals(new BigDecimal("99999"), captor.getValue().getOrcamento());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar atualizar projeto ENCERRADO")
    void atualizarProjeto_deveLancarExcecao_quandoProjetoEncerrado() {

        projetoPadrao.setStatus(StatusProjeto.ENCERRADO);
        when(projetoRepository.findByIdAndDeletedFalse(projetoPadrao.getId())).thenReturn(Optional.of(projetoPadrao));

        assertThrows(BusinessException.class, () -> {
            projetoService.atualizarProjeto(projetoPadrao.getId(), new AtualizarProjetoDTO());
        });
    }

    @Test
    @DisplayName("Deve adicionar membros a um projeto com sucesso")
    void adicionarMembros_deveAdicionarMembros() {

        Membro novoMembro = new Membro();
        novoMembro.setId(UUID.randomUUID());
        novoMembro.setNome("Novo Membro");
        novoMembro.setAtribuicao(Atribuicao.FUNCIONARIO);

        when(projetoRepository.findByIdAndDeletedFalse(projetoPadrao.getId())).thenReturn(Optional.of(projetoPadrao));
        when(membroService.buscarOuCriarMembroLocal(novoMembro.getId())).thenReturn(novoMembro);

        doNothing().when(membroService).validarLimiteDeProjetosAtivos(novoMembro);
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projetoPadrao);
        when(projetoMapper.toDetalhesDTO(any(Projeto.class), any(Risco.class))).thenReturn(new ProjetoDetalhesDTO());

        projetoService.adicionarMembros(List.of(novoMembro.getId()), projetoPadrao.getId());

        ArgumentCaptor<Projeto> captor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository).save(captor.capture());
        assertTrue(captor.getValue().getMembros().contains(novoMembro));
        assertEquals(2, captor.getValue().getMembros().size());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao adicionar membro que não é FUNCIONARIO")
    void adicionarMembros_deveLancarExcecao_quandoMembroNaoEFuncionario() {
        Membro novoMembro = new Membro();
        novoMembro.setId(UUID.randomUUID());
        novoMembro.setAtribuicao(Atribuicao.STAKEHOLDER);

        when(projetoRepository.findByIdAndDeletedFalse(projetoPadrao.getId())).thenReturn(Optional.of(projetoPadrao));
        when(membroService.buscarOuCriarMembroLocal(novoMembro.getId())).thenReturn(novoMembro);

        assertThrows(BusinessException.class, () -> {
            projetoService.adicionarMembros(List.of(novoMembro.getId()), projetoPadrao.getId());
        });
    }
}