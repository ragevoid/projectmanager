package com.desafio.projectmanager.dto.response;

import java.util.UUID;

import com.desafio.projectmanager.model.membro.Atribuicao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MembroMockadoDTO {
    private UUID id;
    private String nome;
    private Atribuicao atribuicao;

}
