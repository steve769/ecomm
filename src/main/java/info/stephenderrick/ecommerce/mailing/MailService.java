package info.stephenderrick.ecommerce.mailing;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final MailContentBuilder mailContentBuilder;

    @Async
    public void sendMail(NotificationEmail notificationEmail){

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(notificationEmail.getFrom());
        simpleMailMessage.setTo(notificationEmail.getRecipient());
        simpleMailMessage.setText(notificationEmail.getBody());
        simpleMailMessage.setSubject(notificationEmail.getSubject());

        try{
            javaMailSender.send(simpleMailMessage);
        }catch (MailException e){
            throw new MailSendException("Email Sending Failed");
        }
    }
}