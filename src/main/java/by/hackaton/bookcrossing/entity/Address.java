package by.hackaton.bookcrossing.entity;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class Address {
    private String country;
    private String city;
    private String postcode;
    private String street;
    private String flat;
    private String apartment;
}
