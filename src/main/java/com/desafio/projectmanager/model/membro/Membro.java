package com.desafio.projectmanager.model.membro;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.desafio.projectmanager.model.projeto.Projeto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Membro {

    private UUID id; 

    private String nome;

    private Atribuicao atribuicao;

    private Set<Projeto> projetos = new HashSet<>();
}
