package org.innowise.internship.orderservice.repositories;

import org.innowise.internship.orderservice.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    public List<Item> findAllByIdIn(List<Long> itemIds);
}
