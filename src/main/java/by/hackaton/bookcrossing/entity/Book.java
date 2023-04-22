package by.hackaton.bookcrossing.entity;

import by.hackaton.bookcrossing.entity.enums.BookStatus;
import by.hackaton.bookcrossing.entity.enums.Obtain;
import by.hackaton.bookcrossing.entity.enums.SendType;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Author> authors;
    private int year;
    private String ISBN;
    private String additional;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Account owner;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Account receiver;
    @Enumerated(EnumType.STRING)
    private BookStatus status = BookStatus.AVAILABLE;
    @ElementCollection
    @CollectionTable(
            name = "book_send_type",
            joinColumns = @JoinColumn(name = "book_id")
    )
    private List<SendType> sendTypes;
    @Enumerated(EnumType.STRING)
    private Obtain obtain;
    private String language;
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookOrder> bookOrders;
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookRequest> bookRequests;
    private byte[] image;
}
