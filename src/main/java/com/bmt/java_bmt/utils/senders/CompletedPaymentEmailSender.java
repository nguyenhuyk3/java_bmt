package com.bmt.java_bmt.utils.senders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bmt.java_bmt.dto.others.FABItem;
import com.bmt.java_bmt.dto.others.TicketInformation;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompletedPaymentEmailSender {
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

    private String generateFoodItemsHtml(List<FABItem> FABItems) {
        if (FABItems == null || FABItems.isEmpty()) {
            return "";
        }

        StringBuilder html = new StringBuilder();

        html.append("<div style=\"margin: 30px 0;\">\n")
                .append(
                        "    <h3 style=\"color: #00bc69; margin-bottom: 20px; text-align: center; font-size: 1.4em; font-weight: bold;\">🍿 ĐỒ ĂN & THỨC UỐNG</h3>\n")
                .append(
                        "    <div style=\"background: #f8f9fa; border-radius: 12px; padding: 20px; border-left: 4px solid #00bc69; box-shadow: 0 2px 8px rgba(0,0,0,0.1);\">\n")
                .append("        <table style=\"width: 100%; border-collapse: collapse;\">\n");

        for (int i = 0; i < FABItems.size(); i++) {
            FABItem item = FABItems.get(i);

            html.append("            <tr>\n")
                    .append(
                            "                <td style=\"padding: 12px 15px; background: white; border-radius: 10px; box-shadow: 0 2px 6px rgba(0,0,0,0.08); margin-bottom: 10px; width: 70%; vertical-align: middle;")
                    .append(i < FABItems.size() - 1 ? " border-bottom: 10px solid transparent;" : "")
                    .append("\">\n")
                    .append("                    <span style=\"font-weight: 600; color: #333; font-size: 1.05em;\">")
                    .append(item.getEmoji() != null ? item.getEmoji() + " " : "")
                    .append(item.getName())
                    .append("</span>\n")
                    .append("                </td>\n")
                    .append(
                            "                <td style=\"padding: 12px 15px; background: white; border-radius: 10px; box-shadow: 0 2px 6px rgba(0,0,0,0.08); text-align: right; width: 30%; vertical-align: middle;")
                    .append(i < FABItems.size() - 1 ? " border-bottom: 10px solid transparent;" : "")
                    .append("\">\n")
                    .append(
                            "                    <span style=\"background: #00bc69; color: white; padding: 6px 14px; border-radius: 20px; font-weight: bold; font-size: 0.9em; min-width: 35px; display: inline-block; text-align: center;\">x")
                    .append(item.getQuantity())
                    .append("</span>\n")
                    .append("                </td>\n")
                    .append("            </tr>\n");

            if (i < FABItems.size() - 1) {
                html.append("            <tr><td colspan=\"2\" style=\"height: 10px;\"></td></tr>\n");
            }
        }

        html.append("        </table>\n").append("    </div>\n").append("</div>\n");

        return html.toString();
    }

    public void sendTicketConfirmation(
            String toEmail, String subject, TicketInformation ticketInformation, String htmlFilePath)
            throws IOException, MessagingException {
        String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFilePath)));

        htmlContent = htmlContent.replace("{{.film_title}}", ticketInformation.getFilmTitle());
        htmlContent = htmlContent.replace("{{.genres}}", ticketInformation.getGenres());
        htmlContent = htmlContent.replace("{{.duration}}", String.valueOf(ticketInformation.getDuration()));
        htmlContent = htmlContent.replace("{{.poster_url}}", ticketInformation.getPosterUrl());
        htmlContent = htmlContent.replace("{{.cinema_name}}", ticketInformation.getCinemaName());
        htmlContent = htmlContent.replace("{{.city}}", ticketInformation.getCity());
        htmlContent = htmlContent.replace("{{.address}}", ticketInformation.getAddress());
        htmlContent = htmlContent.replace("{{.auditorium}}", ticketInformation.getAuditorium());
        htmlContent = htmlContent.replace("{{.show_date}}", ticketInformation.getShowDate());
        htmlContent = htmlContent.replace("{{.show_time}}", ticketInformation.getShowTime());
        htmlContent = htmlContent.replace("{{.seats}}", ticketInformation.getSeats());
        htmlContent = htmlContent.replace("{{.from_email}}", FROM_EMAIL);

        String foodItemsHtml = generateFoodItemsHtml(ticketInformation.getFABItems());

        htmlContent = htmlContent.replace("{{.fab_items}}", foodItemsHtml);

        Properties properties = new Properties();

        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=utf-8");

        Transport.send(message);
    }
}
