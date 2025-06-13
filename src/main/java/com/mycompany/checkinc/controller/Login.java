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
import javax.servlet.http.HttpSession;

@ManagedBean(name = "login")
@SessionScoped
public class Login implements Serializable {
    private String username;
    private String password;
    private boolean loggedIn;
    private Integer idUsuario; 

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

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String iniciarSesion() {
        // TODO: Reemplazar con consulta real a la base de datos
        if (username.equals("admin") && password.equals("1234")) {
            loggedIn = true;
            idUsuario = 1; // TODO: Obtener el ID real del usuario desde la base de datos
            
            HttpSession sesion = (HttpSession) FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(true);
            sesion.setAttribute("username", username);
            sesion.setAttribute("idUsuario", idUsuario);
            
            return "/views/registros/registroGlucosa?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Error de autenticación", 
                    "Usuario o contraseña incorrectos"));
            return null;
        }
    }

    public String cerrarSesion() {
        loggedIn = false;
        username = null;
        password = null;
        idUsuario = null;
        
        HttpSession sesion = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        
        return "/index?faces-redirect=true";
    }
}
