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
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean(name = "login")
@SessionScoped
public class Login implements Serializable {
    private static final Logger logger = Logger.getLogger(Login.class.getName());
    
    private String username;
    private String password;
    private Usuario user;
    
    @EJB
    private UsuarioFacadeLocal ufl;
    
    public Login() {
        this.user = new Usuario();
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
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String iniciarSesion() {
        try {
            if (username == null || username.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "El usuario y la contraseña son obligatorios", null));
                return null;
            }

            this.user = this.ufl.iniciarSesion(username.trim(), password);
            
            if (user != null && user.getIdUsuario() != null) {
                HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(true);
                
                session.setAttribute("username", user.getUser());
                session.setAttribute("userId", user.getIdUsuario());
                session.setAttribute("userRole", user.getIdRol().getNombre());
                
                logger.log(Level.INFO, "Inicio de sesión exitoso para el usuario: {0}", username);
                
                return "/views/registros/registroGlucosa.xhtml?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Usuario o contraseña incorrectos", null));
                return null;
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error durante el inicio de sesión", e);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error al iniciar sesión", null));
            return null;
        }
    }
    
    public String logout() {
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
            
            if (session != null) {
                logger.log(Level.INFO, "Cierre de sesión para el usuario: {0}", username);
                session.invalidate();
            }
            
            this.username = null;
            this.password = null;
            this.user = null;
            
            return "/index.xhtml?faces-redirect=true";
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error durante el cierre de sesión", e);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error al cerrar sesión", null));
            return null;
        }
    }
}
