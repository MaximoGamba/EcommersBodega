package com.uade.tpo.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.OrderItem;

@Repository 
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> { 

    List<OrderItem> findByOrder_Id(Long orderId); 

    Optional<OrderItem> findByOrder_IdAndId(Long orderId, Long itemId); 
}
