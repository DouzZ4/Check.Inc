/* package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Glucosa;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.GlucosaFacadeLocal;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "registroGlucosa")
@ViewScoped
public class RegistroGlucosa implements Serializable {
    
    @EJB
    private GlucosaFacadeLocal glucosaFacade;
    
    @EJB
    private UsuarioFacadeLocal usuarioFacade;
    
    @ManagedProperty("#{login}")
    private Login login;
    
    private Integer id;
    private Double nivelGlucosa;
    private Date fechaHora;
    private List<Glucosa> registros;
    private boolean editando;
    
    public RegistroGlucosa() {
        this.fechaHora = new Date();
    }
    
    @PostConstruct
    public void init() {
        if (login != null && login.isLoggedIn()) {
            cargarRegistros();
        }
    }
    
    private void cargarRegistros() {
        Usuario usuario = usuarioFacade.find(login.getIdUsuario());
        if (usuario != null) {
            this.registros = glucosaFacade.findByUsuario(usuario);
        } else {
            this.registros = new ArrayList<>();
        }
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
    
    public void setLogin(Login login) { this.login = login; }
    
    // Métodos de acción
    public void registrar() {
        if (login.iniciarSesion() == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe iniciar sesión para registrar");
            return;
        }
        
        try {
            Usuario usuario = usuarioFacade.find(login.getIdUsuario());
            if (usuario == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no encontrado");
                return;
            }
            
            Glucosa glucosa;
            if (editando) {
                glucosa = glucosaFacade.find(id);
                if (glucosa == null) {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Registro no encontrado");
                    return;
                }
                glucosa.setNivelGlucosa(nivelGlucosa.floatValue());
                glucosa.setFechaHora(fechaHora);
                glucosaFacade.edit(glucosa);
                addMessage(FacesMessage.SEVERITY_INFO, "Registro actualizado", "El registro ha sido actualizado correctamente");
                editando = false;
            } else {
                glucosa = new Glucosa();
                glucosa.setNivelGlucosa(nivelGlucosa.floatValue());
                glucosa.setFechaHora(fechaHora);
                glucosa.setIdUsuario(usuario);
                glucosaFacade.create(glucosa);
                addMessage(FacesMessage.SEVERITY_INFO, "Registro guardado", "El registro ha sido guardado correctamente");
            }
            
            cargarRegistros();
            limpiarFormulario();
            
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo procesar el registro: " + e.getMessage());
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
            cargarRegistros();
            addMessage(FacesMessage.SEVERITY_INFO, "Registro eliminado", "El registro ha sido eliminado correctamente");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el registro: " + e.getMessage());
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
    
    // Para el gráfico
    public String getRegistrosJSONString() {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < registros.size(); i++) {
            Glucosa r = registros.get(i);
            json.append("{\"nivelGlucosa\":").append(r.getNivelGlucosa())
                .append(",\"fechaHora\":\"").append(r.getFechaHora().getTime()).append("\"}");
            if (i < registros.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
}*/