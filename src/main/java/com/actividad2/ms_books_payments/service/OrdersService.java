package com.actividad2.ms_books_payments.service;

import com.actividad2.ms_books_payments.data.model.Order;
import com.actividad2.ms_books_payments.controller.model.OrderRequest;
import java.util.List;

public interface OrdersService {
	
	Order createOrder(OrderRequest request);

	Order getOrder(String id);

	List<Order> getOrders();

}
