package com.desafio.projectmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.desafio.projectmanager.dto.request.AdicionarMembrosRequestDTO;
import com.desafio.projectmanager.dto.request.AtualizarProjetoDTO;
import com.desafio.projectmanager.dto.request.ProjetoFiltroDTO;
import com.desafio.projectmanager.dto.request.ProjetoRequestDTO;
import com.desafio.projectmanager.dto.response.ProjetoDetalhesDTO;
import com.desafio.projectmanager.dto.response.ProjetoResumoDTO;
import com.desafio.projectmanager.handler.exceptions.BusinessException;
import com.desafio.projectmanager.handler.exceptions.NotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ProjetoService")
class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private ProjetoMapper projetoMapper;

    @Mock
    private MembroService membroService;

    @InjectMocks
    private ProjetoService projetoService;

    private UUID projetoId;
    private UUID gerenteId;
    private Projeto projeto;
    private ProjetoRequestDTO projetoRequestDTO;
    private ProjetoDetalhesDTO projetoDetalhesDTO;
    private Membro gerente;

    @BeforeEach
    void setUp() {
        projetoId = UUID.randomUUID();
        gerenteId = UUID.randomUUID();

        gerente = new Membro();
        gerente.setId(gerenteId);
        gerente.setNome("Gerente Teste");
        gerente.setAtribuicao(Atribuicao.FUNCIONARIO);

        projeto = new Projeto();
        projeto.setId(projetoId);
        projeto.setNome("Projeto Teste");
        projeto.setGerenteId(gerenteId);
        projeto.setStatus(StatusProjeto.EM_ANALISE);
        projeto.setDataInicio(LocalDate.now());
        projeto.setDataFinalPrevisao(LocalDate.now().plusMonths(2));
        projeto.setOrcamento(new BigDecimal("50000.00"));
        projeto.setMembrosIds(new HashSet<>(Set.of(gerenteId)));

        projetoRequestDTO = new ProjetoRequestDTO();
        projetoRequestDTO.setNome("Novo Projeto");
        projetoRequestDTO.setGerenteId(gerenteId);
        projetoRequestDTO.setDataInicio(LocalDate.now());
        projetoRequestDTO.setDataFinalPrevisao(LocalDate.now().plusMonths(5));
        projetoRequestDTO.setOrcamento(new BigDecimal("200000.00"));

        projetoDetalhesDTO = new ProjetoDetalhesDTO();
        projetoDetalhesDTO.setId(projetoId);
        projetoDetalhesDTO.setNome("Projeto Detalhes");
    }

    @Test
    @DisplayName("criarProjeto deveria salvar e retornar o projeto com risco MEDIO")
    void criarProjeto_deveriaCriarEretornarProjeto_quandoDadosForemValidosComRiscoMedio() {
        when(membroService.buscarMembroPorID(gerenteId)).thenReturn(gerente);
        when(projetoRepository.findProjetosAtivosPorMembro(gerenteId)).thenReturn(Collections.emptyList());
        when(projetoMapper.toEntity(projetoRequestDTO)).thenReturn(projeto);
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);
        when(projetoMapper.toDetalhesDTO(projeto)).thenReturn(projetoDetalhesDTO);

        
        projeto.setDataFinalPrevisao(projetoRequestDTO.getDataFinalPrevisao());
        projeto.setOrcamento(projetoRequestDTO.getOrcamento());

        ProjetoDetalhesDTO resultado = projetoService.criarProjeto(projetoRequestDTO);

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository).save(projetoCaptor.capture());
        Projeto projetoSalvo = projetoCaptor.getValue();

        assertNotNull(resultado);
        assertEquals(Risco.MEDIO, projetoSalvo.getClassificacaoRisco());
        assertEquals(gerenteId, projetoSalvo.getGerenteId());
        assertTrue(projetoSalvo.getMembrosIds().contains(gerenteId));
    }

    @Test
    @DisplayName("criarProjeto deveria definir risco BAIXO quando orçamento e prazo forem baixos")
    void criarProjeto_deveriaDefinirRiscoBaixo_quandoOrcamentoEPrazoForemBaixos() {
        projetoRequestDTO.setDataFinalPrevisao(LocalDate.now().plusMonths(3));
        projetoRequestDTO.setOrcamento(new BigDecimal("100000.00"));

        Projeto projetoRiscoBaixo = new Projeto();
        projetoRiscoBaixo.setDataInicio(projetoRequestDTO.getDataInicio());
        projetoRiscoBaixo.setDataFinalPrevisao(projetoRequestDTO.getDataFinalPrevisao());
        projetoRiscoBaixo.setOrcamento(projetoRequestDTO.getOrcamento());

        when(membroService.buscarMembroPorID(gerenteId)).thenReturn(gerente);
        when(projetoRepository.findProjetosAtivosPorMembro(gerenteId)).thenReturn(Collections.emptyList());
        when(projetoMapper.toEntity(projetoRequestDTO)).thenReturn(projetoRiscoBaixo);
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projetoRiscoBaixo);

        projetoService.criarProjeto(projetoRequestDTO);

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository).save(projetoCaptor.capture());

        assertEquals(Risco.BAIXO, projetoCaptor.getValue().getClassificacaoRisco());
    }

    @Test
    @DisplayName("criarProjeto deveria definir risco ALTO quando prazo for longo")
    void criarProjeto_deveriaDefinirRiscoAlto_quandoPrazoForLongo() {
        projetoRequestDTO.setDataFinalPrevisao(LocalDate.now().plusMonths(7));
        projetoRequestDTO.setOrcamento(new BigDecimal("200000.00"));

        Projeto projetoRiscoAlto = new Projeto();
        projetoRiscoAlto.setDataInicio(projetoRequestDTO.getDataInicio());
        projetoRiscoAlto.setDataFinalPrevisao(projetoRequestDTO.getDataFinalPrevisao());
        projetoRiscoAlto.setOrcamento(projetoRequestDTO.getOrcamento());

        when(membroService.buscarMembroPorID(gerenteId)).thenReturn(gerente);
        when(projetoRepository.findProjetosAtivosPorMembro(gerenteId)).thenReturn(Collections.emptyList());
        when(projetoMapper.toEntity(projetoRequestDTO)).thenReturn(projetoRiscoAlto);
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projetoRiscoAlto);

        projetoService.criarProjeto(projetoRequestDTO);

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository).save(projetoCaptor.capture());

        assertEquals(Risco.ALTO, projetoCaptor.getValue().getClassificacaoRisco());
    }

    @Test
    @DisplayName("criarProjeto deveria definir risco ALTO quando orçamento for alto")
    void criarProjeto_deveriaDefinirRiscoAlto_quandoOrcamentoForAlto() {
        projetoRequestDTO.setDataFinalPrevisao(LocalDate.now().plusMonths(5));
        projetoRequestDTO.setOrcamento(new BigDecimal("500000.01"));

        Projeto projetoRiscoAlto = new Projeto();
        projetoRiscoAlto.setDataInicio(projetoRequestDTO.getDataInicio());
        projetoRiscoAlto.setDataFinalPrevisao(projetoRequestDTO.getDataFinalPrevisao());
        projetoRiscoAlto.setOrcamento(projetoRequestDTO.getOrcamento());

        when(membroService.buscarMembroPorID(gerenteId)).thenReturn(gerente);
        when(projetoRepository.findProjetosAtivosPorMembro(gerenteId)).thenReturn(Collections.emptyList());
        when(projetoMapper.toEntity(projetoRequestDTO)).thenReturn(projetoRiscoAlto);
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projetoRiscoAlto);

        projetoService.criarProjeto(projetoRequestDTO);

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository).save(projetoCaptor.capture());

        assertEquals(Risco.ALTO, projetoCaptor.getValue().getClassificacaoRisco());
    }

    @Test
    @DisplayName("criarProjeto deveria lançar BusinessException quando gerente atingir máximo de projetos")
    void criarProjeto_deveriaLancarBusinessException_quandoGerenteAtingirMaximoDeProjetos() {
        when(membroService.buscarMembroPorID(gerenteId)).thenReturn(gerente);
        when(projetoRepository.findProjetosAtivosPorMembro(gerenteId))
                .thenReturn(Collections.nCopies(10, new Projeto()));

        assertThrows(BusinessException.class, () -> projetoService.criarProjeto(projetoRequestDTO));
    }

    @Test
    @DisplayName("eliminarProjeto deveria marcar como deletado quando status for permitido")
    void eliminarProjeto_deveriaMarcarProjetoComoDeletado_quandoStatusPermitirExclusao() {
        projeto.setStatus(StatusProjeto.ANALISE_REALIZADA);
        when(projetoRepository.findByIdAndDeletedFalse(projetoId)).thenReturn(Optional.of(projeto));

        projetoService.eliminarProjeto(projetoId);

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository).save(projetoCaptor.capture());

        assertTrue(projetoCaptor.getValue().getDeleted());
    }

    @Test
    @DisplayName("eliminarProjeto deveria lançar BusinessException quando status for INICIADO")
    void eliminarProjeto_deveriaLancarBusinessException_quandoStatusNaoPermitirExclusao() {
        projeto.setStatus(StatusProjeto.INICIADO);
        when(projetoRepository.findByIdAndDeletedFalse(projetoId)).thenReturn(Optional.of(projeto));

        assertThrows(BusinessException.class, () -> projetoService.eliminarProjeto(projetoId));
    }

    @Test
    @DisplayName("atualizarProjeto deveria alterar os dados e recalcular o risco")
    void atualizarProjeto_deveriaAtualizarDadosDoProjeto_quandoDadosValidos() {
        when(projetoRepository.findByIdAndDeletedFalse(projetoId)).thenReturn(Optional.of(projeto));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);
        when(projetoMapper.toDetalhesDTO(any(Projeto.class))).thenReturn(projetoDetalhesDTO);

        AtualizarProjetoDTO dadosParaAtualizar = new AtualizarProjetoDTO();
        dadosParaAtualizar.setNome("Nome Atualizado");
        dadosParaAtualizar.setOrcamento(new BigDecimal("600000.00"));

        projetoService.atualizarProjeto(projetoId, dadosParaAtualizar);

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository).save(projetoCaptor.capture());
        Projeto projetoSalvo = projetoCaptor.getValue();

        assertEquals("Nome Atualizado", projetoSalvo.getNome());
        assertEquals(new BigDecimal("600000.00"), projetoSalvo.getOrcamento());
        assertEquals(Risco.ALTO, projetoSalvo.getClassificacaoRisco());
    }

    @Test
    @DisplayName("atualizarProjeto deveria lançar BusinessException quando projeto estiver ENCERRADO")
    void atualizarProjeto_deveriaLancarBusinessException_quandoStatusForEncerrado() {
        projeto.setStatus(StatusProjeto.ENCERRADO);
        when(projetoRepository.findByIdAndDeletedFalse(projetoId)).thenReturn(Optional.of(projeto));

        assertThrows(BusinessException.class,
                () -> projetoService.atualizarProjeto(projetoId, new AtualizarProjetoDTO()));
    }

    @Test
    @DisplayName("atualizarProjeto deveria lançar BusinessException quando novo gerente não for membro")
    void atualizarProjeto_deveriaLancarBusinessException_quandoNovoGerenteNaoForMembroDoProjeto() {

        UUID novoGerenteId = UUID.randomUUID();
        Membro novoGerente = new Membro();
        novoGerente.setId(novoGerenteId);
        novoGerente.setNome("Futuro Gerente");
        AtualizarProjetoDTO dadosParaAtualizar = new AtualizarProjetoDTO();
        dadosParaAtualizar.setGerenteId(novoGerenteId);

        when(projetoRepository.findByIdAndDeletedFalse(projetoId)).thenReturn(Optional.of(projeto));
        when(membroService.buscarMembroPorID(novoGerenteId)).thenReturn(novoGerente);

        assertThrows(BusinessException.class, () -> projetoService.atualizarProjeto(projetoId, dadosParaAtualizar));

        verify(projetoRepository, never()).save(any(Projeto.class));
    }

    @Test
    @DisplayName("alterarStatus deveria atualizar o status quando a transição for válida")
    void alterarStatus_deveriaAlterarStatus_quandoTransicaoForValida() {
        projeto.setStatus(StatusProjeto.EM_ANALISE);
        StatusProjeto novoStatus = StatusProjeto.ANALISE_REALIZADA;

        when(projetoRepository.findByIdAndDeletedFalse(projetoId)).thenReturn(Optional.of(projeto));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);

        projetoService.alterarStatus(projetoId, novoStatus);

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository).save(projetoCaptor.capture());

        assertEquals(novoStatus, projetoCaptor.getValue().getStatus());
        assertNull(projetoCaptor.getValue().getDataFinalReal());
    }

    @Test
    @DisplayName("alterarStatus deveria definir data final real quando o novo status for ENCERRADO")
    void alterarStatus_deveriaAlterarStatusEDataFinal_quandoNovoStatusForEncerrado() {
        projeto.setStatus(StatusProjeto.EM_ANDAMENTO);
        StatusProjeto novoStatus = StatusProjeto.ENCERRADO;

        when(projetoRepository.findByIdAndDeletedFalse(projetoId)).thenReturn(Optional.of(projeto));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);

        projetoService.alterarStatus(projetoId, novoStatus);

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository).save(projetoCaptor.capture());

        assertEquals(novoStatus, projetoCaptor.getValue().getStatus());
        assertEquals(LocalDate.now(), projetoCaptor.getValue().getDataFinalReal());
    }

    @Test
    @DisplayName("alterarStatus deveria lançar BusinessException quando a transição for inválida")
    void alterarStatus_deveriaLancarBusinessException_quandoTransicaoDeStatusForInvalida() {
        projeto.setStatus(StatusProjeto.EM_ANALISE);
        StatusProjeto novoStatus = StatusProjeto.ENCERRADO;

        when(projetoRepository.findByIdAndDeletedFalse(projetoId)).thenReturn(Optional.of(projeto));

        assertThrows(BusinessException.class, () -> projetoService.alterarStatus(projetoId, novoStatus));
    }

    @Test
    @DisplayName("listarProjetosPorFiltro deveria retornar página de projetos quando encontrados")
    void listarProjetosPorFiltro_deveriaRetornarPaginaDeProjetos_quandoFiltrosCorresponderem() {
        Pageable pageable = Pageable.unpaged();
        Page<Projeto> paginaDeProjetos = new PageImpl<>(List.of(projeto));
        
        when(projetoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(paginaDeProjetos);
        when(projetoMapper.toResumoDTO(any(Projeto.class))).thenReturn(new ProjetoResumoDTO());

        Page<ProjetoResumoDTO> resultado = projetoService.listarProjetosPorFiltro(new ProjetoFiltroDTO(), pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
    }

    @Test
    @DisplayName("listarProjetosPorFiltro deveria lançar NotFoundException quando nenhum projeto corresponder")
    void listarProjetosPorFiltro_deveriaLancarNotFoundException_quandoNenhumProjetoCorresponder() {
        Pageable pageable = Pageable.unpaged();

        when(projetoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());

        assertThrows(NotFoundException.class,
                () -> projetoService.listarProjetosPorFiltro(new ProjetoFiltroDTO(), pageable));
    }

    @Test
    @DisplayName("adicionarMembros deveria adicionar membros válidos ao projeto")
    void adicionarMembros_deveriaAdicionarNovosMembros_quandoMembrosForemValidos() {
        UUID membroId = UUID.randomUUID();
        Membro novoMembro = new Membro();
        novoMembro.setId(membroId);
        novoMembro.setNome("Novo Membro");
        novoMembro.setAtribuicao(Atribuicao.FUNCIONARIO);

        AdicionarMembrosRequestDTO request = new AdicionarMembrosRequestDTO();
        request.setMembrosIds(List.of(membroId));

        when(projetoRepository.findByIdAndDeletedFalse(projetoId)).thenReturn(Optional.of(projeto));
        when(membroService.buscarMembroPorID(membroId)).thenReturn(novoMembro);
        when(projetoRepository.findProjetosAtivosPorMembro(membroId)).thenReturn(Collections.emptyList());

        projetoService.adicionarMembros(request, projetoId);

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository).save(projetoCaptor.capture());

        assertTrue(projetoCaptor.getValue().getMembrosIds().contains(membroId));
        assertEquals(2, projetoCaptor.getValue().getMembrosIds().size());
    }

    @Test
    @DisplayName("adicionarMembros deveria lançar BusinessException quando membro não for FUNCIONARIO")
    void adicionarMembros_deveriaLancarBusinessException_quandoMembroNaoForFuncionario() {
        UUID membroId = UUID.randomUUID();
        Membro novoMembro = new Membro();
        novoMembro.setId(membroId);
        novoMembro.setNome("Membro Gerente");
        novoMembro.setAtribuicao(Atribuicao.STAKEHOLDER);

        AdicionarMembrosRequestDTO request = new AdicionarMembrosRequestDTO();
        request.setMembrosIds(List.of(membroId));

        when(projetoRepository.findByIdAndDeletedFalse(projetoId)).thenReturn(Optional.of(projeto));
        when(membroService.buscarMembroPorID(membroId)).thenReturn(novoMembro);

        assertThrows(BusinessException.class, () -> projetoService.adicionarMembros(request, projetoId));
    }

    @Test
    @DisplayName("adicionarMembros deveria lançar BusinessException quando membro atingir máximo de projetos")
    void adicionarMembros_deveriaLancarBusinessException_quandoMembroAtingirMaximoDeProjetos() {
        UUID membroId = UUID.randomUUID();
        Membro novoMembro = new Membro();
        novoMembro.setId(membroId);
        novoMembro.setNome("Membro Ocupado");
        novoMembro.setAtribuicao(Atribuicao.FUNCIONARIO);

        AdicionarMembrosRequestDTO request = new AdicionarMembrosRequestDTO();
        request.setMembrosIds(List.of(membroId));

        when(projetoRepository.findByIdAndDeletedFalse(projetoId)).thenReturn(Optional.of(projeto));
        when(membroService.buscarMembroPorID(membroId)).thenReturn(novoMembro);
        when(projetoRepository.findProjetosAtivosPorMembro(membroId))
                .thenReturn(Collections.nCopies(10, new Projeto()));

        assertThrows(BusinessException.class, () -> projetoService.adicionarMembros(request, projetoId));
    }
}