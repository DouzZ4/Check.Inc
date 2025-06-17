/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Glucosa;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author davidalonso
 */
@Local
public interface GlucosaFacadeLocal {

    void create(Glucosa glucosa);

    void edit(Glucosa glucosa);

    void remove(Glucosa glucosa);

    Glucosa find(Object id);

    List<Glucosa> findAll();

    List<Glucosa> findRange(int[] range);

    int count();
    
}
