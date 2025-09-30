package com.desafio.projectmanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.desafio.projectmanager.dto.request.ProjetoRequestDTO;
import com.desafio.projectmanager.dto.response.ProjetoDetalhesDTO;
import com.desafio.projectmanager.dto.response.ProjetoResumoDTO;
import com.desafio.projectmanager.handler.exceptions.BusinessException;
import com.desafio.projectmanager.mapper.ProjetoMapper;
import com.desafio.projectmanager.model.projeto.Projeto;
import com.desafio.projectmanager.model.projeto.Risco;
import com.desafio.projectmanager.model.projeto.StatusProjeto;
import com.desafio.projectmanager.repository.ProjetoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjetoService {

     private final ProjetoRepository projetoRepository;
    private final ProjetoMapper projetoMapper; 

    private static final BigDecimal ORCAMENTO_RISCO_BAIXO_LIMITE = new BigDecimal("100000.00");
    private static final BigDecimal ORCAMENTO_RISCO_ALTO_INICIO = new BigDecimal("500000.00");
    private static final long PRAZO_RISCO_BAIXO_MESES = 3;
    private static final long PRAZO_RISCO_ALTO_MESES = 6;


    public List<ProjetoResumoDTO> listarProjetos() {
        return projetoRepository.findAllByDeletedFalse().stream()
                .map(projeto -> {
                    Risco risco = calcularRisco(projeto);
                    return projetoMapper.toResumoDTO(projeto, risco); 
                })
                .collect(Collectors.toList());
    }

    public ProjetoDetalhesDTO encontrarPorId(UUID projetoId) {
        Projeto projeto = projetoRepository.findByIdAndDeletedFalse(projetoId)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado com ID: " + projetoId));

        Risco risco = calcularRisco(projeto);
        
        return projetoMapper.toDetalhesDTO(projeto, risco);
    }
    
    public ProjetoDetalhesDTO salvarProjeto(ProjetoRequestDTO projetoDTO) {
 
        Projeto projeto = projetoMapper.toEntity(projetoDTO);
        Projeto projetoSalvo = projetoRepository.save(projeto);
        Risco risco = calcularRisco(projetoSalvo);
        return projetoMapper.toDetalhesDTO(projetoSalvo, risco);
    }

    public void eliminarProjeto(UUID projetoId) {
        Projeto projeto = projetoRepository.findByIdAndDeletedFalse(projetoId)
            .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado com ID: " + projetoId));

        StatusProjeto status = projeto.getStatus();

        if (status == StatusProjeto.INICIADO || status == StatusProjeto.EM_ANDAMENTO || status == StatusProjeto.ENCERRADO) {
            throw new BusinessException("Projeto com status '" + status + "' não pode ser excluído.");
        }
        
        projeto.setDeleted(true);
        projetoRepository.save(projeto);
    }

    private Risco calcularRisco(Projeto projeto) {
        long mesesTotais = calcularMesesTotais(projeto);
        BigDecimal orcamento = projeto.getOrcamento();

        if (esRiscoAlto(mesesTotais, orcamento)) {
            return Risco.ALTO;
        }
        if (esRiscoBaixo(mesesTotais, orcamento)) {
            return Risco.BAIXO;
        }
        return Risco.MEDIO;
    }

    private long calcularMesesTotais(Projeto projeto) {
        LocalDate inicio = projeto.getDataInicio();
        LocalDate fimPrevisto = projeto.getDataFinalPrevisao();
        return ChronoUnit.MONTHS.between(inicio, fimPrevisto);
    }

    private boolean esRiscoBaixo(long mesesTotais, BigDecimal orcamento) {
        boolean prazoOk = mesesTotais <= PRAZO_RISCO_BAIXO_MESES;
        boolean orcamentoOk = orcamento.compareTo(ORCAMENTO_RISCO_BAIXO_LIMITE) <= 0;
        return prazoOk && orcamentoOk;
    }

    private boolean esRiscoAlto(long mesesTotais, BigDecimal orcamento) {
        boolean prazoOk = mesesTotais > PRAZO_RISCO_ALTO_MESES;
        boolean orcamentoOk = orcamento.compareTo(ORCAMENTO_RISCO_ALTO_INICIO) > 0;
        return prazoOk || orcamentoOk;
    }

}