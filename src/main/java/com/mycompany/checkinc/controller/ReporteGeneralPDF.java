package com.mycompany.checkinc.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.checkinc.entities.Cita;
import com.mycompany.checkinc.entities.Glucosa;
import com.mycompany.checkinc.entities.Medicamento;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.CitaFacadeLocal;
import com.mycompany.checkinc.services.GlucosaFacadeLocal;
import com.mycompany.checkinc.services.MedicamentoFacadeLocal;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

public class ReporteGeneralPDF extends ReporteBasePDF {
    // Método para crear el gráfico de tendencia de glucosa
    private Image crearGraficoGlucosa(List<Glucosa> glucosaList) {
        try {
            org.jfree.data.category.DefaultCategoryDataset dataset = new org.jfree.data.category.DefaultCategoryDataset();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM");
            // Filtrar registros de la última semana
            java.util.Date hoy = new java.util.Date();
            java.util.Calendar tempCal = java.util.Calendar.getInstance();
            tempCal.setTime(hoy);
            tempCal.add(java.util.Calendar.DAY_OF_YEAR, -7);
            java.util.Date ultimaSemana = tempCal.getTime();
            List<Glucosa> registrosSemanales = glucosaList.stream()
                .filter(g -> g.getFechaHora().after(ultimaSemana))
                .sorted((g1, g2) -> g1.getFechaHora().compareTo(g2.getFechaHora()))
                .collect(java.util.stream.Collectors.toList());
            if (registrosSemanales.isEmpty()) {
                return null;
            }
            for (Glucosa g : registrosSemanales) {
                dataset.addValue(g.getNivelGlucosa(), "Nivel de Glucosa", sdf.format(g.getFechaHora()));
            }
            org.jfree.chart.JFreeChart lineChart = org.jfree.chart.ChartFactory.createLineChart(
                    "Tendencia de Glucosa",
                    "Fecha",
                    "Nivel (mg/dL)",
                    dataset,
                    org.jfree.chart.plot.PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);
            lineChart.setBackgroundPaint(java.awt.Color.white);
            org.jfree.chart.plot.CategoryPlot plot = lineChart.getCategoryPlot();
            plot.setBackgroundPaint(java.awt.Color.white);
            plot.setDomainGridlinePaint(new java.awt.Color(220, 220, 220));
            plot.setRangeGridlinePaint(new java.awt.Color(220, 220, 220));
            org.jfree.chart.renderer.category.LineAndShapeRenderer renderer = 
                (org.jfree.chart.renderer.category.LineAndShapeRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new java.awt.Color(47, 85, 151));
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
            org.jfree.chart.ChartUtilities.writeChartAsPNG(baos, lineChart, 500, 300);
            return Image.getInstance(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private final Usuario usuario;
    private final GlucosaFacadeLocal glucosaFacade;
    private final MedicamentoFacadeLocal medicamentoFacade;
    private final CitaFacadeLocal citaFacade;

    public ReporteGeneralPDF(Usuario usuario, GlucosaFacadeLocal glucosaFacade, MedicamentoFacadeLocal medicamentoFacade, CitaFacadeLocal citaFacade) {
        this.usuario = usuario;
        this.glucosaFacade = glucosaFacade;
        this.medicamentoFacade = medicamentoFacade;
        this.citaFacade = citaFacade;
    }

    @Override
    protected void generarReporte(Document document, Usuario usuario) throws DocumentException {
        // Colores y fuentes
        BaseColor azul = new BaseColor(48, 88, 166);
        BaseColor naranja = new BaseColor(244, 85, 1);
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, azul);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 11);
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, naranja);
        Font legendFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);

        // Logo y fecha de emisión
        try {
            Image logo = Image.getInstance(FacesContext.getCurrentInstance()
                .getExternalContext().getRealPath("/resources/images/LOGO PNG.png"));
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            // Si no hay logo, no se detiene el reporte
        }
        SimpleDateFormat sdfFechaEmision = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
        Paragraph fecha = new Paragraph("Fecha de emisión: " + sdfFechaEmision.format(new java.util.Date()), new Font(Font.FontFamily.HELVETICA, 10));
        fecha.setAlignment(Element.ALIGN_RIGHT);
        document.add(fecha);

        document.add(new Paragraph(" "));
        // Título del reporte
        Paragraph title = new Paragraph("Reporte Completo del Paciente", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);

        // Encabezado del paciente
        document.add(new Paragraph("Reporte de Paciente", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.DARK_GRAY)));
        document.add(new Paragraph("Nombre: " + usuario.getNombres() + " " + usuario.getApellidos(), cellFont));
        document.add(new Paragraph("Documento: " + usuario.getDocumento(), cellFont));
        document.add(new Paragraph("Correo: " + usuario.getCorreo(), cellFont));
        document.add(new Paragraph("Edad: " + usuario.getEdad(), cellFont));
        document.add(new Paragraph("Tipo de Diabetes: " + usuario.getTipoDiabetes(), cellFont));
        document.add(new Paragraph("Insulodependiente: " + (usuario.getEsInsulodependiente() ? "Sí" : "No"), cellFont));
        document.add(new Paragraph(" "));

        // Glucosa
        List<Glucosa> glucosaList = glucosaFacade.findByUsuario(usuario);
        if (!glucosaList.isEmpty()) {
            Paragraph glucosaSection = new Paragraph("Registros de Glucosa", sectionFont);
            glucosaSection.setSpacingBefore(10f);
            glucosaSection.setSpacingAfter(10f);
            document.add(glucosaSection);

            // Gráfico de tendencia de glucosa
            try {
                Image chartImage = crearGraficoGlucosa(glucosaList);
                if (chartImage != null) {
                    chartImage.scaleToFit(500, 300);
                    chartImage.setAlignment(Element.ALIGN_CENTER);
                    document.add(chartImage);
                    Paragraph legend = new Paragraph("Gráfico de tendencia de los últimos 7 días", legendFont);
                    legend.setAlignment(Element.ALIGN_CENTER);
                    legend.setSpacingAfter(20f);
                    document.add(legend);
                }
            } catch (Exception e) {
                // Si no hay gráfico, no se detiene el reporte
            }

            PdfPTable glucosaTable = new PdfPTable(3);
            glucosaTable.setWidthPercentage(100);
            glucosaTable.setSpacingBefore(10f);
            glucosaTable.addCell(estilizarCelda("Nivel de Glucosa", headerFont, azul));
            glucosaTable.addCell(estilizarCelda("Fecha y Hora", headerFont, azul));
            glucosaTable.addCell(estilizarCelda("Momento del Día", headerFont, azul));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (Glucosa g : glucosaList) {
                glucosaTable.addCell(new Phrase(String.valueOf(g.getNivelGlucosa()), cellFont));
                glucosaTable.addCell(new Phrase(sdf.format(g.getFechaHora()), cellFont));
                glucosaTable.addCell(new Phrase(g.getMomentoDia() != null ? g.getMomentoDia() : "N/A", cellFont));
            }
            document.add(glucosaTable);
            document.add(new Paragraph(" "));
        }

        // Citas
        List<Cita> citaList = citaFacade.findByUsuario(usuario);
        if (!citaList.isEmpty()) {
            Paragraph citaSection = new Paragraph("Citas Médicas", sectionFont);
            citaSection.setSpacingBefore(10f);
            citaSection.setSpacingAfter(10f);
            document.add(citaSection);
            PdfPTable citaTable = new PdfPTable(3);
            citaTable.setWidthPercentage(100);
            citaTable.setSpacingBefore(10f);
            citaTable.addCell(estilizarCelda("Fecha", headerFont, azul));
            citaTable.addCell(estilizarCelda("Hora", headerFont, azul));
            citaTable.addCell(estilizarCelda("Motivo", headerFont, azul));
            SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
            for (Cita c : citaList) {
                citaTable.addCell(new Phrase(sdfFecha.format(c.getFecha()), cellFont));
                citaTable.addCell(new Phrase(sdfHora.format(c.getHora()), cellFont));
                citaTable.addCell(new Phrase(c.getMotivo(), cellFont));
            }
            document.add(citaTable);
            document.add(new Paragraph(" "));
        }

        // Medicamentos
        List<Medicamento> medicamentoList = medicamentoFacade.findByUsuario(usuario);
        if (!medicamentoList.isEmpty()) {
            Paragraph medSection = new Paragraph("Medicamentos", sectionFont);
            medSection.setSpacingBefore(10f);
            medSection.setSpacingAfter(10f);
            document.add(medSection);
            PdfPTable medTable = new PdfPTable(5);
            medTable.setWidthPercentage(100);
            medTable.setSpacingBefore(10f);
            medTable.addCell(estilizarCelda("Nombre", headerFont, azul));
            medTable.addCell(estilizarCelda("Dosis", headerFont, azul));
            medTable.addCell(estilizarCelda("Frecuencia", headerFont, azul));
            medTable.addCell(estilizarCelda("Fecha Inicio", headerFont, azul));
            medTable.addCell(estilizarCelda("Fecha Fin", headerFont, azul));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Medicamento m : medicamentoList) {
                medTable.addCell(new Phrase(m.getNombre(), cellFont));
                medTable.addCell(new Phrase(m.getDosis(), cellFont));
                medTable.addCell(new Phrase(m.getFrecuencia(), cellFont));
                medTable.addCell(new Phrase(sdf.format(m.getFechaInicio()), cellFont));
                medTable.addCell(new Phrase(sdf.format(m.getFechaFin()), cellFont));
            }
            document.add(medTable);
        }
    }

    public void exportarPDF() throws IOException, DocumentException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"reporte_completo.pdf\"");
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            generarReporte(document, usuario);
        } finally {
            document.close();
            context.responseComplete();
        }
    }
}
