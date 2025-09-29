package com.desafio.projectmanager.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.desafio.projectmanager.model.projeto.ClassificacaoRisco;
import com.desafio.projectmanager.model.projeto.StatusProjeto;

import lombok.Data;

@Data
public class ProjetoResponseDTO {
    private UUID id;
    private String nome;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFinalPrevisao;
    private LocalDateTime dataFinalReal;
    private BigDecimal orcamento;
    private String descricao;
    private StatusProjeto status;
    private ClassificacaoRisco classificacaoRisco;
    private UUID gerenteId;
    private Set<MembroResponseDTO> membros;
    private UUID empresaId;
    private Boolean deleted = false;
}
