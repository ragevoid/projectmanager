package com.desafio.projectmanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.desafio.projectmanager.dto.response.RelatorioProjetosDTO;
import com.desafio.projectmanager.service.ProjetoRelatorioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/relatorio")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Endpoints para a geração de relatórios")
public class RelatorioController {

    private final ProjetoRelatorioService relatorioService;

    @GetMapping()
    @Operation(summary = "Gera os dados para o relatório de projetos em formato JSON")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "JSON Com dados de relatorios especificados"),
            @ApiResponse(responseCode = "505", description = "Erro grave", content = @Content)
    })
    public ResponseEntity<RelatorioProjetosDTO> gerarRelatorioProjetosJson() {
        RelatorioProjetosDTO relatorio = relatorioService.gerarRelatorio();
        return ResponseEntity.ok(relatorio);
    }
}
