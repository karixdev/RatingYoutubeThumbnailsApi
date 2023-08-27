package com.github.karixdev.ratingyoutubethumbnailsapi.email;

public interface EmailServiceProvider {
    void sendEmail(String recipientEmail, String topic, String body);
}
