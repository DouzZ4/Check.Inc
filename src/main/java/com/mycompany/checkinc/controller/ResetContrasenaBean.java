package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.services.ResetService;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class ResetContrasenaBean implements Serializable {

    private String correo;
                  
    @EJB
    private ResetService resetService;

    public void enviarCorreo() {
        try {
            resetService.enviarTokenPorCorreo(correo);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Correo enviado",
                            "Revise su bandeja para continuar con el proceso."));

        } catch (Exception e) {

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error",
                            e.getMessage()));
        }
    }

    // GETTERS & SETTERS
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
 