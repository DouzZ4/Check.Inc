package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Glucosa;
import com.mycompany.checkinc.entities.Anomalia;
import com.mycompany.checkinc.entities.Cita;
import com.mycompany.checkinc.entities.Medicamento;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.GlucosaFacadeLocal;
import com.mycompany.checkinc.services.AnomaliaFacadeLocal;
import com.mycompany.checkinc.services.CitaFacadeLocal;
import com.mycompany.checkinc.services.MedicamentoFacadeLocal;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import com.mycompany.checkinc.services.ServicioPrediccionML;

import org.primefaces.model.charts.line.LineChartModel;

import java.util.Arrays;

import java.util.Comparator;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name = "patientDashboardBean")
@ViewScoped
public class PatientDashboardBean implements Serializable {

    @EJB
    private GlucosaFacadeLocal glucosaFacade;
    @EJB
    private AnomaliaFacadeLocal anomaliaFacade;
    @EJB
    private CitaFacadeLocal citaFacade;
    @EJB
    private MedicamentoFacadeLocal medicamentoFacade;
    @EJB
    private UsuarioFacadeLocal usuarioFacade;
    @EJB
    private ServicioPrediccionML servicioML;

    private Integer pacienteId;
    private Usuario paciente;
    private List<Glucosa> glucosaReciente;
    private List<Anomalia> anomaliasRecientes;
    private List<Cita> proximasCitas;
    private List<Medicamento> medicamentos;
    private String chartDataJson;
    private org.primefaces.model.charts.line.LineChartModel lineChartModel;

    // 🆕 ML Predictions
    private String nivelRiesgo;
    private Double riesgoScore;
    private List<String> recomendaciones;
    private boolean mlDisponible = false;

    // Constants for glucose levels
    private static final double GLUCOSE_LOW_THRESHOLD = 70.0;
    private static final double GLUCOSE_HIGH_THRESHOLD = 180.0;

    @PostConstruct
    public void init() {
        try {
            // Obtener usuario de la sesión (Login bean)
            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);

            if (session != null) {
                Usuario usuarioSession = (Usuario) session.getAttribute("usuario");
                if (usuarioSession != null) {
                    paciente = usuarioSession;
                    pacienteId = paciente.getIdUsuario();
                }
            }

            // Si no hay usuario en sesión, intentar obtener del Request param (para caso de
            // acceso directo)
            if (paciente == null) {
                String paramId = context.getExternalContext().getRequestParameterMap().get("pacienteId");
                if (paramId != null && !paramId.isEmpty()) {
                    pacienteId = Integer.parseInt(paramId);
                    paciente = usuarioFacade.find(pacienteId);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            paciente = null;
        }
        loadData();
    }

    public void loadData() {
        if (paciente == null)
            return;

        try {
            // Obtiene registros de glucosa para este paciente (todos)
            List<Glucosa> all = glucosaFacade.findAll();
            glucosaReciente = new ArrayList<>();
            for (Glucosa g : all) {
                if (g.getIdUsuario() != null && g.getIdUsuario().getIdUsuario().equals(pacienteId)) {
                    glucosaReciente.add(g);
                }
            }
            // Ordenar descendente por fecha (más reciente primero)
            glucosaReciente.sort((g1, g2) -> g2.getFechaHora().compareTo(g1.getFechaHora()));

            // Anomalias del paciente
            List<Anomalia> allA = anomaliaFacade.findAll();
            anomaliasRecientes = new ArrayList<>();
            for (Anomalia a : allA) {
                if (a.getIdUsuario() != null && a.getIdUsuario().getIdUsuario().equals(pacienteId)) {
                    anomaliasRecientes.add(a);
                }
            }
            // Ordenar descendente por fecha
            anomaliasRecientes.sort((a1, a2) -> a2.getFechaHora().compareTo(a1.getFechaHora()));

            // Citas futuras (simple filtrado por fecha)
            List<Cita> allC = citaFacade.findAll();
            proximasCitas = new ArrayList<>();
            Date now = new Date();
            for (Cita c : allC) {
                if (c.getIdUsuario() != null && c.getIdUsuario().getIdUsuario().equals(pacienteId)) {
                    if (c.getFecha() != null && !c.getFecha().before(now)) {
                        proximasCitas.add(c);
                    }
                }
            }
            // Ordenar ascendente por fecha (próximas primero)
            proximasCitas.sort((c1, c2) -> c1.getFecha().compareTo(c2.getFecha()));

            // Medicamentos activos del paciente (si aplica)
            List<Medicamento> allM = medicamentoFacade.findAll();
            medicamentos = new ArrayList<>();
            for (Medicamento m : allM) {
                if (m.getIdUsuario() != null && m.getIdUsuario().getIdUsuario().equals(pacienteId)) {
                    medicamentos.add(m);
                }
            }

            generarDatosGrafico();
            loadMLPredictions(); // 🆕 Cargar predicciones ML
        } catch (Exception ex) {
            ex.printStackTrace();
            glucosaReciente = new ArrayList<>();
            anomaliasRecientes = new ArrayList<>();
            proximasCitas = new ArrayList<>();
            medicamentos = new ArrayList<>();
            chartDataJson = "";
        }
    }

    /**
     * 🆕 Carga predicciones y recomendaciones desde el servicio ML
     */
    private void loadMLPredictions() {
        if (pacienteId == null)
            return;

        recomendaciones = new ArrayList<>();
        mlDisponible = false;

        try {
            // Verificar disponibilidad del servicio
            if (!servicioML.verificarDisponibilidad()) {
                System.out.println("Servicio ML no disponible");
                return;
            }

            // Cargar evaluación de riesgo
            com.fasterxml.jackson.databind.JsonNode riesgoResponse = servicioML.evaluarRiesgo(pacienteId);

            if (riesgoResponse != null) {
                mlDisponible = true;

                // Extraer nivel de riesgo
                if (riesgoResponse.has("risk_level")) {
                    nivelRiesgo = riesgoResponse.get("risk_level").asText().toUpperCase();
                }

                // Extraer score de riesgo
                if (riesgoResponse.has("risk_score")) {
                    riesgoScore = riesgoResponse.get("risk_score").asDouble();
                }

                // Extraer recomendaciones
                if (riesgoResponse.has("recommendations")) {
                    com.fasterxml.jackson.databind.JsonNode recs = riesgoResponse.get("recommendations");
                    for (com.fasterxml.jackson.databind.JsonNode rec : recs) {
                        recomendaciones.add(rec.asText());
                    }
                }

                System.out.println("✅ ML Predictions cargadas exitosamente");
            }
        } catch (Exception ex) {
            System.err.println("⚠️ Error al cargar predicciones ML: " + ex.getMessage());
            // No bloquear la carga del dashboard si falla ML
            mlDisponible = false;
        }
    }

    /**
     * 🆕 Sincroniza el historial local con IA y fuerza entrenamiento.
     */
    public void sincronizarHistorialIA() {
        if (pacienteId == null || glucosaReciente == null || glucosaReciente.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Sin datos", "No hay historial para sincronizar."));
            return;
        }

        try {
            // 1. Sincronización Masiva
            boolean syncExito = servicioML.sincronizarLecturasMasivas(glucosaReciente);

            if (syncExito) {
                // 2. Entrenar Modelo
                boolean trainExito = servicioML.entrenarModelo(pacienteId);

                if (trainExito) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "IA actualizada con tu historial."));

                    // 3. Recargar predicciones
                    loadMLPredictions();
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Parcial",
                                    "Datos enviados, pero el entrenamiento tardará unos segundos."));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "No se pudo conectar con el servidor de IA."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fallo interno: " + e.getMessage()));
        }
    }

    private void generarDatosGrafico() {
        org.primefaces.model.charts.line.LineChartModel model = new org.primefaces.model.charts.line.LineChartModel();
        org.primefaces.model.charts.ChartData data = new org.primefaces.model.charts.ChartData();

        // Dataset principal para niveles de glucosa
        org.primefaces.model.charts.line.LineChartDataSet dataSet = new org.primefaces.model.charts.line.LineChartDataSet();

        // Dataset para límite bajo
        org.primefaces.model.charts.line.LineChartDataSet lowThresholdSet = new org.primefaces.model.charts.line.LineChartDataSet();

        // Dataset para límite alto
        org.primefaces.model.charts.line.LineChartDataSet highThresholdSet = new org.primefaces.model.charts.line.LineChartDataSet();

        java.util.List<Object> values = new java.util.ArrayList<>();
        java.util.List<Object> lowThresholdValues = new java.util.ArrayList<>();
        java.util.List<Object> highThresholdValues = new java.util.ArrayList<>();
        java.util.List<String> labels = new java.util.ArrayList<>();

        // Usar una copia ordenada ascendentemente para el gráfico
        List<Glucosa> chartDataList = new ArrayList<>(glucosaReciente);
        chartDataList.sort(Comparator.comparing(Glucosa::getFechaHora));

        if (chartDataList != null) {
            java.text.SimpleDateFormat sdfShort = new java.text.SimpleDateFormat("dd/MM HH:mm"); // Eje X más detallado

            for (Glucosa g : chartDataList) {
                double nivelVal = g.getNivelGlucosa();
                values.add(nivelVal);
                lowThresholdValues.add(GLUCOSE_LOW_THRESHOLD);
                highThresholdValues.add(GLUCOSE_HIGH_THRESHOLD);
                labels.add(sdfShort.format(g.getFechaHora()));

                // Colorear los puntos según el nivel
                String pointColor;
                if (nivelVal < GLUCOSE_LOW_THRESHOLD) {
                    pointColor = "#e53e3e"; // Rojo para bajo
                } else if (nivelVal > GLUCOSE_HIGH_THRESHOLD) {
                    pointColor = "#dd6b20"; // Naranja para alto
                } else {
                    pointColor = "#38a169"; // Verde para normal
                }
                dataSet.setPointBackgroundColor(pointColor);
            }
        }

        // Configuración del dataset principal
        dataSet.setData(values);
        dataSet.setLabel("Nivel de Glucosa");
        dataSet.setFill(true);
        dataSet.setBorderColor("#3058a6");
        dataSet.setBackgroundColor("rgba(48,88,166,0.1)");
        dataSet.setPointRadius(5); // Puntos ligeramente más grandes para visibilidad
        dataSet.setPointHoverRadius(7);
        dataSet.setTension(0.3); // Suavizado ligero, similar al otro gráfico
        dataSet.setShowLine(true);

        // Configuración del límite bajo
        lowThresholdSet.setData(lowThresholdValues);
        lowThresholdSet.setLabel("Límite Bajo (70 mg/dL)");
        lowThresholdSet.setBorderColor("#e53e3e");
        lowThresholdSet.setBorderDash(Arrays.asList(5, 5));
        lowThresholdSet.setPointRadius(0);
        lowThresholdSet.setFill(false);

        // Configuración del límite alto
        highThresholdSet.setData(highThresholdValues);
        highThresholdSet.setLabel("Límite Alto (180 mg/dL)");
        highThresholdSet.setBorderColor("#dd6b20");
        highThresholdSet.setBorderDash(Arrays.asList(5, 5));
        highThresholdSet.setPointRadius(0);
        highThresholdSet.setFill(false);

        data.setLabels(labels);
        data.addChartDataSet(dataSet);
        data.addChartDataSet(lowThresholdSet);
        data.addChartDataSet(highThresholdSet);

        model.setData(data);

        // Configuración de opciones del gráfico
        org.primefaces.model.charts.axes.cartesian.CartesianScales scales = new org.primefaces.model.charts.axes.cartesian.CartesianScales();

        // Configuración del eje Y
        org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes yAxes = new org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes();
        yAxes.setOffset(true);
        yAxes.setBeginAtZero(true);

        // Configuración del eje X
        org.primefaces.model.charts.axes.cartesian.category.CartesianCategoryAxes xAxes = new org.primefaces.model.charts.axes.cartesian.category.CartesianCategoryAxes();
        xAxes.setOffset(true);

        scales.addYAxesData(yAxes);
        scales.addXAxesData(xAxes);

        org.primefaces.model.charts.optionconfig.legend.Legend legend = new org.primefaces.model.charts.optionconfig.legend.Legend();
        legend.setDisplay(true);
        legend.setPosition("top");

        org.primefaces.model.charts.optionconfig.title.Title title = new org.primefaces.model.charts.optionconfig.title.Title();
        title.setDisplay(false); // Título ya está en el HTML del dashboard

        org.primefaces.model.charts.line.LineChartOptions options = new org.primefaces.model.charts.line.LineChartOptions();
        options.setScales(scales);
        options.setLegend(legend);
        options.setTitle(title);
        options.setResponsive(true);

        model.setOptions(options);
        this.lineChartModel = model;
    }

    // Método para calcular promedio general
    public double getPromedioGlucosa() {
        if (glucosaReciente == null || glucosaReciente.isEmpty())
            return 0.0;
        double sum = 0;
        for (Glucosa g : glucosaReciente) {
            sum += g.getNivelGlucosa();
        }
        return Math.round((sum / glucosaReciente.size()) * 100.0) / 100.0;
    }

    // Getters y setters
    public Integer getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Integer pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Usuario getPaciente() {
        return paciente;
    }

    public List<Glucosa> getGlucosaReciente() {
        return glucosaReciente;
    }

    public List<Anomalia> getAnomaliasRecientes() {
        return anomaliasRecientes;
    }

    public List<Cita> getProximasCitas() {
        return proximasCitas;
    }

    public List<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public String getChartDataJson() {
        return chartDataJson;
    }

    public LineChartModel getLineChartModel() {
        return lineChartModel;
    }

    // 🆕 Getters for ML Predictions
    public String getNivelRiesgo() {
        return nivelRiesgo;
    }

    public Double getRiesgoScore() {
        return riesgoScore;
    }

    public List<String> getRecomendaciones() {
        return recomendaciones != null ? recomendaciones : new ArrayList<>();
    }

    public boolean isMlDisponible() {
        return mlDisponible;
    }

    public String getColorRiesgo() {
        if (nivelRiesgo == null)
            return "secondary";

        switch (nivelRiesgo.toLowerCase()) {
            case "bajo":
                return "#38a169"; // Verde
            case "medio":
                return "#dd6b20"; // Naranja
            case "alto":
                return "#e53e3e"; // Rojo
            default:
                return "#666"; // Gris
        }
    }
}
