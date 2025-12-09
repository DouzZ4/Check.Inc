package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Alerta;
import com.mycompany.checkinc.services.AlertaFacadeLocal;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "alertaBean")
@RequestScoped
public class AlertaBean implements Serializable {

    @EJB
    private AlertaFacadeLocal alertaFacade;

    private List<Alerta> alertas;

    @PostConstruct
    public void init() {
        // Por defecto cargamos todas, pero idealmente deberíamos cargar solo las del
        // usuario en sesión si no es ADMIN
        // Para este ejemplo simple, intentaremos obtener el usuario de la sesión
        javax.faces.context.FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        if (context != null) {
            Object usuarioObj = context.getExternalContext().getSessionMap().get("usuario");
            if (usuarioObj instanceof com.mycompany.checkinc.entities.Usuario) {
                com.mycompany.checkinc.entities.Usuario u = (com.mycompany.checkinc.entities.Usuario) usuarioObj;
                // Asumiendo que agregamos un findByUsuario en AlertaFacade, si no existe,
                // filtramos en memoria
                // Para hacerlo rápido, filtraremos en memoria o usaremos una query nueva si es
                // posible.
                // Dado que no puedo editar AlertaFacade ahora mismo facil, cargo todas y
                // filtro.
                List<Alerta> todas = alertaFacade.findAll();
                alertas = new java.util.ArrayList<>();
                for (Alerta a : todas) {
                    if (a.getIdUsuario() != null && a.getIdUsuario().equals(u)) {
                        alertas.add(a);
                    }
                }
                // Ordenar por fecha descendente
                java.util.Collections.sort(alertas, new java.util.Comparator<Alerta>() {
                    @Override
                    public int compare(Alerta o1, Alerta o2) {
                        return o2.getFechaHora().compareTo(o1.getFechaHora());
                    }
                });
                return;
            }
        }
        alertas = alertaFacade.findAll();
    }

    public void marcarVisto(Alerta a) {
        try {
            if (a != null) {
                a.setVisto(Boolean.TRUE);
                alertaFacade.update(a);
                // Recargar
                init();
            }
        } catch (Exception e) {
            System.err.println("⚠️ [WARN] Error marcando alerta como vista: " + e.getMessage());
        }
    }

    public List<Alerta> getAlertas() {
        return alertas;
    }
}
