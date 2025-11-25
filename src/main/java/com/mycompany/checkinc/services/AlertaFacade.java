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

    @Override
    public Alerta find(Object id) {
        return em.find(Alerta.class, id);
    }

    @Override
    public Alerta update(Alerta alerta) {
        return em.merge(alerta);
    }

    @Override
    public int markOlderThan(java.util.Date threshold) {
        javax.persistence.Query q = em.createQuery("UPDATE Alerta a SET a.visto = TRUE WHERE a.fechaHora < :thr AND (a.visto = FALSE OR a.visto IS NULL)");
        q.setParameter("thr", threshold);
        return q.executeUpdate();
    }
}
