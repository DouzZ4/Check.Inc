package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Alerta;
import com.mycompany.checkinc.services.AlertaFacadeLocal;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named(value = "alertaBean")
@RequestScoped
public class AlertaBean implements Serializable {

    @EJB
    private AlertaFacadeLocal alertaFacade;

    private List<Alerta> alertas;

    @PostConstruct
    public void init() {
        alertas = alertaFacade.findAll();
    }

    public void marcarVisto(Alerta a) {
        try {
            if (a != null) {
                a.setVisto(Boolean.TRUE);
                alertaFacade.update(a);
                alertas = alertaFacade.findAll();
            }
        } catch (Exception e) {
            System.err.println("⚠️ [WARN] Error marcando alerta como vista: " + e.getMessage());
        }
    }

    public List<Alerta> getAlertas() {
        return alertas;
    }
}
