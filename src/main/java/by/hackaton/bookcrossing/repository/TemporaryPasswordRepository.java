package by.hackaton.bookcrossing.repository;

import by.hackaton.bookcrossing.entity.TemporaryPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface TemporaryPasswordRepository extends JpaRepository<TemporaryPassword, String> {

    boolean existsByEmailAndCode(String email, String code);

    @Modifying
    void deleteByEmailAndCode(String email, String code);
}
