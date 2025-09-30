package com.desafio.projectmanager.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.desafio.projectmanager.dto.request.AdicionarMembrosRequestDTO;
import com.desafio.projectmanager.dto.request.ProjetoFiltroDTO;
import com.desafio.projectmanager.dto.request.ProjetoRequestDTO;
import com.desafio.projectmanager.dto.response.ProjetoDetalhesDTO;
import com.desafio.projectmanager.dto.response.ProjetoResumoDTO;
import com.desafio.projectmanager.model.projeto.StatusProjeto;
import com.desafio.projectmanager.service.ProjetoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projeto")
@RequiredArgsConstructor
@Tag(name = "Projetos", description = "Endpoints para o gerenciamento de projetos")
public class ProjetoController {

        private final ProjetoService projetoService;

        @PostMapping
        @Operation(summary = "Cria um novo projeto")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Projeto criado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
        })
        public ResponseEntity<ProjetoDetalhesDTO> criarProjeto(
                        @Valid @RequestBody ProjetoRequestDTO projetoRequestDTO) {
                System.out.println("EL id es: " + projetoRequestDTO.toString());
                ProjetoDetalhesDTO projetoCriado = projetoService.criarProjeto(projetoRequestDTO);
                return ResponseEntity.ok().body(projetoCriado);
        }

        @GetMapping("search/{id}")
        @Operation(summary = "Busca um projeto por ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Projeto encontrado"),
                        @ApiResponse(responseCode = "404", description = "Projeto não encontrado com o ID fornecido")
        })
        public ResponseEntity<ProjetoDetalhesDTO> encontrarPorId(
                        @Parameter(description = "ID do projeto a ser buscado", required = true) @PathVariable UUID id) {
                ProjetoDetalhesDTO projeto = projetoService.encontrarPorId(id);
                return ResponseEntity.ok(projeto);
        }

        @PostMapping("/search")
        @Operation(summary = "Lista (busca) projetos de forma paginada usando filtros no corpo da requisição", description = "Retorna uma página de projetos. Os filtros são enviados via JSON no corpo da requisição, enquanto a paginação e ordenação continuam na URL.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de projetos retornada com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Nenhum projeto encontrado para os filtros fornecidos")
        })
        public ResponseEntity<Page<ProjetoResumoDTO>> listarProjetosPorFiltro(

                        @Valid @RequestBody ProjetoFiltroDTO filtros,
                        @Parameter(hidden = true) @PageableDefault(size = 10, page = 0, sort = "nome") Pageable pageable) {

                Page<ProjetoResumoDTO> pagina = projetoService.listarProjetosPorFiltro(filtros, pageable);
                return ResponseEntity.ok(pagina);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Exclui um projeto (soft delete)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Projeto excluído com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Exclusão não permitida devido ao status do projeto"),
                        @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
        })
        public ResponseEntity<Void> eliminarProjeto(
                        @Parameter(description = "ID do projeto a ser excluído", required = true) @PathVariable UUID id) {
                projetoService.eliminarProjeto(id);
                return ResponseEntity.noContent().build();
        }

        @PatchMapping("/{id}/status/{novoStatus}")
        @Operation(summary = "Altera o status de um projeto")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Transição de status inválida ou status desconhecido"),
                        @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
        })
        public ResponseEntity<ProjetoDetalhesDTO> alterarStatus(
                        @Parameter(description = "ID do projeto para alterar o status", required = true) @PathVariable UUID id,
                        @Parameter(description = "O novo status para o projeto", required = true, example = "EM_ANDAMENTO") @PathVariable StatusProjeto novoStatus) {

                ProjetoDetalhesDTO projetoAtualizado = projetoService.alterarStatus(id, novoStatus);
                return ResponseEntity.ok(projetoAtualizado);
        }

        @PostMapping("/{projetoId}/membros")
        @Operation(summary = "Adiciona um ou mais membros a um projeto específico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Membros adicionados com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Regra de negócio violada (ex: limite de membros, atribuição inválida)"),
                        @ApiResponse(responseCode = "404", description = "Projeto ou um dos membros externos não encontrado")
        })
        public ResponseEntity<ProjetoDetalhesDTO> adicionarMembrosAoProjeto(
                        @Parameter(description = "ID do projeto", required = true) @PathVariable UUID projetoId,

                        @Valid @RequestBody AdicionarMembrosRequestDTO request) {

                ProjetoDetalhesDTO projetoAtualizado = projetoService.adicionarMembros(request.getMembrosIds(),
                                projetoId);
                return ResponseEntity.ok(projetoAtualizado);
        }
}
