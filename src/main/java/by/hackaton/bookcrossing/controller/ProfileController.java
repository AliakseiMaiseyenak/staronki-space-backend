package by.hackaton.bookcrossing.controller;

import by.hackaton.bookcrossing.dto.AccountDto;
import by.hackaton.bookcrossing.dto.BookCabinetDto;
import by.hackaton.bookcrossing.dto.request.AccountProfileRequest;
import by.hackaton.bookcrossing.dto.request.NewPasswordRequest;
import by.hackaton.bookcrossing.dto.response.AccountProfileResponse;
import by.hackaton.bookcrossing.dto.response.AccountShortResponse;
import by.hackaton.bookcrossing.service.AccountService;
import by.hackaton.bookcrossing.util.AuthUtils;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private AccountService accountService;

    public ProfileController(AccountService service) {
        this.accountService = service;
    }

    @GetMapping
    public ResponseEntity<AccountProfileResponse> getUserProfile(Authentication auth) {
        return ok(accountService.getUser(AuthUtils.getEmailFromAuth(auth)));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AccountShortResponse>> getAllUsers() {
        return ok(accountService.getAllUsers());
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookCabinetDto>> getMyBooks(Authentication auth) {
        String email = AuthUtils.getEmailFromAuth(auth);
        return ok(accountService.getMyBooks(email));
    }

    @GetMapping("/received-books")
    public ResponseEntity<List<BookCabinetDto>> getReceivedBooks(Authentication auth) {
        String email = AuthUtils.getEmailFromAuth(auth);
        return ok(accountService.getReceivedBooks(email));
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<AccountProfileResponse> updateUserProfile(@RequestBody AccountProfileRequest request, Authentication auth) {
        return ok(accountService.updateUser(AuthUtils.getEmailFromAuth(auth), request));
    }

    @PutMapping("/avatar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<AccountDto> updateUserAvatar(@RequestParam MultipartFile avatar, Authentication auth) {
        accountService.updateUserAvatar(AuthUtils.getEmailFromAuth(auth), avatar);
        return ok().build();
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> changePassword(@RequestBody NewPasswordRequest request, Authentication auth) {
        accountService.changePassword(request, AuthUtils.getEmailFromAuth(auth));
        return ok().build();
    }
}
