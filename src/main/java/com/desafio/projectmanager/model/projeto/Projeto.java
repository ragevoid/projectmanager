package com.desafio.projectmanager.model.projeto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.desafio.projectmanager.model.membro.Membro;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "projeto") 
@NoArgsConstructor
@Getter
@Setter
public class Projeto {
   @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataFinalPrevisao;

    @Column
    private LocalDate dataFinalReal;

    @Column(nullable = false)
    private BigDecimal orcamento;

    @Column(nullable = false, length = 5000) 
    private String descricao;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) 
    private StatusProjeto status = StatusProjeto.EM_ANALISE;

    @Transient
    private Risco classificacaoRisco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gerente_id", nullable = false) 
    private Membro gerente;

    @ManyToMany(cascade=CascadeType.PERSIST )
    @JoinTable(
        name = "projeto_membros",
        joinColumns = @JoinColumn(name = "projeto_id"),
        inverseJoinColumns = @JoinColumn(name = "membro_id")
    )
    private Set<Membro> membros = new HashSet<>();

    @Column(nullable = false)
    private Boolean deleted = false;

}
