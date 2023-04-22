package by.hackaton.bookcrossing.controller;

import by.hackaton.bookcrossing.dto.BookDto;
import by.hackaton.bookcrossing.dto.OrderDto;
import by.hackaton.bookcrossing.dto.request.CreateOrderRequest;
import by.hackaton.bookcrossing.dto.request.OrderRequest;
import by.hackaton.bookcrossing.service.OrderService;
import by.hackaton.bookcrossing.util.AuthUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<OrderDto>> getReceivedOrders(Authentication auth) {
        String email = AuthUtils.getEmailFromAuth(auth);
        return ok(orderService.getOrdersByReceiver(email));
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOwnerOrders(Authentication auth) {
        String email = AuthUtils.getEmailFromAuth(auth);
        return ok(orderService.getOrdersByOwner(email));
    }

    @PostMapping("/sent")
    public ResponseEntity<BookDto> sendBook(@RequestBody CreateOrderRequest dto, Authentication auth) {
        return ok(orderService.sendBook(dto, AuthUtils.getEmailFromAuth(auth)));
    }

    @PostMapping("/cancel")
    public ResponseEntity<BookDto> cancelSending(@RequestBody OrderRequest request, Authentication auth) {
        return ok(orderService.cancel(request.bookId, AuthUtils.getEmailFromAuth(auth)));
    }

    @PutMapping("/receive")
    public ResponseEntity<BookDto> receiveBook(@RequestBody OrderRequest request, Authentication auth) {
        return ok(orderService.receiveBook(request.bookId, AuthUtils.getEmailFromAuth(auth)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable("id") Long id, @RequestBody OrderRequest request, Authentication auth) {
        return ok(orderService.updateOrder(id, request, AuthUtils.getEmailFromAuth(auth)));
    }
}
