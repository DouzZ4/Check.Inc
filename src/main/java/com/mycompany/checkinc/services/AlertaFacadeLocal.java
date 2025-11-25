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
    /**
     * Marca como vistas las alertas con fechaHora anterior a la fecha indicada (bulk update).
     * Retorna el número de filas afectadas.
     */
    int markOlderThan(java.util.Date threshold);
}
