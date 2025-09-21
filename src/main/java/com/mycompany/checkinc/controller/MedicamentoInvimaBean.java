package com.mycompany.checkinc.controller;

// MedicamentoBean.java
import com.mycompany.checkinc.services.DatosGovApiService;
import com.mycompany.checkinc.entities.MedicamentoInvima;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped // El estado del bean se conserva mientras estés en la misma página.
public class MedicamentoInvimaBean implements Serializable {

    private String terminoBusqueda;
    private List<MedicamentoInvima> resultados;
    private String mensajeError;

    // Obtenemos la instancia Singleton del servicio
    private final DatosGovApiService apiService = DatosGovApiService.getInstance();

    public MedicamentoInvimaBean() {
        resultados = new ArrayList<>();
    }

    /**
     * Acción llamada desde el botón en la página JSF.
     */
    public void buscar() {
        mensajeError = null; // Limpiar errores previos
        if (terminoBusqueda == null || terminoBusqueda.trim().isEmpty()) {
            mensajeError = "Por favor, ingrese un término de búsqueda.";
            resultados.clear();
            return;
        }

        try {
            // Llama al servicio para obtener los datos
            resultados = apiService.buscarMedicamentos(terminoBusqueda);
            if (resultados.isEmpty()) {
                mensajeError = "No se encontraron resultados para '" + terminoBusqueda + "'.";
            }
        } catch (Exception e) {
            e.printStackTrace(); // Es buena práctica registrar el error completo en el log del servidor
            mensajeError = "Ocurrió un error al consultar el servicio. Intente más tarde.";
            resultados.clear();
        }
    }

    // --- Getters y Setters para que la vista pueda acceder a las propiedades ---
    public String getTerminoBusqueda() {
        return terminoBusqueda;
    }

    public void setTerminoBusqueda(String terminoBusqueda) {
        this.terminoBusqueda = terminoBusqueda;
    }

    public List<MedicamentoInvima> getResultados() {
        return resultados;
    }

    public String getMensajeError() {
        return mensajeError;
    }
}
