package com.desafio.projectmanager.service;


import org.springframework.stereotype.Service;

import com.desafio.projectmanager.mapper.MembroMapper;
import com.desafio.projectmanager.repository.MembroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembroService {

    private final MembroRepository membroRepository;
    private final MembroApiAdapter membroApiAdapter; 
    private final MembroMapper membroMapper;

}