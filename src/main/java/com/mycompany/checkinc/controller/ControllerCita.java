package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Cita;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.CitaFacadeLocal;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "controllerCita")
@ViewScoped
public class ControllerCita implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ControllerCita.class.getName());
    private static final long serialVersionUID = 1L;
    
    private Cita cita = new Cita();
    
    @EJB
    private CitaFacadeLocal cfl;

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
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
}
