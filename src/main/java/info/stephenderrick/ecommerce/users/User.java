package info.stephenderrick.ecommerce.users;

import info.stephenderrick.ecommerce.security.Role;
import info.stephenderrick.ecommerce.token.VerificationToken;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull(message = "Firstname cannot be null")
    private String firstName;
    @NotNull(message = "Lastname cannot be null")
    private String lastName;
    @Email(message = "Email format is invalid")
    @NotNull(message = "Email cannot be null")
    @Column(unique = true)
    private String email;
    @Size(min = 6)
    @NotNull(message = "Password cannot be null")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean enabled;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "token_id")
    private VerificationToken token;


}
