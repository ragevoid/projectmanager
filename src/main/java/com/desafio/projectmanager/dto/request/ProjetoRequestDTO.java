package com.desafio.projectmanager.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.desafio.projectmanager.model.projeto.StatusProjeto;

import lombok.Data;

@Data
public class ProjetoRequestDTO {
    private String nome;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFinalPrevisao;
    private LocalDateTime dataFinalReal;
    private BigDecimal orcamento;
    private String descricao;
    private StatusProjeto status;
    private UUID gerenteId;
    private Set<UUID> membrosIds;
    private UUID empresaId;
}
