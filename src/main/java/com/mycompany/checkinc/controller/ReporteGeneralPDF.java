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
import java.util.Map;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.LinkedHashMap;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Genera el reporte completo PDF del paciente con glucosa, citas y
 * medicamentos.
 * Incluye gráficos hermosos y estructura organizada por meses.
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
     * Genera la sección de glucosa con gráfico hermoso y tabla organizada por mes.
     */
    private void generarSeccionGlucosa(Document document, List<Glucosa> glucosaList) throws DocumentException {
        agregarSeccion(document, "Registros de Glucosa");

        // Gráfico de tendencia hermoso
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

        // Agrupar por mes (usando clave año*100+mes para ordenar cronológicamente)
        Map<Integer, List<Glucosa>> agrupadoPorMes = glucosaList.stream()
                .collect(Collectors.groupingBy(g -> {
                    Calendar c = Calendar.getInstance();
                    c.setTime(g.getFechaHora());
                    return c.get(Calendar.YEAR) * 100 + (c.get(Calendar.MONTH) + 1);
                }));

        // Ordenar claves descendentes (meses más recientes primero)
        Map<Integer, List<Glucosa>> ordenadoPorMes = agrupadoPorMes.entrySet().stream()
                .sorted(Map.Entry.<Integer, List<Glucosa>>comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));

        SimpleDateFormat sdfMes = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Map.Entry<Integer, List<Glucosa>> entrada : ordenadoPorMes.entrySet()) {
            try {
                int clave = entrada.getKey();
                int year = clave / 100;
                int month = clave % 100;

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month - 1);
                cal.set(Calendar.DAY_OF_MONTH, 1);

                String tituloMes = sdfMes.format(cal.getTime());
                Paragraph mesTitle = new Paragraph(tituloMes, fontSubtitulo);
                mesTitle.setSpacingBefore(10f);
                mesTitle.setSpacingAfter(5f);
                document.add(mesTitle);

                // Ordenar registros del mes por fecha descendente
                List<Glucosa> listaMes = entrada.getValue().stream()
                        .sorted((g1, g2) -> g2.getFechaHora().compareTo(g1.getFechaHora()))
                        .collect(Collectors.toList());

                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.setSpacingBefore(5f);
                table.addCell(crearCeldaHeader("Nivel (mg/dL)"));
                table.addCell(crearCeldaHeader("Fecha y Hora"));
                table.addCell(crearCeldaHeader("Momento del Día"));

                for (Glucosa g : listaMes) {
                    table.addCell(crearCeldaData(String.valueOf(g.getNivelGlucosa())));
                    table.addCell(crearCeldaData(sdf.format(g.getFechaHora())));
                    table.addCell(crearCeldaData(g.getMomentoDia() != null ? g.getMomentoDia() : "N/A"));
                }
                document.add(table);
                document.add(new Paragraph(" "));
            } catch (DocumentException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Genera la sección de citas médicas.
     */
    private void generarSeccionCitas(Document document, List<Cita> citaList) throws DocumentException {
        agregarSeccion(document, "Citas Médicas");

        // Agrupar citas por mes (año*100+mes)
        Map<Integer, List<Cita>> agrupado = citaList.stream()
                .collect(Collectors.groupingBy(c -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(c.getFecha());
                    return cal.get(Calendar.YEAR) * 100 + (cal.get(Calendar.MONTH) + 1);
                }));

        Map<Integer, List<Cita>> ordenado = agrupado.entrySet().stream()
                .sorted(Map.Entry.<Integer, List<Cita>>comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));

        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdfMes = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));

        for (Map.Entry<Integer, List<Cita>> entrada : ordenado.entrySet()) {
            try {
                int clave = entrada.getKey();
                int year = clave / 100;
                int month = clave % 100;

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month - 1);
                cal.set(Calendar.DAY_OF_MONTH, 1);

                Paragraph mesTitle = new Paragraph(sdfMes.format(cal.getTime()), fontSubtitulo);
                mesTitle.setSpacingBefore(8f);
                mesTitle.setSpacingAfter(6f);
                document.add(mesTitle);

                // Ordenar por fecha/hora ascendente dentro del mes
                List<Cita> listaMes = entrada.getValue().stream()
                        .sorted((a, b) -> {
                            int cmp = a.getFecha().compareTo(b.getFecha());
                            if (cmp != 0)
                                return cmp;
                            return a.getHora().compareTo(b.getHora());
                        })
                        .collect(Collectors.toList());

                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setSpacingBefore(5f);
                table.addCell(crearCeldaHeader("Fecha"));
                table.addCell(crearCeldaHeader("Hora"));
                table.addCell(crearCeldaHeader("Motivo"));
                table.addCell(crearCeldaHeader("Estado"));

                for (Cita c : listaMes) {
                    table.addCell(crearCeldaData(sdfFecha.format(c.getFecha())));
                    table.addCell(crearCeldaData(sdfHora.format(c.getHora())));
                    table.addCell(crearCeldaData(c.getMotivo()));
                    table.addCell(crearCeldaData(c.getEstado() != null ? c.getEstado() : "N/A"));
                }
                document.add(table);
                document.add(new Paragraph(" "));
            } catch (DocumentException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Genera la sección de medicamentos.
     */
    private void generarSeccionMedicamentos(Document document, List<Medicamento> medicamentoList)
            throws DocumentException {
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
     * Crea gráfico hermoso de tendencia de glucosa (últimos 7 días).
     */
    protected Image crearGraficoGlucosa(List<Glucosa> glucosaList) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

            // Filtrar últimos 7 días y agrupar por día (promedio por día)
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
                    .collect(Collectors.groupingBy(g -> sdf.format(g.getFechaHora()),
                            Collectors.averagingDouble(g -> g.getNivelGlucosa())));

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

            org.jfree.chart.renderer.category.LineAndShapeRenderer renderer = (org.jfree.chart.renderer.category.LineAndShapeRenderer) plot
                    .getRenderer();
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
            domainAxis.setTickLabelFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 10));

            org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(org.jfree.chart.axis.NumberAxis.createIntegerTickUnits());
            rangeAxis.setTickLabelFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 10));

            // Configurar fuente de la leyenda y título con soporte Unicode
            lineChart.getTitle().setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
            lineChart.getLegend().setItemFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 10));

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
