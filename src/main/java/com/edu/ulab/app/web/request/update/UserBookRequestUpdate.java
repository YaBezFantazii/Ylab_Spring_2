package com.edu.ulab.app.web.request.update;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class UserBookRequestUpdate {
    @Valid
    private UserRequestUpdate userRequest;
    @Valid
    private List<BookRequestUpdate> bookRequests;
}
