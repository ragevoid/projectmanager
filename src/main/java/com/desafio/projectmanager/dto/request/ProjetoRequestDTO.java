package com.desafio.projectmanager.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjetoRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 200, message = "O nome deve ter entre 3 e 200 caracteres")
    private String nome;

    @NotNull(message = "A data de início é obrigatória")
    private LocalDate dataInicio;

    @NotNull(message = "A previsão de término é obrigatória")
    private LocalDate dataFinalPrevisao;

    private LocalDate dataFinalReal;

    @NotNull(message = "O orçamento é obrigatório")
    @Positive(message = "O orçamento deve ser um valor positivo")
    private BigDecimal orcamento;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 5000)
    private String descricao;
    
    @NotNull(message = "O ID do gerente é obrigatório")
    private UUID gerenteId;

    private Set<UUID> membrosIds;
}
