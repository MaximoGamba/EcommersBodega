package com.uade.tpo.demo.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
@Entity 
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "wines")
public class Wine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 120)
    private String winery;

    @Column
    private Integer year;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cepa_id", nullable = false)
    private Cepa cepa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "azucar_id", nullable = false)
    private Azucar azucar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "crianza_id", nullable = false)
    private Crianza crianza;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "elaboracion_id", nullable = false)
    private Elaboracion elaboracion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medida_id", nullable = false)
    private Medida medida;

    
    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercent;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Column(length = 500)
    private String imageUrl;
}
