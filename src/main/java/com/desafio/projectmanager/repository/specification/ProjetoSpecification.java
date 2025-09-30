package com.desafio.projectmanager.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import com.desafio.projectmanager.dto.request.ProjetoFiltroDTO;
import com.desafio.projectmanager.model.projeto.Projeto;

public class ProjetoSpecification {

    public static Specification<Projeto> filterBy(ProjetoFiltroDTO filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (filter.getNome() != null) {
                predicates.add(cb.equal(root.get("nome"), filter.getNome()));
            }

            if (filter.getOrcamentoMin() != null && filter.getOrcamentoMax() != null) {
                predicates.add(cb.between(root.get("orcamento"), filter.getOrcamentoMin(), filter.getOrcamentoMax()));
            } else if (filter.getOrcamentoMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("orcamento"), filter.getOrcamentoMin()));
            } else if (filter.getOrcamentoMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("orcamento"), filter.getOrcamentoMax()));
            }

            if (filter.getDataInicioPrimeira() != null && filter.getDataInicioSegunda() != null) {
                predicates.add(cb.between(root.get("dataInicio"), filter.getDataInicioPrimeira(),
                        filter.getDataInicioSegunda()));
            } else if (filter.getDataInicioPrimeira() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicio"), filter.getDataInicioPrimeira()));
            } else if (filter.getDataInicioSegunda() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataInicio"), filter.getDataInicioSegunda()));
            }

            if (filter.getDataFinalPrevisaoPrimeira() != null && filter.getDataFinalPrevisaoSegunda() != null) {
                predicates.add(cb.between(root.get("dataFinalPrevisao"), filter.getDataFinalPrevisaoPrimeira(),
                        filter.getDataFinalPrevisaoSegunda()));
            } else if (filter.getDataFinalPrevisaoPrimeira() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("dataFinalPrevisao"), filter.getDataFinalPrevisaoPrimeira()));
            } else if (filter.getDataFinalPrevisaoSegunda() != null) {
                predicates
                        .add(cb.lessThanOrEqualTo(root.get("dataFinalPrevisao"), filter.getDataFinalPrevisaoSegunda()));
            }

            if (filter.getDataFinalRealPrimeira() != null && filter.getDataFinalRealSegunda() != null) {
                predicates.add(cb.between(root.get("dataFinalReal"), filter.getDataFinalRealPrimeira(),
                        filter.getDataFinalRealSegunda()));
            } else if (filter.getDataFinalRealPrimeira() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("dataFinalReal"), filter.getDataFinalRealPrimeira()));
            } else if (filter.getDataFinalRealSegunda() != null) {
                predicates
                        .add(cb.lessThanOrEqualTo(root.get("dataFinalReal"), filter.getDataFinalRealSegunda()));
            }

            if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {

                List<Predicate> statusPredicates = filter.getStatus().stream()
                        .map(status -> cb.equal(root.get("status"), status)).toList();

                Predicate statusOPredicate = cb.or(statusPredicates.toArray(new Predicate[0]));

                predicates.add(statusOPredicate);
            }

            if (filter.getGerenteId() != null) {
                predicates.add(cb.equal(root.get("gerente").get("id"), filter.getGerenteId()));
            }

            if (filter.getDescricao() != null) {
                predicates.add(cb.equal(root.get("descricao"), filter.getDescricao()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
