package com.desafio.projectmanager.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.desafio.projectmanager.dto.response.MembroExternoDTO;
import com.desafio.projectmanager.handler.exceptions.BusinessException;
import com.desafio.projectmanager.mapper.MembroMapper;
import com.desafio.projectmanager.model.membro.Membro;
import com.desafio.projectmanager.model.projeto.StatusProjeto;
import com.desafio.projectmanager.repository.MembroRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembroService {

    private final MembroMapper membroMapper;
    private final RestTemplate restTemplate;
    private final MembroRepository membroRepository;
    @Value("${membro.api.url}")
    private String membroApiUrl;


    public Membro buscarMembroAPIPorId(UUID idMockado) {

        try {
            String url = membroApiUrl + "/" + idMockado;
            MembroExternoDTO membro = restTemplate.getForObject(url, MembroExternoDTO.class);
            return membroMapper.toEntity(membro);

        } catch (Exception e) {
            throw new IllegalArgumentException("Membro não encontrado");
        }
    }

    @Transactional
    public Membro buscarOuCriarMembroLocal(UUID idExterno) {
        return membroRepository.findByIdMockado(idExterno)
            .orElseGet(() -> {                
                Membro novoMembro = buscarMembroAPIPorId(idExterno);
                return membroRepository.save(novoMembro);
            });
    }

    public void validarLimiteDeProjetosAtivos(Membro membro) {
        List<StatusProjeto> statusInativos = List.of(StatusProjeto.ENCERRADO, StatusProjeto.CANCELADO);
        
        Long projetosAtivos = membroRepository.countProjetosAtivosPorMembro(membro.getId(), statusInativos);

        if (projetosAtivos >= 3) {
            throw new BusinessException(
                String.format("Alocação negada: O membro '%s' já está alocado em %d projetos ativos (limite de 3).",
                    membro.getNome(), projetosAtivos)
            );
        }
    }




}