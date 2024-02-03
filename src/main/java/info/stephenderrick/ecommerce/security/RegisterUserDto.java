package info.stephenderrick.ecommerce.security;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterUserDto {
    @NotNull(message = "Firstname is null")
    private String firstName;
    @NotNull(message = "Lastname is null")
    private String lastName;
    @NotNull(message = "Email is null")
    @Email(message = "Invalid email format")
    private String email;
    @NotNull(message = "Password is null")
    private String password;
}
