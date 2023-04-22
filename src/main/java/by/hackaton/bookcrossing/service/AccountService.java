package by.hackaton.bookcrossing.service;

import by.hackaton.bookcrossing.dto.BookCabinetDto;
import by.hackaton.bookcrossing.dto.request.AccountProfileRequest;
import by.hackaton.bookcrossing.dto.request.NewPasswordRequest;
import by.hackaton.bookcrossing.dto.response.AccountProfileResponse;
import by.hackaton.bookcrossing.dto.response.AccountShortResponse;
import by.hackaton.bookcrossing.entity.Account;
import by.hackaton.bookcrossing.repository.AccountRepository;
import by.hackaton.bookcrossing.service.exceptions.BadRequestException;
import java.io.IOException;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static java.util.stream.Collectors.toList;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private ModelMapper modelMapper;

    public AccountService(AccountRepository accountRepository, ModelMapper modelMapper) {
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
    }

    public AccountProfileResponse getUser(String email) {
        Account user = findAccount(email);
        return modelMapper.map(user, AccountProfileResponse.class);
    }

    public AccountProfileResponse updateUser(String email, AccountProfileRequest request) {
        Account user = findAccount(email);
        modelMapper.map(request, user);
        accountRepository.save(user);
        return modelMapper.map(user, AccountProfileResponse.class);
    }

    public void updateUserAvatar(String email, MultipartFile avatar) {
        try {
            Account user = findAccount(email);
            user.setAvatar(avatar.getBytes());
            accountRepository.save(user);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while avatar reading");
        }
    }

    public void changePassword(NewPasswordRequest request, String email) {
        Account account = findAccount(email);
        if(account.getPassword().equals(request.getOldPassword())) {
            account.setPassword(request.getNewPassword());
        } else {
            throw new BadRequestException("Wrong password");
        }
        accountRepository.save(account);
    }

    private Account findAccount(String email) {
        return accountRepository.findByEmail(email).orElseThrow(
                () -> new BadRequestException("Account not found"));
    }

    public List<BookCabinetDto> getMyBooks(String email) {
        Account account = findAccount(email);
        return account.getBooks().stream().map(b -> modelMapper.map(b, BookCabinetDto.class)).collect(toList());
    }

    public List<BookCabinetDto> getReceivedBooks(String email) {
        Account account = findAccount(email);
        return account.getSentBooks().stream().map(b -> modelMapper.map(b, BookCabinetDto.class)).collect(toList());
    }

    public List<AccountShortResponse> getAllUsers() {
        return accountRepository.findAll().stream().map(a -> modelMapper.map(a, AccountShortResponse.class)).collect(toList());
    }
}
