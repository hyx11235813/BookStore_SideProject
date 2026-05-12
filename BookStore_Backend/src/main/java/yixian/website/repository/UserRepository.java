package yixian.website.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yixian.website.model.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {


    @Query(value = "select * from Users where 1=1 " +
            "and (:name is null or name like :name%) " +
            "and (:account is null or account like :account%) " +
            "and (:email is null or email like :email%) " +
            "and (:role is null or role like :role) " +
            "and (:isActive is null or isActive = :isActive) " +
            "and (:creationDateStart is null or creation_date >= :creationDateStart) " +
            "and (:creationDateEnd is null or creation_date < DATEADD(day,1,:creationDateEnd)) " +
            "and (:editDateStart is null or edit_date >= :editDateStart) " +
            "and (:editDateEnd is null or edit_date < DATEADD(day,1,:editDateEnd))",
            nativeQuery = true)
    Page<Users> findUserByParams(
            @Param("name") String name,
            @Param("account") String account,
            @Param("email") String email,
            @Param("role") String role,
            @Param("isActive") Boolean isActive,
            @Param("creationDateStart") String creationDateStart,
            @Param("creationDateEnd") String creationDateEnd,
            @Param("editDateStart") String editDateStart,
            @Param("editDateEnd") String editDateEnd,
            Pageable pageable);

    Optional<Users> findUserByEmail(String email);

    Optional<Users> findUserByAccount(String account);


    Optional<Users> findUserByActivationCode(String activationCode);

    @Modifying
    @Query("UPDATE Users u SET u.activationCode='' where u.id=:id")
    void deactivateActivationCode(@Param("id") long id);

    boolean existsByAccount(String account);

    boolean existsByEmail(String email);
}
