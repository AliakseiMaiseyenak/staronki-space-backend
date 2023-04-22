package by.hackaton.bookcrossing.dto.request;

import by.hackaton.bookcrossing.dto.AddressDto;
import lombok.Data;

@Data
public class AccountProfileRequest {
    public String username;
    public String email;
    public AddressDto address;
    public String contact;
}
