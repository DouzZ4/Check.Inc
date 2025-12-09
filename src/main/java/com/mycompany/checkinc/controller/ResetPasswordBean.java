package com.mycompany.checkinc.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.ResetService;

@Named
@RequestScoped
public class ResetPasswordBean {

    private String token;
    private Usuario usuario;
    private String nuevaPassword;

    public boolean isUsuarioValido() { return usuario != null; }
    public String getNuevaPassword() { return nuevaPassword; }
    public void setNuevaPassword(String nuevaPassword) { this.nuevaPassword = nuevaPassword; }

    @PostConstruct
    public void init() {
        token = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("token");

        usuario = new ResetService().validarToken(token);
    }

    public void cambiarPassword() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        if (usuario == null) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Token inválido", null));
            return;
        }

        new ResetService().actualizarPassword(usuario, nuevaPassword);

        ctx.addMessage(null, new FacesMessage("Contraseña actualizada correctamente"));
    }
    
    
}
