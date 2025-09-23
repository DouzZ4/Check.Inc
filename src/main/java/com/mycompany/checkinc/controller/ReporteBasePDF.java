package com.mycompany.checkinc.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.mycompany.checkinc.entities.Usuario;

/**
 * Clase abstracta para la estructura básica de reportes PDF.
 * Los módulos específicos deben extender esta clase y agregar su lógica particular.
 */
public abstract class ReporteBasePDF {

    /**
     * Agrega el encabezado del paciente al documento PDF.
     */
    protected void agregarEncabezadoPaciente(Document document, Usuario usuario) throws DocumentException {
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.DARK_GRAY);
        Font fontNormal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
        Paragraph titulo = new Paragraph("Reporte de Paciente", fontTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Nombre: " + usuario.getNombres() + " " + usuario.getApellidos(), fontNormal));
        document.add(new Paragraph("Documento: " + usuario.getDocumento(), fontNormal));
        document.add(new Paragraph("Correo: " + usuario.getCorreo(), fontNormal));
        document.add(new Paragraph("Edad: " + usuario.getEdad(), fontNormal));
        document.add(new Paragraph("Tipo de Diabetes: " + usuario.getTipoDiabetes(), fontNormal));
        document.add(new Paragraph("Insulodependiente: " + (usuario.getEsInsulodependiente() ? "Sí" : "No"), fontNormal));
        document.add(new Paragraph(" "));
    }

    /**
     * Estiliza una celda para tablas PDF.
     */
    protected PdfPCell estilizarCelda(String texto, Font font, BaseColor bgColor) {
        PdfPCell celda = new PdfPCell(new Paragraph(texto, font));
        celda.setBackgroundColor(bgColor);
        celda.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        celda.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        celda.setPadding(8f);
        return celda;
    }

    /**
     * Método abstracto para que cada módulo implemente la generación de su reporte.
     */
    protected abstract void generarReporte(Document document, Usuario usuario) throws DocumentException;
}
