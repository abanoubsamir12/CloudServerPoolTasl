package cloud.server.CloudServer.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
    }

    @ExceptionHandler(InvalidMemoryRequestException.class)
    public ResponseEntity<String> handleInvalidMemory(InvalidMemoryRequestException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
