/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sena.check_harold.services;

import com.sena.check_harold.entities.Recordatorio;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author davidalonso
 */
@Stateless
public class RecordatorioFacade extends AbstractFacade<Recordatorio> implements RecordatorioFacadeLocal {

    @PersistenceContext(unitName = "com.sena_check_harold_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RecordatorioFacade() {
        super(Recordatorio.class);
    }
    
}
