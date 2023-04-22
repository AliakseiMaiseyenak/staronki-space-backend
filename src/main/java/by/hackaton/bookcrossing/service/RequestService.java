package by.hackaton.bookcrossing.service;

import by.hackaton.bookcrossing.dto.response.BookRequestDto;
import by.hackaton.bookcrossing.entity.BookRequest;
import by.hackaton.bookcrossing.entity.enums.ResponseStatus;
import by.hackaton.bookcrossing.repository.BookRequestRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestService {

    private BookRequestRepository bookRequestRepository;
    private ModelMapper modelMapper;

    public RequestService(BookRequestRepository bookRequestRepository, ModelMapper modelMapper) {
        this.bookRequestRepository = bookRequestRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public List<BookRequestDto> requestedBooksNotification(String email) {
        List<BookRequest> requests = bookRequestRepository.findByBookOwnerEmailAndViewedFalse(email);
        bookRequestRepository.updateViewedByBookOwnerEmail(email);
        return requests.stream().map(r -> modelMapper.map(r, BookRequestDto.class)).collect(Collectors.toList());
    }

    public List<BookRequestDto> allBookRequests(String email) {
        List<BookRequest> requests = bookRequestRepository.findByBookOwnerEmail(email);
        return requests.stream().filter(r -> r.getStatus() != ResponseStatus.REJECTED)
                .map(r -> modelMapper.map(r, BookRequestDto.class)).collect(Collectors.toList());
    }

    public List<BookRequestDto> requestedBooks(String email) {
        List<BookRequest> requests = bookRequestRepository.findByUserEmail(email);
        return requests.stream().map(r -> modelMapper.map(r, BookRequestDto.class)).collect(Collectors.toList());
    }

    @Transactional
    public void changeStatus(Long id, ResponseStatus status) {
        bookRequestRepository.updateStatus(id, status);
    }

    public void deleteRequest(Long id) {
        bookRequestRepository.deleteById(id);

    }
}
