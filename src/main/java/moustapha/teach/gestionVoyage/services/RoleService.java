package moustapha.teach.gestionVoyage.services;



import moustapha.teach.gestionVoyage.entities.Role;

import java.util.List;

public interface RoleService {
    Role createRole(Role role);
    Role getRoleById(Long id);
    Role getRoleByName(String name);
    List<Role> getAllRoles();
    Role updateRole(Long id, Role roleDetails);
    void deleteRole(Long id);
    boolean roleExistsByName(String name);

}