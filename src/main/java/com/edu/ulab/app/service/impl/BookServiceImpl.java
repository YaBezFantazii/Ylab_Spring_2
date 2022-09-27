package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.BadRequestExceptionUpdate;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    private boolean bookIsEmpty (BookDto bookDto){
        return bookDto.getTitle() == null && bookDto.getAuthor() == null && bookDto.getPageCount() == 0;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        if (Objects.isNull(bookDto)){throw new NotFoundException("bookDto is null");}
        if (bookIsEmpty(bookDto)) {return null;}
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        if (Objects.isNull(bookDto) || Objects.isNull(bookDto.getId())){
            throw new BadRequestExceptionUpdate(bookDto);}

        if (bookDto.getId()==0) {
            // Если книга пустая и не указан id, то ничего не делаем,
            if (bookIsEmpty(bookDto))  {
                log.info("Don't update/saved book, book is empty: {}", bookDto);
                return null;
            }
            // ,иначе (если не пуста) создаем новую книгу
            return createBook(bookDto);
        }

        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);

        // Ищем книгу с совпадающими id и userId в БД
        Book checkBook = bookRepository.findByIdAndUserId( book.getId(), book.getUserId() );
        if (Objects.nonNull(checkBook)) {
            // Удаляем книгу, если новая книга (bookDto) пустая
            if (bookIsEmpty(bookDto))  {
                bookRepository.deleteById(book.getId());
                log.info("Book delete: {}", book);
                return null;
            }
            // иначе обновляем книгу
            Book updatedBook = bookRepository.save(book);
            log.info("Updated book: {}", updatedBook);
            return bookMapper.bookToBookDto(updatedBook);
        } else {
            log.info("Book don't update, not found: {}", book);
            return null;
        }
    }

    @Override
    public List<BookDto> getBookById(Long id) {
        if (Objects.isNull(id)){throw new NotFoundException("id is null");}
        List<Book> listBook = bookRepository.findByUserId(id);
        return listBook.stream().map(bookMapper::bookToBookDto).toList();
    }

    @Override
    public void deleteBookById(Long id) {
       if (Objects.isNull(id)){throw new NotFoundException("id is null");}
       bookRepository.deleteByUserId(id);
    }
}
