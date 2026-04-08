package com.uade.tpo.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Genera los getters y setters
@Builder // Genera el builder para la clase
@NoArgsConstructor
@AllArgsConstructor // Genera el constructor con todos los argumentos
@Entity // Indica que la clase es una entidad
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "cepas")
public class Cepa {
    @Id // Indica que el campo es la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Genera el id de la entidad

    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;
}
