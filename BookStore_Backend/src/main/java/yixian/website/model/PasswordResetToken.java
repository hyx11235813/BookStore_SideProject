package yixian.website.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import yixian.website.model.Users;

import java.util.Date;
@Getter
@Setter
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @ManyToOne
    private Users user;


    public PasswordResetToken() {

    }

    public PasswordResetToken(String token, Users user, Date expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }


}