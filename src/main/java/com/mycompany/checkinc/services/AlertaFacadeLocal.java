package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Alerta;
import java.util.List;
import javax.ejb.Local;

@Local
public interface AlertaFacadeLocal {
    void create(Alerta alerta);
    List<Alerta> findAll();
    Alerta find(Object id);
    Alerta update(Alerta alerta);
}
