package com.github.karixdev.ratingyoutubethumbnailsapi.email.provider;

import com.github.karixdev.ratingyoutubethumbnailsapi.email.EmailServiceProvider;
import com.github.karixdev.ratingyoutubethumbnailsapi.email.exception.EmailSendingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Component("javaEmailServiceProvider")
@RequiredArgsConstructor
public class JavaEmailServiceProvider implements EmailServiceProvider {
    private final JavaMailSender mailSender;
    @Value("${email-sender.sender}")
    private String sender;


    @Async
    @Override
    public void sendEmail(String recipientEmail, String topic, String body) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(mimeMessage, "utf-8");

        try {
            helper.setFrom(sender);
            helper.setTo(recipientEmail);
            helper.setSubject(topic);
            helper.setText(body, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Error while sending email", e);
            throw new EmailSendingException();
        }
    }
}
