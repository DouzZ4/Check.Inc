/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.sena.check_harold.controller;

import com.sena.check_harold.entities.Cita;
import com.sena.check_harold.services.CitaFacadeLocal;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author davidalonso
 */
@Named(value = "controllerCita")
@ViewScoped
public class controllerCita implements Serializable {

    private static final long serialVersionUID = 1L;

    Cita cita = new Cita();
    @EJB
    CitaFacadeLocal cfl;

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public List<Cita> obtenerCitas(){
        try{
            return this.cfl.findAll();
        } catch (Exception e){
            
        }
        
        return null;
    }
    
    public controllerCita() {
    }
    
}
