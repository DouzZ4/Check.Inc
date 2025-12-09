package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Glucosa;
import com.mycompany.checkinc.entities.Cita;
import com.mycompany.checkinc.services.GlucosaFacadeLocal;
import com.mycompany.checkinc.services.CitaFacadeLocal;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "adminRegistrosBean")
@ViewScoped
public class AdminRegistrosBean implements Serializable {

    @EJB
    private GlucosaFacadeLocal glucosaFacade;

    @EJB
    private CitaFacadeLocal citaFacade;

    private List<Glucosa> listaGlucosaGlobal;
    private List<Cita> listaCitasGlobal;

    @PostConstruct
    public void init() {
        // Cargar todos los registros (podría optimizarse con paginación lazy si fueran
        // muchos)
        listaGlucosaGlobal = glucosaFacade.findAll();
        listaCitasGlobal = citaFacade.findAll();
    }

    public List<Glucosa> getListaGlucosaGlobal() {
        return listaGlucosaGlobal;
    }

    public List<Cita> getListaCitasGlobal() {
        return listaCitasGlobal;
    }
}
