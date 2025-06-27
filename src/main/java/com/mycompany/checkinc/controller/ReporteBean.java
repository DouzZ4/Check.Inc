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

        Paragraph fecha = new Paragraph("Fecha de emisión: " + new Date().toString(), new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
        fecha.setAlignment(Element.ALIGN_RIGHT);
        fecha.setSpacingAfter(10f);
        document.add(fecha);

        // Tabla de medicamentos
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 3, 2, 2, 2, 2});

        String[] headers = {"ID", "Nombre", "Dosis", "Frecuencia", "Inicio", "Fin"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(naranja);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8);
            table.addCell(cell);
        }

        List<Medicamento> medicamentos = medicamentoFacade.findByUsuario(usuario);
        for (Medicamento m : medicamentos) {
            table.addCell(estilizarCelda(String.valueOf(m.getIdMedicamento()), cellFont, grisClaro));
            table.addCell(estilizarCelda(m.getNombre(), cellFont, BaseColor.WHITE));
            table.addCell(estilizarCelda(m.getDosis(), cellFont, grisClaro));
            table.addCell(estilizarCelda(m.getFrecuencia(), cellFont, BaseColor.WHITE));
            table.addCell(estilizarCelda(m.getFechaInicio().toString(), cellFont, grisClaro));
            table.addCell(estilizarCelda(m.getFechaFin().toString(), cellFont, BaseColor.WHITE));
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


    private void exportarGlucosaPDF() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=\"glucosa.pdf\"");

    Document document = new Document();
    try {
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Colores personalizados
        BaseColor azul = new BaseColor(0x30, 0x58, 0xA6);
        BaseColor naranja = new BaseColor(0xF4, 0x55, 0x01);
        BaseColor grisClaro = new BaseColor(211, 211, 211);

        // Fuentes
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, azul);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, naranja);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 11);

        // Logo (ruta relativa desde /resources/images/)
        String logoPath = context.getExternalContext().getRealPath("/resources/images/ICONO.png");
        com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(logoPath);
        logo.scaleAbsolute(50, 50);
        logo.setAlignment(Element.ALIGN_LEFT);
        document.add(logo);

        // Título
        Paragraph title = new Paragraph("Reporte de Niveles de Glucosa", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10f);
        document.add(title);

        // Descripción
        Paragraph descripcion = new Paragraph("Este reporte muestra todos los niveles de glucosa registrados por el paciente a lo largo del tiempo.", subtitleFont);
        descripcion.setAlignment(Element.ALIGN_CENTER);
        descripcion.setSpacingAfter(15f);
        document.add(descripcion);

        // Datos del paciente
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");
        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no autenticado"));
            return;
        }

        Paragraph datosPaciente = new Paragraph(
            "Paciente: " + usuario.getNombres() + " " + usuario.getApellidos() + "\n" +
            "Documento: " + usuario.getDocumento() + "\n" +
            "Correo: " + usuario.getCorreo() + "\n" +
            "Edad: " + usuario.getEdad(), cellFont);
        datosPaciente.setSpacingAfter(20f);
        document.add(datosPaciente);

        // Fecha actual
        Paragraph fecha = new Paragraph("Fecha de generación: " + java.time.LocalDate.now().toString(), cellFont);
        fecha.setAlignment(Element.ALIGN_RIGHT);
        fecha.setSpacingAfter(20f);
        document.add(fecha);

        // Tabla
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 2, 3});

        String[] headers = {"ID", "Nivel de Glucosa", "Fecha y Hora"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(grisClaro);
            table.addCell(cell);
        }

        List<Glucosa> registros = glucosaFacade.findByUsuario(usuario);
        for (Glucosa g : registros) {
            table.addCell(new Phrase(g.getIdGlucosa().toString(), cellFont));
            table.addCell(new Phrase(String.valueOf(g.getNivelGlucosa()), cellFont));
            table.addCell(new Phrase(g.getFechaHora().toString(), cellFont));
        }

        document.add(table);

    } catch (DocumentException | IOException e) {
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
    response.setHeader("Content-Disposition", "attachment; filename=\"citas.pdf\"");

    Document document = new Document();
    try {
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, new BaseColor(48, 88, 166));
        Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 11);

        // Logo
        String logoPath = context.getExternalContext().getRealPath("/resources/images/ICONO.png");
        Image logo = Image.getInstance(logoPath);
        logo.scaleAbsolute(50, 50);
        logo.setAlignment(Element.ALIGN_LEFT);
        document.add(logo);

        // Título
        Paragraph title = new Paragraph("Reporte de Citas Médicas", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5f);
        document.add(title);

        // Subtítulo
        Paragraph description = new Paragraph("Este reporte contiene todas las citas médicas registradas por el paciente con su fecha y motivo correspondiente.", subTitleFont);
        description.setAlignment(Element.ALIGN_CENTER);
        description.setSpacingAfter(15f);
        document.add(description);

        // Datos del paciente
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");
        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no autenticado"));
            return;
        }

        Paragraph datos = new Paragraph("Paciente: " + usuario.getNombres() + " " + usuario.getApellidos() +
                "\nDocumento: " + usuario.getDocumento() +
                "\nCorreo: " + usuario.getCorreo() +
                "\nEdad: " + usuario.getEdad(), cellFont);
        datos.setSpacingAfter(15f);
        document.add(datos);

        // Fecha actual
        Paragraph fecha = new Paragraph("Fecha de emisión: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()), cellFont);
        fecha.setAlignment(Element.ALIGN_RIGHT);
        fecha.setSpacingAfter(10f);
        document.add(fecha);

        // Tabla
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 2, 4});

        String[] headers = {"ID", "Fecha", "Motivo"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new BaseColor(244, 85, 1));
            cell.setPadding(5);
            cell.setBorderColor(BaseColor.GRAY);
            cell.setBorderWidth(1);
            table.addCell(cell);
        }

        List<Cita> citas = citaFacade.findByUsuario(usuario);
        for (Cita c : citas) {
            table.addCell(estilizarCelda(String.valueOf(c.getIdCita()), cellFont));
            table.addCell(estilizarCelda(c.getFecha().toString() + " " + c.getHora().toString(), cellFont));
            table.addCell(estilizarCelda(c.getMotivo(), cellFont));
        }

        document.add(table);
    } catch (DocumentException | IOException e) {
        e.printStackTrace();
    } finally {
        document.close();
        context.responseComplete();
    }
}

private PdfPCell estilizarCelda(String texto, Font font) {
    PdfPCell cell = new PdfPCell(new Phrase(texto, font));
    cell.setPadding(5);
    cell.setBorderColor(BaseColor.LIGHT_GRAY);
    cell.setBorderWidth(1);
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
