package yixian.website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BookAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookAdminApplication.class, args);
    }

}
