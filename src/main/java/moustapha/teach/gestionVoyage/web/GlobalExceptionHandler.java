package moustapha.teach.gestionVoyage.web;

import lombok.extern.slf4j.Slf4j;
import moustapha.teach.gestionVoyage.dto.ErreurDto;
import moustapha.teach.gestionVoyage.service.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErreurDto> api(ApiException e) {
        return erreur(e.getStatus(), e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurDto> validation(MethodArgumentNotValidException e) {
        Map<String, String> champs = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> champs.putIfAbsent(f.getField(), f.getDefaultMessage()));
        return erreur(HttpStatus.BAD_REQUEST, "Certains champs sont invalides", champs);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErreurDto> illisible(Exception e) {
        return erreur(HttpStatus.BAD_REQUEST, "Requête mal formée", null);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErreurDto> concurrence(ObjectOptimisticLockingFailureException e) {
        return erreur(HttpStatus.CONFLICT, "Les places viennent d'être modifiées par un autre client, réessayez", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErreurDto> refuse(AccessDeniedException e) {
        return erreur(HttpStatus.FORBIDDEN, "Votre rôle ne permet pas cette action", null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErreurDto> absent(NoResourceFoundException e) {
        return erreur(HttpStatus.NOT_FOUND, "Ressource introuvable", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurDto> inattendu(Exception e) {
        log.error("Erreur inattendue", e);
        return erreur(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne, réessayez plus tard", null);
    }

    private ResponseEntity<ErreurDto> erreur(HttpStatus status, String message, Map<String, String> champs) {
        return ResponseEntity.status(status).body(new ErreurDto(status.value(), message, champs, LocalDateTime.now()));
    }
}
