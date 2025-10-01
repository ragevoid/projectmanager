package com.desafio.projectmanager.mapper;


import org.mapstruct.Mapper;

import com.desafio.projectmanager.dto.request.ProjetoRequestDTO;
import com.desafio.projectmanager.dto.response.ProjetoDetalhesDTO;
import com.desafio.projectmanager.model.projeto.Projeto;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {MembroMapper.class}) 
public interface ProjetoMapper {

    ProjetoDetalhesDTO toDTO(Projeto projeto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "status", ignore = true),
        @Mapping(target = "deleted", ignore = true),
        @Mapping(target = "classificacaoRisco", ignore = true)
    })
    Projeto toEntity(ProjetoRequestDTO requestDTO);

}