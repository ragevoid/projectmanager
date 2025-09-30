package com.desafio.projectmanager.model.empresa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.desafio.projectmanager.model.membro.Membro;
import com.desafio.projectmanager.model.projeto.Projeto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "empresa") 
@NoArgsConstructor
@Getter
@Setter
public class Empresa {
@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @OneToMany(mappedBy = "empresa", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})

    private Set<Membro> membros = new HashSet<>();

    @OneToMany(mappedBy = "empresa", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})

    private Set<Projeto> projetos = new HashSet<>();

    @Column(nullable = false)
    private Boolean deleted = false;
}
