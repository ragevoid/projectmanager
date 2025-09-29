package com.desafio.projectmanager.model.membro;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.desafio.projectmanager.model.empresa.Empresa;
import com.desafio.projectmanager.model.projeto.Projeto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String password;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Atribuicao atribuicao;

    @ManyToMany
    @JsonBackReference
    @JoinTable(
        name = "membro_projeto",
        joinColumns = @JoinColumn(name = "membro_id"),
        inverseJoinColumns = @JoinColumn(name = "projeto_id")
    )
    private Set<Projeto> projetos= new HashSet<>();

    @Column(nullable = false)
    private Boolean deleted = false;

}
