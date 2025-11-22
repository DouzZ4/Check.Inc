package com.mycompany.checkinc.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.checkinc.entities.Glucosa;
import com.mycompany.checkinc.entities.Medicamento;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.CitaFacadeLocal;
import com.mycompany.checkinc.services.GlucosaFacadeLocal;
import com.mycompany.checkinc.services.MedicamentoFacadeLocal;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

/**
 * Bean de reportes refactorizado para usar la clase base ReporteBasePDF.
 * Centraliza la generación de reportes en PDF con estilos globales consistentes.
 */
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

    private MenuModel model;

    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();
    }

    /**
     * Exporta el reporte general de salud del paciente desde el dashboard.
     * Incluye glucosa, citas y medicamentos en un solo PDF.
     */
    public void exportarReporteSaludPDF() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");

        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "Usuario no autenticado"));
            return;
        }

        try {
            ReporteGeneralPDF reporte = new ReporteGeneralPDF(usuario, glucosaFacade, 
                medicamentoFacade, citaFacade);
            reporte.exportarPDF();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "No se pudo generar el reporte de salud."));
            e.printStackTrace();
        }
    }

    /**
     * Exporta CSV de usuarios (solo admin).
     */
    public void exportarCSV() throws IOException {
        exportar("text/csv", "usuarios.csv", ",");
    }

    /**
     * Exporta XLS de usuarios (solo admin).
     */
    public void exportarXLS() throws IOException {
        exportar("application/vnd.ms-excel", "usuarios.xls", "\t");
    }

    /**
     * Método genérico para exportar usuarios en diferentes formatos.
     */
    private void exportar(String tipo, String nombreArchivo, String separador) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        response.setContentType(tipo);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");

        PrintWriter writer = response.getWriter();
        writer.println("ID" + separador + "Usuario" + separador + "Nombres" + separador + 
                      "Apellidos" + separador + "Edad" + separador + "Correo" + separador + "Documento");

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

    /**
     * Detecta la página actual y exporta el reporte correspondiente.
     * Verifica primero las páginas específicas antes de la página genérica de index.
     */
    public void descargarReporteDinamico() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        String viewId = context.getViewRoot().getViewId();

        if (viewId == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                "Aviso", "No se puede detectar la página actual."));
            return;
        }

        String lower = viewId.toLowerCase();
        
        // Evaluar páginas específicas primero (antes que index genérico)
        if (lower.contains("glucosa") || lower.contains("registroglucosa")) {
            exportarGlucosaPDF();
        } else if (lower.contains("cita") || lower.contains("citas")) {
            exportarCitasPDF();
        } else if (lower.contains("medicamento") || lower.contains("medicamentos")) {
            exportarMedicamentosPDF();
        } else if (lower.contains("index")) {
            // Dashboard del paciente (evaluar al final)
            exportarReporteSaludPDF();
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
                "Aviso", "No se puede generar reporte para esta página."));
        }
    }

    /**
     * Exporta PDF de registros de glucosa del paciente.
     */
    private void exportarGlucosaPDF() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");

        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "Usuario no autenticado"));
            return;
        }

        List<Glucosa> glucosaList = glucosaFacade.findByUsuario(usuario);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"glucosa.pdf\"");

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            ReporteBasePDF basePDF = new ReporteBasePDF() {
                @Override
                protected void generarReporte(Document doc, Usuario usr) throws DocumentException {
                    // No se usa en este contexto
                }
            };

            basePDF.agregarEncabezadoDocumento(document);
            basePDF.agregarTitulo(document, "Reporte de Glucosa");
            basePDF.agregarEncabezadoPaciente(document, usuario);

            if (!glucosaList.isEmpty()) {
                basePDF.agregarSeccion(document, "Registros de Glucosa");

                // Añadir gráfico generado en la clase base (promedio diarios últimos 7 días)
                try {
                    Image chart = basePDF.crearGraficoGlucosa(glucosaList);
                    if (chart != null) {
                        chart.scaleToFit(500, 300);
                        chart.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                        document.add(chart);

                        Paragraph legend = new Paragraph("Gráfico de tendencia (promedio diario - últimos 7 días)", basePDF.fontEnfasis);
                        legend.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                        legend.setSpacingAfter(12f);
                        document.add(legend);
                    }
                } catch (Exception ex) {
                    // Ignorar error de gráfico
                }

                // Agrupar por mes y generar tablas por mes
                java.util.Map<Integer, java.util.List<Glucosa>> agrupado = glucosaList.stream()
                    .collect(java.util.stream.Collectors.groupingBy(g -> {
                        java.util.Calendar cal = java.util.Calendar.getInstance();
                        cal.setTime(g.getFechaHora());
                        return cal.get(java.util.Calendar.YEAR) * 100 + (cal.get(java.util.Calendar.MONTH) + 1);
                    }));

                java.util.List<Integer> mesesOrdenados = agrupado.keySet().stream()
                    .sorted(java.util.Comparator.reverseOrder())
                    .collect(java.util.stream.Collectors.toList());

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                SimpleDateFormat sdfMes = new SimpleDateFormat("MMMM yyyy", new java.util.Locale("es", "ES"));

                for (Integer clave : mesesOrdenados) {
                    int year = clave / 100;
                    int month = clave % 100;
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.set(java.util.Calendar.YEAR, year);
                    cal.set(java.util.Calendar.MONTH, month - 1);
                    cal.set(java.util.Calendar.DAY_OF_MONTH, 1);

                    Paragraph mesTitle = new Paragraph(sdfMes.format(cal.getTime()), basePDF.fontSubtitulo);
                    mesTitle.setSpacingBefore(8f);
                    mesTitle.setSpacingAfter(6f);
                    document.add(mesTitle);

                    java.util.List<Glucosa> listaMes = agrupado.get(clave).stream()
                        .sorted((g1, g2) -> g2.getFechaHora().compareTo(g1.getFechaHora()))
                        .collect(java.util.stream.Collectors.toList());

                    PdfPTable table = new PdfPTable(3);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(5f);
                    table.addCell(basePDF.crearCeldaHeader("Nivel (mg/dL)"));
                    table.addCell(basePDF.crearCeldaHeader("Fecha y Hora"));
                    table.addCell(basePDF.crearCeldaHeader("Momento del Día"));

                    for (Glucosa g : listaMes) {
                        table.addCell(basePDF.crearCeldaData(String.valueOf(g.getNivelGlucosa())));
                        table.addCell(basePDF.crearCeldaData(sdf.format(g.getFechaHora())));
                        table.addCell(basePDF.crearCeldaData(g.getMomentoDia() != null ? g.getMomentoDia() : "N/A"));
                    }
                    document.add(table);
                    document.add(new Paragraph(" "));
                }
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

    /**
     * Exporta PDF de medicamentos del paciente.
     */
    private void exportarMedicamentosPDF() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");

        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "Usuario no autenticado"));
            return;
        }

        List<Medicamento> medicamentoList = medicamentoFacade.findByUsuario(usuario);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"medicamentos.pdf\"");

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            ReporteBasePDF basePDF = new ReporteBasePDF() {
                @Override
                protected void generarReporte(Document doc, Usuario usr) throws DocumentException {
                    // No se usa en este contexto
                }
            };

            basePDF.agregarEncabezadoDocumento(document);
            basePDF.agregarTitulo(document, "Reporte de Medicamentos");
            basePDF.agregarEncabezadoPaciente(document, usuario);

            if (!medicamentoList.isEmpty()) {
                basePDF.agregarSeccion(document, "Medicamentos");

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.addCell(basePDF.crearCeldaHeader("Nombre"));
                table.addCell(basePDF.crearCeldaHeader("Dosis"));
                table.addCell(basePDF.crearCeldaHeader("Frecuencia"));
                table.addCell(basePDF.crearCeldaHeader("Fecha Inicio"));
                table.addCell(basePDF.crearCeldaHeader("Fecha Fin"));

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                for (Medicamento m : medicamentoList) {
                    table.addCell(basePDF.crearCeldaData(m.getNombre()));
                    table.addCell(basePDF.crearCeldaData(m.getDosis()));
                    table.addCell(basePDF.crearCeldaData(m.getFrecuencia()));
                    table.addCell(basePDF.crearCeldaData(sdf.format(m.getFechaInicio())));
                    table.addCell(basePDF.crearCeldaData(sdf.format(m.getFechaFin())));
                }
                document.add(table);
            } else {
                document.add(new Paragraph("No hay medicamentos para mostrar."));
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
            context.responseComplete();
        }
    }

    /**
     * Exporta PDF de citas del paciente.
     */
    private void exportarCitasPDF() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");

        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "Usuario no autenticado"));
            return;
        }

        List<com.mycompany.checkinc.entities.Cita> citaList = citaFacade.findByUsuario(usuario);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"citas.pdf\"");

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            ReporteBasePDF basePDF = new ReporteBasePDF() {
                @Override
                protected void generarReporte(Document doc, Usuario usr) throws DocumentException {
                    // no-op
                }
            };

            basePDF.agregarEncabezadoDocumento(document);
            basePDF.agregarTitulo(document, "Reporte de Citas");
            basePDF.agregarEncabezadoPaciente(document, usuario);

            if (citaList == null || citaList.isEmpty()) {
                document.add(new Paragraph("No hay citas registradas."));
                return;
            }

            // Agrupar por mes (año*100+mes)
            java.util.Map<Integer, java.util.List<com.mycompany.checkinc.entities.Cita>> agrupado = citaList.stream()
                .collect(java.util.stream.Collectors.groupingBy(c -> {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(c.getFecha());
                    return cal.get(java.util.Calendar.YEAR) * 100 + (cal.get(java.util.Calendar.MONTH) + 1);
                }));

            java.util.List<Integer> ordenMeses = agrupado.keySet().stream()
                .sorted(java.util.Comparator.reverseOrder())
                .collect(java.util.stream.Collectors.toList());

            SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdfMes = new SimpleDateFormat("MMMM yyyy", new java.util.Locale("es", "ES"));

            for (Integer clave : ordenMeses) {
                int year = clave / 100;
                int month = clave % 100;
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(java.util.Calendar.YEAR, year);
                cal.set(java.util.Calendar.MONTH, month - 1);
                cal.set(java.util.Calendar.DAY_OF_MONTH, 1);

                Paragraph mesTitle = new Paragraph(sdfMes.format(cal.getTime()), basePDF.fontSubtitulo);
                mesTitle.setSpacingBefore(8f);
                mesTitle.setSpacingAfter(6f);
                document.add(mesTitle);

                java.util.List<com.mycompany.checkinc.entities.Cita> listaMes = agrupado.get(clave).stream()
                    .sorted((a, b) -> {
                        int cmp = a.getFecha().compareTo(b.getFecha());
                        if (cmp != 0) return cmp;
                        return a.getHora().compareTo(b.getHora());
                    })
                    .collect(java.util.stream.Collectors.toList());

                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setSpacingBefore(5f);
                table.addCell(basePDF.crearCeldaHeader("Fecha"));
                table.addCell(basePDF.crearCeldaHeader("Hora"));
                table.addCell(basePDF.crearCeldaHeader("Motivo"));
                table.addCell(basePDF.crearCeldaHeader("Estado"));

                for (com.mycompany.checkinc.entities.Cita c : listaMes) {
                    table.addCell(basePDF.crearCeldaData(sdfFecha.format(c.getFecha())));
                    table.addCell(basePDF.crearCeldaData(sdfHora.format(c.getHora())));
                    table.addCell(basePDF.crearCeldaData(c.getMotivo()));
                    table.addCell(basePDF.crearCeldaData(c.getEstado() != null ? c.getEstado() : "N/A"));
                }
                document.add(table);
                document.add(new Paragraph(" "));
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
            context.responseComplete();
        }
    }

    public MenuModel getModel() {
        return model;
    }
}
