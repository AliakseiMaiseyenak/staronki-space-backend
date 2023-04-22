package by.hackaton.bookcrossing.dto.response;

import by.hackaton.bookcrossing.dto.AddressDto;
import lombok.Data;

@Data
public class AccountProfileResponse {
    public Long id;
    public String username;
    public String email;
    public Double longitude;
    public Double latitude;
    public AddressDto address;
    public String contact;
    public byte[] avatar;
}
