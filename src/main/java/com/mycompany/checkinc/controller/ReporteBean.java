package com.mycompany.checkinc.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.checkinc.entities.Medicamento;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.MedicamentoFacadeLocal;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
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

        if (viewId.contains("glucosa")) {
            exportarGlucosaPDF();
        } else if (viewId.contains("Medicamentos")) {
            exportarMedicamentosPDF();
        } else if (viewId.contains("Citas")) {
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
        response.setHeader("Content-Disposition", "attachment; filename=\"medicamentos.pdf\"");

        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");
        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario no autenticado.", null));
            return;
        }

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 11);

            Paragraph title = new Paragraph("Medicamentos del Paciente", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 3, 2, 2, 2, 2});

            String[] headers = {"ID", "Nombre", "Dosis", "Frecuencia", "Inicio", "Fin"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(new BaseColor(211, 211, 211));
                table.addCell(cell);
            }

            List<Medicamento> medicamentos = medicamentoFacade.findByUsuario(usuario);

            for (Medicamento m : medicamentos) {
                table.addCell(new Phrase(String.valueOf(m.getIdMedicamento()), cellFont));
                table.addCell(new Phrase(m.getNombre(), cellFont));
                table.addCell(new Phrase(m.getDosis(), cellFont));
                table.addCell(new Phrase(m.getFrecuencia(), cellFont));
                table.addCell(new Phrase(m.getFechaInicio().toString(), cellFont));
                table.addCell(new Phrase(m.getFechaFin().toString(), cellFont));
            }

            document.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
            context.responseComplete();
        }
    }

    private void exportarGlucosaPDF() throws IOException {
        // Aquí va la lógica para exportar glucosa
    }

    private void exportarCitasPDF() throws IOException {
        // Aquí va la lógica para exportar citas
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
