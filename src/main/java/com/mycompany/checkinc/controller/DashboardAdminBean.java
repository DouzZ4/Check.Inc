package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import com.mycompany.checkinc.services.GlucosaFacadeLocal;
import com.mycompany.checkinc.services.CitaFacadeLocal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "dashboardAdminBean")
@RequestScoped
public class DashboardAdminBean {

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    @EJB
    private GlucosaFacadeLocal glucosaFacade;

    @EJB
    private CitaFacadeLocal citaFacade;

    private int totalUsuarios;
    private int totalRegistrosGlucosa;
    private int totalCitas;

    @PostConstruct
    public void init() {
        try {
            totalUsuarios = usuarioFacade.count();
            totalRegistrosGlucosa = glucosaFacade.count();
            totalCitas = citaFacade.count();
        } catch (Exception e) {
            totalUsuarios = 0;
            totalRegistrosGlucosa = 0;
            totalCitas = 0;
            // Podrías loguear este error
        }
    }

    // Getters
    public int getTotalUsuarios() {
        return totalUsuarios;
    }

    public int getTotalRegistrosGlucosa() {
        return totalRegistrosGlucosa;
    }

    public int getTotalCitas() {
        return totalCitas;
    }

private String errorStats;

public String getErrorStats() {
    return errorStats;
}

    
} 

