package info.stephenderrick.ecommerce.security;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginUserDto {
    @NotNull(message = "Email is null")
    private String email;
    @NotNull(message = "Password is null")
    private String password;
}
