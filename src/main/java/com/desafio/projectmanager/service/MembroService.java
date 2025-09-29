package com.desafio.projectmanager.service;

import org.springframework.stereotype.Service;

import com.desafio.projectmanager.repository.MembroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembroService {
    private final MembroRepository membroRepository;

}
