package com.desafio.projectmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.desafio.projectmanager.handler.exceptions.BusinessException;
import com.desafio.projectmanager.model.projeto.ClassificacaoRisco;
import com.desafio.projectmanager.model.projeto.Projeto;
import com.desafio.projectmanager.model.projeto.StatusProjeto;
import com.desafio.projectmanager.repository.ProjetoRepository;

@ExtendWith(MockitoExtension.class)
public class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @InjectMocks
    private ProjetoService projetoService;

    @Test
    void listarProjetos_deveriaRetornarListaDeProjetos_quandoExistemProjetos() {

        UUID uuidA = UUID.randomUUID();
        UUID uuidB = UUID.randomUUID();

        Projeto projeto1 = new Projeto();
        projeto1.setId(uuidA);
        projeto1.setNome("Projeto Apollo");

        Projeto projeto2 = new Projeto();
        projeto2.setId(uuidB);
        projeto2.setNome("Projeto X");

        List<Projeto> listaDeProjetos = List.of(projeto1, projeto2);

        when(projetoRepository.findAllByDeletedFalse()).thenReturn(listaDeProjetos);

        List<Projeto> resultado = projetoService.listarProjetos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Projeto Apollo", resultado.get(0).getNome());

        verify(projetoRepository, times(1)).findAllByDeletedFalse();
    }

    @Test
    void crearProjeto_deveriaClasificarComoBaixo_quandoCondicoesDeRiesgoBaixo() {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusMonths(2);
        BigDecimal orcamento = new BigDecimal("50000.00");

        Projeto projetoParaTestar = new Projeto();
        projetoParaTestar.setDataInicio(inicio);
        projetoParaTestar.setDataFinalPrevisao(fin);
        projetoParaTestar.setOrcamento(orcamento);
        projetoParaTestar.setNome("Projeto teste");

        when(projetoRepository.save(any(Projeto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Projeto projetoCriado = projetoService.salvarProjeto(projetoParaTestar);

        assertNotNull(projetoCriado);
        assertEquals(ClassificacaoRisco.BAIXO, projetoCriado.getClassificacaoRisco());

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository, times(1)).save(projetoCaptor.capture());

        Projeto projetoGuardado = projetoCaptor.getValue();
        assertEquals(ClassificacaoRisco.BAIXO, projetoGuardado.getClassificacaoRisco());
    }

    @Test
    void crearProjeto_deveriaClasificarComoMedio_quandoCondicoesDeRiesgoMedio() {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusMonths(2);
        BigDecimal orcamento = new BigDecimal("200000.00");

        Projeto projetoParaTestar = new Projeto();
        projetoParaTestar.setDataInicio(inicio);
        projetoParaTestar.setDataFinalPrevisao(fin);
        projetoParaTestar.setOrcamento(orcamento);
        projetoParaTestar.setNome("Projeto teste");

        when(projetoRepository.save(any(Projeto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Projeto projetoCriado = projetoService.salvarProjeto(projetoParaTestar);

        assertNotNull(projetoCriado);
        assertEquals(ClassificacaoRisco.MEDIO, projetoCriado.getClassificacaoRisco());

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository, times(1)).save(projetoCaptor.capture());

        Projeto projetoGuardado = projetoCaptor.getValue();
        assertEquals(ClassificacaoRisco.MEDIO, projetoGuardado.getClassificacaoRisco());
    }

    @Test
    void crearProjeto_deveriaClasificarComoAlto_quandoCondicoesDeRiesgoAlto() {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusMonths(2);
        BigDecimal orcamento = new BigDecimal("600000.00");

        Projeto projetoParaTestar = new Projeto();
        projetoParaTestar.setDataInicio(inicio);
        projetoParaTestar.setDataFinalPrevisao(fin);
        projetoParaTestar.setOrcamento(orcamento);
        projetoParaTestar.setNome("Projeto teste");

        when(projetoRepository.save(any(Projeto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Projeto projetoCriado = projetoService.salvarProjeto(projetoParaTestar);

        assertNotNull(projetoCriado);
        assertEquals(ClassificacaoRisco.ALTO, projetoCriado.getClassificacaoRisco());

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository, times(1)).save(projetoCaptor.capture());

        Projeto projetoGuardado = projetoCaptor.getValue();
        assertEquals(ClassificacaoRisco.ALTO, projetoGuardado.getClassificacaoRisco());
    }

    @Test
    void eliminarProjeto_deveriaSalvarProjetoComDeletedTrue_quandoStatusEmAnalise() {
        UUID uuid = UUID.randomUUID();

        Projeto projeto = new Projeto();
        projeto.setId(uuid);
        projeto.setNome("Projeto Apollo");
        projeto.setStatus(StatusProjeto.EM_ANALISE);
        projeto.setDeleted(false);

        when(projetoRepository.save(any(Projeto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(projetoRepository.findById(uuid)).thenReturn(Optional.of(projeto));

        Projeto projetoEliminado = projetoService.eliminarProjeto(uuid);

        verify(projetoRepository, times(1)).save(projeto);
        verify(projetoRepository, times(1)).findById(uuid);

        assertNotNull(projetoEliminado);
        assertTrue(projetoEliminado.getDeleted());
        assertEquals("Projeto Apollo", projetoEliminado.getNome());
    }

    @Test
    void eliminarProjeto_deveriaRetornarBusinessException_quandoStatusIniciado() {
        UUID uuid = UUID.randomUUID();

        Projeto projeto = new Projeto();
        projeto.setId(uuid);
        projeto.setNome("Projeto Apollo");
        projeto.setStatus(StatusProjeto.INICIADO);
        projeto.setDeleted(false);

        when(projetoRepository.findById(uuid)).thenReturn(Optional.of(projeto));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            projetoService.eliminarProjeto(uuid);
        });

        assertEquals("Projeto não pode ser eliminado, Verifique Status", exception.getMessage());

        verify(projetoRepository, times(0)).save(projeto);
        verify(projetoRepository, times(1)).findById(uuid);

    }

    @Test
    void encontrarPorId_deveriaRetornarIllegalArgumentException_quandoProjetoNãoExiste() {
        UUID uuid = UUID.randomUUID();

        Projeto projeto = new Projeto();
        projeto.setId(uuid);
        projeto.setNome("Projeto Apollo");

        when(projetoRepository.findById(uuid)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projetoService.eliminarProjeto(uuid);
        });

        assertEquals("Projeto não encontrado con ID: " + uuid, exception.getMessage());

        verify(projetoRepository, times(1)).findById(uuid);

    }

    @Test
    void encontrarPorId_deveriaRetornarProjeto_quandoProjetoExiste() {
        UUID uuid = UUID.randomUUID();
        Projeto projeto = new Projeto();
        projeto.setId(uuid);
        projeto.setNome("Projeto Apollo");

        when(projetoRepository.findById(uuid)).thenReturn(Optional.of(projeto));

        Projeto projetoEncontrado = projetoService.encontrarPorId(uuid);

        verify(projetoRepository, times(1)).findById(uuid);

        assertNotNull(projetoEncontrado);
        assertEquals("Projeto Apollo", projetoEncontrado.getNome());

    }

}
