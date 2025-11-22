package com.mycompany.checkinc.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.entities.Glucosa;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;

/**
 * Clase abstracta para la estructura básica de reportes PDF.
 * Proporciona métodos compartidos para todos los reportes.
 */
public abstract class ReporteBasePDF {

    // Colores estandarizados
    protected static final BaseColor COLOR_AZUL = new BaseColor(48, 88, 166);
    protected static final BaseColor COLOR_NARANJA = new BaseColor(244, 85, 1);
    protected static final BaseColor COLOR_GRIS = new BaseColor(127, 127, 127);
    
    // Fuentes estandarizadas
    protected final Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, COLOR_AZUL);
    protected final Font fontSubtitulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, COLOR_NARANJA);
    protected final Font fontHeader = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    protected final Font fontCelda = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
    protected final Font fontEnfasis = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, COLOR_GRIS);
    protected final Font fontPackage = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

    /**
     * Agrega encabezado estándar al documento (logo + fecha).
     */
    protected void agregarEncabezadoDocumento(Document document) throws DocumentException {
        try {
            Image logo = Image.getInstance(FacesContext.getCurrentInstance()
                .getExternalContext().getRealPath("/resources/images/LOGO PNG.png"));
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            // Si no hay logo, continuar sin detener el reporte
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
        Paragraph fecha = new Paragraph("Fecha de emisión: " + sdf.format(new Date()), 
            new Font(Font.FontFamily.HELVETICA, 10));
        fecha.setAlignment(Element.ALIGN_RIGHT);
        document.add(fecha);
        
        document.add(new Paragraph(" "));
    }

    /**
     * Agrega título del reporte.
     */
    protected void agregarTitulo(Document document, String titulo) throws DocumentException {
        Paragraph title = new Paragraph(titulo, fontTitulo);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);
    }

    /**
     * Agrega encabezado con datos del paciente.
     */
    protected void agregarEncabezadoPaciente(Document document, Usuario usuario) throws DocumentException {
        Paragraph header = new Paragraph("Datos del Paciente", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY));
        header.setSpacingBefore(10f);
        header.setSpacingAfter(10f);
        document.add(header);
        
        document.add(new Paragraph("Nombre: " + usuario.getNombres() + " " + usuario.getApellidos(), fontPackage));
        document.add(new Paragraph("Documento: " + usuario.getDocumento(), fontPackage));
        document.add(new Paragraph("Correo: " + usuario.getCorreo(), fontPackage));
        document.add(new Paragraph("Edad: " + usuario.getEdad(), fontPackage));
        document.add(new Paragraph("Tipo de Diabetes: " + (usuario.getTipoDiabetes() != null ? usuario.getTipoDiabetes() : "N/A"), fontPackage));
        document.add(new Paragraph("Insulodependiente: " + (usuario.getEsInsulodependiente() ? "Sí" : "No"), fontPackage));
        document.add(new Paragraph(" "));
    }

    /**
     * Agrega sección de título.
     */
    protected void agregarSeccion(Document document, String titulo) throws DocumentException {
        Paragraph section = new Paragraph(titulo, fontSubtitulo);
        section.setSpacingBefore(15f);
        section.setSpacingAfter(10f);
        document.add(section);
    }

    /**
     * Estiliza una celda de tabla para headers.
     */
    protected PdfPCell crearCeldaHeader(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fontHeader));
        celda.setBackgroundColor(COLOR_AZUL);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setPadding(8f);
        return celda;
    }

    /**
     * Estiliza una celda de tabla para datos.
     */
    protected PdfPCell crearCeldaData(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fontCelda));
        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setPadding(6f);
        return celda;
    }

    /**
     * Crea un gráfico de tendencia de glucosa (promedio por día) a partir de una lista de registros.
     * Devuelve un `Image` de iText listo para añadirse al documento, o `null` si no hay datos.
     */
    protected Image crearGraficoGlucosa(List<Glucosa> glucosaList) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

            // Filtrar últimos 7 días
            java.util.Date hoy = new java.util.Date();
            java.util.Calendar tempCal = java.util.Calendar.getInstance();
            tempCal.setTime(hoy);
            tempCal.add(java.util.Calendar.DAY_OF_YEAR, -7);
            java.util.Date ultimaSemana = tempCal.getTime();

            List<Glucosa> registrosSemanales = glucosaList.stream()
                .filter(g -> g.getFechaHora().after(ultimaSemana))
                .collect(java.util.stream.Collectors.toList());

            if (registrosSemanales.isEmpty()) {
                return null;
            }

            // Agrupar por día (dd/MM) y calcular promedio para cada día
            Map<String, Double> promedioPorDia = registrosSemanales.stream()
                .collect(java.util.stream.Collectors.groupingBy(g -> sdf.format(g.getFechaHora()),
                        java.util.stream.Collectors.averagingDouble(g -> g.getNivelGlucosa())));

            // Ordenar por fecha cronológica
            List<String> diasOrdenados = promedioPorDia.keySet().stream()
                .sorted((d1, d2) -> {
                    try {
                        java.util.Date dd1 = sdf.parse(d1);
                        java.util.Date dd2 = sdf.parse(d2);
                        return dd1.compareTo(dd2);
                    } catch (Exception ex) {
                        return d1.compareTo(d2);
                    }
                })
                .collect(java.util.stream.Collectors.toList());

            for (String dia : diasOrdenados) {
                dataset.addValue(promedioPorDia.get(dia), "Nivel de Glucosa", dia);
            }

            JFreeChart lineChart = ChartFactory.createLineChart(
                    "Tendencia de Glucosa",
                    "Fecha",
                    "Nivel (mg/dL)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);

            // Personalización del gráfico
            lineChart.setBackgroundPaint(java.awt.Color.white);

            org.jfree.chart.plot.CategoryPlot plot = lineChart.getCategoryPlot();
            plot.setBackgroundPaint(java.awt.Color.white);
            plot.setDomainGridlinePaint(new java.awt.Color(220, 220, 220));
            plot.setRangeGridlinePaint(new java.awt.Color(220, 220, 220));

            org.jfree.chart.renderer.category.LineAndShapeRenderer renderer =
                (org.jfree.chart.renderer.category.LineAndShapeRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new java.awt.Color(48, 88, 166));
            renderer.setSeriesStroke(0, new java.awt.BasicStroke(2.0f));
            renderer.setSeriesShapesVisible(0, true);
            renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));
            renderer.setDrawOutlines(true);
            renderer.setUseFillPaint(true);
            renderer.setSeriesFillPaint(0, java.awt.Color.WHITE);
            plot.setRenderer(renderer);

            org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setLowerMargin(0.05);
            domainAxis.setUpperMargin(0.05);
            domainAxis.setTickLabelFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));

            org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(org.jfree.chart.axis.NumberAxis.createIntegerTickUnits());
            rangeAxis.setTickLabelFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));

            lineChart.getLegend().setItemFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            ChartUtilities.writeChartAsPNG(baos, lineChart, 500, 300);
            return Image.getInstance(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Método abstracto que cada subclase debe implementar.
     */
    protected abstract void generarReporte(Document document, Usuario usuario) throws DocumentException;
}

