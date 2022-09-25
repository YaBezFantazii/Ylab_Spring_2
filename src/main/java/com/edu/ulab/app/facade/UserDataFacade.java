package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;

import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.web.request.create.UserBookRequest;
import com.edu.ulab.app.web.request.update.UserBookRequestUpdate;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserServiceImplTemplate userService;
    private final BookServiceImplTemplate bookService;
    //private final UserServiceImpl userService;
    //private final BookServiceImpl bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserServiceImplTemplate userService,
                          BookServiceImplTemplate bookService,
                          //BookServiceImpl bookService,
                          //UserServiceImpl userService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

//        List<Long> bookIdList = userBookRequest.getBookRequests()
//                .stream()
//                .filter(Objects::nonNull)
//                .map(bookMapper::bookRequestToBookDto)
//                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
//                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
//                .map(bookService::createBook)
//                .peek(createdBook -> log.info("Created book: {}", createdBook))
//                .map(BookDto::getId)
//                .toList();

        List<BookDto> bookList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .toList();

//        log.info("Collected book ids: {}", bookIdList);
        log.info("Collected book : {}", bookList);

        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .fullName(createdUser.getFullName())
                .title(createdUser.getTitle())
                .age(createdUser.getAge())
//                .booksIdList(bookIdList)
                .bookList(bookList)
                .build();
    }

    public UserBookResponse updateUserWithBooks(UserBookRequestUpdate userBookRequestUpdate) {
        log.info("Got user book create request: {}", userBookRequestUpdate);
        UserDto userDto = userMapper.userRequestUpdateToUserDto(userBookRequestUpdate.getUserRequest());
        log.info("Mapped user request: {}", userDto);
        UserDto updatedUser = userService.updateUser(userDto);
        log.info("Updated user: {}", updatedUser);
        List<BookDto> bookList = userBookRequestUpdate.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestUpdateToBookDto)
                .peek(bookDto -> bookDto.setUserId(updatedUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::updateBook)
                .filter(Objects::nonNull)
                .peek(mappedBookDto -> log.info("updated book (list): {}", mappedBookDto))
                .toList();

        return UserBookResponse.builder()
                .userId(updatedUser.getId())
                .fullName(updatedUser.getFullName())
                .title(updatedUser.getTitle())
                .age(updatedUser.getAge())
                .bookList(bookList)
                .build();
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        log.info("Got user id: {}", userId);
        UserDto getUser = userService.getUserById(userId);
        log.info("Get user: {}", getUser);
        List<BookDto> bookList = bookService.getBookById(userId)
                .stream()
                .filter(Objects::nonNull)
                .peek(getBook -> log.info("Get book: {}", getBook))
                .toList();

        return UserBookResponse.builder()
                .userId(getUser.getId())
                .fullName(getUser.getFullName())
                .title(getUser.getTitle())
                .age(getUser.getAge())
                .bookList(bookList)
                .build();
    }

    public void deleteUserWithBooks(Long userId) {
        userService.deleteUserById(userId);
        log.info("Delete user id: {}", userId);
        bookService.deleteBookById(userId);
        log.info("Delete books by user id: {}", userId);
    }
}
