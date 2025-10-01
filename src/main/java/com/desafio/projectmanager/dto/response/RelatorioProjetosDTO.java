package com.desafio.projectmanager.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioProjetosDTO {
    private List<RelatorioStatusDTO> dadosPorStatus;
    private double mediaDuracaoProjetosEncerrados;
    private long totalMembrosUnicos;
    private LocalDate dataGeracao;
}