package com.desafio.projectmanager.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.desafio.projectmanager.dto.response.RelatorioProjetosDTO;
import com.desafio.projectmanager.dto.response.RelatorioStatusDTO;
import com.desafio.projectmanager.model.projeto.Projeto;
import com.desafio.projectmanager.model.projeto.StatusProjeto;
import com.desafio.projectmanager.repository.ProjetoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para PorjetoRelatorioService")
class ProjetoRelatorioServiceTest {

        @Mock
        private ProjetoRepository projetoRepository;

        @InjectMocks
        private ProjetoRelatorioService projetoRelatorioService;

        @Test
        @DisplayName("gerarRelatorio deveria retornar DTO completo quando existem projetos em vários status")
        void gerarRelatorio_deveriaRetornarRelatorioCompleto_quandoExistemProjetosEmVariosStatus() {

                LocalDate dataInicio1 = LocalDate.of(2024, 1, 1);
                LocalDate dataFinal1 = LocalDate.of(2024, 1, 11);
                Projeto projetoEncerrado1 = new Projeto();
                projetoEncerrado1.setStatus(StatusProjeto.ENCERRADO);
                projetoEncerrado1.setDataInicio(dataInicio1);
                projetoEncerrado1.setDataFinalReal(dataFinal1);
                projetoEncerrado1.setOrcamento(new BigDecimal("50000.00"));

                LocalDate dataInicio2 = LocalDate.of(2024, 2, 1);
                LocalDate dataFinal2 = LocalDate.of(2024, 2, 21);
                Projeto projetoEncerrado2 = new Projeto();
                projetoEncerrado2.setStatus(StatusProjeto.ENCERRADO);
                projetoEncerrado2.setDataInicio(dataInicio2);
                projetoEncerrado2.setDataFinalReal(dataFinal2);
                projetoEncerrado2.setOrcamento(new BigDecimal("100000.00"));

                Projeto projetoEmAnalise = new Projeto();
                projetoEmAnalise.setStatus(StatusProjeto.EM_ANALISE);
                projetoEmAnalise.setOrcamento(new BigDecimal("25000.00"));

                UUID membro1 = UUID.randomUUID();
                UUID membro2 = UUID.randomUUID();
                UUID membro3 = UUID.randomUUID();

                projetoEncerrado1.setMembrosIds(Set.of(membro1, membro2));
                projetoEmAnalise.setMembrosIds(Set.of(membro1, membro3));

                when(projetoRepository.findByStatus(any(StatusProjeto.class))).thenReturn(Collections.emptyList());
                when(projetoRepository.findByStatus(StatusProjeto.ENCERRADO))
                                .thenReturn(Arrays.asList(projetoEncerrado1, projetoEncerrado2));
                when(projetoRepository.findByStatus(StatusProjeto.EM_ANALISE))
                                .thenReturn(Collections.singletonList(projetoEmAnalise));
                when(projetoRepository.findAllMembrosIds())
                                .thenReturn(Arrays.asList(Set.of(membro1, membro2), Set.of(membro2, membro3)));

                RelatorioProjetosDTO resultado = projetoRelatorioService.gerarRelatorio();

                assertNotNull(resultado);
                assertEquals(3, resultado.getTotalMembrosUnicos());
                assertEquals(15.0, resultado.getMediaDuracaoProjetosEncerrados());
                assertEquals(LocalDate.now(), resultado.getDataGeracao());

                Map<StatusProjeto, RelatorioStatusDTO> relatorioPorStatus = resultado.getDadosPorStatus().stream()
                                .collect(Collectors.toMap(RelatorioStatusDTO::getStatus, dto -> dto));

                assertEquals(2, relatorioPorStatus.get(StatusProjeto.ENCERRADO).getQuantidadeProjetos());
                assertEquals(new BigDecimal("150000.00"),
                                relatorioPorStatus.get(StatusProjeto.ENCERRADO).getTotalOrcado());

                assertEquals(1, relatorioPorStatus.get(StatusProjeto.EM_ANALISE).getQuantidadeProjetos());
                assertEquals(new BigDecimal("25000.00"),
                                relatorioPorStatus.get(StatusProjeto.EM_ANALISE).getTotalOrcado());

                assertEquals(0, relatorioPorStatus.get(StatusProjeto.ANALISE_REALIZADA).getQuantidadeProjetos());
                assertEquals(BigDecimal.ZERO, relatorioPorStatus.get(StatusProjeto.ANALISE_REALIZADA).getTotalOrcado());
        }

        @Test
        @DisplayName("gerarRelatorio deveria retornar DTO com valores zerados quando não existem projetos")
        void gerarRelatorio_deveriaRetornarRelatorioComValoresZerados_quandoNaoExistemProjetos() {
                when(projetoRepository.findByStatus(any(StatusProjeto.class))).thenReturn(Collections.emptyList());
                when(projetoRepository.findAllMembrosIds()).thenReturn(Collections.emptyList());

                RelatorioProjetosDTO resultado = projetoRelatorioService.gerarRelatorio();

                assertNotNull(resultado);
                assertEquals(0, resultado.getTotalMembrosUnicos());
                assertEquals(0.0, resultado.getMediaDuracaoProjetosEncerrados());
                assertEquals(LocalDate.now(), resultado.getDataGeracao());

                resultado.getDadosPorStatus().forEach(relatorioStatus -> {
                        assertEquals(0, relatorioStatus.getQuantidadeProjetos());
                        assertEquals(BigDecimal.ZERO, relatorioStatus.getTotalOrcado());
                });
        }

        @Test
        @DisplayName("gerarRelatorio deveria retornar média de duração zero quando projetos encerrados não possuem datas")
        void gerarRelatorio_deveriaRetornarMediaDuracaoZero_quandoProjetosEncerradosNaoPossuemDatas() {
                Projeto projetoEncerradoSemDatas = new Projeto();
                projetoEncerradoSemDatas.setStatus(StatusProjeto.ENCERRADO);
                projetoEncerradoSemDatas.setOrcamento(new BigDecimal("10000.00"));

                Projeto projetoEncerradoSoComInicio = new Projeto();
                projetoEncerradoSoComInicio = projetoEncerradoSemDatas;
                projetoEncerradoSoComInicio.setDataInicio(LocalDate.now());

                when(projetoRepository.findByStatus(any(StatusProjeto.class)))
                                .thenReturn(Collections.emptyList());
                when(projetoRepository.findAllMembrosIds())
                                .thenReturn(Collections.emptyList());

                RelatorioProjetosDTO resultado = projetoRelatorioService.gerarRelatorio();

                assertNotNull(resultado);
                assertEquals(0.0, resultado.getMediaDuracaoProjetosEncerrados());
                assertEquals(0, resultado.getTotalMembrosUnicos());
        }

        @Test
        @DisplayName("gerarRelatorio deveria calcular corretamente total de membros unicos quando há sobreposição")
        void gerarRelatorio_deveriaCalcularCorretamenteTotalMembrosUnicos_quandoHaSobreposicao() {
                UUID membro1 = UUID.randomUUID();
                UUID membro2 = UUID.randomUUID();
                UUID membro3 = UUID.randomUUID();

                Set<UUID> membrosProjeto1 = Set.of(membro1, membro2);
                Set<UUID> membrosProjeto2 = Set.of(membro2, membro3);
                Set<UUID> membrosProjeto3 = Set.of(membro1, membro3);

                when(projetoRepository.findByStatus(any(StatusProjeto.class))).thenReturn(Collections.emptyList());
                when(projetoRepository.findAllMembrosIds())
                                .thenReturn(Arrays.asList(membrosProjeto1, membrosProjeto2, membrosProjeto3));

                RelatorioProjetosDTO resultado = projetoRelatorioService.gerarRelatorio();

                assertNotNull(resultado);
                assertEquals(3, resultado.getTotalMembrosUnicos());
        }
}