package com.desafio.projectmanager.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.desafio.projectmanager.dto.response.MembroResponseDTO;
import com.desafio.projectmanager.model.membro.Membro;

@Mapper(componentModel = "spring")
public interface MembroMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    MembroResponseDTO toDto (Membro membro);

    List<MembroResponseDTO>toDtoList(List<Membro> membros);

}
