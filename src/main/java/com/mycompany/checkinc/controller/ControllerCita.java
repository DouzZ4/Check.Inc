package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Cita;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.CitaFacadeLocal;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * Bean de gestión de citas médicas para usuarios autenticados.
 * Permite crear y consultar citas filtradas por el usuario en sesión.
 *
 * @author angel
 */
@ManagedBean(name = "controllerCita")
@ViewScoped
public class ControllerCita implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ControllerCita.class.getName());
    private static final long serialVersionUID = 1L;

    /**
     * Entidad de cita en edición o creación.
     */
    Cita cita = new Cita();
    /**
     * Usuario auxiliar (no se usa directamente, ver getUser()).
     */
    Usuario user = new Usuario();
    /**
     * EJB para operaciones CRUD sobre citas.
     */
    @EJB
    CitaFacadeLocal cfl;
    /**
     * EJB para operaciones sobre usuarios (no usado directamente aquí).
     */
    @EJB
    UsuarioFacadeLocal ufl;

    /**
     * Devuelve la cita actual en edición o creación.
     * @return cita actual
     */
    public Cita getCita() {
        return cita;
    }

    /**
     * Asigna la cita actual en edición o creación.
     * @param cita nueva cita
     */
    public void setCita(Cita cita) {
        this.cita = cita;
    }

    /**
     * Inicializa el proceso de creación de una nueva cita y redirige a la vista de creación.
     * @return ruta de la vista de creación de cita
     */
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

    /**
     * Guarda la cita en la base de datos, asociándola al usuario autenticado.
     * Muestra mensajes de éxito o error en la interfaz.
     */
    public void crearCitaP2() {
        // Asigna el usuario de sesión directamente antes de crear la cita
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion != null) {
            cita.setIdUsuario(usuarioSesion);
            try {
                this.cfl.create(cita);
                FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cita Registrada Correctamente", "MSG_INFO");
                facesContext.addMessage(null, fm);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al registrar cita", e);
                FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al registrar cita", e.getMessage());
                facesContext.addMessage(null, fm);
            }
        } else {
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario no autenticado", "Debes iniciar sesión para registrar una cita");
            facesContext.addMessage(null, fm);
        }
    }

    /**
     * Obtiene la lista de citas del usuario autenticado.
     * @return lista de citas del usuario en sesión, o vacía si no hay usuario
     */
    public List<Cita> obtenerCitas() {
        // Solo retorna las citas del usuario en sesión
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
            Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
            if (usuarioSesion != null) {
                List<Cita> citas = cfl.findByUsuario(usuarioSesion);
                LOGGER.log(Level.INFO, "Número de citas encontradas para usuario {0}: {1}", new Object[]{usuarioSesion.getUser(), citas.size()});
                return citas;
            } else {
                LOGGER.log(Level.WARNING, "No hay usuario en sesión para obtener citas");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener citas", e);
            return Collections.emptyList(); // Nunca retornar null
        }
    }

    /**
     * Constructor requerido por JSF.
     */
    public ControllerCita() {
        // Constructor vacío requerido por JSF
    }

    /**
     * Devuelve el usuario autenticado actual (de la sesión).
     * @return usuario en sesión, o un usuario vacío si no hay sesión
     */
    public Usuario getUser() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        return usuarioSesion != null ? usuarioSesion : new Usuario();
    }

    /**
     * Setter para el usuario (no se usa, pero requerido por JSF).
     * @param user usuario a asignar
     */
    public void setUser(Usuario user) {
        this.user = user;
    }

}
