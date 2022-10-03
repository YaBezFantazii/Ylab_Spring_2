package com.edu.ulab.app.web.request.create;

import lombok.Data;
import javax.validation.constraints.Size;
@Data
public class UserRequest {
    @Size(max = 255, message = "[UserRequest.fullname max length = 255]")
    private String fullName;
    @Size (max = 255, message = "[UserRequest.title max length = 255]")
    private String title;
    private int age;
}
