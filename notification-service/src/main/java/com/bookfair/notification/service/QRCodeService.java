package com.bookfair.notification.service;

import com.bookfair.notification.exception.QRCodeGenerationException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
@Slf4j
public class QRCodeService {

    @Value("${app.notification.qrcode.width:300}")
    private int qrCodeWidth;
    
    @Value("${app.notification.qrcode.height:300}")
    private int qrCodeHeight;

    public byte[] generateQRCodeImage(String qrContent) {
        try {
            log.debug("Generating QR code for content: {}", qrContent);
            
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, qrCodeWidth, qrCodeHeight);

            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", outputStream);

            byte[] qrCodeBytes = outputStream.toByteArray();
            log.debug("QR code generated successfully. Size: {} bytes", qrCodeBytes.length);
            
            return qrCodeBytes;
        } catch (Exception e) {
            log.error("Failed to generate QR code for content: {}", qrContent, e);
            throw new QRCodeGenerationException("Failed to generate QR code", e);
        }
    }
}
