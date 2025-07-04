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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Date;
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

    public void exportarPDF() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"usuarios.pdf\"");

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 11);

            Paragraph title = new Paragraph("Listado de Usuarios Registrados", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 2, 3, 3, 1, 4, 2});

            String[] headers = {"ID", "Usuario", "Nombres", "Apellidos", "Edad", "Correo", "Documento"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            List<Usuario> usuarios = usuarioFacade.findAll();
            for (Usuario u : usuarios) {
                table.addCell(new Phrase(String.valueOf(u.getIdUsuario()), cellFont));
                table.addCell(new Phrase(u.getUser(), cellFont));
                table.addCell(new Phrase(u.getNombres(), cellFont));
                table.addCell(new Phrase(u.getApellidos(), cellFont));
                table.addCell(new Phrase(String.valueOf(u.getEdad()), cellFont));
                table.addCell(new Phrase(u.getCorreo(), cellFont));
                table.addCell(new Phrase(String.valueOf(u.getDocumento()), cellFont));
            }

            document.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
            context.responseComplete();
        }
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
        context.responseComplete();
    }

    public void exportarMedicamentosPDF() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=medicamentos.pdf");

    Document document = new Document();
    try {
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Colores corporativos
        BaseColor azul = new BaseColor(48, 88, 166);
        BaseColor naranja = new BaseColor(244, 85, 1);
        BaseColor grisClaro = new BaseColor(230, 230, 230);

        // Logo del sistema (ICONO.png)
        String logoPath = context.getExternalContext().getRealPath("/resources/images/ICONO.png");
        if (logoPath != null) {
            Image logo = Image.getInstance(logoPath);
            logo.scaleAbsolute(60, 60);
            logo.setAlignment(Element.ALIGN_LEFT);
            document.add(logo);
        }

        // Datos del paciente
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");
        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no autenticado"));
            return;
        }

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, azul);
        Font subFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);

        Paragraph title = new Paragraph("Reporte de Medicamentos", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10f);
        document.add(title);

        Paragraph description = new Paragraph("Este reporte muestra los medicamentos registrados para el tratamiento del paciente.", subFont);
        description.setAlignment(Element.ALIGN_CENTER);
        description.setSpacingAfter(15f);
        document.add(description);

        Paragraph datosPaciente = new Paragraph(String.format("Nombre: %s %s\nDocumento: %s\nCorreo: %s\nEdad: %d",
                usuario.getNombres(), usuario.getApellidos(), usuario.getDocumento(), usuario.getCorreo(), usuario.getEdad()), subFont);
        datosPaciente.setSpacingAfter(20f);
        document.add(datosPaciente);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new java.util.Locale("es", "ES"));
        Paragraph fecha = new Paragraph("Fecha de emisión: " + sdf.format(new java.util.Date()), new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
        fecha.setAlignment(Element.ALIGN_RIGHT);
        fecha.setSpacingAfter(10f);
        document.add(fecha);

        // Tabla de medicamentos
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{3, 2, 2, 2, 2});

        String[] headers = {"Nombre", "Dosis", "Frecuencia", "Inicio", "Fin"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(naranja);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8);
            table.addCell(cell);
        }

        List<Medicamento> medicamentos = medicamentoFacade.findByUsuario(usuario);
        java.text.SimpleDateFormat sdfFechaHora = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", new java.util.Locale("es", "ES"));

        for (Medicamento m : medicamentos) {
            table.addCell(estilizarCelda(m.getNombre(), cellFont, BaseColor.WHITE));
            table.addCell(estilizarCelda(m.getDosis(), cellFont, grisClaro));
            table.addCell(estilizarCelda(m.getFrecuencia(), cellFont, BaseColor.WHITE));
            table.addCell(estilizarCelda(sdfFechaHora.format(m.getFechaInicio()), cellFont, grisClaro));
            table.addCell(estilizarCelda(sdfFechaHora.format(m.getFechaFin()), cellFont, BaseColor.WHITE));
        }

        document.add(table);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        document.close();
        context.responseComplete();
    }
}

    private void exportarGlucosaPDF() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=glucosa.pdf");

    Document document = new Document();
    try {
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Colores corporativos
        BaseColor azul = new BaseColor(48, 88, 166);
        BaseColor naranja = new BaseColor(244, 85, 1);
        BaseColor grisClaro = new BaseColor(230, 230, 230);

        // Logo del sistema (ICONO.png)
        String logoPath = context.getExternalContext().getRealPath("/resources/images/ICONO.png");
        if (logoPath != null) {
            Image logo = Image.getInstance(logoPath);
            logo.scaleAbsolute(60, 60);
            logo.setAlignment(Element.ALIGN_LEFT);
            document.add(logo);
        }

        // Datos del paciente
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");
        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no autenticado"));
            return;
        }

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, azul);
        Font subFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);

        Paragraph title = new Paragraph("Reporte de Niveles de Glucosa", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10f);
        document.add(title);

        Paragraph description = new Paragraph("Este reporte muestra los niveles de glucosa registrados por el paciente.", subFont);
        description.setAlignment(Element.ALIGN_CENTER);
        description.setSpacingAfter(15f);
        document.add(description);

        Paragraph datosPaciente = new Paragraph(String.format("Nombre: %s %s\nDocumento: %s\nCorreo: %s\nEdad: %d",
                usuario.getNombres(), usuario.getApellidos(), usuario.getDocumento(), usuario.getCorreo(), usuario.getEdad()), subFont);
        datosPaciente.setSpacingAfter(20f);
        document.add(datosPaciente);

        // Fecha en español
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new java.util.Locale("es", "ES"));
        Paragraph fecha = new Paragraph("Fecha de emisión: " + sdf.format(new java.util.Date()), new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
        fecha.setAlignment(Element.ALIGN_RIGHT);
        fecha.setSpacingAfter(10f);
        document.add(fecha);

        // Tabla de glucosa (sin ID)
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{2, 3});

        String[] headers = {"Nivel de Glucosa (mg/dL)", "Fecha y Hora"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(naranja);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8);
            table.addCell(cell);
        }

        List<Glucosa> registros = glucosaFacade.findByUsuario(usuario);
        java.text.SimpleDateFormat sdfFechaHora = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", new java.util.Locale("es", "ES"));
        for (Glucosa g : registros) {
            table.addCell(estilizarCelda(String.valueOf(g.getNivelGlucosa()), cellFont, grisClaro));
            table.addCell(estilizarCelda(sdfFechaHora.format(g.getFechaHora()), cellFont, BaseColor.WHITE));
        }

        document.add(table);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        document.close();
        context.responseComplete();
    }
}

private void exportarCitasPDF() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=citas.pdf");

    Document document = new Document();
    try {
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Colores corporativos
        BaseColor azul = new BaseColor(48, 88, 166);
        BaseColor naranja = new BaseColor(244, 85, 1);
        BaseColor grisClaro = new BaseColor(230, 230, 230);

        // Logo del sistema (ICONO.png)
        String logoPath = context.getExternalContext().getRealPath("/resources/images/ICONO.png");
        if (logoPath != null) {
            Image logo = Image.getInstance(logoPath);
            logo.scaleAbsolute(60, 60);
            logo.setAlignment(Element.ALIGN_LEFT);
            document.add(logo);
        }

        // Datos del paciente
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");
        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no autenticado"));
            return;
        }

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, azul);
        Font subFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);

        Paragraph title = new Paragraph("Reporte de Citas Médicas", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10f);
        document.add(title);

        Paragraph description = new Paragraph("Este reporte contiene todas las citas médicas registradas por el paciente con su fecha y motivo correspondiente.", subFont);
        description.setAlignment(Element.ALIGN_CENTER);
        description.setSpacingAfter(15f);
        document.add(description);

        Paragraph datosPaciente = new Paragraph(String.format("Nombre: %s %s\nDocumento: %s\nCorreo: %s\nEdad: %d",
                usuario.getNombres(), usuario.getApellidos(), usuario.getDocumento(), usuario.getCorreo(), usuario.getEdad()), subFont);
        datosPaciente.setSpacingAfter(20f);
        document.add(datosPaciente);

        // Fecha en español
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new java.util.Locale("es", "ES"));
        Paragraph fecha = new Paragraph("Fecha de emisión: " + sdf.format(new java.util.Date()), new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
        fecha.setAlignment(Element.ALIGN_RIGHT);
        fecha.setSpacingAfter(10f);
        document.add(fecha);

        // Tabla de citas (sin ID)
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{2, 4});

        String[] headers = {"Fecha y Hora", "Motivo"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(naranja);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8);
            table.addCell(cell);
        }

        List<Cita> citas = citaFacade.findByUsuario(usuario);
        java.text.SimpleDateFormat sdfFecha = new java.text.SimpleDateFormat("dd/MM/yyyy", new java.util.Locale("es", "ES"));
        java.text.SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm", new java.util.Locale("es", "ES"));
        for (Cita c : citas) {
            String fechaHora = sdfFecha.format(c.getFecha()) + " " + sdfHora.format(c.getHora());
            table.addCell(estilizarCelda(fechaHora, cellFont, grisClaro));
            table.addCell(estilizarCelda(c.getMotivo(), cellFont, BaseColor.WHITE));
        }

        document.add(table);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        document.close();
        context.responseComplete();
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



    private MenuModel model;

    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();
    }

    public MenuModel getModel() {
        return model;
    }
}
