package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.NivelesGlucosa;
import com.mycompany.checkinc.entities.Usuario;
import javax.ejb.Local;

/**
 * Interfaz local para NivelesGlucosaFacade.
 */
@Local
public interface NivelesGlucosaFacadeLocal {

    void create(NivelesGlucosa nivelesGlucosa);

    void edit(NivelesGlucosa nivelesGlucosa);

    void remove(NivelesGlucosa nivelesGlucosa);

    NivelesGlucosa find(Object id);

    java.util.List<NivelesGlucosa> findAll();

    java.util.List<NivelesGlucosa> findRange(int[] range);

    int count();

    NivelesGlucosa findByUsuario(Usuario usuario);

    NivelesGlucosa findByTipoDiabetes(String tipoDiabetes);

    NivelesGlucosa findDefaultNiveles();

    String determinarEstadoGlucosa(Float nivelGlucosa, Usuario usuario);

    String obtenerRangoTexto(Usuario usuario);

    String obtenerRecomendacion(String estado);

}
