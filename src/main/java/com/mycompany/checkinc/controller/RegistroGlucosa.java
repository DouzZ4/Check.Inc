package com.mycompany.checkinc.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import com.mycompany.checkinc.model.GlucosaModel;

@ManagedBean(name = "registroGlucosa")
@ViewScoped
public class RegistroGlucosa implements Serializable {
    @ManagedProperty("#{login}")
    private Login login;
    
    private GlucosaModel model;
    private Integer id;
    private Double nivelGlucosa;
    private Date fechaHora;
    private List<RegistroGlucosaDTO> registros;
    private boolean editando;
    
    public RegistroGlucosa() {
        this.model = new GlucosaModel();
        this.fechaHora = new Date();
        this.registros = new ArrayList<>();
    }
    
    @PostConstruct
    public void init() {
        if (login != null && login.isLoggedIn()) {
            cargarRegistros();
        }
    }
    
    private void cargarRegistros() {
        this.registros = model.obtenerRegistros(login.getIdUsuario());
    }
    
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Double getNivelGlucosa() { return nivelGlucosa; }
    public void setNivelGlucosa(Double nivelGlucosa) { this.nivelGlucosa = nivelGlucosa; }
    
    public Date getFechaHora() { return fechaHora; }
    public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }
    
    public List<RegistroGlucosaDTO> getRegistros() { return registros; }
    
    public boolean isEditando() { return editando; }
    public void setEditando(boolean editando) { this.editando = editando; }
    
    public void setLogin(Login login) { this.login = login; }
    
    // Métodos de acción
    public void registrar() {
        if (!login.isLoggedIn()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe iniciar sesión para registrar");
            return;
        }
        
        boolean exito;
        if (editando) {
            exito = model.actualizarRegistro(id, nivelGlucosa, fechaHora);
            if (exito) {
                addMessage(FacesMessage.SEVERITY_INFO, "Registro actualizado", "El registro ha sido actualizado correctamente");
                editando = false;
            }
        } else {
            exito = model.guardarRegistro(login.getIdUsuario(), nivelGlucosa, fechaHora);
            if (exito) {
                addMessage(FacesMessage.SEVERITY_INFO, "Registro guardado", "El registro ha sido guardado correctamente");
            }
        }
        
        if (exito) {
            cargarRegistros();
            limpiarFormulario();
        }
    }
    
    public void editar(RegistroGlucosaDTO registro) {
        this.id = registro.getId();
        this.nivelGlucosa = registro.getNivelGlucosa();
        this.fechaHora = registro.getFechaHora();
        this.editando = true;
    }
    
    public void eliminar(RegistroGlucosaDTO registro) {
        if (model.eliminarRegistro(registro.getId())) {
            registros.remove(registro);
            addMessage(FacesMessage.SEVERITY_INFO, "Registro eliminado", "El registro ha sido eliminado correctamente");
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
    
    // Obtener datos para el gráfico en formato JSON
    public String getRegistrosJSONString() {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < registros.size(); i++) {
            RegistroGlucosaDTO r = registros.get(i);
            json.append("{\"nivelGlucosa\":").append(r.getNivelGlucosa())
                .append(",\"fechaHora\":\"").append(r.getFechaHora().getTime()).append("\"}");
            if (i < registros.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
    
    // Clase interna para los registros
    public static class RegistroGlucosaDTO implements Serializable {
        private Integer id;
        private Double nivelGlucosa;
        private Date fechaHora;
        
        public RegistroGlucosaDTO(Integer id, Double nivelGlucosa, Date fechaHora) {
            this.id = id;
            this.nivelGlucosa = nivelGlucosa;
            this.fechaHora = fechaHora;
        }
        
        // Getters
        public Integer getId() { return id; }
        public Double getNivelGlucosa() { return nivelGlucosa; }
        public Date getFechaHora() { return fechaHora; }
    }
}
