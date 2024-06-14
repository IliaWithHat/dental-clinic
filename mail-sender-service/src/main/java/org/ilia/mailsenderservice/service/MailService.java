package org.ilia.mailsenderservice.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ilia.mailsenderservice.entity.EmailDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${EMAIL_ADDRESS}")
    private String senderEmailAddress;

    @SneakyThrows
    public void sendEmail(EmailDetails emailDetails) {
        log.debug("In sendEmail(), receiver: {}, subject: {}", emailDetails.getReceiverEmail(), emailDetails.getSubject());
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, UTF_8.name());
        messageHelper.setFrom(senderEmailAddress);
        messageHelper.setTo(emailDetails.getReceiverEmail());
        messageHelper.setSubject(emailDetails.getSubject());
        messageHelper.setText(emailDetails.getContent(), true);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.error(e.getMessage());
        }
    }
}
