package com.desafio.projectmanager.model.projeto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Risco classificacaoRisco;

    @Column(name = "gerente_id")
    private UUID gerenteId;

    @ElementCollection
    @Column(name = "membro_id")
    private Set<UUID> membrosIds = new HashSet<>();

    @Column(nullable = false)
    private Boolean deleted = false;

}
