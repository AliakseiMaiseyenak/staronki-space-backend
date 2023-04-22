package by.hackaton.bookcrossing.controller;

import by.hackaton.bookcrossing.dto.ResponseStatusDto;
import by.hackaton.bookcrossing.dto.response.BookRequestDto;
import by.hackaton.bookcrossing.service.RequestService;
import by.hackaton.bookcrossing.util.AuthUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<BookRequestDto>> getRequestedBookNotification(Authentication auth) {
        String email = AuthUtils.getEmailFromAuth(auth);
        return ok(requestService.requestedBooksNotification(email));
    }

    @GetMapping
    public ResponseEntity<List<BookRequestDto>> getAllBookRequests(Authentication auth) {
        String email = AuthUtils.getEmailFromAuth(auth);
        return ok(requestService.allBookRequests(email));
    }

    @GetMapping("/requested")
    public ResponseEntity<List<BookRequestDto>> getRequestedBook(Authentication auth) {
        String email = AuthUtils.getEmailFromAuth(auth);
        return ok(requestService.requestedBooks(email));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody ResponseStatusDto dto){
        requestService.changeStatus(id, dto.getStatus());
        return ok().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id){
        requestService.deleteRequest(id);
        return ok().build();
    }
}
