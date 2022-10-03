package com.edu.ulab.app.web.request.update;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class BookRequestUpdate {
    private long id;
    @Size (max = 255, message = "[BookRequestUpdate.title max length = 255]")
    private String title;
    @Size (max = 255, message = "[BookRequestUpdate.author max length = 255]")
    private String author;
    private long pageCount;
}
