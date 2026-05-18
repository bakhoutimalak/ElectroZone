package com.formation.ecommerce.service;

import com.formation.ecommerce.model.*;
import com.formation.ecommerce.model.Order.OrderStatus;
import com.formation.ecommerce.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ItemRepository itemRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public Order getOrCreateCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        return orderRepository.findByUserAndStatus(user, OrderStatus.CART)
                .orElseGet(() -> orderRepository.save(new Order(user)));
    }

    public Order addToCart(String username, Long itemId, int quantity) {
        Order cart = getOrCreateCart(username);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable"));
        if (item.getQuantityInStock() < quantity) {
            throw new IllegalStateException("Stock insuffisant : " + item.getQuantityInStock() + " disponible(s)");
        }
        Optional<OrderItem> existing = orderItemRepository.findByOrderAndItemId(cart, itemId);
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
            orderItemRepository.save(existing.get());
        } else {
            cart.addItem(new OrderItem(cart, item, quantity));
        }
        return orderRepository.save(cart);
    }

    public Order updateCartItem(String username, Long orderItemId, int quantity) {
        Order cart = getOrCreateCart(username);
        OrderItem oi = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Ligne introuvable"));
        if (quantity <= 0) {
            cart.removeItem(oi);
        } else {
            oi.setQuantity(quantity);
            orderItemRepository.save(oi);
        }
        return orderRepository.save(cart);
    }

    public Order removeFromCart(String username, Long orderItemId) {
        return updateCartItem(username, orderItemId, 0);
    }

    public Order confirmCart(String username) {
        Order cart = getOrCreateCart(username);
        if (cart.getOrderItems().isEmpty()) {
            throw new IllegalStateException("Le panier est vide");
        }
        for (OrderItem oi : cart.getOrderItems()) {
            Item item = oi.getItem();
            if (item.getQuantityInStock() < oi.getQuantity()) {
                throw new IllegalStateException("Stock insuffisant pour : " + item.getName());
            }
            item.setQuantityInStock(item.getQuantityInStock() - oi.getQuantity());
            itemRepository.save(item);
        }
        cart.setStatus(OrderStatus.CONFIRMED);
        return orderRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public List<Order> findAllByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        return orderRepository.findByUserWithItems(user);
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}
