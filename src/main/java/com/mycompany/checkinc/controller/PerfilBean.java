/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mycompany.checkinc.controller;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name = "PerfilBean")
@ViewScoped
public class PerfilBean implements Serializable {

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    private Usuario usuario;

@PostConstruct
public void init() {
    FacesContext context = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) context.getExternalContext().getSession(false);

    if (session != null) {
        usuario = (Usuario) session.getAttribute("usuario");
    }

    if (usuario == null) {
        context.addMessage(null, new FacesMessage(
            FacesMessage.SEVERITY_WARN, 
            "No hay usuario en sesión. Por favor inicia sesión nuevamente.", 
            null));
    }
}

 // Getters y setters
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public void actualizarPerfil() {
        try {
            usuarioFacade.edit(usuario);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Perfil actualizado correctamente", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al actualizar el perfil: " + e.getMessage(), null));
        }
    }

}
