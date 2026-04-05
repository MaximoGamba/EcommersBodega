package com.uade.tpo.demo.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils; // Utilidad para manejar strings

import com.uade.tpo.demo.entity.Wine;

import jakarta.persistence.criteria.Predicate; // Predicado para filtrar los vinos

public final class WineSpecification { // Clase para filtrar los vinos

    private WineSpecification() { // Constructor privado para evitar la instanciación de la clase
    }

    public static Specification<Wine> withFilters(String name, BigDecimal minPrice, BigDecimal maxPrice, Integer year, // Método
                                                                                                                       // para
                                                                                                                       // filtrar
                                                                                                                       // los
                                                                                                                       // vinos
            Long colorId, Long cepaId, Long azucarId, Long crianzaId, Long elaboracionId, Long medidaId) { // Método
                                                                                                           // para
                                                                                                           // filtrar
                                                                                                           // los vinos
        return (root, query, cb) -> { // Método para filtrar los vinos
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(name)) { // Si el nombre no es nulo, se filtra por nombre
                predicates.add(cb.like(cb.lower(root.get("name")), // Se filtra por nombre
                        "%" + name.toLowerCase(Locale.ROOT) + "%")); // Se filtra por nombre
            }
            if (minPrice != null) { // Si el precio mínimo no es nulo, se filtra por precio mínimo
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) { // Si el precio máximo no es nulo, se filtra por precio máximo
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice)); // Se filtra por precio máximo
            }
            if (year != null) { // Si el año no es nulo, se filtra por año
                predicates.add(cb.equal(root.get("year"), year)); // Se filtra por año
            }
            if (colorId != null) { // Si el id del color no es nulo, se filtra por id del color
                predicates.add(cb.equal(root.get("color").get("id"), colorId)); // Se filtra por id del color
            }
            if (cepaId != null) { // Si el id de la cepa no es nulo, se filtra por id de la cepa
                predicates.add(cb.equal(root.get("cepa").get("id"), cepaId)); // Se filtra por id de la cepa
            }
            if (azucarId != null) { // Si el id del azúcar no es nulo, se filtra por id del azúcar
                predicates.add(cb.equal(root.get("azucar").get("id"), azucarId)); // Se filtra por id del azúcar
            }
            if (crianzaId != null) { // Si el id de la crianza no es nulo, se filtra por id de la crianza
                predicates.add(cb.equal(root.get("crianza").get("id"), crianzaId)); // Se filtra por id de la crianza
            }
            if (elaboracionId != null) { // Si el id de la elaboración no es nulo, se filtra por id de la elaboración
                predicates.add(cb.equal(root.get("elaboracion").get("id"), elaboracionId)); // Se filtra por id de la
                                                                                            // elaboración
            }
            if (medidaId != null) { // Si el id de la medida no es nulo, se filtra por id de la medida
                predicates.add(cb.equal(root.get("medida").get("id"), medidaId)); // Se filtra por id de la medida
            }
            // Catálogo: no mostrar productos sin stock
            predicates.add(cb.greaterThan(root.get("stock"), 0));
            return cb.and(predicates.toArray(Predicate[]::new)); // Se filtra por todas las condiciones
        };
    } // Método para filtrar los vinos
}
