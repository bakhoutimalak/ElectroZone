package com.formation.ecommerce.repository;

import com.formation.ecommerce.model.Order;
import com.formation.ecommerce.model.Order.OrderStatus;
import com.formation.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    Optional<Order> findByUserAndStatus(User user, OrderStatus status);

    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.item " +
           "WHERE o.user = :user")
    List<Order> findByUserWithItems(@Param("user") User user);
}
