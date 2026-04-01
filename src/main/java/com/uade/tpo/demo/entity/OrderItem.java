package com.uade.tpo.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Genera los getters y setters
@Builder // Genera el builder para la clase
@NoArgsConstructor // Genera el constructor sin argumentos
@AllArgsConstructor // Genera el constructor con todos los argumentos
@Entity // Indica que la clase es una entidad
@Table(name = "order_items")

public class OrderItem {
    @Id // Indica que el campo es la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Genera el id de la entidad

    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Indica que la relación es many to one
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // Referencia a la orden

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Indica que la relación es many to one
    @JoinColumn(name = "wine_id", nullable = false)
    private Wine wine; // Referencia al vino

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}
