package by.hackaton.bookcrossing.dto;

import by.hackaton.bookcrossing.entity.enums.Obtain;
import by.hackaton.bookcrossing.entity.enums.SendType;
import java.time.LocalDate;
import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private BookDto book;
    private AccountShortDto receiver;
    private SendType sendType;
    private Obtain obtain;
    private LocalDate startDate;
    private LocalDate endDate;
}
