package org.innowise.internship.orderservice.repositories;

import org.innowise.internship.orderservice.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByIdIn(List<Long> ids);
    List<Order> findByStatusIn(List<String> statuses);
}
