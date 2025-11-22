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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Genera el reporte completo PDF del paciente con glucosa, citas y medicamentos.
 */
public class ReporteGeneralPDF extends ReporteBasePDF {

    private final Usuario usuario;
    private final GlucosaFacadeLocal glucosaFacade;
    private final MedicamentoFacadeLocal medicamentoFacade;
    private final CitaFacadeLocal citaFacade;

    public ReporteGeneralPDF(Usuario usuario, GlucosaFacadeLocal glucosaFacade, 
                             MedicamentoFacadeLocal medicamentoFacade, CitaFacadeLocal citaFacade) {
        this.usuario = usuario;
        this.glucosaFacade = glucosaFacade;
        this.medicamentoFacade = medicamentoFacade;
        this.citaFacade = citaFacade;
    }

    @Override
    protected void generarReporte(Document document, Usuario usuario) throws DocumentException {
        agregarEncabezadoDocumento(document);
        agregarTitulo(document, "Reporte Completo de Salud");
        agregarEncabezadoPaciente(document, usuario);

        // Sección de Glucosa
        List<Glucosa> glucosaList = glucosaFacade.findByUsuario(usuario);
        if (!glucosaList.isEmpty()) {
            generarSeccionGlucosa(document, glucosaList);
        }

        // Sección de Citas
        List<Cita> citaList = citaFacade.findByUsuario(usuario);
        if (!citaList.isEmpty()) {
            generarSeccionCitas(document, citaList);
        }

        // Sección de Medicamentos
        List<Medicamento> medicamentoList = medicamentoFacade.findByUsuario(usuario);
        if (!medicamentoList.isEmpty()) {
            generarSeccionMedicamentos(document, medicamentoList);
        }
    }

    /**
     * Genera la sección de glucosa con gráfico y tabla.
     */
    private void generarSeccionGlucosa(Document document, List<Glucosa> glucosaList) throws DocumentException {
        agregarSeccion(document, "Registros de Glucosa");

        // Gráfico de tendencia
        try {
            Image chartImage = crearGraficoGlucosa(glucosaList);
            if (chartImage != null) {
                chartImage.scaleToFit(500, 300);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(chartImage);
                
                Paragraph legend = new Paragraph("Gráfico de tendencia de los últimos 7 días", fontEnfasis);
                legend.setAlignment(Element.ALIGN_CENTER);
                legend.setSpacingAfter(20f);
                document.add(legend);
            }
        } catch (Exception e) {
            // Continuar sin gráfico si hay error
        }

        // Tabla de glucosa
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.addCell(crearCeldaHeader("Nivel (mg/dL)"));
        table.addCell(crearCeldaHeader("Fecha y Hora"));
        table.addCell(crearCeldaHeader("Momento del Día"));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Glucosa g : glucosaList) {
            table.addCell(crearCeldaData(String.valueOf(g.getNivelGlucosa())));
            table.addCell(crearCeldaData(sdf.format(g.getFechaHora())));
            table.addCell(crearCeldaData(g.getMomentoDia() != null ? g.getMomentoDia() : "N/A"));
        }
        document.add(table);
        document.add(new Paragraph(" "));
    }

    /**
     * Genera la sección de citas.
     */
    private void generarSeccionCitas(Document document, List<Cita> citaList) throws DocumentException {
        agregarSeccion(document, "Citas Médicas");

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.addCell(crearCeldaHeader("Fecha"));
        table.addCell(crearCeldaHeader("Hora"));
        table.addCell(crearCeldaHeader("Motivo"));

        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
        for (Cita c : citaList) {
            table.addCell(crearCeldaData(sdfFecha.format(c.getFecha())));
            table.addCell(crearCeldaData(sdfHora.format(c.getHora())));
            table.addCell(crearCeldaData(c.getMotivo()));
        }
        document.add(table);
        document.add(new Paragraph(" "));
    }

    /**
     * Genera la sección de medicamentos.
     */
    private void generarSeccionMedicamentos(Document document, List<Medicamento> medicamentoList) throws DocumentException {
        agregarSeccion(document, "Medicamentos");

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.addCell(crearCeldaHeader("Nombre"));
        table.addCell(crearCeldaHeader("Dosis"));
        table.addCell(crearCeldaHeader("Frecuencia"));
        table.addCell(crearCeldaHeader("Fecha Inicio"));
        table.addCell(crearCeldaHeader("Fecha Fin"));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Medicamento m : medicamentoList) {
            table.addCell(crearCeldaData(m.getNombre()));
            table.addCell(crearCeldaData(m.getDosis()));
            table.addCell(crearCeldaData(m.getFrecuencia()));
            table.addCell(crearCeldaData(sdf.format(m.getFechaInicio())));
            table.addCell(crearCeldaData(sdf.format(m.getFechaFin())));
        }
        document.add(table);
    }

    /**
     * Crea gráfico de tendencia de glucosa (últimos 7 días).
     */
    private Image crearGraficoGlucosa(List<Glucosa> glucosaList) {
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
                .sorted((g1, g2) -> g1.getFechaHora().compareTo(g2.getFechaHora()))
                .collect(java.util.stream.Collectors.toList());

            if (registrosSemanales.isEmpty()) {
                return null;
            }

            for (Glucosa g : registrosSemanales) {
                dataset.addValue(g.getNivelGlucosa(), "Nivel de Glucosa", sdf.format(g.getFechaHora()));
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
     * Exporta el reporte a PDF.
     */
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
