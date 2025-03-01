package com.actividad2.ms_books_payments.facade;

import com.actividad2.ms_books_payments.controller.model.BookRequest;
import com.actividad2.ms_books_payments.controller.model.OrderRequest;
import com.actividad2.ms_books_payments.data.model.Order;
import com.actividad2.ms_books_payments.facade.model.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class BooksFacade {

  @Value("${getBook.url}")
  private String getBookUrl;

  @Value("${postBook.url}")
  private String postBookUrl;

  private final RestTemplate restTemplate;

  public Book getBook(String id) {

    try {
      String url = String.format(getBookUrl, id);
      log.info("Getting product with ID {}. Request to {}", id, url);
      return restTemplate.getForObject(url, Book.class);
    } catch (HttpClientErrorException e) {
      log.error("Client Error: {}, Product with ID {}", e.getStatusCode(), id);
      return null;
    } catch (HttpServerErrorException e) {
      log.error("Server Error: {}, Product with ID {}", e.getStatusCode(), id);
      return null;
    } catch (Exception e) {
      log.error("Error: {}, Product with ID {}", e.getMessage(), id);
      return null;
    }
  }

  public boolean isStockAvailable(String id, int quantity) {
    Book book = getBook(id);
    log.error("Libro a consultar.", book);
    if (book == null) {
      log.error("El libro con ID {} no existe.", id);
      return false;
    }
    return book.getStock() >= quantity && book.isVisible();
  }

  public boolean updateStock(OrderRequest request) {
    List<Map<String, Object>> requestBody = new ArrayList<>();
    for (BookRequest bookRequest : request.getOrders()) {
        Map<String, Object> book = new HashMap<>();
        book.put("bookId", bookRequest.getBookId());
        book.put("quantity", bookRequest.getQuantity());
        requestBody.add(book);
    }

    try {
      log.info("Actualizando stock para el libro con ID {} en {}", requestBody, postBookUrl);
      ResponseEntity<String> response = restTemplate.postForEntity(postBookUrl, requestBody, String.class);
      log.info("Response: {}", response);
      if (response.getStatusCode().is2xxSuccessful()) {
          log.info("Stock actualizado correctamente para el libro con ID ");
          return true;
      } else {
          log.error("Error al actualizar stock del libro con ID : {}", response.getBody());
          return false;
      }
    } catch (HttpClientErrorException e) {
        if (e.getStatusCode().is4xxClientError() && e.getResponseBodyAsString().contains("Stock insuficiente para el libro")) {
            log.error("Stock insuficiente para el libro con ID " );
        } else {
            log.error("Error de cliente al actualizar stock del libro con ID: {}", e.getResponseBodyAsString());
        }
        return false;
    } catch (HttpServerErrorException e) {
        log.error("Error del servidor al actualizar stock del libro con ID : {}", e.getStatusCode());
        return false;
    } catch (Exception e) {
        log.error("Error inesperado al actualizar stock del libro con ID : {}", e.getMessage());
        return false;
    }

}


}