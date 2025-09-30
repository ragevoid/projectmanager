package com.desafio.projectmanager.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import com.desafio.projectmanager.model.projeto.Risco;
import com.desafio.projectmanager.model.projeto.StatusProjeto;

import lombok.Data;

@Data
public class ProjetoDetalhesDTO {
    private UUID id;
    private String nome;
    private LocalDate dataInicio;
    private LocalDate dataPrevisaoFim;
    private LocalDate dataFimReal;
    private BigDecimal orcamento;
    private String descricao;
    private StatusProjeto status;
    private Risco classificacaoRisco; 
    private MembroResumoDTO gerente; 
    private Set<MembroResumoDTO> membros;
    
}
