package by.hackaton.bookcrossing.entity;

import by.hackaton.bookcrossing.entity.enums.ResponseStatus;
import by.hackaton.bookcrossing.entity.enums.SendType;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class BookRequest {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Account user;
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @ManyToOne
    @JoinColumn(name = "book_owner_id", nullable = false)
    private Account bookOwner;
    private Boolean viewed = false;
    @Enumerated(EnumType.STRING)
    private SendType sendType;
    @Enumerated(EnumType.STRING)
    private ResponseStatus status = ResponseStatus.NO_RESPONSE;
}
