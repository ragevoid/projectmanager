package com.desafio.projectmanager.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AtualizarProjetoDTO {


    @Size(min = 3, max = 200, message = "O nome deve ter entre 3 e 200 caracteres")
    private String nome;

    private LocalDate dataInicio;

    @FutureOrPresent(message = "A data de previsão de término não pode ser no passado.")
    private LocalDate dataFinalPrevisao;

    @Positive(message = "O orçamento deve ser um valor positivo")
    private BigDecimal orcamento;

    @Size(max = 5000)
    private String descricao;

    private UUID gerenteId;

}



