/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Glucosa;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author davidalonso
 */
@Stateless
public class GlucosaFacade extends AbstractFacade<Glucosa> implements GlucosaFacadeLocal {

    @PersistenceContext(unitName = "com.mycompany_CheckInc_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GlucosaFacade() {
        super(Glucosa.class);
    }
    
    @Override
    public List<Glucosa> findByUsuario(com.mycompany.checkinc.entities.Usuario usuario) {
        return em.createQuery("SELECT g FROM Glucosa g WHERE g.idUsuario = :usuario ORDER BY g.fechaHora", Glucosa.class)
                .setParameter("usuario", usuario)
                .getResultList();
    }
}
