package by.hackaton.bookcrossing.repository;

import by.hackaton.bookcrossing.entity.BookOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<BookOrder, Long> {

    List<BookOrder> findByReceiver_Email(@Param("email") String email);

    List<BookOrder> findByBook_Owner_Email(@Param("email") String email);

    Optional<BookOrder> findByBook_IdAndActiveTrue(@Param("id") Long bookId);

    @Modifying
    @Query("DELETE FROM BookOrder o WHERE o.book.id = :bookId AND o.active = true")
    void deleteActiveOrder(Long bookId);
}
