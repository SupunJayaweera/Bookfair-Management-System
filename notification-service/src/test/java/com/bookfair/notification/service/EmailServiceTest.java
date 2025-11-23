package com.bookfair.notification.service;

import com.bookfair.notification.exception.EmailSendException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.internet.MimeMessage;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private QRCodeService qrCodeService;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@bookfair.lk");
    }

    @Test
    void testSendReservationConfirmation_Success() throws Exception {
        // Arrange
        String toEmail = "user@example.com";
        Long reservationId = 123L;
        Set<Long> stallIds = Set.of(1L, 2L, 3L);
        String qrCode = "QR123456";
        byte[] qrCodeImage = new byte[]{1, 2, 3};

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(qrCodeService.generateQRCodeImage(qrCode)).thenReturn(qrCodeImage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        assertDoesNotThrow(() -> 
            emailService.sendReservationConfirmation(toEmail, reservationId, stallIds, qrCode)
        );

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(qrCodeService, times(1)).generateQRCodeImage(qrCode);
    }

    @Test
    void testSendReservationConfirmation_Failure() {
        // Arrange
        String toEmail = "user@example.com";
        Long reservationId = 123L;
        Set<Long> stallIds = Set.of(1L, 2L, 3L);
        String qrCode = "QR123456";

        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail server error"));

        // Act & Assert
        assertThrows(EmailSendException.class, () -> 
            emailService.sendReservationConfirmation(toEmail, reservationId, stallIds, qrCode)
        );

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
