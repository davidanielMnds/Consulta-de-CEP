package exception;

import org.springframework.http.HttpStatus;

public class CepException extends RuntimeException{
    private final HttpStatus status;

    public CepException(String mensagem, HttpStatus status) {
        super(mensagem);
        this.status=status;
    }

    public HttpStatus getStatus(){return status;}

}
