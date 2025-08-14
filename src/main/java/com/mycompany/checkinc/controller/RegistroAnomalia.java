/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Anomalia;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.AnomaliaFacadeLocal;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

/**
 *
 * @author davidalonso
 */
@ManagedBean(name = "registroAnomalia")
@ViewScoped
public class RegistroAnomalia implements Serializable {

    private static final String USUARIO_SESSION_KEY = "usuario";
    private static final String ERROR = "Error";
    
    @EJB
    private AnomaliaFacadeLocal anomaliaFacade;
    
    @EJB
    private UsuarioFacadeLocal usuarioFacade;
    
    private Integer idAnomalia;
    private String descripcion;
    private Date fechaHora;
    private String sintomas;
    private String gravedad;
    private Boolean resuelto;
    private List<Anomalia> registros;
    private boolean editando;
    
    //-- Filtrar por resuelto--//
    private String filtroResuelto;
    public String getFiltroResuelto() { return filtroResuelto; }
    public void setFiltroResuelto(String filtroResuelto) { this.filtroResuelto = filtroResuelto; }
    
  
    public RegistroAnomalia() {
        this.fechaHora = new Date();
    }
    
    @PostConstruct
    public void init() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USUARIO_SESSION_KEY);
        if (usuario != null) {
            cargarRegistros(usuario);
        }
    }

    private void cargarRegistros(Usuario usuario) {
        this.registros = anomaliaFacade.findByUsuario(usuario);
    }

    public Integer getIdAnomalia() {
        return idAnomalia;
    }

    public void setIdAnomalia(Integer idAnomalia) {
        this.idAnomalia = idAnomalia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getGravedad() {
        return gravedad;
    }

    public void setGravedad(String gravedad) {
        this.gravedad = gravedad;
    }

    public Boolean getResuelto() {
        return resuelto;
    }

    public void setResuelto(Boolean resuelto) {
        this.resuelto = resuelto;
    }

    public List<Anomalia> getRegistros() {
        return registros;
    }

    public void setRegistros(List<Anomalia> registros) {
        this.registros = registros;
    }

    public boolean isEditando() {
        return editando;
    }

    public void setEditando(boolean editando) {
        this.editando = editando;
    }
    
    public void registrar() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USUARIO_SESSION_KEY);
        if (usuario == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "Debe iniciar sesión para registrar");
            return;
        }
        try {
            if (editando) {
                Anomalia anomalia = anomaliaFacade.find(idAnomalia);
                if (anomalia == null) {
                    addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "Registro no encontrado");
                    return;
                }
                anomalia.setDescripcion(descripcion);
                anomalia.setFechaHora(fechaHora);
                anomalia.setSintomas(sintomas);
                anomalia.setGravedad(gravedad);
                anomalia.setResuelto(resuelto);
                anomaliaFacade.edit(anomalia);
                addMessage(FacesMessage.SEVERITY_INFO, "Registro actualizado", "La Anomalia ha sido actualizada");
                editando = false;
            } else {
                Anomalia anomalia = new Anomalia();
                anomalia.setDescripcion(descripcion);
                anomalia.setFechaHora(fechaHora);
                anomalia.setSintomas(sintomas);
                anomalia.setGravedad(gravedad);
                anomalia.setResuelto(resuelto);
                anomalia.setIdUsuario(usuario);
                anomaliaFacade.create(anomalia);
                addMessage(FacesMessage.SEVERITY_INFO, "Registro guardado", "El medicamento ha sido guardado correctamente");
            }
            cargarRegistros(usuario);
            limpiarFormulario();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "No se pudo procesar el registro: " + e.getMessage());
        }
    }
    
    public void editar(Anomalia anomalia) {
        this.idAnomalia = anomalia.getIdAnomalia();
        this.descripcion = anomalia.getDescripcion();
        this.fechaHora = anomalia.getFechaHora();
        this.sintomas = anomalia.getSintomas();
        this.gravedad = anomalia.getGravedad();
        this.resuelto = anomalia.getResuelto();
        this.editando = true;
    }
    
    public void eliminar(Anomalia anomalia) {
        try {
            anomaliaFacade.remove(anomalia);
            Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USUARIO_SESSION_KEY);
            if (usuario != null) {
                cargarRegistros(usuario);
            }
            addMessage(FacesMessage.SEVERITY_INFO, "Registro eliminado", "La Anomalia ha sido eliminado correctamente");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "No se pudo eliminar el registro: " + e.getMessage());
        }
    }
    
    public void cancelarEdicion() {
        limpiarFormulario();
        editando = false;
    }
    
    private void limpiarFormulario() {
        this.idAnomalia = null;
        this.descripcion = null;
        this.fechaHora = new Date();
        this.sintomas = null;
        this.gravedad = null;
        this.resuelto = null;
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }

}
