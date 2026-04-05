package com.uade.tpo.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.OrderItem;

@Repository // Indica que la clase es un repositorio
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> { // Extiende de JpaRepository para que
                                                                              // tenga los métodos de CRUD

    List<OrderItem> findByOrder_Id(Long orderId); // Busca los items de un pedido por el id del pedido

    Optional<OrderItem> findByOrder_IdAndId(Long orderId, Long itemId); // Busca un item de un pedido por el id del
                                                                        // pedido y el id del item
}
