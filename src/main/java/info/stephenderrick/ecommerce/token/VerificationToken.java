package info.stephenderrick.ecommerce.token;


import com.fasterxml.jackson.annotation.JsonIgnore;
import info.stephenderrick.ecommerce.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(mappedBy = "token")
    @JsonIgnore
    private User user;

    private String token;
    private LocalDateTime createdAt;
}
