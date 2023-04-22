package by.hackaton.bookcrossing.dto.response;

import by.hackaton.bookcrossing.dto.AddressShortDto;
import lombok.Data;

@Data
public class AccountShortResponse {
    public Long id;
    public String username;
    public Double longitude;
    public Double latitude;
    public AddressShortDto address;
    public String contact;
}
