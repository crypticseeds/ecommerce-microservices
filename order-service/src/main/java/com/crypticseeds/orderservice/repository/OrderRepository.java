package com.crypticseeds.orderservice.repository;

import com.crypticseeds.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
