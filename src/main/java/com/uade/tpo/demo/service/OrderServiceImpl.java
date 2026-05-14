package com.uade.tpo.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.OrderItem;
import com.uade.tpo.demo.entity.OrderStatus;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.entity.Wine;
import com.uade.tpo.demo.exceptions.BadRequestException;
import com.uade.tpo.demo.exceptions.ResourceNotFoundException;
import com.uade.tpo.demo.repository.OrderItemRepository;
import com.uade.tpo.demo.repository.OrderRepository;
import com.uade.tpo.demo.repository.UserRepository;
import com.uade.tpo.demo.repository.WineRepository;
import com.uade.tpo.demo.service.payload.OrderLineInput;
import com.uade.tpo.demo.service.payload.PedidoCreateInput;
import com.uade.tpo.demo.service.payload.PedidoUpdateInput;

@Service
public class OrderServiceImpl implements OrderService { 

    @Autowired
    private OrderRepository orderRepository; 

    @Autowired
    private OrderItemRepository orderItemRepository; 

    @Autowired
    private UserRepository userRepository; 

    @Autowired
    private WineRepository wineRepository; 

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrders() { 
        return orderRepository.findAll();
    } 

    @Override
    @Transactional(readOnly = true) 
    public Order getOrderById(Long id) { 
        return orderRepository.findById(id) 
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + id)); 
    } 

    @Override
    @Transactional(readOnly = true) 
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUser_Id(userId);
    } 

    @Override
    @Transactional 
    public Order createOrder(Long actingUserId, boolean actingUserIsAdmin, PedidoCreateInput input) { 
        User owner = resolveOwner(actingUserId, actingUserIsAdmin,
                input == null ? null : input.getUserId()); 
        Order order = new Order(); 
        order.setUser(owner);
        order.setCreatedAt(LocalDateTime.now()); 
        order.setStatus(OrderStatus.CREATED); 
        order.setTotal(BigDecimal.ZERO);
        order.setItems(new ArrayList<>()); 
        order = orderRepository.save(order);
        if (input != null && input.getItems() != null) { 
            for (OrderLineInput line : input.getItems()) { 
                if (line.getWineId() == null || line.getQuantity() == null || line.getQuantity() <= 0) { 
                    throw new BadRequestException("Cada ítem requiere wineId y quantity > 0");
                }
                addOrMergeItem(order.getId(), line.getWineId(), line.getQuantity()); 
            }
            order = orderRepository.findById(order.getId()).orElseThrow(); 
        }
        return order; 
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, PedidoUpdateInput input) { 
        Order order = getOrderById(id); 
        if (input != null) {
            if (input.getStatus() != null) { 
                order.setStatus(input.getStatus()); 
            }
            if (input.getTotal() != null) { 
                order.setTotal(input.getTotal()); 
            }
        }
        return orderRepository.save(order); 
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) { 
        Order order = getOrderById(id); 
        if (order.getStatus() == OrderStatus.CANCELLED) { 
            return; 
        }
        restoreStockForOrder(order.getId()); 
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order); 
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItems(Long orderId) {
        getOrderById(orderId); 
        return orderItemRepository.findByOrder_Id(orderId); 
    } 

    @Override
    @Transactional
    public OrderItem addOrderItem(Long orderId, Long wineId, int quantity) { 
        if (quantity <= 0) { 
            throw new BadRequestException("La cantidad debe ser mayor a 0"); 
                                                                        
        }
        return addOrMergeItem(orderId, wineId, quantity); 
    } 

    @Override
    @Transactional
    public OrderItem updateOrderItem(Long orderId, Long itemId, int quantity) { 
        if (quantity <= 0) { 
            throw new BadRequestException("La cantidad debe ser mayor a 0"); 
                                                                             
        }
        Order order = getOrderById(orderId); 
        assertOrderEditable(order); 
        OrderItem item = orderItemRepository.findByOrder_IdAndId(orderId, itemId) 
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pedido no encontrado"));
        Wine wine = wineRepository.findById(item.getWine().getId()) 
                .orElseThrow(() -> new ResourceNotFoundException("Vino no encontrado"));
        int oldQty = item.getQuantity(); 
        int delta = quantity - oldQty; 
        if (delta > 0 && wine.getStock() < delta) { 
            throw new BadRequestException("Stock insuficiente para el vino: " + wine.getId());                                                                                        
        }
        wine.setStock(wine.getStock() - delta); 
        wineRepository.save(wine);
        item.setQuantity(quantity);
        item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP)); 
        OrderItem saved = orderItemRepository.save(item); 
        recalcOrderTotal(orderId); 
        return saved; 
    }

    @Override
    @Transactional
    public void removeOrderItem(Long orderId, Long itemId) { 
        Order order = getOrderById(orderId); 
        assertOrderEditable(order); 
        OrderItem item = orderItemRepository.findByOrder_IdAndId(orderId, itemId) 
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pedido no encontrado"));
        Wine wine = wineRepository.findById(item.getWine().getId()) 
                .orElseThrow(() -> new ResourceNotFoundException("Vino no encontrado"));
        wine.setStock(wine.getStock() + item.getQuantity()); 
        wineRepository.save(wine);
        orderItemRepository.delete(item); 
        recalcOrderTotal(orderId); 
    } 

    private OrderItem addOrMergeItem(Long orderId, Long wineId, int quantity) { 
        Order order = getOrderById(orderId);
        assertOrderEditable(order); 
        Wine wine = wineRepository.findById(wineId) 
                .orElseThrow(() -> new ResourceNotFoundException("Vino no encontrado: " + wineId));
        List<OrderItem> existing = orderItemRepository.findByOrder_Id(orderId); 
        Optional<OrderItem> sameWine = existing.stream()
                .filter(i -> i.getWine().getId().equals(wineId))
                .findFirst();
        if (sameWine.isPresent()) {
            return mergeExistingLine(sameWine.get(), quantity, orderId);
        }
        if (wine.getStock() < quantity) {
            throw new BadRequestException("Stock insuficiente para el vino: " + wineId);
        }
        BigDecimal unit = effectiveUnitPrice(wine);
        BigDecimal subtotal = unit.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
        wine.setStock(wine.getStock() - quantity);
        wineRepository.save(wine);
        OrderItem item = OrderItem.builder()
                .order(order)
                .wine(wine)
                .quantity(quantity)
                .unitPrice(unit)
                .subtotal(subtotal)
                .build();
        if (order.getItems() == null) {
            order.setItems(new ArrayList<>());
        }
        order.getItems().add(item);
        OrderItem saved = orderItemRepository.save(item);
        recalcOrderTotal(orderId);
        return saved;
    }

    private OrderItem mergeExistingLine(OrderItem item, int additionalQty, Long orderId) { 
                                                                                           
        Wine wine = wineRepository.findById(item.getWine().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vino no encontrado"));
        if (wine.getStock() < additionalQty) {
            throw new BadRequestException("Stock insuficiente para el vino: " + wine.getId());
        }
        wine.setStock(wine.getStock() - additionalQty);
        wineRepository.save(wine);
        int newQty = item.getQuantity() + additionalQty;
        item.setQuantity(newQty);
        item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(newQty))
                .setScale(2, RoundingMode.HALF_UP));
        OrderItem saved = orderItemRepository.save(item);
        recalcOrderTotal(orderId);
        return saved;
    }

    private void assertOrderEditable(Order order) { 
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BadRequestException("Solo se pueden modificar ítems de pedidos en estado CREATED"); 
                                                                                                          
        }
    } 

    private void restoreStockForOrder(Long orderId) { 
        List<OrderItem> items = orderItemRepository.findByOrder_Id(orderId); 
        for (OrderItem item : items) { 
            Wine wine = wineRepository.findById(item.getWine().getId()) 
                    .orElseThrow(() -> new ResourceNotFoundException("Vino no encontrado"));
            wine.setStock(wine.getStock() + item.getQuantity()); 
            wineRepository.save(wine);
        }
    } 

    private void recalcOrderTotal(Long orderId) { 
        Order order = getOrderById(orderId); 
        List<OrderItem> items = orderItemRepository.findByOrder_Id(orderId); 
        BigDecimal total = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);
        orderRepository.save(order);
    }

    private User resolveOwner(Long actingUserId, boolean actingUserIsAdmin, Long requestedUserId) {
        if (requestedUserId != null) {
            if (!actingUserIsAdmin && !actingUserId.equals(requestedUserId)) {
                throw new BadRequestException("No autorizado a crear pedidos para otro usuario");
            }
            return userRepository.findById(requestedUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + requestedUserId));
        }
        return userRepository.findById(actingUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + actingUserId));
    }

    private static BigDecimal effectiveUnitPrice(Wine wine) {
        if (wine.getDiscountPercent() == null || wine.getDiscountPercent().signum() == 0) {
            return wine.getPrice().setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal factor = BigDecimal.ONE.subtract(
                wine.getDiscountPercent().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return wine.getPrice().multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
