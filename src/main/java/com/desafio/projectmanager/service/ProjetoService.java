package com.desafio.projectmanager.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.desafio.projectmanager.model.projeto.ClassificacaoRisco;
import com.desafio.projectmanager.model.projeto.Projeto;
import com.desafio.projectmanager.repository.ProjetoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjetoService {
    private final ProjetoRepository projetoRepository;

    public List<Projeto> listarProjetos() {
        return projetoRepository.findAllByDeletedFalse().stream()
                .collect(Collectors.toList());
    }

    public Projeto CrearProjeto(Projeto projeto){
        ClassificacaoRisco risco= validarRisco(projeto);
        projeto.setClassificacaoRisco(risco);
        return projetoRepository.save(projeto);
    }

    public ClassificacaoRisco validarRisco(Projeto projeto) {

        long mesesTotais = CalcularMesesTotais(projeto);
        double orcamentoDouble = projeto.getOrcamento().doubleValue();

        if (esRiscoAlto(mesesTotais, orcamentoDouble)) {
        return ClassificacaoRisco.ALTO;
        }
        if (esRiscoBajo(mesesTotais, orcamentoDouble)) {
            return ClassificacaoRisco.BAIXO;
        }
        
        return ClassificacaoRisco.MEDIO;
    }

    public long CalcularMesesTotais(Projeto projeto) {
        LocalDateTime inicio = projeto.getDataInicio();
        LocalDateTime finPrevisto = projeto.getDataFinalPrevisao();
        return ChronoUnit.MONTHS.between(inicio, finPrevisto);
    }

    public boolean esRiscoBajo(Long mesesTotais, double orcamento) {
        boolean verificaMeses = mesesTotais <= 3;
        boolean verificaOrcamento = orcamento < 100001;
        return verificaMeses && verificaOrcamento;
    }
       public boolean esRiscoAlto(Long mesesTotais, double orcamento) {
        boolean verificaMeses = mesesTotais > 6;
        boolean verificaOrcamento = orcamento > 500000;
        return verificaMeses || verificaOrcamento;
    }




}