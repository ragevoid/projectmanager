package com.desafio.projectmanager.mapper;


import org.mapstruct.Mapper;

import com.desafio.projectmanager.dto.request.ProjetoRequestDTO;
import com.desafio.projectmanager.dto.response.ProjetoDetalhesDTO;
import com.desafio.projectmanager.dto.response.ProjetoResumoDTO;
import com.desafio.projectmanager.model.projeto.Projeto;
import com.desafio.projectmanager.model.projeto.Risco;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {MembroMapper.class}) 
public interface ProjetoMapper {


    @Mappings({
        @Mapping(source = "projeto.gerente.nome", target = "gerenteNome"), 
        @Mapping(source = "risco", target = "classificacaoRisco") 
    })
    ProjetoResumoDTO toResumoDTO(Projeto projeto, Risco risco);

 
    @Mappings({
        @Mapping(source = "risco", target = "classificacaoRisco")
    })
    ProjetoDetalhesDTO toDetalhesDTO(Projeto projeto, Risco risco);


    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "status", ignore = true),
        @Mapping(target = "gerente", ignore = true),
        @Mapping(target = "empresa", ignore = true),
        @Mapping(target = "membros", ignore = true),
        @Mapping(target = "deleted", ignore = true),
        @Mapping(target = "classificacaoRisco", ignore = true)
    })
    Projeto toEntity(ProjetoRequestDTO requestDTO);

}