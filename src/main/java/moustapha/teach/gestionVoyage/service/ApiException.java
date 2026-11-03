package moustapha.teach.gestionVoyage.service;

import org.springframework.http.HttpStatus;

/** Erreur métier traduite en réponse HTTP par GlobalExceptionHandler. */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public static ApiException introuvable(String quoi) {
        return new ApiException(HttpStatus.NOT_FOUND, quoi + " introuvable");
    }

    public static ApiException conflit(String message) {
        return new ApiException(HttpStatus.CONFLICT, message);
    }

    public static ApiException invalide(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiException interdit(String message) {
        return new ApiException(HttpStatus.FORBIDDEN, message);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
