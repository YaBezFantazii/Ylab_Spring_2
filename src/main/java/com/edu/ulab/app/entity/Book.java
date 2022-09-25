package com.edu.ulab.app.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Size(max = 255, message = "[book.title max length = 255]")
    private String title;
    @Size (max = 255, message = "[book.author max length = 255]")
    private String author;
    private long pageCount;


}
