package com.bookfair.notification.service;

import com.bookfair.notification.exception.EmailSendException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final QRCodeService qrCodeService;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @CircuitBreaker(name = "emailService", fallbackMethod = "sendEmailFallback")
    @Retry(name = "emailService")
    public void sendReservationConfirmation(String toEmail, Long reservationId, Set<Long> stallIds, String qrCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Bookfair Reservation Confirmation - #" + reservationId);

            String htmlContent = buildEmailContent(reservationId, stallIds, qrCode);
            helper.setText(htmlContent, true);

            // Generate and attach QR code
            byte[] qrCodeImage = qrCodeService.generateQRCodeImage(qrCode);
            helper.addAttachment("qr-code.png", new ByteArrayResource(qrCodeImage));

            mailSender.send(message);
            log.info("Reservation confirmation email sent successfully to {} for reservation #{}", toEmail, reservationId);
        } catch (Exception e) {
            log.error("Failed to send email to {} for reservation #{}", toEmail, reservationId, e);
            throw new EmailSendException("Failed to send email to " + toEmail, e);
        }
    }

    private void sendEmailFallback(String toEmail, Long reservationId, Set<Long> stallIds, String qrCode, Exception ex) {
        log.error("Circuit breaker activated. Fallback triggered for email to {} for reservation #{}", toEmail, reservationId);
        // In production, you might want to:
        // 1. Store the notification in a dead letter queue
        // 2. Save to database for manual retry
        // 3. Send to alternative notification channel (SMS, push notification)
        throw new EmailSendException("Email service is currently unavailable. Notification saved for retry.", ex);
    }

    private String buildEmailContent(Long reservationId, Set<Long> stallIds, String qrCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("<head><style>");
        sb.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        sb.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        sb.append(".header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }");
        sb.append(".content { background-color: #f9f9f9; padding: 20px; margin-top: 20px; }");
        sb.append(
                ".stall-info { background-color: white; padding: 15px; margin: 10px 0; border-left: 4px solid #3498db; }");
        sb.append(".qr-section { text-align: center; margin: 20px 0; padding: 20px; background-color: white; }");
        sb.append(".footer { text-align: center; margin-top: 20px; color: #777; font-size: 12px; }");
        sb.append("</style></head>");
        sb.append("<body>");
        sb.append("<div class='container'>");
        sb.append("<div class='header'>");
        sb.append("<h1>Colombo International Bookfair</h1>");
        sb.append("<h2>Reservation Confirmed!</h2>");
        sb.append("</div>");
        sb.append("<div class='content'>");
        sb.append("<p>Dear Exhibitor,</p>");
        sb.append("<p>Your stall reservation has been confirmed successfully!</p>");
        sb.append("<div class='stall-info'>");
        sb.append("<h3>Reservation Details</h3>");
        sb.append("<p><strong>Reservation ID:</strong> ").append(reservationId).append("</p>");
        sb.append("<p><strong>Reserved Stalls:</strong> ")
                .append(String.join(", ", stallIds.stream().map(String::valueOf).toArray(String[]::new)))
                .append("</p>");
        sb.append("<p><strong>QR Code:</strong> ").append(qrCode).append("</p>");
        sb.append("</div>");
        sb.append("<div class='qr-section'>");
        sb.append("<h3>Your Entry Pass QR Code</h3>");
        sb.append("<p>Please download the attached QR code and present it at the exhibition entrance.</p>");
        sb.append("<p><em>This QR code is unique to your reservation and should not be shared.</em></p>");
        sb.append("</div>");
        sb.append("<p><strong>Important Information:</strong></p>");
        sb.append("<ul>");
        sb.append("<li>Save this QR code for entry to the exhibition premises</li>");
        sb.append("<li>The exhibition will be held from [Date] to [Date]</li>");
        sb.append("<li>Setup time: [Time]</li>");
        sb.append("<li>Opening hours: [Time]</li>");
        sb.append("</ul>");
        sb.append("<p>If you have any questions, please contact us at info@bookfair.lk</p>");
        sb.append("</div>");
        sb.append("<div class='footer'>");
        sb.append("<p>© 2025 Sri Lanka Book Publishers' Association. All rights reserved.</p>");
        sb.append("</div>");
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }
}
