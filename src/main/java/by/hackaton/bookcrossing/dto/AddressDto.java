package by.hackaton.bookcrossing.dto;

import lombok.Data;

@Data
public class AddressDto {
    private String country;
    private String city;
    private String postcode;
    private String street;
    private String flat;
    private String apartment;
}
