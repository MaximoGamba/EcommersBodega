package com.uade.tpo.demo.service;

import java.util.List;

import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.OrderItem;
import com.uade.tpo.demo.service.payload.PedidoCreateInput;
import com.uade.tpo.demo.service.payload.PedidoUpdateInput;

public interface OrderService { // Servicio para pedidos

    List<Order> getOrders(); // Método para obtener todos los pedidos

    Order getOrderById(Long id); // Método para obtener un pedido por su id

    List<Order> getOrdersByUserId(Long userId); // Método para obtener todos los pedidos de un usuario por su id

    Order createOrder(Long actingUserId, boolean actingUserIsAdmin, PedidoCreateInput input); // Método para crear un
                                                                                              // pedido

    Order updateOrder(Long id, PedidoUpdateInput input); // Método para actualizar un pedido

    void cancelOrder(Long id); // Método para cancelar un pedido

    List<OrderItem> getOrderItems(Long orderId); // Método para obtener los items de un pedido por su id

    OrderItem addOrderItem(Long orderId, Long wineId, int quantity); // Método para agregar un item a un pedido

    OrderItem updateOrderItem(Long orderId, Long itemId, int quantity); // Método para actualizar un item de un pedido

    void removeOrderItem(Long orderId, Long itemId); // Método para eliminar un item de un pedido
}
