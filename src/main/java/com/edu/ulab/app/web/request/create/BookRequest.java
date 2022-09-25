package com.edu.ulab.app.web.request.create;

import lombok.Data;

import javax.validation.constraints.Size;
@Data
public class BookRequest {
    @Size (max = 255, message = "[BookRequest.title max length = 255]")
    private String title;
    @Size (max = 255, message = "[BookRequest.author max length = 255]")
    private String author;
    private long pageCount;

}
