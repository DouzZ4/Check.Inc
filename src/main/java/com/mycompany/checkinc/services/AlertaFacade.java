package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Alerta;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AlertaFacade implements AlertaFacadeLocal {

    @PersistenceContext(unitName = "com.mycompany_CheckInc_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    public void create(Alerta alerta) {
        em.persist(alerta);
    }

    @Override
    public java.util.List<Alerta> findAll() {
        return em.createNamedQuery("Alerta.findAll", Alerta.class).getResultList();
    }
}
