package com.desafio.projectmanager.dto.response;

import java.util.UUID;

import com.desafio.projectmanager.model.membro.Atribuicao;

import lombok.Data;

@Data
public class MembroResumoDTO {
    private UUID id;
    private String nome;
    private Atribuicao atribuicao;
}