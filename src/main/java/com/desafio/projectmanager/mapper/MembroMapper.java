package com.desafio.projectmanager.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.desafio.projectmanager.dto.response.MembroMockadoDTO;
import com.desafio.projectmanager.model.membro.Membro;

@Mapper(componentModel = "spring")
public interface MembroMapper {

    @Mappings({
        @Mapping(target = "nome", source = "nome"),
        @Mapping(target = "atribuicao", source = "atribuicao")
    })
    MembroMockadoDTO toDTO(Membro membro);

    @Mappings({
        @Mapping(target = "projetos", ignore = true)
    })
    Membro toEntity(MembroMockadoDTO externoDTO);
}