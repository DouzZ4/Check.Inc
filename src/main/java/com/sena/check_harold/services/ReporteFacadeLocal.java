/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sena.check_harold.services;

import com.sena.check_harold.entities.Reporte;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author davidalonso
 */
@Local
public interface ReporteFacadeLocal {

    void create(Reporte reporte);

    void edit(Reporte reporte);

    void remove(Reporte reporte);

    Reporte find(Object id);

    List<Reporte> findAll();

    List<Reporte> findRange(int[] range);

    int count();
    
}
