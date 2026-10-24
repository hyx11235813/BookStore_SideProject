package yixian.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yixian.website.entity.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenAndUserId(String token, Integer userId);

    void deleteByUserId(Integer userId);
}
