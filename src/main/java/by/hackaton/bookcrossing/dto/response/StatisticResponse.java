package by.hackaton.bookcrossing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StatisticResponse {
    private long bookAmount;
    private long accountAmount;
    private long orderAmount;
}
