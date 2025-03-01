package com.actividad2.ms_books_payments.data;

import com.actividad2.ms_books_payments.data.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}

