package by.hackaton.bookcrossing.repository;

import by.hackaton.bookcrossing.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account getUserByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    Optional<Account> findByUsername(String username);

    Optional<Account> findByEmail(String email);

    @Modifying
    @Query("UPDATE Account a SET a.enabled = TRUE where a.email = :email")
    void verifyAccount(@Param("email") String email);
}
