package com.bookfair.reservation.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;

@Service
public class QRCodeService {

    @Value("${qr.verification.url:http://localhost:5173/verify}")
    private String verificationBaseUrl;

    public String generateQRCode(Long reservationId) {
        // Generate a unique token
        String token = UUID.randomUUID().toString();
        // Create verification URL with token
        return verificationBaseUrl + "?token=" + token + "&rid=" + reservationId;
    }

    public String generateQRCodeImage(String qrContent) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}
