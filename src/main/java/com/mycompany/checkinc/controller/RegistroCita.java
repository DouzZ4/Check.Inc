package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Cita;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.CitaFacadeLocal;
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

@ManagedBean(name = "registroCita")
@ViewScoped
public class RegistroCita implements Serializable {
    private static final String USUARIO_SESSION_KEY = "usuario";
    private static final String ERROR = "Error";

    @EJB
    private CitaFacadeLocal citaFacade;
    
    @EJB
    private UsuarioFacadeLocal usuarioFacade;
    
    private Integer id;
    private Date fecha;
    private Date hora;
    private String motivo;
    private List<Cita> registros;
    private boolean editando;
    
    // --- Filtro y método filtrado para motivo ---
    private String filtroMotivo;
    public String getFiltroMotivo() { return filtroMotivo; }
    public void setFiltroMotivo(String filtroMotivo) { this.filtroMotivo = filtroMotivo; }

    public List<Cita> getRegistrosFiltrados() {
        if (registros == null) return java.util.Collections.emptyList();
        java.util.stream.Stream<Cita> stream = registros.stream();
        if (filtroMotivo != null && !filtroMotivo.isEmpty()) {
            stream = stream.filter(c -> c.getMotivo() != null && c.getMotivo().toLowerCase().contains(filtroMotivo.toLowerCase()));
        }
        return stream.collect(java.util.stream.Collectors.toList());
    }
    
    public RegistroCita() {
        this.fecha = new Date();
        this.hora = new Date();
    }
    
    @PostConstruct
    public void init() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USUARIO_SESSION_KEY);
        if (usuario != null) {
            cargarRegistros(usuario);
        }
    }
    
    private void cargarRegistros(Usuario usuario) {
        this.registros = citaFacade.findByUsuario(usuario);
    }
    
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    
    public Date getHora() { return hora; }
    public void setHora(Date hora) { this.hora = hora; }
    
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    
    public List<Cita> getRegistros() { return registros; }
    
    public boolean isEditando() { return editando; }
    public void setEditando(boolean editando) { this.editando = editando; }
    
    // Método para el botón 'Ahora' en la vista
    public void setFechaHoraNow() {
        Date now = new Date();
        this.fecha = now;
        this.hora = now;
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
                Cita cita = citaFacade.find(id);
                if (cita == null) {
                    addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "Registro no encontrado");
                    return;
                }
                cita.setFecha(fecha);
                cita.setHora(hora);
                cita.setMotivo(motivo);
                citaFacade.edit(cita);
                addMessage(FacesMessage.SEVERITY_INFO, "Registro actualizado", "La cita ha sido actualizada correctamente");
                editando = false;
            } else {
                Cita cita = new Cita();
                cita.setFecha(fecha);
                cita.setHora(hora);
                cita.setMotivo(motivo);
                cita.setIdUsuario(usuario);
                citaFacade.create(cita);
                addMessage(FacesMessage.SEVERITY_INFO, "Registro guardado", "La cita ha sido guardada correctamente");
            }
            cargarRegistros(usuario);
            limpiarFormulario();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "No se pudo procesar el registro: " + e.getMessage());
        }
    }
    
    public void editar(Cita cita) {
        this.id = cita.getIdCita();
        this.fecha = cita.getFecha();
        this.hora = cita.getHora();
        this.motivo = cita.getMotivo();
        this.editando = true;
    }
    
    public void eliminar(Cita cita) {
        try {
            citaFacade.remove(cita);
            Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USUARIO_SESSION_KEY);
            if (usuario != null) {
                cargarRegistros(usuario);
            }
            addMessage(FacesMessage.SEVERITY_INFO, "Registro eliminado", "La cita ha sido eliminada correctamente");
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
        this.fecha = new Date();
        this.hora = new Date();
        this.motivo = null;
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
}
