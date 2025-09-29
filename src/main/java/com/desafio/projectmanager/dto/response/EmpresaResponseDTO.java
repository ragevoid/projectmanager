package com.desafio.projectmanager.dto.response;

import java.util.UUID;

import lombok.Data;

@Data
public class EmpresaResponseDTO {
    private UUID id;
    private String nome;
    private Boolean deleted;
}
