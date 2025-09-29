package com.desafio.projectmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.desafio.projectmanager.model.projeto.ClassificacaoRisco;
import com.desafio.projectmanager.model.projeto.Projeto;
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

        Projeto projetoCriado = projetoService.CrearProjeto(projetoParaTestar);

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

        Projeto projetoCriado = projetoService.CrearProjeto(projetoParaTestar);

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

        Projeto projetoCriado = projetoService.CrearProjeto(projetoParaTestar);

        assertNotNull(projetoCriado);
        assertEquals(ClassificacaoRisco.ALTO, projetoCriado.getClassificacaoRisco());

        ArgumentCaptor<Projeto> projetoCaptor = ArgumentCaptor.forClass(Projeto.class);
        verify(projetoRepository, times(1)).save(projetoCaptor.capture());

        Projeto projetoGuardado = projetoCaptor.getValue();
        assertEquals(ClassificacaoRisco.ALTO, projetoGuardado.getClassificacaoRisco());
    }

}
