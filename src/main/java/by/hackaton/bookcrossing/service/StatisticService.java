package by.hackaton.bookcrossing.service;

import by.hackaton.bookcrossing.dto.response.StatisticResponse;
import by.hackaton.bookcrossing.repository.AccountRepository;
import by.hackaton.bookcrossing.repository.BookRepository;
import by.hackaton.bookcrossing.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {

    private BookRepository bookRepository;
    private AccountRepository accountRepository;
    private OrderRepository orderRepository;


    public StatisticResponse getStatistic() {
        long bookAmount = bookRepository.count();
        long accountAmount = accountRepository.count();
        long orderAmount = orderRepository.count();

        return new StatisticResponse(bookAmount, accountAmount, orderAmount);
    }
}
