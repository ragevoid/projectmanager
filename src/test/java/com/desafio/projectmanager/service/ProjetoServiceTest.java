package com.desafio.projectmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.desafio.projectmanager.dto.request.ProjetoRequestDTO;
import com.desafio.projectmanager.dto.response.ProjetoDetalhesDTO;
import com.desafio.projectmanager.dto.response.ProjetoResumoDTO;
import com.desafio.projectmanager.handler.exceptions.BusinessException;
import com.desafio.projectmanager.mapper.ProjetoMapper;
import com.desafio.projectmanager.model.projeto.Projeto;
import com.desafio.projectmanager.model.projeto.Risco;
import com.desafio.projectmanager.model.projeto.StatusProjeto;
import com.desafio.projectmanager.repository.ProjetoRepository;

@ExtendWith(MockitoExtension.class)
class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private ProjetoMapper projetoMapper;

    @InjectMocks
    private ProjetoService projetoService;

    private Projeto criarProjetoPadrao() {
        Projeto projeto = new Projeto();
        projeto.setId(UUID.randomUUID());
        projeto.setNome("Projeto de Teste");
        projeto.setDataInicio(LocalDate.now());
        projeto.setDataFinalPrevisao(LocalDate.now().plusMonths(2));
        projeto.setOrcamento(new BigDecimal("50000.00"));
        projeto.setStatus(StatusProjeto.EM_ANALISE);
        projeto.setDeleted(false);
        return projeto;
    }

    @Test
    void listarProjetos_deveRetornarListaDeResumoDTOs() {
        Projeto projeto = criarProjetoPadrao();
        ProjetoResumoDTO resumoDTO = new ProjetoResumoDTO();
        resumoDTO.setId(projeto.getId());
        resumoDTO.setNome(projeto.getNome());
        resumoDTO.setStatus(projeto.getStatus());
        resumoDTO.setClassificacaoRisco(Risco.BAIXO);

        when(projetoRepository.findAllByDeletedFalse()).thenReturn(List.of(projeto));
        when(projetoMapper.toResumoDTO(projeto, Risco.BAIXO)).thenReturn(resumoDTO);

        List<ProjetoResumoDTO> resultado = projetoService.listarProjetos();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Projeto de Teste", resultado.get(0).getNome());
        assertEquals(Risco.BAIXO, resultado.get(0).getClassificacaoRisco());

        verify(projetoRepository, times(1)).findAllByDeletedFalse();
        verify(projetoMapper, times(1)).toResumoDTO(any(Projeto.class), any(Risco.class));
    }

    @Test
    void encontrarPorId_deveRetornarDetalhesDTO_quandoProjetoExiste() {

        UUID id = UUID.randomUUID();
        Projeto projeto = criarProjetoPadrao();
        ProjetoDetalhesDTO detalhesDTO = new ProjetoDetalhesDTO();
        detalhesDTO.setId(id);
        detalhesDTO.setNome("Projeto Detalhado");
        detalhesDTO.setClassificacaoRisco(Risco.BAIXO);

        when(projetoRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(projeto));
        when(projetoMapper.toDetalhesDTO(projeto, Risco.BAIXO)).thenReturn(detalhesDTO);

        ProjetoDetalhesDTO resultado = projetoService.encontrarPorId(id);

        assertNotNull(resultado);
        assertEquals("Projeto Detalhado", resultado.getNome());
        assertEquals(Risco.BAIXO, resultado.getClassificacaoRisco());

        verify(projetoRepository, times(1)).findByIdAndDeletedFalse(id);
    }

    @Test
    void encontrarPorId_deveLancarExcecao_quandoProjetoNaoExiste() {

        UUID id = UUID.randomUUID();
        when(projetoRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            projetoService.encontrarPorId(id);
        });

        verify(projetoRepository, times(1)).findByIdAndDeletedFalse(id);
    }

    @Test
    void salvarProjeto_deveRetornarRiscoBaixo() {
        ProjetoRequestDTO requestDTO = new ProjetoRequestDTO();
        requestDTO.setDataInicio(LocalDate.now());
        requestDTO.setDataFinalPrevisao(LocalDate.now().plusMonths(2));
        requestDTO.setOrcamento(new BigDecimal("90000.00"));
        Projeto projetoMapeado = new Projeto();
        projetoMapeado.setDataInicio(requestDTO.getDataInicio());
        projetoMapeado.setDataFinalPrevisao(requestDTO.getDataFinalPrevisao());
        projetoMapeado.setOrcamento(requestDTO.getOrcamento());

        when(projetoMapper.toEntity(requestDTO)).thenReturn(projetoMapeado);
        when(projetoRepository.save(projetoMapeado)).thenReturn(projetoMapeado);
        when(projetoMapper.toDetalhesDTO(any(Projeto.class), eq(Risco.BAIXO))).thenReturn(new ProjetoDetalhesDTO());

        projetoService.salvarProjeto(requestDTO);

        verify(projetoMapper, times(1)).toDetalhesDTO(any(Projeto.class), eq(Risco.BAIXO));
    }

    @Test
    void salvarProjeto_deveRetornarRiscoMedio() {
        ProjetoRequestDTO requestDTO = new ProjetoRequestDTO();
        requestDTO.setDataInicio(LocalDate.now());
        requestDTO.setDataFinalPrevisao(LocalDate.now().plusMonths(5));
        requestDTO.setOrcamento(new BigDecimal("250000.00"));
        Projeto projetoMapeado = new Projeto();
        projetoMapeado.setDataInicio(requestDTO.getDataInicio());
        projetoMapeado.setDataFinalPrevisao(requestDTO.getDataFinalPrevisao());
        projetoMapeado.setOrcamento(requestDTO.getOrcamento());

        when(projetoMapper.toEntity(requestDTO)).thenReturn(projetoMapeado);
        when(projetoRepository.save(projetoMapeado)).thenReturn(projetoMapeado);
        when(projetoMapper.toDetalhesDTO(any(Projeto.class), eq(Risco.MEDIO))).thenReturn(new ProjetoDetalhesDTO());

        projetoService.salvarProjeto(requestDTO);

        verify(projetoMapper, times(1)).toDetalhesDTO(any(Projeto.class), eq(Risco.MEDIO));
    }

    @Test
    void salvarProjeto_deveRetornarRiscoAlto_porOrcamento() {

        ProjetoRequestDTO requestDTO = new ProjetoRequestDTO();
        requestDTO.setDataInicio(LocalDate.now());
        requestDTO.setDataFinalPrevisao(LocalDate.now().plusMonths(2));
        requestDTO.setOrcamento(new BigDecimal("600000.00"));

        Projeto projetoMapeado = new Projeto();
        projetoMapeado.setDataInicio(requestDTO.getDataInicio());
        projetoMapeado.setDataFinalPrevisao(requestDTO.getDataFinalPrevisao());
        projetoMapeado.setOrcamento(requestDTO.getOrcamento());

        when(projetoMapper.toEntity(requestDTO)).thenReturn(projetoMapeado);
        when(projetoRepository.save(projetoMapeado)).thenReturn(projetoMapeado);
        when(projetoMapper.toDetalhesDTO(any(Projeto.class), eq(Risco.ALTO))).thenReturn(new ProjetoDetalhesDTO());

        projetoService.salvarProjeto(requestDTO);

        verify(projetoMapper, times(1)).toDetalhesDTO(any(Projeto.class), eq(Risco.ALTO));
    }

    @Test
    void eliminarProjeto_deveMarcarComoDeleted_quandoStatusPermite() {

        UUID id = UUID.randomUUID();
        Projeto projeto = criarProjetoPadrao();
        projeto.setStatus(StatusProjeto.EM_ANALISE);

        when(projetoRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(projeto));

        projetoService.eliminarProjeto(id);

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository, times(1)).save(projetoCaptor.capture());

        Projeto projetoSalvo = projetoCaptor.getValue();
        assertTrue(projetoSalvo.getDeleted());
    }

    @Test
    void eliminarProjeto_deveLancarExcecao_quandoStatusNaoPermite() {
        UUID id = UUID.randomUUID();
        Projeto projeto = criarProjetoPadrao();
        projeto.setStatus(StatusProjeto.INICIADO);

        when(projetoRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(projeto));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            projetoService.eliminarProjeto(id);
        });

        assertEquals("Projeto com status 'INICIADO' não pode ser excluído.", exception.getMessage());

        verify(projetoRepository, never()).save(any(Projeto.class));
    }
}