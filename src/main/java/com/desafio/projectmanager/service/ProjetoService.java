package com.desafio.projectmanager.service;

import org.springframework.stereotype.Service;

import com.desafio.projectmanager.repository.ProjetoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjetoService {
    private final ProjetoRepository projetoRepository;

}
