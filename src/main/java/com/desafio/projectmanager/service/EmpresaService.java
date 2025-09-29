package com.desafio.projectmanager.service;

import org.springframework.stereotype.Service;

import com.desafio.projectmanager.repository.EmpresaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService {
    private final EmpresaRepository empresaRepository;

}
