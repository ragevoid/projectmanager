package com.desafio.projectmanager.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProjetoFiltroDTO {
    private String nome;
    private BigDecimal orcamentoMin;
    private BigDecimal orcamentoMax;
}
