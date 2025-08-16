package br.com.cesarcastro.apis.ccshortener.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class QRCodeGenerator {

    public static byte[] generatePng(String content, int size) {
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M); // L,M,Q,H
            hints.put(EncodeHintType.MARGIN, 1); // borda (quiet zone)

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
                return baos.toByteArray();
            }
        }catch (WriterException | IOException e) {
            log.error("Erro ao gerar o QR Code: " + e.getMessage(), e);
            return "".getBytes(StandardCharsets.UTF_8);
        }
    }

    public static byte[] generatePng(String content) {
        return generatePng(content, 300);
    }
}
