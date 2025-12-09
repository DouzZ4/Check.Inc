/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Cita;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author davidalonso
 */
@Local
public interface CitaFacadeLocal {

    void create(Cita cita);

    void edit(Cita cita);

    void remove(Cita cita);

    Cita find(Object id);

    List<Cita> findAll();

    List<Cita> findRange(int[] range);

    int count();

    List<Cita> findByUsuario(com.mycompany.checkinc.entities.Usuario usuario);

    List<Cita> findCitasManana();
}
