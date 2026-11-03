package moustapha.teach.gestionVoyage.dto;

public record AuthResponse(String token, long expireDansSecondes, UtilisateurDto utilisateur) {
}
