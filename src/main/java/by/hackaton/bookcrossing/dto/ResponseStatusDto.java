package by.hackaton.bookcrossing.dto;

import by.hackaton.bookcrossing.entity.enums.ResponseStatus;
import lombok.Data;

@Data
public class ResponseStatusDto {
    private ResponseStatus status;
}
