package by.hackaton.bookcrossing.service;

import by.hackaton.bookcrossing.dto.AccountDto;
import by.hackaton.bookcrossing.dto.request.LoginRequest;
import by.hackaton.bookcrossing.dto.request.PasswordRequest;
import by.hackaton.bookcrossing.dto.request.SignInRequest;
import by.hackaton.bookcrossing.dto.security.AuthResponse;
import by.hackaton.bookcrossing.dto.security.Token;
import by.hackaton.bookcrossing.dto.security.TokenProvider;
import by.hackaton.bookcrossing.entity.Account;
import by.hackaton.bookcrossing.entity.TemporaryPassword;
import by.hackaton.bookcrossing.entity.VerificationStatus;
import by.hackaton.bookcrossing.entity.enums.Role;
import by.hackaton.bookcrossing.repository.AccountRepository;
import by.hackaton.bookcrossing.repository.TemporaryPasswordRepository;
import by.hackaton.bookcrossing.repository.VerificationStatusRepository;
import by.hackaton.bookcrossing.service.exceptions.BadRequestException;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private AuthenticationManager authenticationManager;
    private TokenProvider tokenProvider;
    private ModelMapper modelMapper;
    private AccountRepository accountRepository;
    private VerificationStatusRepository verificationStatusRepository;
    private EmailService emailService;
    private TemporaryPasswordRepository temporaryPasswordRepository;

    public AuthService(AuthenticationManager authenticationManager, TokenProvider tokenProvider,
                       ModelMapper modelMapper, AccountRepository accountRepository,
                       VerificationStatusRepository verificationStatusRepository, EmailService emailService,
                       TemporaryPasswordRepository temporaryPasswordRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.modelMapper = modelMapper;
        this.accountRepository = accountRepository;
        this.verificationStatusRepository = verificationStatusRepository;
        this.emailService = emailService;
        this.temporaryPasswordRepository = temporaryPasswordRepository;
    }

    @Transactional
    public void signIn(SignInRequest request) {
        Optional<Account> account = accountRepository.findByEmail(request.getEmail());
        if (account.isPresent()) {
            if (account.get().isEnabled()) {
                throw new BadRequestException("Email already exists");
            } else {
                verificationStatusRepository.findByEmail(request.getEmail())
                        .ifPresent(code -> verificationStatusRepository.delete(code));
            }
        } else {
            Account newAccount = modelMapper.map(request, Account.class);
            newAccount.setRole(Role.USER);
            accountRepository.save(newAccount);
        }
        String verificationCode = RandomStringUtils.randomAlphabetic(12);
        VerificationStatus status = new VerificationStatus(request.getEmail(), verificationCode);
        verificationStatusRepository.save(status);
        emailService.confirmEmailMessage(request.getEmail(), "https://www.staronki.space/confirm?email=" + request.getEmail() + "&code=" + verificationCode);
    }

    public void resetPassword(String email) {
        if (!accountRepository.existsByEmail(email)) {
            throw new BadRequestException("Email not found");
        }
        String verificationCode = RandomStringUtils.randomAlphabetic(12);
        TemporaryPassword temp = temporaryPasswordRepository.findById(email).orElse(new TemporaryPassword(email));
        temp.setCode(verificationCode);
        temporaryPasswordRepository.save(temp);
        emailService.resetPasswordMessage(email, "https://www.staronki.space/password?email=" + email + "&code=" + verificationCode);
    }

    @Transactional
    public void confirmEmail(String email, String code) {
        VerificationStatus status = verificationStatusRepository.findByEmailAndCode(email, code).orElseThrow(
                () -> new BadRequestException("Code not found")
        );
        verificationStatusRepository.delete(status);
        accountRepository.verifyAccount(email);
    }

    public AuthResponse login(LoginRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found")
        );
        if (!account.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Wrong password");
        }
        if (!account.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Email not confirmed");
        }
        Authentication authentication = authenticate(request);
        String accessToken = tokenProvider.createToken(authentication, account.getPassword());
        return new AuthResponse(new Token(accessToken), modelMapper.map(account, AccountDto.class));
    }

    private Authentication authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    @Transactional
    public void changePasswordWithCode(PasswordRequest request) {
        if (temporaryPasswordRepository.existsByEmailAndCode(request.getEmail(), request.getCode())) {
            Account account = accountRepository.findByEmail(request.getEmail()).orElseThrow(
                    () -> new BadRequestException("Account not found")
            );
            account.setPassword(request.getPassword());
            accountRepository.save(account);
            temporaryPasswordRepository.deleteByEmailAndCode(request.getEmail(), request.getCode());
        } else {
            throw new BadRequestException("Email and code not found");
        }
    }

    public void deleteByEmail(String email) {
        Optional<Account> account = accountRepository.findByEmail(email);
        account.ifPresent(account1 -> accountRepository.delete(account1));
    }

    public void deleteLast() {
        List<Account> accounts = accountRepository.findAll();
        accountRepository.delete(accounts.get(accounts.size() - 1));
    }
}
