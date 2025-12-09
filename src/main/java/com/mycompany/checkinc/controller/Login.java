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
public class Login implements Serializable {

    private String username;
    private String password;
    private boolean authenticated;
    private Usuario usuarioActual;

    @EJB
    private UsuarioFacadeLocal ufl;

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

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public String iniciarSesion() {
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
                this.usuarioActual = user;
                this.authenticated = true;

                context.getExternalContext().getFlash().setKeepMessages(true);
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "¡Bienvenido " + user.getNombres() + "!", null));

                // Redirección según rol
                int rol = user.getIdRol().getIdRol();
                if (rol == 1) {
                    return "/views/inicioGestion.xhtml?faces-redirect=true"; // Admin
                } else if (rol == 2) {
                    return "/views/paciente/dashboard.xhtml?faces-redirect=true"; // Paciente
                } else {
                    context.addMessage(null, new FacesMessage(
                            FacesMessage.SEVERITY_WARN, "Rol no reconocido", null));
                    return null;
                }
            } else {
                this.username = null;
                this.password = null;
                context.addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Usuario o contraseña incorrectos", null));
                return null;
            }
        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Error inesperado al iniciar sesión: " + ex.getMessage(), null));
            return null;
        }
    }

    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        this.username = null;
        this.password = null;
        this.authenticated = false;
        this.usuarioActual = null;
        return "/index.xhtml?faces-redirect=true";
    }

    public String cerrarSesion() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index.xhtml?faces-redirect=true";
    }
}
