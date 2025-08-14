/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Anomalia;
import com.mycompany.checkinc.entities.Usuario;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author davidalonso
 */
@Stateless
public class AnomaliaFacade extends AbstractFacade<Anomalia> implements AnomaliaFacadeLocal {

    @PersistenceContext(unitName = "com.mycompany_CheckInc_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AnomaliaFacade() {
        super(Anomalia.class);
    }

    @Override
    public List<Anomalia> findByUsuario(Usuario usuario) {
        return em.createQuery("SELECT a FROM Anomalia a WHERE a.idUsuario = :usuario ORDER BY a.fechaHora DESC", Anomalia.class)
                .setParameter("usuario", usuario)
                .getResultList();
    }
    
}
