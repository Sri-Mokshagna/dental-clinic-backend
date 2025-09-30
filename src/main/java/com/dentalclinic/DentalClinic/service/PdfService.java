package com.dentalclinic.DentalClinic.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.dentalclinic.DentalClinic.model.Bill;

@Service
public class PdfService {

    public Path generateBillPdf(Bill bill, String fileName) throws IOException {
        // Create a new PDF document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        // Start writing content
        PDPageContentStream content = new PDPageContentStream(document, page);
        content.setFont(PDType1Font.HELVETICA_BOLD, 16);
        content.beginText();
        content.newLineAtOffset(50, 700);
        content.showText("Dental Clinic Bill");
        content.endText();

        // Bill details
        content.setFont(PDType1Font.HELVETICA, 12);
        content.beginText();
        content.newLineAtOffset(50, 650);
        content.showText("Bill ID: " + bill.getId());
        content.newLineAtOffset(0, -20);
        content.showText("Patient: " + bill.getPatient().getFullName());
        content.newLineAtOffset(0, -20);
        content.showText("Amount: ₹" + bill.getAmount());
        content.newLineAtOffset(0, -20);
        content.showText("Status: " + bill.getStatus());
        content.endText();

        // Close stream
        content.close();

        // Save to file system (uploads folder)
        Path path = Paths.get("uploads/" + fileName);
        document.save(path.toFile());
        document.close();

        return path;
    }
}
