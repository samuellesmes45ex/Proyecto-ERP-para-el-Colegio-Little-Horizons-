package com.littlehorizons.erp.util;

import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.Student;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Service for generating DIAN-compliant simulated Electronic Invoices in PDF format.
 */
public class PdfInvoiceGenerator {

    public static final String SCHOOL_NAME = "Colegio Internacional Little Horizons S.A.S.";
    public static final String SCHOOL_NIT = "900.123.456-7";
    public static final String SCHOOL_ADDRESS = "Carrera 45 # 120-35, Bogotá D.C., Colombia";
    public static final String SCHOOL_PHONE = "+57 (601) 789-0123";
    public static final String SCHOOL_EMAIL = "facturacion@littlehorizons.edu.co";
    public static final String DIAN_RESOLUTION = "Resolución DIAN No. 18760000001 del 15/01/2026, Prefijo FAC, Rango 0001 al 99999";

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$#,##0.00");

    /**
     * Calculates the simulated CUFE (SHA-256 hash).
     */
    public static String calculateCufe(String invoiceNumber, LocalDate date, String totalStr) {
        try {
            String raw = invoiceNumber + "|" + SCHOOL_NIT + "|" + date + "|" + totalStr + "|DIAN-SECRET-KEY-LITTLE-HORIZONS";
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "cufe-simulado-" + System.currentTimeMillis();
        }
    }

    /**
     * Generates a DIAN Electronic Invoice PDF file.
     *
     * @param invoiceNumber Invoice number (e.g. FAC-00105)
     * @param payment       Payment data
     * @param student       Associated student
     * @param guardian      Associated responsible guardian (or null)
     * @return File object representing the generated PDF
     */
    public static File generateInvoicePdf(String invoiceNumber, Payment payment, Student student, Guardian guardian) throws Exception {
        File dir = new File("invoices");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String safeNumber = invoiceNumber.replace("/", "-").replace("\\", "-");
        File pdfFile = new File(dir, "factura_" + safeNumber + ".pdf");

        LocalDate issueDate = payment.getPaymentDate() != null ? payment.getPaymentDate() : LocalDate.now();
        String cufe = calculateCufe(invoiceNumber, issueDate, payment.getAmountPaid().toString());

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();

        // Fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(26, 54, 93));
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
        Font cufeFont = FontFactory.getFont(FontFactory.COURIER, 7, Color.GRAY);

        // Header Table (School Info + Invoice Number Box)
        PdfPTable topTable = new PdfPTable(2);
        topTable.setWidthPercentage(100);
        topTable.setWidths(new float[]{65, 35});

        PdfPCell schoolCell = new PdfPCell();
        schoolCell.setBorder(PdfPCell.NO_BORDER);
        schoolCell.addElement(new Paragraph(SCHOOL_NAME, titleFont));
        schoolCell.addElement(new Paragraph("NIT: " + SCHOOL_NIT, subtitleFont));
        schoolCell.addElement(new Paragraph(SCHOOL_ADDRESS, subtitleFont));
        schoolCell.addElement(new Paragraph("Tel: " + SCHOOL_PHONE + " | " + SCHOOL_EMAIL, subtitleFont));
        schoolCell.addElement(new Paragraph(DIAN_RESOLUTION, subtitleFont));
        topTable.addCell(schoolCell);

        PdfPCell invBoxCell = new PdfPCell();
        invBoxCell.setBackgroundColor(new Color(240, 244, 248));
        invBoxCell.setPadding(10);
        invBoxCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        invBoxCell.addElement(new Paragraph("FACTURA ELECTRÓNICA DE VENTA", boldFont));
        invBoxCell.addElement(new Paragraph(invoiceNumber, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, new Color(197, 48, 48))));
        invBoxCell.addElement(new Paragraph("Fecha de Emisión: " + issueDate.format(DateTimeFormatter.ISO_DATE), subtitleFont));
        invBoxCell.addElement(new Paragraph("Método de Pago: " + payment.getPaymentMethod(), subtitleFont));
        topTable.addCell(invBoxCell);

        document.add(topTable);
        document.add(new Paragraph(" "));

        // Customer & Student Details Table
        PdfPTable clientTable = new PdfPTable(2);
        clientTable.setWidthPercentage(100);
        clientTable.setWidths(new float[]{50, 50});

        PdfPCell guardianBox = new PdfPCell();
        guardianBox.setBackgroundColor(new Color(248, 249, 250));
        guardianBox.setPadding(8);
        guardianBox.addElement(new Paragraph("DATOS DEL CLIENTE / ACUDIENTE", boldFont));
        if (guardian != null) {
            guardianBox.addElement(new Paragraph("Nombre: " + guardian.getFullName(), bodyFont));
            guardianBox.addElement(new Paragraph("Doc: " + guardian.getDocumentType() + " " + guardian.getDocumentNumber(), bodyFont));
            guardianBox.addElement(new Paragraph("Teléfono: " + guardian.getPrimaryPhone(), bodyFont));
            guardianBox.addElement(new Paragraph("Dirección: " + (guardian.getAddress() != null ? guardian.getAddress() : "Bogotá"), bodyFont));
        } else {
            guardianBox.addElement(new Paragraph("Nombre: Acudiente Registrado", bodyFont));
            guardianBox.addElement(new Paragraph("Responsable del pago institucional", bodyFont));
        }
        clientTable.addCell(guardianBox);

        PdfPCell studentBox = new PdfPCell();
        studentBox.setBackgroundColor(new Color(248, 249, 250));
        studentBox.setPadding(8);
        studentBox.addElement(new Paragraph("DATOS DEL ESTUDIANTE BENEFICIARIO", boldFont));
        if (student != null) {
            studentBox.addElement(new Paragraph("Nombre: " + student.getFullName(), bodyFont));
            studentBox.addElement(new Paragraph("Doc: " + student.getDocumentType() + " " + student.getDocumentNumber(), bodyFont));
            studentBox.addElement(new Paragraph("Año Académico: " + (payment.getAcademicYear() != null ? payment.getAcademicYear() : LocalDate.now().getYear()), bodyFont));
            studentBox.addElement(new Paragraph("Período: " + (payment.getAcademicPeriod() != null ? payment.getAcademicPeriod() : "Anual"), bodyFont));
        } else {
            studentBox.addElement(new Paragraph("Estudiante ID: " + payment.getStudentId(), bodyFont));
        }
        clientTable.addCell(studentBox);

        document.add(clientTable);
        document.add(new Paragraph(" "));

        // Concepts Itemized Table
        PdfPTable itemsTable = new PdfPTable(4);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[]{55, 15, 15, 15});

        String[] headers = {"Descripción del Concepto Educativo", "Cantidad", "Valor Unitario", "Subtotal"};
        for (String h : headers) {
            PdfPCell hCell = new PdfPCell(new Phrase(h, headerFont));
            hCell.setBackgroundColor(new Color(26, 54, 93));
            hCell.setPadding(6);
            hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemsTable.addCell(hCell);
        }

        String conceptDesc = payment.getConceptName() != null ? payment.getConceptName() : "Servicio Educativo / Pensión Escolar";
        if (payment.getNotes() != null && !payment.getNotes().trim().isEmpty()) {
            conceptDesc += " (" + payment.getNotes() + ")";
        }

        PdfPCell descCell = new PdfPCell(new Phrase(conceptDesc, bodyFont));
        descCell.setPadding(6);
        itemsTable.addCell(descCell);

        PdfPCell qtyCell = new PdfPCell(new Phrase("1", bodyFont));
        qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        qtyCell.setPadding(6);
        itemsTable.addCell(qtyCell);

        PdfPCell valCell = new PdfPCell(new Phrase(CURRENCY_FORMAT.format(payment.getAmountPaid()), bodyFont));
        valCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valCell.setPadding(6);
        itemsTable.addCell(valCell);

        PdfPCell subCell = new PdfPCell(new Phrase(CURRENCY_FORMAT.format(payment.getAmountPaid()), bodyFont));
        subCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        subCell.setPadding(6);
        itemsTable.addCell(subCell);

        document.add(itemsTable);

        // Totals Table
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(40);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.setWidths(new float[]{50, 50});

        totalTable.addCell(new Phrase("Subtotal:", boldFont));
        PdfPCell stCell = new PdfPCell(new Phrase(CURRENCY_FORMAT.format(payment.getAmountPaid()), bodyFont));
        stCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.addCell(stCell);

        totalTable.addCell(new Phrase("IVA (Exento Ley 1819):", bodyFont));
        PdfPCell ivaCell = new PdfPCell(new Phrase("$0.00", bodyFont));
        ivaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.addCell(ivaCell);

        totalTable.addCell(new Phrase("TOTAL A PAGAR:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(26, 54, 93))));
        PdfPCell grandTotalCell = new PdfPCell(new Phrase(CURRENCY_FORMAT.format(payment.getAmountPaid()), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(26, 54, 93))));
        grandTotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.addCell(grandTotalCell);

        document.add(totalTable);
        document.add(new Paragraph(" "));

        // DIAN Electronic Compliance Box (CUFE + QR Code)
        PdfPTable dianTable = new PdfPTable(2);
        dianTable.setWidthPercentage(100);
        dianTable.setWidths(new float[]{75, 25});

        PdfPCell cufeCell = new PdfPCell();
        cufeCell.setBorder(PdfPCell.BOX);
        cufeCell.setBackgroundColor(new Color(250, 250, 250));
        cufeCell.setPadding(8);
        cufeCell.addElement(new Paragraph("DOCUMENTO OFICIAL DIAN - FACTURACIÓN ELECTRÓNICA", boldFont));
        cufeCell.addElement(new Paragraph("CUFE (Código Único de Factura Electrónica):", boldFont));
        cufeCell.addElement(new Paragraph(cufe, cufeFont));
        cufeCell.addElement(new Paragraph("Recibo de caja asociado: " + (payment.getReceiptNumber() != null ? payment.getReceiptNumber() : "N/A"), subtitleFont));
        cufeCell.addElement(new Paragraph("Este documento es una representación gráfica de la factura electrónica emitida según normativa DIAN.", subtitleFont));
        dianTable.addCell(cufeCell);

        // QR Code embedding
        PdfPCell qrCell = new PdfPCell();
        qrCell.setBorder(PdfPCell.BOX);
        qrCell.setBackgroundColor(new Color(250, 250, 250));
        qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        qrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        qrCell.setPadding(5);

        try {
            String qrData = "NumFac=" + invoiceNumber + "\nFecFac=" + issueDate + "\nNitFac=" + SCHOOL_NIT +
                    "\nValFac=" + payment.getAmountPaid() + "\nCUFE=" + cufe;
            byte[] qrBytes = QrCodeGenerator.generateQrPng(qrData, 100, 100);
            Image qrImage = Image.getInstance(qrBytes);
            qrImage.setAlignment(Element.ALIGN_CENTER);
            qrCell.addElement(qrImage);
        } catch (Exception e) {
            qrCell.addElement(new Paragraph("[QR Code]", subtitleFont));
        }
        dianTable.addCell(qrCell);

        document.add(dianTable);

        document.close();
        return pdfFile;
    }
}
