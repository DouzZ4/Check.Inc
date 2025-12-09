package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped; // Uso de CDI RequestScoped
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject; // Uso de inyección CDI para EJB/Facade
import javax.inject.Named;

/**
 * Backing Bean para la página de restablecimiento de contraseña (donde se introduce el token).
 * Usa @RequestScoped para asegurar que se crea una instancia fresca en el POST del formulario, 
 * permitiendo que el @PostConstruct se ejecute de nuevo.
 */
@Named("restablecerPasswordBean")
@SessionScoped
public class RestablecerPasswordBean implements Serializable {

    // 1. Inyección de la fachada usando @Inject (recomendado)
    @Inject
    private UsuarioFacadeLocal usuarioFacade;

    private String token;
    private Usuario usuario;

    private String nuevaPassword;
    private String confirmarPassword;

    private boolean tokenValido = false;

    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        
        // El token se leerá tanto en la petición inicial (GET) como en el postback (POST)
        // si la URL base no cambia.
        token = fc.getExternalContext().getRequestParameterMap().get("token");

        if (token != null && !token.isEmpty()) {
            usuario = usuarioFacade.findByToken(token);

            if (usuario != null && usuario.getTokenExpira() != null) {

                // Convertir java.util.Date/Timestamp a LocalDateTime para la comparación
                LocalDateTime expira = new Timestamp(usuario.getTokenExpira().getTime()).toLocalDateTime();

                if (expira.isAfter(LocalDateTime.now())) {
                    tokenValido = true;
                }
            }
        }
    }

    public void actualizarPassword() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        // Si el bean no se inicializó correctamente o el token expiró (en el GET o POST)
        if (!tokenValido) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "El enlace no es válido o ya expiró.", null));
            return;
        }

        // Validación de coincidencia de contraseñas
        if (nuevaPassword == null || confirmarPassword == null ||
            !nuevaPassword.equals(confirmarPassword)) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Las contraseñas no coinciden.", null));
            return;
        }

        // Lógica de actualización de base de datos
        usuarioFacade.actualizarPasswordRecuperacion(usuario, nuevaPassword);

        // Notificación de éxito
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Tu contraseña ha sido actualizada correctamente.", null));
        
        // (Opcional): Si deseas redirigir a login después del éxito:
        // try {
        //     FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    // --- GETTERS & SETTERS ---
    public String getNuevaPassword() { return nuevaPassword; }
    public void setNuevaPassword(String nuevaPassword) { this.nuevaPassword = nuevaPassword; }

    public String getConfirmarPassword() { return confirmarPassword; }
    public void setConfirmarPassword(String confirmarPassword) { this.confirmarPassword = confirmarPassword; }

    public boolean isTokenValido() { return tokenValido; }
    
    // Si necesitas el token para diagnóstico, puedes añadir su getter
    // public String getToken() { return token; }
}