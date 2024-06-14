package org.ilia.mailsenderservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilia.mailsenderservice.entity.MailDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${EMAIL_ADDRESS}")
    private String senderEmailAddress;

    public void sendEmailToUser(MailDetails mailDetails) {
        String receiverEmail = mailDetails.getUserEmail();
        String subject = switch (mailDetails.getSubject()) {
            case APPOINTMENT_CONFIRMATION -> "Appointment Confirmation";
            case APPOINTMENT_REMINDER -> "Reminder: Upcoming Appointment";
            case WELCOME -> "Welcome to Dental Clinic!";
        };
        String content = generateContent(mailDetails);

        sendEmail(receiverEmail, subject, content);
    }

    private void sendEmail(String receiverEmail, String subject, String content) {
        log.debug("In sendEmail(), receiver: {}, subject: {}", receiverEmail, subject);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, UTF_8.name());
            messageHelper.setFrom(senderEmailAddress);
            messageHelper.setTo(receiverEmail);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            log.error(e.getMessage());
        }
    }

    private String generateContent(MailDetails mailDetails) {
        Context context = new Context();
        context.setVariable("mailDetails", mailDetails);

        String templateName = switch (mailDetails.getSubject()) {
            case APPOINTMENT_CONFIRMATION -> "appointment-confirmation";
            case APPOINTMENT_REMINDER -> "appointment-reminder";
            case WELCOME -> "welcome";
        };

        return templateEngine.process("email/" + templateName, context);
    }
}
