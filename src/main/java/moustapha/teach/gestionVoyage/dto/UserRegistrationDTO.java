package moustapha.teach.gestionVoyage.dto;


import moustapha.teach.gestionVoyage.enums.RoleType;

public class UserRegistrationDTO {
    private String username;
    private String name;
    private String phone;
    private String password;
    private String roleName; // Gardé comme String pour la réception des requêtes

    public UserRegistrationDTO() {
    }

    public UserRegistrationDTO(String username, String name, String phone, String password, String roleName) {
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.roleName = roleName;
    }

    // Getters et Setters standards
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
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