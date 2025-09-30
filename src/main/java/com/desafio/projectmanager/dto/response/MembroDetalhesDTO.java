package com.desafio.projectmanager.dto.response;

import java.util.Set;
import java.util.UUID;

import com.desafio.projectmanager.model.membro.Atribuicao;

import lombok.Data;

@Data
public class MembroDetalhesDTO {
    private UUID id;
    private UUID idExterno;
    private String nome;
    private Atribuicao atribuicao;
    
    private Set<ProjetoResumoDTO> projetos; 
}