package com.edu.ulab.app.web.request.update;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UserRequestUpdate  {
    private long id;
    @Size(max = 255, message = "[UserRequestUpdate.fullname max length = 255]")
    private String fullName;
    @Size (max = 255, message = "[UserRequestUpdate.title max length = 255]")
    private String title;
    private int age;
}
