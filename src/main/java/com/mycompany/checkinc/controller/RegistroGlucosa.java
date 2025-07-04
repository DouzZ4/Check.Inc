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
    private org.primefaces.model.charts.line.LineChartModel lineChartModel;
    
    // --- Filtro y método filtrado para nivel de glucosa ---
    private String filtroNivel;
    public String getFiltroNivel() { return filtroNivel; }
    public void setFiltroNivel(String filtroNivel) { this.filtroNivel = filtroNivel; }

    public List<Glucosa> getRegistrosFiltrados() {
        if (registros == null) return java.util.Collections.emptyList();
        java.util.stream.Stream<Glucosa> stream = registros.stream();
        if (filtroNivel != null && !filtroNivel.isEmpty()) {
            stream = stream.filter(g -> String.valueOf(g.getNivelGlucosa()).contains(filtroNivel));
        }
        return stream.collect(java.util.stream.Collectors.toList());
    }
    
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
        org.primefaces.model.charts.line.LineChartModel model = new org.primefaces.model.charts.line.LineChartModel();
        org.primefaces.model.charts.ChartData data = new org.primefaces.model.charts.ChartData();
        org.primefaces.model.charts.line.LineChartDataSet dataSet = new org.primefaces.model.charts.line.LineChartDataSet();

        java.util.List<Object> values = new java.util.ArrayList<>();
        java.util.List<String> labels = new java.util.ArrayList<>();
        if (registros != null) {
            java.text.SimpleDateFormat sdfShort = new java.text.SimpleDateFormat("dd/MM"); // Eje X
            java.text.SimpleDateFormat sdfFull = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm"); // Tooltip
            for (Glucosa g : registros) {
                // El valor sigue siendo numérico, pero la etiqueta del eje X es corta
                values.add(g.getNivelGlucosa());
                // La etiqueta del eje X es corta
                labels.add(sdfShort.format(g.getFechaHora()));
                // Guardamos la fecha completa en el label del dataset para el tooltip (truco visual)
            }
            // Truco: mostrar la fecha completa en el label del dataset (esto aparece en el tooltip)
            if (!registros.isEmpty()) {
                dataSet.setLabel("Nivel de Glucosa (" + sdfFull.format(registros.get(registros.size()-1).getFechaHora()) + ")");
            }
        }
        dataSet.setData(values);
        dataSet.setLabel("Nivel de Glucosa");
        dataSet.setFill(true);
        dataSet.setBorderColor("#3058a6"); // Azul fuerte
        dataSet.setBackgroundColor("rgba(48,88,166,0.15)"); // Relleno suave
        dataSet.setPointBackgroundColor("#f45501"); // Naranja para los puntos
        dataSet.setPointRadius(5);
        dataSet.setPointHoverRadius(7);
        dataSet.setTension(0.4); // Línea suavizada
        dataSet.setShowLine(true);

        data.setLabels(labels);
        data.addChartDataSet(dataSet);
        model.setData(data);

        // Opciones del gráfico
        org.primefaces.model.charts.line.LineChartOptions options = new org.primefaces.model.charts.line.LineChartOptions();
        options.setResponsive(true);
        options.setLegend(new org.primefaces.model.charts.optionconfig.legend.Legend());
        options.getLegend().setDisplay(true);
        options.getLegend().setPosition("top");
        options.setTitle(new org.primefaces.model.charts.optionconfig.title.Title());
        options.getTitle().setDisplay(true);
        options.getTitle().setText("Tendencia de Glucosa");
        model.setOptions(options);

        this.lineChartModel = model;
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

    public org.primefaces.model.charts.line.LineChartModel getLineChartModel() {
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
