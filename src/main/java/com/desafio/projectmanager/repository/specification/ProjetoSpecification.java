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
             return cb.and(predicates.toArray(new Predicate[0]));
         };
     }
}
