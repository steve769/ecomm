package info.stephenderrick.ecommerce.utility;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidationFieldResponse {
    public static ResponseEntity<Map<String, Object>>  validationCheckingFailed(BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            List<String> errorMessages = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            Map<String, Object> res = new HashMap<>();
            res.put("errors", errorMessages);

            return APIResponse.genericResponse("failure", 12, res,HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
