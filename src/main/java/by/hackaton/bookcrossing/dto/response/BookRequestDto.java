package by.hackaton.bookcrossing.dto.response;

import by.hackaton.bookcrossing.dto.BookShortDto;
import by.hackaton.bookcrossing.entity.enums.ResponseStatus;
import by.hackaton.bookcrossing.entity.enums.SendType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {
    public Long id;
    public BookShortDto book;
    public AccountShortResponse user;
    public AccountShortResponse bookOwner;
    public ResponseStatus status;
    public SendType sendType;
}
