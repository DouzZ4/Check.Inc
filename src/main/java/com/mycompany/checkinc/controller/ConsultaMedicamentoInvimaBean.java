package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.services.MedicamentoApiService;
import com.mycompany.checkinc.services.MedicamentoApiService.MedicamentoInvimaDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@ManagedBean(name = "consultaMedicamentoInvima")
@ViewScoped
public class ConsultaMedicamentoInvimaBean implements Serializable {
    private String nombreBusqueda;
    private String numeroRegistroBusqueda;
    private List<MedicamentoInvimaDTO> resultados;

    public void buscar() {
        try {
            if ((nombreBusqueda == null || nombreBusqueda.trim().isEmpty()) && 
                (numeroRegistroBusqueda == null || numeroRegistroBusqueda.trim().isEmpty())) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_WARN, 
                    "Aviso", "Por favor ingrese un nombre o número de registro para buscar."));
                return;
            }
            
            MedicamentoApiService service = new MedicamentoApiService();
            resultados = service.buscarPorNombreONumero(nombreBusqueda, numeroRegistroBusqueda);
            
            if (resultados.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "Información", "No se encontraron resultados para la búsqueda."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "Éxito", "Se encontraron " + resultados.size() + " resultado(s)."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "Ocurrió un error al realizar la búsqueda: " + e.getMessage()));
        }
    }

    public void limpiar() {
        nombreBusqueda = null;
        numeroRegistroBusqueda = null;
        resultados = new ArrayList<>();
    }

    // Getters y setters
    public String getNombreBusqueda() { return nombreBusqueda; }
    public void setNombreBusqueda(String nombreBusqueda) { this.nombreBusqueda = nombreBusqueda; }
    public String getNumeroRegistroBusqueda() { return numeroRegistroBusqueda; }
    public void setNumeroRegistroBusqueda(String numeroRegistroBusqueda) { this.numeroRegistroBusqueda = numeroRegistroBusqueda; }
    public List<MedicamentoInvimaDTO> getResultados() { return resultados; }
    public void setResultados(List<MedicamentoInvimaDTO> resultados) { this.resultados = resultados; }
}
