package info.stephenderrick.ecommerce.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ForgotPasswordDto {
    @NotNull(message = "Email is null")
    @Email(message = "Invalid email")
    private String email;
}
