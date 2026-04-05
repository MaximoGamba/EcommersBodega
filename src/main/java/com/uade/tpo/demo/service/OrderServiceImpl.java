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
public class OrderServiceImpl implements OrderService { // Implementación del servicio para pedidos

    @Autowired
    private OrderRepository orderRepository; // Repositorio para pedidos

    @Autowired
    private OrderItemRepository orderItemRepository; // Repositorio para items de pedidos

    @Autowired
    private UserRepository userRepository; // Repositorio para usuarios

    @Autowired
    private WineRepository wineRepository; // Repositorio para vinos

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrders() { // Método para obtener todos los pedidos
        return orderRepository.findAll();
    } // Método para obtener todos los pedidos

    @Override
    @Transactional(readOnly = true) // Método para obtener un pedido por su id
    public Order getOrderById(Long id) { // Método para obtener un pedido por su id
        return orderRepository.findById(id) // Busca un pedido por su id
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + id)); // Lanza una excepción
                                                                                                  // si el pedido no
                                                                                                  // existe
    } // Método para obtener un pedido por su id

    @Override
    @Transactional(readOnly = true) // Método para obtener todos los pedidos de un usuario por su id
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUser_Id(userId);
    } // Método para obtener todos los pedidos de un usuario por su id

    @Override
    @Transactional // Método para crear un pedido
    public Order createOrder(Long actingUserId, boolean actingUserIsAdmin, PedidoCreateInput input) { // Método para
                                                                                                      // crear un pedido
        User owner = resolveOwner(actingUserId, actingUserIsAdmin,
                input == null ? null : input.getUserId()); // Resuelve el owner del pedido
        Order order = new Order(); // Crea un nuevo pedido
        order.setUser(owner);
        order.setCreatedAt(LocalDateTime.now()); // Establece la fecha de creación del pedido
        order.setStatus(OrderStatus.CREATED); // Establece el estado del pedido
        order.setTotal(BigDecimal.ZERO);
        order.setItems(new ArrayList<>()); // Establece los items del pedido
        order = orderRepository.save(order);
        if (input != null && input.getItems() != null) { // Si hay items en el pedido
            for (OrderLineInput line : input.getItems()) { // Para cada item en el pedido
                if (line.getWineId() == null || line.getQuantity() == null || line.getQuantity() <= 0) { // Si el wineId
                                                                                                         // o la
                                                                                                         // cantidad es
                                                                                                         // nula o menor
                                                                                                         // a 0
                    throw new BadRequestException("Cada ítem requiere wineId y quantity > 0");
                }
                addOrMergeItem(order.getId(), line.getWineId(), line.getQuantity()); // Agrega o mergea el item al
                                                                                     // pedido
            }
            order = orderRepository.findById(order.getId()).orElseThrow(); // Busca un pedido por su id
        }
        return order; // Retorna el pedido creado
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, PedidoUpdateInput input) { // Método para actualizar un pedido
        Order order = getOrderById(id); // Obtiene el pedido por su id
        if (input != null) {
            if (input.getStatus() != null) { // Si el estado es nulo
                order.setStatus(input.getStatus()); // Establece el estado del pedido
            }
            if (input.getTotal() != null) { // Si el total es nulo
                order.setTotal(input.getTotal()); // Establece el total del pedido
            }
        }
        return orderRepository.save(order); // Guarda el pedido
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) { // Método para cancelar un pedido
        Order order = getOrderById(id); // Obtiene el pedido por su id
        if (order.getStatus() == OrderStatus.CANCELLED) { // Si el estado del pedido es CANCELLED
            return; // Retorna
        }
        restoreStockForOrder(order.getId()); // Restaura el stock del pedido
        order.setStatus(OrderStatus.CANCELLED); // Establece el estado del pedido
        orderRepository.save(order); // Guarda el pedido
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItems(Long orderId) {
        getOrderById(orderId); // Obtiene el pedido por su id
        return orderItemRepository.findByOrder_Id(orderId); // Busca los items del pedido por su id
    } // Método para obtener los items de un pedido por su id

    @Override
    @Transactional
    public OrderItem addOrderItem(Long orderId, Long wineId, int quantity) { // Método para agregar un item a un pedido
        if (quantity <= 0) { // Si la cantidad es menor a 0
            throw new BadRequestException("La cantidad debe ser mayor a 0"); // Lanza una excepción si la cantidad es
                                                                             // menor a 0
        }
        return addOrMergeItem(orderId, wineId, quantity); // Agrega o mergea el item al pedido
    } // Método para agregar un item a un pedido

    @Override
    @Transactional
    public OrderItem updateOrderItem(Long orderId, Long itemId, int quantity) { // Método para actualizar un item de un
                                                                                // pedido
        if (quantity <= 0) { // Si la cantidad es menor a 0
            throw new BadRequestException("La cantidad debe ser mayor a 0"); // Lanza una excepción si la cantidad es
                                                                             // menor a 0
        }
        Order order = getOrderById(orderId); // Obtiene el pedido por su id
        assertOrderEditable(order); // Verifica si el pedido es editable
        OrderItem item = orderItemRepository.findByOrder_IdAndId(orderId, itemId) // Busca el item por su id
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pedido no encontrado"));
        Wine wine = wineRepository.findById(item.getWine().getId()) // Busca el vino por su id
                .orElseThrow(() -> new ResourceNotFoundException("Vino no encontrado"));
        int oldQty = item.getQuantity(); // Obtiene la cantidad del item
        int delta = quantity - oldQty; // Calcula la diferencia entre la cantidad nueva y la cantidad anterior
        if (delta > 0 && wine.getStock() < delta) { // Si la diferencia es mayor a 0 y el stock del vino es menor a la
                                                    // diferencia
            throw new BadRequestException("Stock insuficiente para el vino: " + wine.getId()); // Lanza una excepción si
                                                                                               // el stock del vino es
                                                                                               // menor a la diferencia
        }
        wine.setStock(wine.getStock() - delta); // Restaura el stock del vino
        wineRepository.save(wine);
        item.setQuantity(quantity);
        item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP)); // Establece el subtotal del item
        OrderItem saved = orderItemRepository.save(item); // Guarda el item
        recalcOrderTotal(orderId); // Recalcula el total del pedido
        return saved; // Retorna el item actualizado
    }

    @Override
    @Transactional
    public void removeOrderItem(Long orderId, Long itemId) { // Método para eliminar un item de un pedido
        Order order = getOrderById(orderId); // Obtiene el pedido por su id
        assertOrderEditable(order); // Verifica si el pedido es editable
        OrderItem item = orderItemRepository.findByOrder_IdAndId(orderId, itemId) // Busca el item por su id
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pedido no encontrado"));
        Wine wine = wineRepository.findById(item.getWine().getId()) // Busca el vino por su id
                .orElseThrow(() -> new ResourceNotFoundException("Vino no encontrado"));
        wine.setStock(wine.getStock() + item.getQuantity()); // Restaura el stock del vino
        wineRepository.save(wine);
        orderItemRepository.delete(item); // Elimina el item
        recalcOrderTotal(orderId); // Recalcula el total del pedido
    } // Método para eliminar un item de un pedido

    private OrderItem addOrMergeItem(Long orderId, Long wineId, int quantity) { // Método para agregar o mergear un item
                                                                                // a un pedido
        Order order = getOrderById(orderId);
        assertOrderEditable(order); // Verifica si el pedido es editable
        Wine wine = wineRepository.findById(wineId) // Busca el vino por su id
                .orElseThrow(() -> new ResourceNotFoundException("Vino no encontrado: " + wineId));
        List<OrderItem> existing = orderItemRepository.findByOrder_Id(orderId); // Busca los items del pedido por su id
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

    private OrderItem mergeExistingLine(OrderItem item, int additionalQty, Long orderId) { // Método para mergear un
                                                                                           // item existente a un pedido
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

    private void assertOrderEditable(Order order) { // Método para verificar si el pedido es editable
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BadRequestException("Solo se pueden modificar ítems de pedidos en estado CREATED"); // Lanza una
                                                                                                          // excepción
                                                                                                          // si el
                                                                                                          // pedido no
                                                                                                          // es editable
        }
    } // Método para verificar si el pedido es editable

    private void restoreStockForOrder(Long orderId) { // Método para restaurar el stock del pedido
        List<OrderItem> items = orderItemRepository.findByOrder_Id(orderId); // Busca los items del pedido por su id
        for (OrderItem item : items) { // Para cada item en el pedido
            Wine wine = wineRepository.findById(item.getWine().getId()) // Busca el vino por su id
                    .orElseThrow(() -> new ResourceNotFoundException("Vino no encontrado"));
            wine.setStock(wine.getStock() + item.getQuantity()); // Restaura el stock del vino
            wineRepository.save(wine); // Guarda el vino
        }
    } // Método para restaurar el stock del pedido

    private void recalcOrderTotal(Long orderId) { // Método para recalcular el total del pedido
        Order order = getOrderById(orderId); // Obtiene el pedido por su id
        List<OrderItem> items = orderItemRepository.findByOrder_Id(orderId); // Busca los items del pedido por su id
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
