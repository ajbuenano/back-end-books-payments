package com.actividad2.ms_books_payments.facade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Book {
	private String id;
	private String title;
	private String image;
	private String description;
	private String year;
	private String isbn;
	private String author;
	private String category;
    private double price;
	private int score;
    private int stock;
	private boolean visible;
}
