package com.demo.HotelBooking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email:kirankumarmaddukuri8@gmail.com}")
    private String senderEmail;

    @Value("${brevo.sender.name:Hotel Booking}")
    private String senderName;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendRegistrationEmail(String email, String name) {
        String html = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;\">" +
                "<h2 style=\"color: #007bff; text-align: center;\">Welcome to Hotel Booking!</h2>" +
                "<p style=\"font-size: 16px; color: #333;\">Hello <strong>" + name + "</strong>,</p>" +
                "<p style=\"font-size: 16px; color: #333;\">Your account has been successfully created. You can now browse and book luxury hotels across India.</p>" +
                "<div style=\"text-align: center; margin-top: 30px;\"><a href=\"http://localhost:5173/\" style=\"background-color: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold;\">Explore Hotels</a></div>" +
                "</div>";
        
        return sendEmailAndReturnError(email, "Welcome to Hotel Booking!", html) == null;
    }

    public String sendRegistrationEmailWithError(String email, String name) {
        String html = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;\">" +
                "<h2 style=\"color: #007bff; text-align: center;\">Welcome to Hotel Booking!</h2>" +
                "<p style=\"font-size: 16px; color: #333;\">Hello <strong>" + name + "</strong>,</p>" +
                "<p style=\"font-size: 16px; color: #333;\">Your account has been successfully created. You can now browse and book luxury hotels across India.</p>" +
                "<div style=\"text-align: center; margin-top: 30px;\"><a href=\"http://localhost:5173/\" style=\"background-color: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold;\">Explore Hotels</a></div>" +
                "</div>";
        
        return sendEmailAndReturnError(email, "Welcome to Hotel Booking!", html);
    }

    public boolean sendBookingConfirmation(String email, Long bookingId, String hotelName) {
        String html = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;\">" +
                "<h2 style=\"color: #28a745; text-align: center;\">Booking Confirmed!</h2>" +
                "<p style=\"font-size: 16px; color: #333;\">Great news! Your booking at <strong>" + hotelName + "</strong> has been officially confirmed by our admin team.</p>" +
                "<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0; text-align: center;\">" +
                "<h3 style=\"margin: 0; color: #555;\">Booking ID: #" + bookingId + "</h3>" +
                "</div>" +
                "<p style=\"font-size: 16px; color: #333;\">We look forward to hosting you. Safe travels!</p>" +
                "</div>";
                
        return sendEmailAndReturnError(email, "Booking Confirmation - Hotel Booking", html) == null;
    }

    public boolean sendUserInitiatedCancellation(String email, Long bookingId) {
        String html = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;\">" +
                "<h2 style=\"color: #dc3545; text-align: center;\">Booking Cancelled</h2>" +
                "<p style=\"font-size: 16px; color: #333;\">You have successfully cancelled your booking (ID: #" + bookingId + ").</p>" +
                "<p style=\"font-size: 16px; color: #333;\">If this was you, you can safely ignore this email. If you did not make this cancellation, please contact us immediately.</p>" +
                "</div>";
                
        return sendEmailAndReturnError(email, "Booking Cancelled by User - Hotel Booking", html) == null;
    }

    public boolean sendAdminInitiatedCancellation(String email, Long bookingId) {
        String html = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;\">" +
                "<h2 style=\"color: #dc3545; text-align: center;\">Booking Request Rejected / Cancelled</h2>" +
                "<p style=\"font-size: 16px; color: #333;\">We're writing to let you know that your booking request (ID: #" + bookingId + ") has been declined or cancelled by the hotel administration.</p>" +
                "<p style=\"font-size: 16px; color: #333;\">If you believe this was a mistake, or if you'd like to book another stay, please visit our website or contact support.</p>" +
                "</div>";
                
        return sendEmailAndReturnError(email, "Booking Rejected - Hotel Booking", html) == null;
    }

    private String sendEmailAndReturnError(String email, String subject, String body) {
        try {
            String url = "https://api.brevo.com/v3/smtp/email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> payload = new HashMap<>();
            payload.put("sender", Map.of("name", senderName, "email", senderEmail));
            payload.put("to", List.of(Map.of("email", email, "name", email)));
            payload.put("subject", subject);
            payload.put("htmlContent", body);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Email sent to {} with subject '{}' via Brevo API", email, subject);
                return null;
            }

            String errorMessage = "Brevo API responded with status " + response.getStatusCode();
            logger.error(errorMessage);
            return errorMessage;
        } catch (RestClientException e) {
            logger.error("Failed to send email to {} with subject '{}' via Brevo API: {}", email, subject, e.getMessage(), e);
            return e.getMessage();
        }
    }
}
