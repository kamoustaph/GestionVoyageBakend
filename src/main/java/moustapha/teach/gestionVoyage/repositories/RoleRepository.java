package moustapha.teach.gestionVoyage.repositories;
import moustapha.teach.gestionVoyage.entities.Role;
import moustapha.teach.gestionVoyage.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);

}