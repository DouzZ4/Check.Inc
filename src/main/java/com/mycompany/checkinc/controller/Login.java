/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mycompany.checkinc.controller;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "login")
@SessionScoped
public class Login implements Serializable {
    private String username;
    private String password;
    private boolean loggedIn;

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
    }    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String iniciarSesion() {
        if ("admin".equals(username) && "1234".equals(password)) {
            loggedIn = true;
            return "registroGlucosa"; 
        } else {
            FacesContext fc = FacesContext.getCurrentInstance(); 
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de autenticación", "Usuario o contraseña incorrectos");
            fc.addMessage(null, fm);
            return null; 
        }
    }    public String cerrarSesion() {
        loggedIn = false;
        username = null;
        password = null;
        return "/index?faces-redirect=true"; // Redirige al inicio
    }
}
