package info.stephenderrick.ecommerce.security;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody RegisterUserDto registerUserDto, BindingResult bindingResult){
       return authService.registerUser(registerUserDto, bindingResult);
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@Valid @RequestBody LoginUserDto loginUserDto, BindingResult bindingResult){
        return authService.loginUser(loginUserDto, bindingResult);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<Map<String, Object>> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto, BindingResult bindingResult){
        return authService.forgotPassword(forgotPasswordDto, bindingResult);
    }

    @PostMapping("/resetMyPassword")
    public ResponseEntity<Map<String, Object>> resetMyPassword(@RequestBody ResetMyPasswordDto resetMyPasswordDto, @RequestParam String userId){
        return authService.resetMyPassword(resetMyPasswordDto, userId);
    }
    @GetMapping("/confirmEmail/{token}")
    public ResponseEntity<Map<String, Object>> confirmEmail(@PathVariable String token){
        return authService.confirmEmail(token);
    }

}
