package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Shipment;

@Repository 
public interface ShipmentRepository extends JpaRepository<Shipment, Long> { 

    Optional<Shipment> findByOrder_Id(Long orderId); 
}
