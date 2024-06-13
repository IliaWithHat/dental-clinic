package org.ilia.mailsenderservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilia.mailsenderservice.entity.EmailDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${EMAIL_ADDRESS}")
    private String senderEmailAddress;

    public void sendEmail(EmailDetails emailDetails) {
        log.debug("In sendEmail(), receiver: {}, subject: {}", emailDetails.getReceiverEmail(), emailDetails.getSubject());
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try {
            mimeMessageHelper.setFrom(senderEmailAddress);
            mimeMessageHelper.setTo(emailDetails.getReceiverEmail());
            mimeMessageHelper.setSubject(emailDetails.getSubject());
            mimeMessage.setContent(emailDetails.getContent(), "text/html; charset=utf-8");
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
        mailSender.send(mimeMessage);
    }
}
