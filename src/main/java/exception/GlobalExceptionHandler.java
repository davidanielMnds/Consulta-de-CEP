package exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    
    @ExceptionHandler(CepException.class)
    public ResponseEntity<String> handle(CepException e) {
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

}
