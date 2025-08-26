package moustapha.teach.gestionVoyage.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
public class Notification {

    public enum NotificationType {
        SYSTEM_NOTIFICATION, EMAIL_NOTIFICATION, SMS_NOTIFICATION
    }

    public enum NotificationChannel {
        EMAIL, SMS, IN_APP, PUSH
    }

    public enum NotificationStatus {
        PENDING, SENT, DELIVERED, FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.PENDING;

    private LocalDateTime sentAt;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String errorMessage;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Notification() {}

    public Notification(String title, String message, NotificationType type,
                        NotificationChannel channel, User user) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.channel = channel;
        this.user = user;
    }
}