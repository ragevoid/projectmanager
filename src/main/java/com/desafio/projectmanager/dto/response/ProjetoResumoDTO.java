package com.desafio.projectmanager.dto.response;

import java.util.UUID;

import com.desafio.projectmanager.model.projeto.Risco;
import com.desafio.projectmanager.model.projeto.StatusProjeto;

import lombok.Data;

@Data
public class ProjetoResumoDTO {
    private UUID id;
    private String nome;
    private StatusProjeto status;
    private String gerenteNome; 
    private Risco classificacaoRisco; 
}