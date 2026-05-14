package com.uade.tpo.demo.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils; 

import com.uade.tpo.demo.entity.Wine;

import jakarta.persistence.criteria.Predicate; 

public final class WineSpecification { // Clase para filtrar los vinos

    private WineSpecification() { 
    }

    public static Specification<Wine> withFilters(String name, BigDecimal minPrice, BigDecimal maxPrice, Integer year,
            Long colorId, Long cepaId, Long azucarId, Long crianzaId, Long elaboracionId, Long medidaId,
            boolean includeInactive) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + name.toLowerCase(Locale.ROOT) + "%"));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (year != null) {
                predicates.add(cb.equal(root.get("year"), year));
            }
            if (colorId != null) {
                predicates.add(cb.equal(root.get("color").get("id"), colorId));
            }
            if (cepaId != null) {
                predicates.add(cb.equal(root.get("cepa").get("id"), cepaId));
            }
            if (azucarId != null) {
                predicates.add(cb.equal(root.get("azucar").get("id"), azucarId));
            }
            if (crianzaId != null) {
                predicates.add(cb.equal(root.get("crianza").get("id"), crianzaId));
            }
            if (elaboracionId != null) {
                predicates.add(cb.equal(root.get("elaboracion").get("id"), elaboracionId));
            }
            if (medidaId != null) {
                predicates.add(cb.equal(root.get("medida").get("id"), medidaId));
            }
            if (!includeInactive) {
                predicates.add(cb.isTrue(root.get("active")));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
