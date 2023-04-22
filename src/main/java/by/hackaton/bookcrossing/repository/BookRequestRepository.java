package by.hackaton.bookcrossing.repository;

import by.hackaton.bookcrossing.entity.BookRequest;
import by.hackaton.bookcrossing.entity.enums.ResponseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {

    List<BookRequest> findByBookOwnerEmailAndViewedFalse(String email);

    List<BookRequest> findByBookOwnerEmail(String ownerEmail);

    List<BookRequest> findByUserEmail(String email);

    @Modifying
    @Query("UPDATE BookRequest r SET r.viewed = TRUE WHERE r.bookOwner IN (SELECT a FROM Account a WHERE a.email = :email)")
    void updateViewedByBookOwnerEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE BookRequest r SET r.status = :status WHERE r.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") ResponseStatus status);

    @Modifying
    @Query("DELETE FROM BookRequest b WHERE b.bookOwner IN (SELECT a FROM Account a WHERE a.email = :ownerEmail) " +
            "AND b.user IN (SELECT a FROM Account a WHERE a.username = :username) " +
            "AND b.book IN (SELECT b FROM Book b WHERE b.id = :bookId)")
    void deleteRequestByBookOwnerEmailAndUserEmailAndBookId(@Param("ownerEmail") String ownerEmail, @Param("username") String username, @Param("bookId") Long bookId);
}
