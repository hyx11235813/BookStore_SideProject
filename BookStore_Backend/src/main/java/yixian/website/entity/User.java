package yixian.website.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "account")
    private String account;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "name")
    private String name;
    @Column(name = "role")
    private String role;
    @Column(name = "isActive", nullable = false)//帳號開通、能否使用的狀態
    private Boolean isActive;
    @Column(name = "avatar")
    private String avatar;
    @CreatedDate
    @Column(name = "creation_date")
    private Date creationDate;
    @LastModifiedDate
    @Column(name = "edit_date")
    private Date editDate;
    @Column(name = "login_date")
    private Date loginDate;
    @Column(name = "last_login_date")
    private Date lastLoginDate;
    @Column(name = "activation_code")
    private String activationCode;
}
