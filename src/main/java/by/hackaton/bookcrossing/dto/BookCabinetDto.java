package by.hackaton.bookcrossing.dto;

import by.hackaton.bookcrossing.entity.enums.BookStatus;
import by.hackaton.bookcrossing.entity.enums.Obtain;
import by.hackaton.bookcrossing.entity.enums.SendType;
import lombok.Data;

import java.util.List;

@Data
public class BookCabinetDto extends BookShortDto {
    private BookStatus status;
    private AccountShortDto owner;
    private AccountShortDto receiver;
    private String additional;
    private List<SendType> sendTypes;
    private Obtain obtain;
    private byte[] image;
}
