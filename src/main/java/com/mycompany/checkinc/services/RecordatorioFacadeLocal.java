/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Recordatorio;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author davidalonso
 */
@Local
public interface RecordatorioFacadeLocal {

    void create(Recordatorio recordatorio);

    void edit(Recordatorio recordatorio);

    void remove(Recordatorio recordatorio);

    Recordatorio find(Object id);

    List<Recordatorio> findAll();

    List<Recordatorio> findRange(int[] range);

    int count();
    
}
