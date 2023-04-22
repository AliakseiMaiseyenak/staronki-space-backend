package by.hackaton.bookcrossing.entity;

import by.hackaton.bookcrossing.entity.enums.Role;
import java.util.List;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue
    private Long id;
    private String email;
    @Column(unique = true)
    private String username;
    private String password;
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Book> books;
    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Book> sentBooks;
    private Double latitude;
    private Double longitude;
    private Address address;
    private String contact;
    private boolean enabled = false;
    private Boolean deleted = false;

    @Enumerated(EnumType.STRING)
    private Role role;
    private byte[] avatar;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<BookRequest> userRequests;
    @OneToMany(mappedBy = "bookOwner", cascade = CascadeType.ALL)
    private List<BookRequest> ownerRequests;
}
