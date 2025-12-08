/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Cita;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author davidalonso
 */
@Stateless
public class CitaFacade extends AbstractFacade<Cita> implements CitaFacadeLocal {

    @PersistenceContext(unitName = "com.mycompany_CheckInc_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CitaFacade() {
        super(Cita.class);
    }

    @Override
    public List<Cita> findByUsuario(com.mycompany.checkinc.entities.Usuario usuario) {
        return em
                .createQuery("SELECT c FROM Cita c WHERE c.idUsuario = :usuario ORDER BY c.fecha DESC, c.hora DESC",
                        Cita.class)
                .setParameter("usuario", usuario)
                .getResultList();
    }

    @Override
    public List<Cita> findCitasManana() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
        // Reset time to 00:00:00 for strict Date comparison if stored as DATE
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        java.util.Date manana = cal.getTime();

        return em.createQuery("SELECT c FROM Cita c WHERE c.fecha = :manana", Cita.class)
                .setParameter("manana", manana)
                .getResultList();
    }
}
