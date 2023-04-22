package by.hackaton.bookcrossing.controller;

import by.hackaton.bookcrossing.dto.*;
import by.hackaton.bookcrossing.dto.response.OnMapResponse;
import by.hackaton.bookcrossing.service.BookService;
import by.hackaton.bookcrossing.util.AuthUtils;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/books")
public class BookController {

    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getBooks() {
        return ok(bookService.getBooks());
    }

    @GetMapping("/format")
    public ResponseEntity<List<OnMapResponse>> getBooksWithUser() {
        return ok(bookService.getBooksWithUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBook(@PathVariable("id") Long id) {
        return ok(bookService.getBookById(id));
    }

    @PostMapping("/{id}/request")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> requestBook(@PathVariable("id") Long id, @RequestBody SendTypeDto dto, Authentication auth) {
        String email = AuthUtils.getEmailFromAuth(auth);
        bookService.askBookById(id, dto, email);
        return ok().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<String>> getBooksByFilter(BookFilter filter) {
        List<String> books = bookService.autoComplete(filter);
        return ok(books);
    }

    @GetMapping("/find")
    public ResponseEntity<BookShortDto> getBooksByISBN(@PathParam("isbn") String isbn) {
        BookShortDto book = bookService.findBookByISBN(isbn);
        return ok(book);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BookDto> createBook(@RequestBody @Valid BookDto dto, Authentication auth) {
        return ok(bookService.createBook(dto, AuthUtils.getEmailFromAuth(auth)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable("id") Long id, @RequestParam BookDto dto, Authentication auth) {
        return ok(bookService.updateBook(id, dto, AuthUtils.getEmailFromAuth(auth)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BookDto> updateBookStatus(@PathVariable("id") Long id, Authentication auth) {
        return ok(bookService.updateStatus(id, AuthUtils.getEmailFromAuth(auth)));
    }

    @GetMapping("/{id}/order")
    public ResponseEntity<OrderDto> getBookOrder(@PathVariable("id") Long id, Authentication auth) {
        return ok(bookService.getBookOrder(id, AuthUtils.getEmailFromAuth(auth)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return ok().build();
    }
}
