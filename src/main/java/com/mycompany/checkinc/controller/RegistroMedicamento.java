package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Medicamento;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.MedicamentoFacadeLocal;
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

@ManagedBean(name = "registroMedicamento")
@ViewScoped
public class RegistroMedicamento implements Serializable {
    private static final String USUARIO_SESSION_KEY = "usuario";
    private static final String ERROR = "Error";

    @EJB
    private MedicamentoFacadeLocal medicamentoFacade;
    
    @EJB
    private UsuarioFacadeLocal usuarioFacade;
    
    private Integer id;
    private String nombre;
    private String dosis;
    private String frecuencia;
    private Date fechaInicio;
    private Date fechaFin;
    private List<Medicamento> registros;
    private boolean editando;
    
    public RegistroMedicamento() {
        this.fechaInicio = new Date();
        this.fechaFin = new Date();
    }
    
    @PostConstruct
    public void init() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USUARIO_SESSION_KEY);
        if (usuario != null) {
            cargarRegistros(usuario);
        }
    }
    
    private void cargarRegistros(Usuario usuario) {
        this.registros = medicamentoFacade.findByUsuario(usuario);
    }
    
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }
    
    public String getFrecuencia() { return frecuencia; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }
    
    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }
    
    public List<Medicamento> getRegistros() { return registros; }
    
    public boolean isEditando() { return editando; }
    public void setEditando(boolean editando) { this.editando = editando; }
    
    // Métodos de acción
    public void registrar() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USUARIO_SESSION_KEY);
        if (usuario == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "Debe iniciar sesión para registrar");
            return;
        }
        try {
            if (editando) {
                Medicamento medicamento = medicamentoFacade.find(id);
                if (medicamento == null) {
                    addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "Registro no encontrado");
                    return;
                }
                medicamento.setNombre(nombre);
                medicamento.setDosis(dosis);
                medicamento.setFrecuencia(frecuencia);
                medicamento.setFechaInicio(fechaInicio);
                medicamento.setFechaFin(fechaFin);
                medicamentoFacade.edit(medicamento);
                addMessage(FacesMessage.SEVERITY_INFO, "Registro actualizado", "El medicamento ha sido actualizado correctamente");
                editando = false;
            } else {
                Medicamento medicamento = new Medicamento();
                medicamento.setNombre(nombre);
                medicamento.setDosis(dosis);
                medicamento.setFrecuencia(frecuencia);
                medicamento.setFechaInicio(fechaInicio);
                medicamento.setFechaFin(fechaFin);
                medicamento.setIdUsuario(usuario);
                medicamentoFacade.create(medicamento);
                addMessage(FacesMessage.SEVERITY_INFO, "Registro guardado", "El medicamento ha sido guardado correctamente");
            }
            cargarRegistros(usuario);
            limpiarFormulario();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "No se pudo procesar el registro: " + e.getMessage());
        }
    }
    
    public void editar(Medicamento medicamento) {
        this.id = medicamento.getIdMedicamento();
        this.nombre = medicamento.getNombre();
        this.dosis = medicamento.getDosis();
        this.frecuencia = medicamento.getFrecuencia();
        this.fechaInicio = medicamento.getFechaInicio();
        this.fechaFin = medicamento.getFechaFin();
        this.editando = true;
    }
    
    public void eliminar(Medicamento medicamento) {
        try {
            medicamentoFacade.remove(medicamento);
            Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USUARIO_SESSION_KEY);
            if (usuario != null) {
                cargarRegistros(usuario);
            }
            addMessage(FacesMessage.SEVERITY_INFO, "Registro eliminado", "El medicamento ha sido eliminado correctamente");
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
        this.nombre = null;
        this.dosis = null;
        this.frecuencia = null;
        this.fechaInicio = new Date();
        this.fechaFin = new Date();
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
}
