/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name = "login")
@SessionScoped
public class Login implements Serializable {    private String username;
    private String password;
    private boolean authenticated;
    
    @EJB
    private UsuarioFacadeLocal ufl;
    
    public String irALogin() {
        return "/views/usuarios/login.xhtml?faces-redirect=true";
    }

    public String irARegistro() {
        return "/views/usuarios/registrousuario.xhtml?faces-redirect=true";
    }
    // Getters y setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }    public String iniciarSesion() {
        this.authenticated = false;
        FacesContext context = FacesContext.getCurrentInstance();
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR, 
                "Usuario y contraseña son requeridos", null));
            return null;
        }
        try {
            Usuario user = ufl.iniciarSesion(username.trim(), password);
            if (user != null && user.getIdUsuario() != null) {
                HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
                session.setAttribute("usuario", user);
                this.authenticated = true;
                // Mantener mensajes tras redirección
                context.getExternalContext().getFlash().setKeepMessages(true);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "¡Bienvenido " + user.getNombres()+ "!", null));
                return "/index.xhtml?faces-redirect=true";
            } else {
                this.username = null;
                this.password = null;
                context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, 
                    "Usuario o contraseña incorrectos", null));
                return null;
            }
        } catch (IllegalArgumentException ex) {
            context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR, "Error inesperado al iniciar sesión: " + ex.getMessage(), null));
            return null;
        }
    }
      public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        
        if (session != null) {
            session.invalidate();
        }
        
        // Limpiar datos locales
        this.username = null;
        this.password = null;
        this.authenticated = false;
        
        return "/index.xhtml?faces-redirect=true";
    }
      
    //Cierre de Sesion
    public String cerrarSesion(){
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index.xhtml?faces-redirect=true";
    }
}
