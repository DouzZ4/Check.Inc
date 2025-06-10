/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sena.check_harold.services;

import com.sena.check_harold.entities.Anomalia;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author davidalonso
 */
@Local
public interface AnomaliaFacadeLocal {

    void create(Anomalia anomalia);

    void edit(Anomalia anomalia);

    void remove(Anomalia anomalia);

    Anomalia find(Object id);

    List<Anomalia> findAll();

    List<Anomalia> findRange(int[] range);

    int count();
    
}
