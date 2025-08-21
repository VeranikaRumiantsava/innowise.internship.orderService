package org.innowise.internship.orderservice.repositories;

import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.innowise.internship.orderservice.entities.Order;
import org.innowise.internship.orderservice.entities.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByIdInAndUserId(List<Long> ids, Long userId);

    List<Order> findByStatusAndUserId(OrderStatus status, Long userId);
}
