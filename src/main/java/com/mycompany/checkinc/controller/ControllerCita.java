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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean(name = "controllerCita")
@ViewScoped
public class ControllerCita implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ControllerCita.class.getName());
    private static final long serialVersionUID = 1L;

    Cita cita = new Cita();
    Usuario user = new Usuario();
    List<SelectItem> listaUsuario;
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

    public String crearCitaP1() {
        LOGGER.info("Ejecutando crearCitaP1()");
        try {
            cita = new Cita();
            String path = "views/registros/Citas/crearCitas.xhtml?faces-redirect=true";
            LOGGER.log(Level.INFO, "Redirigiendo a: {0}", path);
            return path;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en crearCitaP1", e);
            return null; // JSF mostrará un error claro
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

    public List<SelectItem> listarUsuario() {
        listaUsuario = new ArrayList<>();
        try {
            for (Usuario user : this.ufl.findAll()) {
                SelectItem item = new SelectItem(user.getIdUsuario(),user.getUser());
                listaUsuario.add(item);
            }
            return listaUsuario;
        } catch (Exception e){
             return null;
        }
    }

}
