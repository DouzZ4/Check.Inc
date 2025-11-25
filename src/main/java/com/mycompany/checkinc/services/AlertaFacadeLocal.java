package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Alerta;
import javax.ejb.Local;

@Local
public interface AlertaFacadeLocal {
    void create(Alerta alerta);
}
