package moustapha.teach.gestionVoyage.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moustapha.teach.gestionVoyage.enums.ReservationStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voyage_id", nullable = false)
    private Voyage voyage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus statut;

    @Column(name = "date_reservation", nullable = false)
    @CreationTimestamp
    private LocalDateTime dateReservation;

    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;
    @Column(name = "nombre_places", nullable = false)
    private Integer nombrePlaces;

}
