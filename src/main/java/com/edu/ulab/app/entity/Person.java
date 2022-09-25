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
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 255, message = "[person.fullname max length = 255]")
    private String fullName;
    @Size (max = 255, message = "[person.title max length = 255]")
    private String title;
    private int age;

}
