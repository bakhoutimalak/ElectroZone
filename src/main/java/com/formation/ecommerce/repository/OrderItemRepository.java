package com.formation.ecommerce.repository;

import com.formation.ecommerce.model.Order;
import com.formation.ecommerce.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
    Optional<OrderItem> findByOrderAndItemId(Order order, Long itemId);
}
