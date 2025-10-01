package com.desafio.projectmanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.desafio.projectmanager.dto.response.RelatorioProjetosDTO;
import com.desafio.projectmanager.dto.response.RelatorioStatusDTO;
import com.desafio.projectmanager.model.projeto.Projeto;
import com.desafio.projectmanager.model.projeto.StatusProjeto;
import com.desafio.projectmanager.repository.ProjetoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjetoRelatorioService {

    private final ProjetoRepository projetoRepository;

    public RelatorioProjetosDTO gerarRelatorio() {

        List<RelatorioStatusDTO> listaRelatorioStatus = geraRelatorioStatusLista();
        double media = gerarMediaDuracaoPorStatus(StatusProjeto.ENCERRADO);
        long totalMembrosUnicos = geraTotalMembrosUnicos();
        LocalDate agora = LocalDate.now();
        return new RelatorioProjetosDTO(listaRelatorioStatus, media, totalMembrosUnicos, agora);

    }

    private List<RelatorioStatusDTO> geraRelatorioStatusLista() {
        List<StatusProjeto> listaDeStatus = Arrays.asList(StatusProjeto.values());
        List<RelatorioStatusDTO> listaRelatorio = new ArrayList<>();

        for (StatusProjeto statusProjeto : listaDeStatus) {
            RelatorioStatusDTO relatorioStatusDTO = geraRelatorioStatus(statusProjeto);
            listaRelatorio.add(relatorioStatusDTO);
        }
        return listaRelatorio;
    }

    private RelatorioStatusDTO geraRelatorioStatus(StatusProjeto statusProjeto) {
        List<Projeto> projetos = projetoRepository.findByStatus(statusProjeto);
        BigDecimal totalOrcado = new BigDecimal("0");

        for (Projeto projeto : projetos) {
            BigDecimal orcamento = projeto.getOrcamento();
            totalOrcado = totalOrcado.add(orcamento);
        }

        long quantidadeProjetos = projetos.size();
        return new RelatorioStatusDTO(statusProjeto, quantidadeProjetos, totalOrcado);
    }

    private double gerarMediaDuracaoPorStatus(StatusProjeto status) {

        List<Projeto> projetosEncerrados = projetoRepository.findByStatus(status);
        double mediaDias = projetosEncerrados.stream()
                .filter(p -> p.getDataInicio() != null && p.getDataFinalReal() != null)
                .mapToLong(p -> ChronoUnit.DAYS.between(p.getDataInicio(), p.getDataFinalReal()))
                .average()
                .orElse(0.0);

        return mediaDias;
    }

    private long geraTotalMembrosUnicos() {
        List<Set<UUID>> todosOsMembros = projetoRepository.findAllMembrosIds();
        Set<UUID> membrosUnicos = new HashSet<>();
        todosOsMembros.forEach(membrosUnicos::addAll);
        return membrosUnicos.size();
    }

}
