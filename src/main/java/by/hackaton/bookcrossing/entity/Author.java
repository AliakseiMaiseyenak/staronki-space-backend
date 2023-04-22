package by.hackaton.bookcrossing.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Author {
    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
    @ManyToOne
    private Book book;
}
