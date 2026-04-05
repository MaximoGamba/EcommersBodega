package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Shipment;

@Repository // Indica que la clase es un repositorio
public interface ShipmentRepository extends JpaRepository<Shipment, Long> { // Extiende de JpaRepository para que tenga
                                                                            // los métodos de CRUD

    Optional<Shipment> findByOrder_Id(Long orderId); // Busca un envío por el id del pedido
}
