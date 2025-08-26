package moustapha.teach.gestionVoyage.services.impl;


import moustapha.teach.gestionVoyage.entities.Role;
import moustapha.teach.gestionVoyage.repositories.RoleRepository;
import moustapha.teach.gestionVoyage.enums.RoleType;
import moustapha.teach.gestionVoyage.services.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rôle non trouvé avec l'ID : " + id));
    }

    @Override
    public Role getRoleByName(String name) {
        // Conversion du String en ERole
        RoleType roleEnum;
        try {
            roleEnum = RoleType.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new NoSuchElementException("Rôle non trouvé avec le nom : " + name);
        }

        return roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new NoSuchElementException("Rôle non trouvé avec le nom : " + name));
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role updateRole(Long id, Role roleDetails) {
        Role role = getRoleById(id);
        // Mise à jour des champs nécessaires
        if (roleDetails.getName() != null) {
            role.setName(roleDetails.getName());
        }
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = getRoleById(id);
        roleRepository.delete(role);
    }

    @Override
    public boolean roleExistsByName(String name) {
        try {
            RoleType roleEnum = RoleType.valueOf(name);
            return roleRepository.findByName(roleEnum).isPresent();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}