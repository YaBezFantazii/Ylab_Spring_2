package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.BadRequestExceptionUpdate;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.mapper.BookMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;
    private final BookMapper bookMapper;

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate, BookMapper bookMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookMapper = bookMapper;
    }
    private boolean bookIsEmpty (BookDto bookDto){
        return  bookDto.getTitle() == null && bookDto.getAuthor() == null && bookDto.getPageCount() == 0;
    }

    private Book mapRowToBook(ResultSet resultSet, int rowNum) throws SQLException {
        Book book = new Book();
        book.setId(resultSet.getLong("ID"));
        book.setTitle(resultSet.getString("TITLE"));
        book.setAuthor(resultSet.getString("AUTHOR"));
        book.setPageCount(resultSet.getLong("PAGE_COUNT"));
        book.setUserId(resultSet.getLong("USER_ID"));
        return book;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        if (Objects.isNull(bookDto)){throw new NotFoundException("bookDto is null");}
        if (bookIsEmpty(bookDto)) {return null;}
        final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    ps.setLong(4, bookDto.getUserId());
                    return ps;
                },
                keyHolder);
        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Saved book: {}", bookDto);
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        if (Objects.isNull(bookDto) || Objects.isNull(bookDto.getId())){
            throw new BadRequestExceptionUpdate(bookDto);
        }
        // Если книга не имеет id,
        if (bookDto.getId()==0) {
            //, и она пустая, то ничего не делаем
            if (bookIsEmpty(bookDto)) {
                log.info("Don't update/saved/delete book, book is empty and not id: {}", bookDto);
                return null;
            }
            //, и она не пустая, создаем новую книгу
            return createBook(bookDto);
        }

        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);

        // Если книга пустая и имеет id, пытаемся удалить книгу из бд
        if (bookIsEmpty(bookDto)){
            final String DELETE_SQL = "DELETE FROM BOOK WHERE ID=? AND USER_ID=?";
            int check = jdbcTemplate.update(DELETE_SQL,
                    book.getId(),
                    book.getUserId());
            if (check==0){
                log.info("Book don't delete, not found: {}", book);
                return null;
            }
            log.info("Book delete: {}", book);
            return null;
        }

        // Обновляем книгу
        final String UPDATE_SQL = "UPDATE BOOK SET TITLE=?, AUTHOR=?, PAGE_COUNT=?, USER_ID=? WHERE ID=? AND USER_ID=?";
        int check = jdbcTemplate.update(UPDATE_SQL,
                book.getTitle(),
                book.getAuthor(),
                book.getPageCount(),
                book.getUserId(),
                book.getId(),
                book.getUserId());
        // Проверка, изменили ли мы книгу в бд
        if (check==0){
            log.info("Book don't update, not found: {}", book);
            return null;
        }
        log.info("Updated book: {}", book);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public List<BookDto> getBookById(Long id) {
        if (Objects.isNull(id)){throw new NotFoundException("id is null");}
        final String GET_SQL = "SELECT * FROM BOOK WHERE USER_ID=?";
        List<Book> listBook = jdbcTemplate.query(
                GET_SQL,
                this::mapRowToBook,
                id);
        return listBook.stream().map(bookMapper::bookToBookDto).toList();
    }

    @Override
    public void deleteBookById(Long id) {
        if (Objects.isNull(id)){throw new NotFoundException("id is null");}
        final String DELETE_SQL = "DELETE FROM BOOK WHERE USER_ID=?";
        jdbcTemplate.update(DELETE_SQL,id);
    }
}
