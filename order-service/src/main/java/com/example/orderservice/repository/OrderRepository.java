package com.example.orderservice.repository;

import com.example.orderservice.entity.OrderEntity;
import org.springframework.data.repository.CrudRepository;


public interface OrderRepository extends CrudRepository<OrderEntity, Long> {
    OrderEntity findByProductId(String productId);
    Iterable<OrderEntity> findByUserId(String userId);
}
