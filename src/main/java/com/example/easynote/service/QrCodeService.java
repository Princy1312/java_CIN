package com.example.easynote.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
public class QrCodeService {
    public String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public String generateBase64(String content) throws WriterException, IOException {
        var writer = new QRCodeWriter();
        var matrix = writer.encode(content, BarcodeFormat.QR_CODE, 300, 300);
        var out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    public String getQrBase64(String token) throws WriterException, IOException {
        return generateBase64("CIN-VERIFY:" + token);
    }
}
