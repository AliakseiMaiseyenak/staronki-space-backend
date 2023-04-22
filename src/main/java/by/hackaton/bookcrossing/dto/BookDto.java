package by.hackaton.bookcrossing.dto;

import by.hackaton.bookcrossing.entity.enums.BookStatus;
import by.hackaton.bookcrossing.entity.enums.Obtain;
import by.hackaton.bookcrossing.entity.enums.SendType;
import lombok.Data;

import java.util.List;

@Data
public class BookDto extends BookShortDto {
    private String additional;
    private AccountShortDto owner;
    private BookStatus status;
    private List<SendType> sendTypes;
    private Obtain obtain;
}
