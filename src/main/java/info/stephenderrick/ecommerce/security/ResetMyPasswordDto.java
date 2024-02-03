package info.stephenderrick.ecommerce.security;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResetMyPasswordDto {
    private String newPassword;
}
