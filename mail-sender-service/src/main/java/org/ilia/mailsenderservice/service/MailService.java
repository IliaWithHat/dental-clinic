package org.ilia.mailsenderservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.experimental.FieldDefaults;
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
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MailService {

    JavaMailSender mailSender;
    TemplateEngine templateEngine;
    String senderEmailAddress;

    public MailService(JavaMailSender mailSender, TemplateEngine templateEngine,
                       @Value("${EMAIL_ADDRESS}") String senderEmailAddress) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.senderEmailAddress = senderEmailAddress;
    }

    public void sendEmailToUser(MailDetails mailDetails) {
        sendEmail(mailDetails.getPatientEmail(), mailDetails.getSubject().getEmailSubject(), generateContent(mailDetails));
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
        return templateEngine.process("email/" + mailDetails.getSubject().getTemplateName(), context);
    }
}
