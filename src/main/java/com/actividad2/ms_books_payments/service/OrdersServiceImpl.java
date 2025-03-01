package com.actividad2.ms_books_payments.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import com.actividad2.ms_books_payments.data.OrderJpaRepository;
import com.actividad2.ms_books_payments.data.model.Book;
import com.actividad2.ms_books_payments.data.model.Order;
import com.actividad2.ms_books_payments.facade.BooksFacade;

import org.springframework.stereotype.Service;
import com.actividad2.ms_books_payments.controller.model.BookRequest;
import com.actividad2.ms_books_payments.controller.model.OrderRequest;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdersServiceImpl implements OrdersService {

  private final OrderJpaRepository orderRepository;
  private final BooksFacade booksFacade;

  public OrdersServiceImpl(OrderJpaRepository orderRepository, BooksFacade booksFacade) {
      this.orderRepository = orderRepository;
      this.booksFacade = booksFacade;
  }

  @Override
  @Transactional
  public Order createOrder(OrderRequest request) {
    if (request.getOrders() == null || request.getOrders().isEmpty()) {
      throw new IllegalArgumentException("La orden no puede estar vacía.");
    }

    List<Book> booksWithPrices = new ArrayList<>();
    double totalPrice = 0.0;

    for (BookRequest bookRequest : request.getOrders()) {
      if (bookRequest.getPrice() <= 0 || bookRequest.getQuantity() <= 0) {
        throw new IllegalArgumentException("Precio y cantidad deben ser mayores a 0.");
      }

      if (!booksFacade.isStockAvailable(bookRequest.getBookId(), bookRequest.getQuantity())) {
        throw new IllegalArgumentException("Stock insuficiente o no visible para el libro con ID: " + bookRequest.getBookId());
      }

        Book book = new Book();
        book.setBookId(bookRequest.getBookId());
        book.setPrice(bookRequest.getPrice());
        book.setQuantity(bookRequest.getQuantity());
        booksWithPrices.add(book);
        totalPrice += book.getPrice() * book.getQuantity();
    }
    BigDecimal roundedTotal = new BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP);

    Order order = new Order();
    order.setBooks(booksWithPrices);
    order.setTotalPrice(roundedTotal.doubleValue());
    order.setStatus("CREATED");
    Order savedOrder = orderRepository.save(order);

    boolean stockUpdated = booksFacade.updateStock(request);

    if (!stockUpdated) {
        throw new IllegalArgumentException("No se pudo actualizar el stock para el libro con ID: " + request.getOrders().get(0).getBookId());
    }

    return savedOrder;
  }

  @Override
  public Order getOrder(String id) {
    return orderRepository.findById(Long.valueOf(id)).orElse(null);
  }

  @Override
  public List<Order> getOrders() {
    List<Order> orders = orderRepository.findAll();
    return orders.isEmpty() ? null : orders;
  }
}
