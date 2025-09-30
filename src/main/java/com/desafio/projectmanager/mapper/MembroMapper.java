package com.desafio.projectmanager.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.desafio.projectmanager.dto.response.MembroDetalhesDTO;
import com.desafio.projectmanager.dto.response.MembroExternoDTO;
import com.desafio.projectmanager.dto.response.MembroResumoDTO;
import com.desafio.projectmanager.model.membro.Membro;

@Mapper(componentModel = "spring", uses = {ProjetoMapper.class})
public interface MembroMapper {

    MembroResumoDTO toResumoDTO(Membro membro);


    MembroDetalhesDTO toDetalhesDTO(Membro membro);
    
    @Mappings({
        @Mapping(source = "id", target = "idMockado"), 
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "projetos", ignore = true)
    })
    Membro toEntity(MembroExternoDTO externoDTO);
}