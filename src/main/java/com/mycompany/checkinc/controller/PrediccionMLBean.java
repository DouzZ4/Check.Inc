package com.mycompany.checkinc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mycompany.checkinc.entities.Glucosa;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.GlucosaFacadeLocal;
import com.mycompany.checkinc.services.ServicioPrediccionML;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

/**
 * Managed Bean para funcionalidades de predicción con Machine Learning.
 * 
 * @author Check.Inc Team
 */
@ManagedBean(name = "prediccionMLBean")
@ViewScoped
public class PrediccionMLBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ServicioPrediccionML servicioML;

    @EJB
    private GlucosaFacadeLocal glucosaFacade;

    // Datos de predicción
    private List<PrediccionDTO> predicciones;
    private int horasAdelante = 6; // Por defecto 6 horas

    // Datos de evaluación de riesgo
    private String nivelRiesgo;
    private Double riesgoScore;
    private Double promedioGlucosa;
    private Integer eventosHipo;
    private Integer eventosHiper;

    // Recomendaciones
    private List<String> recomendaciones;

    // Estado del servicio
    private boolean servicioDisponible;
    private boolean cargando = false;
    private String mensajeError;

    @PostConstruct
    public void init() {
        predicciones = new ArrayList<>();
        recomendaciones = new ArrayList<>();
        verificarServicio();
    }

    /**
     * Verifica si el servicio ML está disponible.
     */
    public void verificarServicio() {
        servicioDisponible = servicioML.verificarDisponibilidad();
        if (!servicioDisponible) {
            addMessage(FacesMessage.SEVERITY_WARN,
                    "Servicio de predicción no disponible",
                    "El servicio de Machine Learning está temporalmente fuera de línea.");
        }
    }

    /**
     * Carga predicciones de glucosa para el usuario actual.
     */
    public void cargarPredicciones() {
        if (!servicioDisponible) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Servicio no disponible");
            return;
        }

        cargando = true;
        mensajeError = null;
        predicciones.clear();

        try {
            Usuario usuario = obtenerUsuarioActual();
            if (usuario == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no encontrado en sesión");
                return;
            }

            JsonNode response = servicioML.obtenerPredicciones(
                    usuario.getIdUsuario(),
                    horasAdelante);

            if (response != null && response.has("predictions")) {
                JsonNode predictions = response.get("predictions");

                for (JsonNode pred : predictions) {
                    PrediccionDTO dto = new PrediccionDTO();
                    dto.setTimestamp(pred.get("timestamp").asText());
                    dto.setNivelPredicho(pred.get("predicted_level").asDouble());
                    dto.setConfianza(pred.get("confidence_score").asDouble());
                    predicciones.add(dto);
                }

                addMessage(FacesMessage.SEVERITY_INFO, "Éxito",
                        String.format("Se cargaron %d predicciones", predicciones.size()));
            } else {
                mensajeError = "No se pudieron obtener predicciones. Asegúrate de tener suficientes lecturas de glucosa.";
                addMessage(FacesMessage.SEVERITY_WARN, "Advertencia", mensajeError);
            }

        } catch (Exception e) {
            mensajeError = "Error al cargar predicciones: " + e.getMessage();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", mensajeError);
        } finally {
            cargando = false;
        }
    }

    /**
     * Carga evaluación de riesgo para el usuario actual.
     */
    public void cargarEvaluacionRiesgo() {
        if (!servicioDisponible)
            return;

        cargando = true;

        try {
            Usuario usuario = obtenerUsuarioActual();
            if (usuario == null)
                return;

            JsonNode response = servicioML.evaluarRiesgo(usuario.getIdUsuario());

            if (response != null) {
                nivelRiesgo = response.get("risk_level").asText();
                riesgoScore = response.get("risk_score").asDouble();

                if (response.has("avg_glucose_7d")) {
                    promedioGlucosa = response.get("avg_glucose_7d").asDouble();
                }
                if (response.has("hypoglycemia_events")) {
                    eventosHipo = response.get("hypoglycemia_events").asInt();
                }
                if (response.has("hyperglycemia_events")) {
                    eventosHiper = response.get("hyperglycemia_events").asInt();
                }

                // Cargar recomendaciones también
                if (response.has("recommendations")) {
                    recomendaciones.clear();
                    JsonNode recs = response.get("recommendations");
                    for (JsonNode rec : recs) {
                        recomendaciones.add(rec.asText());
                    }
                }
            }

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Error al cargar evaluación de riesgo: " + e.getMessage());
        } finally {
            cargando = false;
        }
    }

    /**
     * Carga recomendaciones personalizadas.
     */
    public void cargarRecomendaciones() {
        if (!servicioDisponible)
            return;

        cargando = true;
        recomendaciones.clear();

        try {
            Usuario usuario = obtenerUsuarioActual();
            if (usuario == null)
                return;

            JsonNode response = servicioML.obtenerRecomendaciones(usuario.getIdUsuario());

            if (response != null && response.has("recommendations")) {
                JsonNode recs = response.get("recommendations");
                for (JsonNode rec : recs) {
                    recomendaciones.add(rec.asText());
                }

                if (recomendaciones.isEmpty()) {
                    recomendaciones.add("No hay suficientes datos para generar recomendaciones personalizadas.");
                }
            }

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Error al cargar recomendaciones: " + e.getMessage());
        } finally {
            cargando = false;
        }
    }

    /**
     * Sincroniza TODO el historial de glucosa del usuario con el servicio ML.
     */
    public void sincronizarHistorial() {
        if (!servicioDisponible) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Servicio ML no disponible");
            return;
        }

        cargando = true;
        try {
            Usuario usuario = obtenerUsuarioActual();
            if (usuario == null)
                return;

            // 1. Obtener todas las lecturas de la base de datos local
            List<Glucosa> historial = glucosaFacade.findByUsuario(usuario);

            if (historial == null || historial.isEmpty()) {
                addMessage(FacesMessage.SEVERITY_WARN, "Sin datos", "No tienes registros de glucosa para sincronizar.");
                return;
            }

            // 2. Enviar a Railway (Batch Sync)
            boolean exito = servicioML.sincronizarLecturasMasivas(historial);

            if (exito) {
                // 3. Entrenar el modelo inmediatamente
                servicioML.entrenarModelo(usuario.getIdUsuario());

                // 4. Recargar datos del dashboard
                cargarPredicciones();
                cargarEvaluacionRiesgo();
                cargarRecomendaciones();

                addMessage(FacesMessage.SEVERITY_INFO, "Sincronización Completa",
                        "Se han enviado " + historial.size() + " registros a la IA y se ha actualizado el análisis.");
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error de Sincronización",
                        "No se pudieron enviar los datos al servidor de IA.");
            }

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error Crítico",
                    "Fallo durante la sincronización: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cargando = false;
        }
    }

    /**
     * Obtiene el usuario de la sesión actual.
     */
    private Usuario obtenerUsuarioActual() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (Usuario) context.getExternalContext().getSessionMap().get("usuario");
    }

    /**
     * Añade un mensaje de FacesMessage.
     */
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    /**
     * Retorna el color del badge según el nivel de riesgo.
     */
    public String getColorRiesgo() {
        if (nivelRiesgo == null)
            return "secondary";

        switch (nivelRiesgo.toLowerCase()) {
            case "bajo":
                return "success";
            case "medio":
                return "warning";
            case "alto":
                return "danger";
            default:
                return "secondary";
        }
    }

    // ========== Getters y Setters ==========

    public List<PrediccionDTO> getPredicciones() {
        return predicciones;
    }

    public int getHorasAdelante() {
        return horasAdelante;
    }

    public void setHorasAdelante(int horasAdelante) {
        this.horasAdelante = horasAdelante;
    }

    public String getNivelRiesgo() {
        return nivelRiesgo;
    }

    public Double getRiesgoScore() {
        return riesgoScore;
    }

    public Double getPromedioGlucosa() {
        return promedioGlucosa;
    }

    public Integer getEventosHipo() {
        return eventosHipo;
    }

    public Integer getEventosHiper() {
        return eventosHiper;
    }

    public List<String> getRecomendaciones() {
        return recomendaciones;
    }

    public boolean isServicioDisponible() {
        return servicioDisponible;
    }

    public boolean isCargando() {
        return cargando;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    /**
     * DTO para almacenar datos de predicción.
     */
    public static class PrediccionDTO implements Serializable {
        private String timestamp;
        private Double nivelPredicho;
        private Double confianza;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public Double getNivelPredicho() {
            return nivelPredicho;
        }

        public void setNivelPredicho(Double nivelPredicho) {
            this.nivelPredicho = nivelPredicho;
        }

        public Double getConfianza() {
            return confianza;
        }

        public void setConfianza(Double confianza) {
            this.confianza = confianza;
        }
    }
}
