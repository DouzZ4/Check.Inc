package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Cita;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.CitaFacadeLocal;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

@ManagedBean(name = "controllerCita")
@ViewScoped
public class ControllerCita implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ControllerCita.class.getName());
    private static final long serialVersionUID = 1L;

    Cita cita = new Cita();
    Usuario user = new Usuario();
    @EJB
    CitaFacadeLocal cfl;
    @EJB
    UsuarioFacadeLocal ufl;

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public Integer obtenerIdUsuarioSesion() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion != null) {
            this.user = usuarioSesion; // Asigna el usuario de sesión a la variable user
            return user.getIdUsuario();
        }
        return null;
    }

    public String crearCitaP1() {
        LOGGER.info("Ejecutando crearCitaP1()");
        try {
            cita = new Cita();
            String path = "/views/registros/Citas/crearCitas.xhtml?faces-redirect=true";
            LOGGER.log(Level.INFO, "Redirigiendo a: {0}", path);
            return path;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en crearCitaP1", e);
            return null; // JSF mostrará un error claro
        }
    }

    public void crearCitaP2() {
        cita.setIdUsuario(user);
        try {
            this.cfl.create(cita);
            FacesContext fc = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cita Registrada Correctamente", "MSG_INFO");
            fc.addMessage(null, fm);
        } catch (Exception e) {

        }
    }

    public List<Cita> obtenerCitas() {
        try {
            List<Cita> citas = cfl.findAll();
            LOGGER.log(Level.INFO, "Número de citas encontradas: {0}", citas.size());
            return citas;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener citas", e);
            return Collections.emptyList(); // Nunca retornar null
        }
    }

    public List<Cita> obtenerCitasPorUsuario(Usuario usuario) {
        try {
            return cfl.findByUsuario(usuario);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener citas por usuario", e);
            return Collections.emptyList();
        }
    }

    public List<Cita> obtenerCitasUsuarioSesion() {
        LOGGER.log(Level.INFO, "Invocando obtenerCitasUsuarioSesion() (ControllerCita)");
        try {
            Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
            if (usuario != null) {
                LOGGER.log(Level.INFO, "Usuario en sesión: {0}", usuario.getUser());
                List<Cita> citas = cfl.findByUsuario(usuario);
                LOGGER.log(Level.INFO, "Citas encontradas para usuario {0}: {1}", new Object[]{usuario.getUser(), citas.size()});
                return citas;
            } else {
                LOGGER.log(Level.WARNING, "No hay usuario en sesión");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener citas del usuario en sesión", e);
            return Collections.emptyList();
        }
    }

    public ControllerCita() {
        // Constructor vacío requerido por JSF
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

}
