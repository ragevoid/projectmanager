package com.desafio.projectmanager.dto.response;

import java.math.BigDecimal;

import com.desafio.projectmanager.model.projeto.StatusProjeto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RelatorioStatusDTO {
    private StatusProjeto status;
    private long quantidadeProjetos;
    private BigDecimal totalOrcado;
}

