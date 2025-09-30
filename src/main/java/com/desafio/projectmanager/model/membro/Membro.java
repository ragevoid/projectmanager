package com.desafio.projectmanager.model.membro;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.desafio.projectmanager.model.projeto.Projeto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "membro") 
@NoArgsConstructor
@Getter
@Setter
public class Membro {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; 

    @Column(unique = true, nullable = false)
    private UUID idMockado; 

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column()
    private Atribuicao atribuicao;

    @ManyToMany(mappedBy = "membros")
    private Set<Projeto> projetos = new HashSet<>();
}
