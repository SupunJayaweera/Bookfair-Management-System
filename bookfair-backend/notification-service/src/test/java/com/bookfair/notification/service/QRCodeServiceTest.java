package com.bookfair.notification.service;

import com.bookfair.notification.exception.QRCodeGenerationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class QRCodeServiceTest {

    @InjectMocks
    private QRCodeService qrCodeService;

    @Test
    void testGenerateQRCodeImage_Success() {
        // Arrange
        ReflectionTestUtils.setField(qrCodeService, "qrCodeWidth", 300);
        ReflectionTestUtils.setField(qrCodeService, "qrCodeHeight", 300);
        String qrContent = "TEST_QR_CONTENT_123";

        // Act
        byte[] result = qrCodeService.generateQRCodeImage(qrContent);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testGenerateQRCodeImage_WithEmptyContent() {
        // Arrange
        ReflectionTestUtils.setField(qrCodeService, "qrCodeWidth", 300);
        ReflectionTestUtils.setField(qrCodeService, "qrCodeHeight", 300);
        String qrContent = "";

        // Act & Assert
        assertThrows(QRCodeGenerationException.class, () -> 
            qrCodeService.generateQRCodeImage(qrContent)
        );
    }
}
