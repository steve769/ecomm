package info.stephenderrick.ecommerce.security;


import info.stephenderrick.ecommerce.mailing.MailService;
import info.stephenderrick.ecommerce.mailing.NotificationEmail;
import info.stephenderrick.ecommerce.token.VerificationToken;
import info.stephenderrick.ecommerce.token.VerificationTokenRepository;
import info.stephenderrick.ecommerce.users.User;
import info.stephenderrick.ecommerce.users.UserRepository;
import info.stephenderrick.ecommerce.utility.APIResponse;
import info.stephenderrick.ecommerce.utility.ValidationFieldResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @Value("${spring.mail.username}")
    private String FROM_EMAIL;
    @Value("${development.env}")
    private String DEVELOPMENT_SERVER;
    @Override
    public ResponseEntity<Map<String, Object>> registerUser(RegisterUserDto registerUserDto, BindingResult bindingResult) {
        try{
            //Check for Validation Errors
            if(bindingResult.hasErrors()){
               return ValidationFieldResponse.validationCheckingFailed(bindingResult);
            }

            Map<String, Object> responseJson = new HashMap<>();
            User userToRegister = new User();

            userToRegister.setFirstName(registerUserDto.getFirstName());
            userToRegister.setLastName(registerUserDto.getLastName());
            userToRegister.setEmail(registerUserDto.getEmail());
            userToRegister.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
            userToRegister.setEnabled(false);

            userToRegister.setRole(Role.USER);


            String tokenString = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setUser(userToRegister);
            verificationToken.setToken(tokenString);
            verificationToken.setCreatedAt(LocalDateTime.now());


            userToRegister.setToken(verificationToken);
            //Save user & token verification token saving is cascaded by default
            userRepository.save(userToRegister);

            //Send verification URL to user via email after you save the user
            String verificationURL = String.format("http://%s/api/v1/auth/confirmEmail/%s", DEVELOPMENT_SERVER, tokenString);
            String emailBody = String.format("Click the link below to verify your email\n%s", verificationURL);
            log.info("VERIFICATION URL " + verificationURL);
            NotificationEmail email = new NotificationEmail();
            email.setFrom(FROM_EMAIL);
            email.setRecipient(registerUserDto.getEmail());
            email.setSubject("INTERVIEWPEP VERIFY YOUR EMAIL");
            email.setBody(emailBody);

            mailService.sendMail(email);

            responseJson.put("username", String.format("%s was created successfully", registerUserDto.getEmail()));

            return APIResponse.genericResponse("success",11, responseJson,HttpStatus.CREATED);
        }catch(Exception ex){
            return APIResponse.genericResponse("failure",11, ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<Map<String, Object>> loginUser(LoginUserDto loginUserDto, BindingResult bindingResult) {
        try{

            //Check for Validation Errors
            if(bindingResult.hasErrors()){
                return ValidationFieldResponse.validationCheckingFailed(bindingResult);
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUserDto.getEmail(),
                    loginUserDto.getPassword());
            Authentication authenticated = authenticationManager.authenticate(authenticationToken);

            org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authenticated.getPrincipal();

            String jwt = jwtProvider.generateJwtToken(principal.getUsername());

            Map<String, Object> responseJson = new HashMap<>();
            responseJson.put("token", jwt);

            return APIResponse.genericResponse("success",11, responseJson,HttpStatus.OK);
        }catch(Exception ex){
            return APIResponse.genericResponse("failure",12, ex.getMessage(),HttpStatus.UNAUTHORIZED);
        }
    }


    @Override
    public ResponseEntity<Map<String, Object>> forgotPassword(ForgotPasswordDto forgotPasswordDto, BindingResult bindingResult) {
        try{

            //Check for Validation Errors
            if(bindingResult.hasErrors()){
                return ValidationFieldResponse.validationCheckingFailed(bindingResult);
            }

            User userFromDB = userRepository.findByEmail(forgotPasswordDto.getEmail()).orElse(null);

            if(userFromDB == null){
                return APIResponse.genericResponse("failure",12, "Email not found",HttpStatus.BAD_REQUEST);
            }


            NotificationEmail notificationEmail = new NotificationEmail();
            String emailBody = String.format("http://%s/api/v1/auth/resetMyPassword?userId=%s",DEVELOPMENT_SERVER,userFromDB.getId());
            log.info(emailBody);
            notificationEmail.setFrom(FROM_EMAIL);
            notificationEmail.setRecipient(forgotPasswordDto.getEmail());
            notificationEmail.setSubject("RESET YOUR PASSWORD");
            notificationEmail.setBody(emailBody);

            sendEmailToResetPassword(notificationEmail);

            return APIResponse.genericResponse("success",11, "We have sent you an email with reset link",HttpStatus.OK);
        }catch(Exception ex){
            return APIResponse.genericResponse("failure",12, ex.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    private void sendEmailToResetPassword(NotificationEmail notificationEmail) {
        mailService.sendMail(notificationEmail);
    }

    @Override
    public ResponseEntity<Map<String, Object>> confirmEmail(String token) {
        try{
            Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(token);
            VerificationToken tokenWithUser = tokenOptional.get();
            User userToEnable = tokenWithUser.getUser();
            userToEnable.setEnabled(true);

            userRepository.save(userToEnable);

            return APIResponse.genericResponse("success",11, "Email verified successfully",HttpStatus.OK);
        }catch(Exception ex){
            return APIResponse.genericResponse("failure",12, ex.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> resetMyPassword(ResetMyPasswordDto resetMyPasswordDto, String userId) {
        try{
           User userToResetPassword = userRepository.findById(Long.parseLong(userId)).orElse(null);

           if(userToResetPassword == null){
               return APIResponse.genericResponse("failure",12, "Password reset failed, contact admin for help",HttpStatus.BAD_REQUEST);
           }

           userToResetPassword.setPassword(passwordEncoder.encode(resetMyPasswordDto.getNewPassword()));
           userRepository.save(userToResetPassword);

            return APIResponse.genericResponse("success",11, "Password reset successfully",HttpStatus.OK);
        }catch(Exception ex){
            return APIResponse.genericResponse("failure",12, ex.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }


}
