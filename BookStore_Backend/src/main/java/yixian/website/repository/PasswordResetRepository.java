package yixian.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import yixian.website.model.Users;
import yixian.website.model.PasswordResetToken;

import java.util.List;
import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordResetToken, Integer> {


    Optional<PasswordResetToken> findByTokenAndUser(String token, Users user);

//    @Modifying
//    @Transactional
//    void deleteByUser_Id(Integer userId);

    List<PasswordResetToken> findByUser_Id(Integer userId);

}
