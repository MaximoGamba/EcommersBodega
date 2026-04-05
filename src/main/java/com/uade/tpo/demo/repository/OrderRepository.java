package com.uade.tpo.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Order;

@Repository // Indica que la clase es un repositorio
public interface OrderRepository extends JpaRepository<Order, Long> { // Extiende de JpaRepository para que tenga los
                                                                      // métodos de CRUD

    List<Order> findByUser_Id(Long userId); // Busca los pedidos de un usuario por el id del usuario
}
