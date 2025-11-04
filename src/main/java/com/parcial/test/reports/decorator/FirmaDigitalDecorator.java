package com.parcial.test.reports.decorator;

import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class FirmaDigitalDecorator implements ReporteDecorator {

    private final ReporteDecorator reporte;

    @Override
    public String obtenerContenido() {
        String contenidoOriginal = reporte.obtenerContenido();

        // Generar hash SHA-256 del contenido
        String hash = generarHash(contenidoOriginal);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        StringBuilder sb = new StringBuilder();
        sb.append(contenidoOriginal);
        sb.append("\n\n");
        sb.append("╔════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                      FIRMA DIGITAL                                 ║\n");
        sb.append("╠════════════════════════════════════════════════════════════════════╣\n");
        sb.append("║  Algoritmo: SHA-256                                                ║\n");
        sb.append("║  Hash: ").append(String.format("%-58s", hash.substring(0, Math.min(58, hash.length())))).append("║\n");
        sb.append("║  Fecha de firma: ").append(String.format("%-49s", timestamp)).append("║\n");
        sb.append("║  Firmante: Sistema SERF - FinanCorp S.A.                          ║\n");
        sb.append("║  Estado: VÁLIDO ✓                                                  ║\n");
        sb.append("╚════════════════════════════════════════════════════════════════════╝\n");
        sb.append("\n");
        sb.append("Este documento ha sido firmado digitalmente y cualquier modificación\n");
        sb.append("posterior invalidará la firma.\n");

        return sb.toString();
    }

    private String generarHash(String contenido) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(contenido.getBytes(StandardCharsets.UTF_8));

            // Convertir bytes a hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new com.parcial.test.exceptions.ReportGenerationException("Error al generar hash SHA-256 para la firma digital", e);
        }
    }
}

