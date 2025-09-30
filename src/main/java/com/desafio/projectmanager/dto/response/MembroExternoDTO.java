package com.desafio.projectmanager.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MembroExternoDTO {
    private UUID id;
    private String nome;
    private String atribuicao;

}
