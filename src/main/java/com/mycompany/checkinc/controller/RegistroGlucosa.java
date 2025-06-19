package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Glucosa;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.GlucosaFacadeLocal;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartDataSet;

@ManagedBean(name = "registroGlucosa")
@ViewScoped
public class RegistroGlucosa implements Serializable {
    private static final String USUARIO_SESSION_KEY = "usuario";
    private static final String ERROR = "Error";

    @EJB
    private GlucosaFacadeLocal glucosaFacade;
    
    @EJB
    private UsuarioFacadeLocal usuarioFacade;
    
    private Integer id;
    private Double nivelGlucosa;
    private Date fechaHora;
    private List<Glucosa> registros;
    private boolean editando;
    private LineChartModel lineChartModel;
    
    public RegistroGlucosa() {
        this.fechaHora = new Date();
    }
    
    @PostConstruct
    public void init() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USUARIO_SESSION_KEY);
        if (usuario != null) {
            cargarRegistros(usuario);
        }
        initLineChart();
    }
    
    private void cargarRegistros(Usuario usuario) {
        this.registros = glucosaFacade.findByUsuario(usuario);
        initLineChart(); // Actualiza el gráfico al cargar registros
    }

    private void initLineChart() {
        lineChartModel = new LineChartModel();
        ChartData data = new ChartData();
        LineChartDataSet dataSet = new LineChartDataSet();
        java.util.List<Object> values = new java.util.ArrayList<>();
        java.util.List<String> labels = new java.util.ArrayList<>();
        if (registros != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (Glucosa g : registros) {
                values.add(g.getNivelGlucosa());
                labels.add(sdf.format(g.getFechaHora()));
            }
        }
        dataSet.setData(values);
        dataSet.setLabel("Nivel de Glucosa");
        data.setLabels(labels);
        data.addChartDataSet(dataSet);
        lineChartModel.setData(data);
    }
    
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Double getNivelGlucosa() { return nivelGlucosa; }
    public void setNivelGlucosa(Double nivelGlucosa) { this.nivelGlucosa = nivelGlucosa; }
    
    public Date getFechaHora() { return fechaHora; }
    public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }
    
    public List<Glucosa> getRegistros() { return registros; }
    
    public boolean isEditando() { return editando; }
    public void setEditando(boolean editando) { this.editando = editando; }

    public LineChartModel getLineChartModel() {
        return lineChartModel;
    }

    // Método para el botón 'Ahora' en la vista
    public void setFechaHoraNow() {
        this.fechaHora = new Date();
    }
    
    // Métodos de acción
    public void registrar() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USUARIO_SESSION_KEY);
        if (usuario == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "Debe iniciar sesión para registrar");
            return;
        }
        try {
            if (editando) {
                Glucosa glucosa = glucosaFacade.find(id);
                if (glucosa == null) {
                    addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "Registro no encontrado");
                    return;
                }
                glucosa.setNivelGlucosa(nivelGlucosa.floatValue());
                glucosa.setFechaHora(fechaHora);
                glucosaFacade.edit(glucosa);
                addMessage(FacesMessage.SEVERITY_INFO, "Registro actualizado", "El registro ha sido actualizado correctamente");
                editando = false;
            } else {
                Glucosa glucosa = new Glucosa();
                glucosa.setNivelGlucosa(nivelGlucosa.floatValue());
                glucosa.setFechaHora(fechaHora);
                glucosa.setIdUsuario(usuario);
                glucosaFacade.create(glucosa);
                addMessage(FacesMessage.SEVERITY_INFO, "Registro guardado", "El registro ha sido guardado correctamente");
            }
            cargarRegistros(usuario);
            limpiarFormulario();
            initLineChart(); // Actualiza el gráfico después de registrar
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "No se pudo procesar el registro: " + e.getMessage());
        }
    }
    
    public void editar(Glucosa glucosa) {
        this.id = glucosa.getIdGlucosa();
        this.nivelGlucosa = Double.valueOf(glucosa.getNivelGlucosa());
        this.fechaHora = glucosa.getFechaHora();
        this.editando = true;
    }
    
    public void eliminar(Glucosa glucosa) {
        try {
            glucosaFacade.remove(glucosa);
            Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USUARIO_SESSION_KEY);
            if (usuario != null) {
                cargarRegistros(usuario);
            }
            addMessage(FacesMessage.SEVERITY_INFO, "Registro eliminado", "El registro ha sido eliminado correctamente");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "No se pudo eliminar el registro: " + e.getMessage());
        }
    }
    
    public void cancelarEdicion() {
        limpiarFormulario();
        editando = false;
    }
    
    private void limpiarFormulario() {
        this.id = null;
        this.nivelGlucosa = null;
        this.fechaHora = new Date();
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
}
