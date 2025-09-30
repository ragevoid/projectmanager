package com.desafio.projectmanager.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.desafio.projectmanager.model.projeto.StatusProjeto;

import lombok.Data;

@Data
public class ProjetoFiltroDTO {
    private String nome;

    private BigDecimal orcamentoMin;
    private BigDecimal orcamentoMax;

    private LocalDate dataInicioPrimeira;
    private LocalDate dataInicioSegunda;

    private LocalDate dataFinalPrevisaoPrimeira;
    private LocalDate dataFinalPrevisaoSegunda;

    private LocalDate dataFinalRealPrimeira;
    private LocalDate dataFinalRealSegunda;

    private List<StatusProjeto> status;

    private String descricao;

    private UUID gerenteId;



}
