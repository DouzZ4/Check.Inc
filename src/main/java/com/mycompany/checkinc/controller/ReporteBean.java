package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Cita;
import com.mycompany.checkinc.entities.Glucosa;
import com.mycompany.checkinc.entities.Medicamento;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.CitaFacadeLocal;
import com.mycompany.checkinc.services.GlucosaFacadeLocal;
import com.mycompany.checkinc.services.MedicamentoFacadeLocal;
import com.mycompany.checkinc.services.ReportePDFServiceLocal;
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
    
    @EJB
    private GlucosaFacadeLocal glucosaFacade;

    @EJB
    private CitaFacadeLocal citaFacade;
    
    @EJB
    private ReportePDFServiceLocal reportePDFService;

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
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");

        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no autenticado"));
            return;
        }

        if (viewId.contains("registroGlucosa")) {
            List<Glucosa> glucosaList = glucosaFacade.findByUsuario(usuario);
            reportePDFService.exportarGlucosaPDF(response, usuario, glucosaList);
        } else if (viewId.contains("IndexMedicamentos")) {
            List<Medicamento> medicamentoList = medicamentoFacade.findByUsuario(usuario);
            reportePDFService.exportarMedicamentosPDF(response, usuario, medicamentoList);
        } else if (viewId.contains("IndexCitas")) {
            List<Cita> citaList = citaFacade.findByUsuario(usuario);
            reportePDFService.exportarCitasPDF(response, usuario, citaList);
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Advertencia", "No se puede generar el reporte para esta vista."));
        }
    }

    public void exportarReporteCompletoPDF() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        Usuario usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");

        if (usuario == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no autenticado"));
            return;
        }

        List<Glucosa> glucosaList = glucosaFacade.findByUsuario(usuario);
        List<Cita> citaList = citaFacade.findByUsuario(usuario);
        List<Medicamento> medicamentoList = medicamentoFacade.findByUsuario(usuario);

        reportePDFService.exportarReporteCompletoPDF(response, usuario, glucosaList, citaList, medicamentoList);
        
        context.responseComplete();
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
