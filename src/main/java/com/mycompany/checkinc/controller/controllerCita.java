package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Cita;
import com.mycompany.checkinc.services.CitaFacadeLocal;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named(value = "controllerCita")
@ViewScoped
public class controllerCita implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(controllerCita.class.getName());
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
    
    public controllerCita() {
    }
}