package by.hackaton.bookcrossing.service;

import by.hackaton.bookcrossing.dto.BookDto;
import by.hackaton.bookcrossing.dto.OrderDto;
import by.hackaton.bookcrossing.dto.request.CreateOrderRequest;
import by.hackaton.bookcrossing.dto.request.OrderRequest;
import by.hackaton.bookcrossing.entity.Account;
import by.hackaton.bookcrossing.entity.Book;
import by.hackaton.bookcrossing.entity.BookOrder;
import by.hackaton.bookcrossing.entity.enums.BookStatus;
import by.hackaton.bookcrossing.entity.enums.Obtain;
import by.hackaton.bookcrossing.entity.enums.SendType;
import by.hackaton.bookcrossing.repository.AccountRepository;
import by.hackaton.bookcrossing.repository.BookRepository;
import by.hackaton.bookcrossing.repository.BookRequestRepository;
import by.hackaton.bookcrossing.repository.OrderRepository;
import by.hackaton.bookcrossing.service.exceptions.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private BookRepository bookRepository;
    private BookRequestRepository bookRequestRepository;
    private AccountRepository accountRepository;
    private ModelMapper modelMapper;

    public OrderService(OrderRepository orderRepository, BookRepository bookRepository, BookRequestRepository bookRequestRepository,
                        AccountRepository accountRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.bookRequestRepository = bookRequestRepository;
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
    }

    public List<OrderDto> getOrdersByReceiver(String email) {
        return orderRepository.findByReceiver_Email(email).stream()
                .map(o -> modelMapper.map(o, OrderDto.class)).collect(Collectors.toList());
    }

    public List<OrderDto> getOrdersByOwner(String email) {
        return orderRepository.findByBook_Owner_Email(email).stream()
                .map(o -> modelMapper.map(o, OrderDto.class)).collect(Collectors.toList());
    }

    @Transactional
    public BookDto sendBook(CreateOrderRequest dto, String email) {
        Book book = bookRepository.findByIdAndOwner_Email(dto.getBookId(), email).orElseThrow(
                () -> new BadRequestException("Book not found")
        );
        Account sendTo = accountRepository.findByUsername(dto.getReceiver()).orElseThrow(
                () -> new BadRequestException("Account not found")
        );
        BookOrder bookOrder = BookOrder.builder().book(book).receiver(sendTo).sendType(dto.getSendType())
                .obtain(dto.getObtain()).startDate(dto.getStartDate()).endDate(dto.getEndDate()).active(true).build();
        orderRepository.save(bookOrder);
        book.getBookOrders().add(bookOrder);
        book.setStatus(dto.getObtain() == Obtain.PERSONALLY ? BookStatus.RECEIVED : BookStatus.SENT);
        book.setReceiver(sendTo);
        bookRepository.save(book);
        bookRequestRepository.deleteRequestByBookOwnerEmailAndUserEmailAndBookId(email, dto.getReceiver(), dto.getBookId());
        return modelMapper.map(book, BookDto.class);
    }

    @Transactional
    public BookDto cancel(Long bookId, String email) {
        Book book = bookRepository.findByIdAndOwner_Email(bookId, email).orElseThrow(
                () -> new BadRequestException("Book not found")
        );
        book.setStatus(BookStatus.AVAILABLE);
        orderRepository.deleteActiveOrder(bookId);
        bookRepository.save(book);
        return modelMapper.map(book, BookDto.class);
    }

    @Transactional
    public BookDto receiveBook(Long bookId, String email) {
        Book book = bookRepository.findByIdAndReceiver_Email(bookId, email).orElseThrow(
                () -> new BadRequestException("Book not found")
        );
        BookOrder order = book.getBookOrders().stream().filter(BookOrder::isActive).findFirst().orElseThrow(
                () -> new BadRequestException("Order not found")
        );
        if (order.getSendType().equals(SendType.FOREVER)) {
            Account newOwner = accountRepository.findByEmail(email).orElseThrow(
                    () -> new BadRequestException("Account with email '" + email + "' not found")
            );
            book.setOwner(newOwner);
            book.setReceiver(null);
            book.setStatus(BookStatus.NOT_AVAILABLE);
            bookRepository.save(book);
            orderRepository.deleteActiveOrder(bookId);
        }
        if (order.getSendType().equals(SendType.SOLD)) {
            orderRepository.deleteActiveOrder(bookId);
            bookRepository.delete(book);
            return null;
        }
        if (order.getSendType().equals(SendType.TEMP)) {
            book.setStatus(BookStatus.RECEIVED);
        }
        return modelMapper.map(book, BookDto.class);
    }

    public OrderDto updateOrder(Long id, OrderRequest request, String email) {
        BookOrder order = orderRepository.findById(id).orElseThrow(
                () -> new BadRequestException("Order not found")
        );
        order.setEndDate(request.endDate);
        order = orderRepository.save(order);
        return modelMapper.map(order, OrderDto.class);
    }
}
