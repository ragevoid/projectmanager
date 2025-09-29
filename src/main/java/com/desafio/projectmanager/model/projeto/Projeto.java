package com.desafio.projectmanager.model.projeto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.desafio.projectmanager.model.empresa.Empresa;
import com.desafio.projectmanager.model.membro.Membro;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
    private LocalDateTime dataInicio;

    @Column(nullable = false)
    private LocalDateTime dataFinalPrevisao;

    @Column
    private LocalDateTime dataFinalReal;

    @Column(nullable = false)
    private BigDecimal orcamento;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private StatusProjeto status = StatusProjeto.EM_ANALISE;

    @Column
    @Enumerated(EnumType.STRING)
    private ClassificacaoRisco classificacaoRisco;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "membro_id", nullable = false)
    private Membro gerente;

    @Column(nullable = false)
    @ManyToMany(mappedBy = "projetos")
    private Set<Membro> membros= new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    private Boolean deleted = false;

}
