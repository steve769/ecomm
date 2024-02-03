package info.stephenderrick.ecommerce.mailing;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationEmail {
    private String from;
    private String recipient;
    private String subject;
    private String body;
}
