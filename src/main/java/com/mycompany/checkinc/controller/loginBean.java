package com.mycompany.checkinc.controller;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@ManagedBean(name = "loginBean")
@SessionScoped
public class loginBean implements Serializable {

    private String username;
    private String password;

    // Constructor
    public loginBean() {
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Método de login simulado
   public String login() {
    if ("admin".equals(username) && "1234".equals(password)) {
        return "/index?faces-redirect=true"; // Asegura que exista en webapp/index.xhtml
    } else {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Credenciales inválidas", null));
        return null;
    }
}


    // Método de logout
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/views/usuarios/login?faces-redirect=true";
    }
}
