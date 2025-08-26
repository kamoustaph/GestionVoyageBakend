package moustapha.teach.gestionVoyage.services.impl;

import moustapha.teach.gestionVoyage.entities.Notification;
import moustapha.teach.gestionVoyage.entities.Validation;
import moustapha.teach.gestionVoyage.exceptions.NotificationException;
import moustapha.teach.gestionVoyage.repositories.NotificationRepository;
import moustapha.teach.gestionVoyage.services.NotificationService;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private static final String EMAIL_FROM = "no-reply@moustapha.tech";


    private final JavaMailSender javaMailSender;
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(JavaMailSender javaMailSender,
                                   NotificationRepository notificationRepository){
        this.javaMailSender = javaMailSender;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void sendValidationCode(Validation validation) {
        if (validation == null || validation.getUtilisateur() == null) {
            throw new IllegalArgumentException("Validation and user must not be null");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(EMAIL_FROM);
            message.setTo(validation.getUtilisateur().getUsername());
            message.setSubject("Votre code d'activation");
            message.setText(String.format(
                    "Bonjour %s,\n\nVotre code d'activation est : %s\n\nCe code est valable pour 10 minutes.\n\nCordialement,\nL'équipe Moustapha.tech",
                    validation.getUtilisateur().getName(),
                    validation.getCode()
            ));

            javaMailSender.send(message);

            Notification notification = new Notification();
            notification.setTitle("Code de validation");
            notification.setMessage(message.getText());
            notification.setType(Notification.NotificationType.SYSTEM_NOTIFICATION);
            notification.setChannel(Notification.NotificationChannel.EMAIL);
            notification.setUser(validation.getUtilisateur());
            notification.setSentAt(LocalDateTime.now());
            notification.setStatus(Notification.NotificationStatus.DELIVERED);

            notificationRepository.save(notification);

        } catch (MailException e) {
            Notification failedNotification = new Notification();
            failedNotification.setTitle("Échec envoi code activation");
            failedNotification.setMessage("Échec lors de l'envoi du code à " + validation.getUtilisateur().getUsername());
            failedNotification.setType(Notification.NotificationType.SYSTEM_NOTIFICATION);
            failedNotification.setChannel(Notification.NotificationChannel.EMAIL);
            failedNotification.setUser(validation.getUtilisateur());
            failedNotification.setStatus(Notification.NotificationStatus.FAILED);
            failedNotification.setErrorMessage(e.getMessage());

             notificationRepository.save(failedNotification);

            throw new NotificationException("Failed to send validation code", e);
        }
    }
}
