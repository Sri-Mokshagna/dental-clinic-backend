package com.dentalclinic.DentalClinic.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;

/**
 * Very small helper to write a plain-text PDF. Enough to generate a one-page file
 * containing patient name, file type and a short description (for sending via WhatsApp).
 */
public final class PdfGeneratorUtil {

    private PdfGeneratorUtil() {}

    public static Path generateSimplePdf(String title, String body, Path outputFile) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                // Title
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                cs.newLineAtOffset(50, 750);
                cs.showText(title != null ? title : "Document");
                cs.endText();

                // Body (wrap plain)
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 11);
                cs.newLineAtOffset(50, 720);

                String[] lines = wrapText(body != null ? body : "", 90);
                for (String line : lines) {
                    cs.showText(line);
                    cs.newLineAtOffset(0, -14);
                }
                cs.endText();
            }

            // Ensure parent dirs exist
            Files.createDirectories(outputFile.getParent());
            doc.save(outputFile.toFile());
        }
        return outputFile;
    }

    private static String[] wrapText(String text, int lineLength) {
        if (text == null) return new String[]{};
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        java.util.List<String> lines = new java.util.ArrayList<>();
        for (String w : words) {
            if (line.length() + w.length() + 1 > lineLength) {
                lines.add(line.toString().trim());
                line = new StringBuilder();
            }
            line.append(w).append(" ");
        }
        if (line.length() > 0) lines.add(line.toString().trim());
        return lines.toArray(new String[0]);
    }
}
