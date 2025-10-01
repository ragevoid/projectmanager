package com.desafio.projectmanager.model.membro;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.desafio.projectmanager.model.projeto.Projeto;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Membro {

    @Column()
    private UUID id; 

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column()
    private Atribuicao atribuicao;

    @ManyToMany(mappedBy = "membros")
    private Set<Projeto> projetos = new HashSet<>();
}
