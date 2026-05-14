package com.uade.tpo.demo.service;

import java.util.List;

import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.OrderItem;
import com.uade.tpo.demo.service.payload.PedidoCreateInput;
import com.uade.tpo.demo.service.payload.PedidoUpdateInput;

public interface OrderService { 

    List<Order> getOrders(); 

    Order getOrderById(Long id); 

    List<Order> getOrdersByUserId(Long userId); 

    Order createOrder(Long actingUserId, boolean actingUserIsAdmin, PedidoCreateInput input); 

    Order updateOrder(Long id, PedidoUpdateInput input); 

    void cancelOrder(Long id); 

    List<OrderItem> getOrderItems(Long orderId); 

    OrderItem addOrderItem(Long orderId, Long wineId, int quantity); 

    OrderItem updateOrderItem(Long orderId, Long itemId, int quantity); 

    void removeOrderItem(Long orderId, Long itemId); 
}
