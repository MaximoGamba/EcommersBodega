package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Payment;

@Repository // Indica que la clase es un repositorio
public interface PaymentRepository extends JpaRepository<Payment, Long> { // Extiende de JpaRepository para que tenga
                                                                          // los métodos de CRUD

    Optional<Payment> findByOrder_Id(Long orderId); // Busca un pago por el id del pedido
}
