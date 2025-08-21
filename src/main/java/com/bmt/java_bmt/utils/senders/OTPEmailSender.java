package com.bmt.java_bmt.utils.senders;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OTPEmailSender {
    @NonFinal
    @Value("${email.smtp-host}")
    String SMTP_HOST;

    @Value("${email.smtp-port}")
    String SMTP_PORT;

    @Value("${email.username}")
    String USERNAME;

    @Value("${email.password}")
    String PASSWORD;

    @Value("${email.from-email}")
    String FROM_EMAIL;

    /**
     * Sends an email with HTML content from a file, replacing a placeholder with the OTP.
     *
     * @param smtpHost     SMTP server host (e.g., "smtp.gmail.com")
     * @param smtpPort     SMTP server port (e.g., "587")
     * @param username     Sender's email username
     * @param password     Sender's email password (use app-specific password for Gmail)
     * @param fromEmail    Sender's email address
     * @param toEmail      Recipient's email address
     * @param subject      Email subject
     * @param htmlFilePath Path to the HTML template file
     * @param otp          OTP code to insert into the HTML (replaces "{OTP}" placeholder)
     * @throws IOException        If there's an issue reading the HTML file
     * @throws MessagingException If there's an issue sending the email
     */
    public void sendOtpEmail(
            String toEmail, String subject,
            String htmlFilePath, String otp, String expirationTime)
            throws IOException, MessagingException {
        // Read the HTML file content
        String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFilePath)));

        // Replace placeholder with OTP (assuming placeholder is "{OTP}")
        htmlContent = htmlContent.replace("{{.otp}}", otp);
        htmlContent = htmlContent.replace("{{.expiration_time}}", expirationTime);
        htmlContent = htmlContent.replace("{{.from_email}}", FROM_EMAIL);

        // Set up mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Enable TLS for secure connection

        // Create a session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        // Create the email message
        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);

        // Set HTML content
        message.setContent(htmlContent, "text/html; charset=utf-8");

        // Send the email
        Transport.send(message);
    }
}
