package com.desafio.projectmanager.dto.request;


import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class AdicionarMembrosRequestDTO {

    @NotEmpty(message = "A lista de IDs de membros não pode ser vazia.")
    private List<UUID> membrosIds;
}