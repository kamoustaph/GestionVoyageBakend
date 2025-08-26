package moustapha.teach.gestionVoyage.repositories;

import moustapha.teach.gestionVoyage.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Integer> {
}
