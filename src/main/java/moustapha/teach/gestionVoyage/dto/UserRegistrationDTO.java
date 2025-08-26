package moustapha.teach.gestionVoyage.dto;


import lombok.Getter;
import lombok.Setter;
import moustapha.teach.gestionVoyage.enums.RoleType;

@Setter
@Getter
public class UserRegistrationDTO {
    private String username;
    private String name;
    private String phone;
    private String password;
    private String photoProfil;
    private String roleName; // Gardé comme String pour la réception des requêtes

    public UserRegistrationDTO() {
    }

    public UserRegistrationDTO(String username, String name, String phone, String password, String photoProfil, String roleName) {
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.photoProfil = photoProfil;
        this.roleName = roleName;
    }

    public RoleType getRoleAsEnum() {
        try {
            return RoleType.valueOf(roleName);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}