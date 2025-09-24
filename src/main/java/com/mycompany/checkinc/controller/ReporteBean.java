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
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.checkinc.entities.Cita;
import com.mycompany.checkinc.entities.Glucosa;
import com.mycompany.checkinc.entities.Medicamento;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.CitaFacadeLocal;
import com.mycompany.checkinc.services.GlucosaFacadeLocal;
import com.mycompany.checkinc.services.MedicamentoFacadeLocal;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

@ManagedBean(name = "reporteBean")
@RequestScoped
public class ReporteBean implements Serializable {

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    @EJB
    private MedicamentoFacadeLocal medicamentoFacade;
    
    @EJB
    private GlucosaFacadeLocal glucosaFacade;

    @EJB
    private CitaFacadeLocal citaFacade;

    public void exportarCSV() throws IOException {
        exportar("text/csv", "usuarios.csv", ",");
    }

    public void exportarXLS() throws IOException {
        exportar("application/vnd.ms-excel", "usuarios.xls", "\t");
    }

    private void exportar(String tipo, String nombreArchivo, String separador) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        response.setContentType(tipo);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");

        PrintWriter writer = response.getWriter();
        writer.println("ID" + separador + "Usuario" + separador + "Nombres" + separador + "Apellidos" +
                       separador + "Edad" + separador + "Correo" + separador + "Documento");

        List<Usuario> usuarios = usuarioFacade.findAll();
        for (Usuario u : usuarios) {
            writer.println(u.getIdUsuario() + separador +
                           u.getUser() + separador +
                           u.getNombres() + separador +
                           u.getApellidos() + separador +
                           u.getEdad() + separador +
                           u.getCorreo() + separador +
                           u.getDocumento());
        }

        writer.flush();
        writer.close();
        context.responseComplete();
    }

    public void descargarReporteDinamico() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        String viewId = context.getViewRoot().getViewId();

        if (viewId.contains("registroGlucosa")) {
            exportarGlucosaPDF();
        } else if (viewId.contains("IndexMedicamentos")) {
            exportarMedicamentosPDF();
        } else if (viewId.contains("IndexCitas")) {
            exportarCitasPDF();
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Advertencia", "No se puede generar el reporte para esta vista."));
        }
    }

    public void exportarReporteCompletoPDF() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");
        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no autenticado"));
            return;
        }
        try {
            ReporteGeneralPDF reporte = new ReporteGeneralPDF(usuario, glucosaFacade, medicamentoFacade, citaFacade);
            reporte.exportarPDF();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo generar el reporte general."));
            e.printStackTrace();
        }
    }
    
    private void exportarGlucosaPDF() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");

        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no autenticado"));
            return;
        }
        
        List<Glucosa> glucosaList = glucosaFacade.findByUsuario(usuario);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"glucosa.pdf\"");

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(48, 88, 166));
            Paragraph title = new Paragraph("Reporte de Glucosa", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);
            
            // Agregar encabezado con datos del paciente
            agregarEncabezadoPaciente(document, usuario);

            if (!glucosaList.isEmpty()) {
                Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(244, 85, 1));
                document.add(new Paragraph("Registros de Glucosa", sectionFont));
                document.add(new Paragraph(" "));

                // 1. Mostrar gráfico de tendencia de la última semana
                Image chartImage = crearGraficoGlucosa(glucosaList);
                if (chartImage != null) {
                    chartImage.scaleToFit(500, 300);
                    chartImage.setAlignment(Element.ALIGN_CENTER);
                    document.add(chartImage);
                    document.add(new Paragraph(" "));
                    
                    // Agregar leyenda del gráfico
                    Font legendFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
                    Paragraph legend = new Paragraph("Gráfico de tendencia de los últimos 7 días", legendFont);
                    legend.setAlignment(Element.ALIGN_CENTER);
                    legend.setSpacingAfter(20f);
                    document.add(legend);
                } else {
                    // Mensaje si no hay datos recientes
                    Font warningFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, new BaseColor(244, 85, 1));
                    Paragraph warning = new Paragraph("No hay registros de glucosa para la última semana.", warningFont);
                    warning.setAlignment(Element.ALIGN_CENTER);
                    warning.setSpacingAfter(20f);
                    document.add(warning);
                }

                // 2. Organizar y mostrar registros por mes
                SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
                Font cellFont = new Font(Font.FontFamily.HELVETICA, 11);
                Font monthFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(48, 88, 166));
                BaseColor azulHeader = new BaseColor(48, 88, 166);

                // Agrupar y ordenar registros por mes (más reciente primero)
                Map<String, List<Glucosa>> registrosPorMes = glucosaList.stream()
                    .sorted((g1, g2) -> g2.getFechaHora().compareTo(g1.getFechaHora()))
                    .collect(Collectors.groupingBy(g -> new SimpleDateFormat("MMMM yyyy").format(g.getFechaHora())));

                // Procesar cada mes
                registrosPorMes.entrySet().stream()
                    .sorted((e1, e2) -> {
                        try {
                            Date d1 = new SimpleDateFormat("MMMM yyyy").parse(e1.getKey());
                            Date d2 = new SimpleDateFormat("MMMM yyyy").parse(e2.getKey());
                            return d2.compareTo(d1); // Orden descendente
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .forEach(mes -> {
                        try {
                            // Título del mes
                            Paragraph mesTitle = new Paragraph(mes.getKey(), monthFont);
                            mesTitle.setSpacingBefore(15f);
                            mesTitle.setSpacingAfter(10f);
                            document.add(mesTitle);

                            // Tabla del mes
                            PdfPTable tabla = new PdfPTable(3);
                            tabla.setWidthPercentage(100);
                            tabla.setSpacingBefore(5f);
                            tabla.setSpacingAfter(10f);

                            // Encabezados
                            tabla.addCell(estilizarCelda("Nivel de Glucosa", headerFont, azulHeader));
                            tabla.addCell(estilizarCelda("Fecha y Hora", headerFont, azulHeader));
                            tabla.addCell(estilizarCelda("Momento del Día", headerFont, azulHeader));

                            // Ordenar registros del mes cronológicamente
                            List<Glucosa> registrosOrdenados = mes.getValue().stream()
                                .sorted((g1, g2) -> g1.getFechaHora().compareTo(g2.getFechaHora()))
                                .collect(Collectors.toList());

                            // Agregar datos
                            for (Glucosa g : registrosOrdenados) {
                                PdfPCell cellNivel = new PdfPCell(new Phrase(String.valueOf(g.getNivelGlucosa()), cellFont));
                                PdfPCell cellFecha = new PdfPCell(new Phrase(sdfFecha.format(g.getFechaHora()), cellFont));
                                PdfPCell cellMomento = new PdfPCell(new Phrase(g.getMomentoDia() != null ? g.getMomentoDia() : "N/A", cellFont));

                                cellNivel.setHorizontalAlignment(Element.ALIGN_CENTER);
                                cellFecha.setHorizontalAlignment(Element.ALIGN_CENTER);
                                cellMomento.setHorizontalAlignment(Element.ALIGN_CENTER);

                                tabla.addCell(cellNivel);
                                tabla.addCell(cellFecha);
                                tabla.addCell(cellMomento);
                            }

                            document.add(tabla);
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                    });
            } else {
                document.add(new Paragraph("No hay registros de glucosa para mostrar."));
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
            context.responseComplete();
        }
    }

    private void exportarMedicamentosPDF() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");

        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no autenticado"));
            return;
        }

        List<Medicamento> medicamentoList = medicamentoFacade.findByUsuario(usuario);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"medicamentos.pdf\"");

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(48, 88, 166));
            Paragraph title = new Paragraph("Reporte de Medicamentos", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);
            
            // Agregar encabezado con datos del paciente
            agregarEncabezadoPaciente(document, usuario);

            if (!medicamentoList.isEmpty()) {
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
                table.addCell(estilizarCelda("Nombre", headerFont, new BaseColor(48, 88, 166)));
                table.addCell(estilizarCelda("Dosis", headerFont, new BaseColor(48, 88, 166)));
                table.addCell(estilizarCelda("Frecuencia", headerFont, new BaseColor(48, 88, 166)));
                table.addCell(estilizarCelda("Fecha Inicio", headerFont, new BaseColor(48, 88, 166)));
                table.addCell(estilizarCelda("Fecha Fin", headerFont, new BaseColor(48, 88, 166)));

                Font cellFont = new Font(Font.FontFamily.HELVETICA, 11);
                for (Medicamento m : medicamentoList) {
                    table.addCell(new Phrase(m.getNombre(), cellFont));
                    table.addCell(new Phrase(m.getDosis(), cellFont));
                    table.addCell(new Phrase(m.getFrecuencia(), cellFont));
                    table.addCell(new Phrase(new SimpleDateFormat("dd/MM/yyyy").format(m.getFechaInicio()), cellFont));
                    table.addCell(new Phrase(new SimpleDateFormat("dd/MM/yyyy").format(m.getFechaFin()), cellFont));
                }
                document.add(table);
            } else {
                document.add(new Paragraph("No hay registros de medicamentos para mostrar."));
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
            context.responseComplete();
        }
    }

    private void exportarCitasPDF() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");

        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no autenticado"));
            return;
        }

        List<Cita> citaList = citaFacade.findByUsuario(usuario);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"citas.pdf\"");

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(48, 88, 166));
            Paragraph title = new Paragraph("Reporte de Citas", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);
            
            // Agregar encabezado con datos del paciente
            agregarEncabezadoPaciente(document, usuario);

            if (!citaList.isEmpty()) {
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
                table.addCell(estilizarCelda("Fecha", headerFont, new BaseColor(48, 88, 166)));
                table.addCell(estilizarCelda("Hora", headerFont, new BaseColor(48, 88, 166)));
                table.addCell(estilizarCelda("Motivo", headerFont, new BaseColor(48, 88, 166)));

                Font cellFont = new Font(Font.FontFamily.HELVETICA, 11);
                for (Cita c : citaList) {
                    table.addCell(new Phrase(new SimpleDateFormat("dd/MM/yyyy").format(c.getFecha()), cellFont));
                    table.addCell(new Phrase(new SimpleDateFormat("HH:mm").format(c.getHora()), cellFont));
                    table.addCell(new Phrase(c.getMotivo(), cellFont));
                }
                document.add(table);
            } else {
                document.add(new Paragraph("No hay registros de citas para mostrar."));
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
            context.responseComplete();
        }
    }

    private Image crearGraficoGlucosa(List<Glucosa> glucosaList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        
        // Filtrar registros de la última semana
        java.util.Date hoy = new java.util.Date();
        java.util.Calendar tempCal = java.util.Calendar.getInstance();
        tempCal.setTime(hoy);
        tempCal.add(java.util.Calendar.DAY_OF_YEAR, -7);
        java.util.Date ultimaSemana = tempCal.getTime();
        
        // Ordenar y filtrar registros por semana
        List<Glucosa> registrosSemanales = glucosaList.stream()
            .filter(g -> g.getFechaHora().after(ultimaSemana))
            .sorted((g1, g2) -> g1.getFechaHora().compareTo(g2.getFechaHora()))
            .collect(java.util.stream.Collectors.toList());
            
        if (registrosSemanales.isEmpty()) {
            // Si no hay registros de la última semana, mostrar un mensaje
            return null;
        }
            
        for (Glucosa g : registrosSemanales) {
            dataset.addValue(g.getNivelGlucosa(), "Nivel de Glucosa", sdf.format(g.getFechaHora()));
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Tendencia de Glucosa", // título
                "Fecha", // eje X
                "Nivel (mg/dL)", // eje Y
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        // Personalización del gráfico
        lineChart.setBackgroundPaint(Color.white);
        
        // Configurar el plot
        org.jfree.chart.plot.CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        
        // Personalizar la línea
        org.jfree.chart.renderer.category.LineAndShapeRenderer renderer = 
            (org.jfree.chart.renderer.category.LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(47, 85, 151));
        renderer.setSeriesStroke(0, new java.awt.BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setSeriesFillPaint(0, Color.WHITE);
        
        // Configurar el área bajo la curva
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));
        plot.setRenderer(renderer);
        
        // Personalizar ejes
        org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLowerMargin(0.05);
        domainAxis.setUpperMargin(0.05);
        domainAxis.setTickLabelFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
        
        org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(org.jfree.chart.axis.NumberAxis.createIntegerTickUnits());
        rangeAxis.setTickLabelFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
        
        // Ajustar tamaño de la leyenda
        lineChart.getLegend().setItemFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ChartUtilities.writeChartAsPNG(baos, lineChart, 500, 300);
            return Image.getInstance(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private PdfPCell estilizarCelda(String texto, Font font, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        return cell;
    }

    private void agregarEncabezadoPaciente(Document document, Usuario usuario) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(244, 85, 1));
        
        // Logo
        try {
            Image logo = Image.getInstance(FacesContext.getCurrentInstance()
                .getExternalContext().getRealPath("/resources/images/LOGO PNG.png"));
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Fecha de emisión
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
        Paragraph fecha = new Paragraph("Fecha de emisión: " + sdf.format(new java.util.Date()), 
            new Font(Font.FontFamily.HELVETICA, 10));
        fecha.setAlignment(Element.ALIGN_RIGHT);
        document.add(fecha);
        
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Datos del Paciente", sectionFont));
        document.add(new Paragraph("Nombre: " + usuario.getNombres() + " " + usuario.getApellidos()));
        document.add(new Paragraph("Documento: " + usuario.getDocumento()));
        document.add(new Paragraph("Correo: " + usuario.getCorreo()));
        document.add(new Paragraph("Edad: " + usuario.getEdad()));
        document.add(new Paragraph("Tipo de Diabetes: " + (usuario.getTipoDiabetes() != null ? usuario.getTipoDiabetes() : "N/A")));
        document.add(new Paragraph(" "));
    }

    private MenuModel model;

    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();
    }

    public MenuModel getModel() {
        return model;
    }
}
