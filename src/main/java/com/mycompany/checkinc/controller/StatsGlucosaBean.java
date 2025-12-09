package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Glucosa;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.GlucosaFacadeLocal;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.category.CartesianCategoryAxes;

@ManagedBean(name = "statsGlucosaBean")
@ViewScoped
public class StatsGlucosaBean implements Serializable {

    @EJB
    private GlucosaFacadeLocal glucosaFacade;

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    private LineChartModel lineChartModel;
    private List<Glucosa> glucosaRegistros;
    private List<Usuario> pacientes;
    private Integer pacienteSeleccionado;
    private Date fechaInicio;
    private Date fechaFin;
    private String rangoPeriodo = "todos"; // todos, semana, mes
    private GlucosaStats estadisticas;

    // Constants for glucose levels
    private static final double GLUCOSE_LOW_THRESHOLD = 70.0;
    private static final double GLUCOSE_HIGH_THRESHOLD = 180.0;

    public static class GlucosaStats {
        private double promedio;
        private float minimo;
        private float maximo;
        private int registrosAltos;
        private int registrosNormales;
        private int registrosBajos;
        private String conclusion;

        public GlucosaStats(double prom, float min, float max, int altos, int norm, int bajos) {
            this.promedio = Math.round(prom * 100.0) / 100.0;
            this.minimo = min;
            this.maximo = max;
            this.registrosAltos = altos;
            this.registrosNormales = norm;
            this.registrosBajos = bajos;
            generarConclusion();
        }

        private void generarConclusion() {
            if (registrosAltos > registrosNormales && registrosAltos > registrosBajos) {
                conclusion = "⚠️ GLUCOSA ELEVADA: El paciente presenta niveles altos durante este período. Recomendación: ajustar dieta o medicación.";
            } else if (registrosBajos > registrosNormales && registrosBajos > registrosAltos) {
                conclusion = "⚠️ GLUCOSA BAJA: El paciente presenta hipoglucemia. Recomendación: verificar ingesta de alimentos/insulina.";
            } else {
                conclusion = "✓ GLUCOSA CONTROLADA: Niveles dentro de los rangos normales. Continuar con el control actual.";
            }
        }

        public double getPromedio() {
            return promedio;
        }

        public float getMinimo() {
            return minimo;
        }

        public float getMaximo() {
            return maximo;
        }

        public int getRegistrosAltos() {
            return registrosAltos;
        }

        public int getRegistrosNormales() {
            return registrosNormales;
        }

        public int getRegistrosBajos() {
            return registrosBajos;
        }

        public String getConclusion() {
            return conclusion;
        }
    }

    @PostConstruct
    public void init() {
        try {
            pacientes = usuarioFacade.findAll();
            pacienteSeleccionado = 0;
            rangoPeriodo = "todos";
            cambiarPeriodo();
        } catch (Exception e) {
            pacientes = new ArrayList<>();
        }
        loadData();
    }

    public void loadData() {
        try {
            // Primero, actualizar el período de fechas
            cambiarPeriodo();

            List<Glucosa> allGlucosa = glucosaFacade.findAll();
            glucosaRegistros = new ArrayList<>();

            // Filtrar por paciente si está seleccionado
            if (pacienteSeleccionado != null && pacienteSeleccionado > 0) {
                for (Glucosa g : allGlucosa) {
                    if (g.getIdUsuario() != null && g.getIdUsuario().getIdUsuario().equals(pacienteSeleccionado)) {
                        glucosaRegistros.add(g);
                    }
                }
            } else {
                glucosaRegistros = allGlucosa;
            }

            // Filtrar por rango de fechas (inclusivo en ambos extremos)
            if (fechaInicio != null && fechaFin != null) {
                List<Glucosa> filtroPorFecha = new ArrayList<>();
                for (Glucosa g : glucosaRegistros) {
                    Date fechaG = g.getFechaHora();
                    if ((fechaG.equals(fechaInicio) || fechaG.after(fechaInicio)) &&
                            (fechaG.equals(fechaFin) || fechaG.before(fechaFin))) {
                        filtroPorFecha.add(g);
                    }
                }
                glucosaRegistros = filtroPorFecha;
            }

            // Ordenar por fecha ascendente para el gráfico
            glucosaRegistros.sort((g1, g2) -> g1.getFechaHora().compareTo(g2.getFechaHora()));

            // Generar datos para el gráfico
            crearModeloGrafico();

            // Calcular estadísticas
            calcularEstadisticas();
        } catch (Exception ex) {
            ex.printStackTrace();
            glucosaRegistros = new ArrayList<>();
            lineChartModel = new LineChartModel();
            estadisticas = new GlucosaStats(0, 0, 0, 0, 0, 0);
        }
    }

    private void crearModeloGrafico() {
        lineChartModel = new LineChartModel();
        ChartData data = new ChartData();

        LineChartDataSet dataSet = new LineChartDataSet();
        LineChartDataSet lowThresholdSet = new LineChartDataSet();
        LineChartDataSet highThresholdSet = new LineChartDataSet();

        List<Object> values = new ArrayList<>();
        List<Object> lowThresholdValues = new ArrayList<>();
        List<Object> highThresholdValues = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");

        for (Glucosa g : glucosaRegistros) {
            values.add(g.getNivelGlucosa());
            lowThresholdValues.add(GLUCOSE_LOW_THRESHOLD);
            highThresholdValues.add(GLUCOSE_HIGH_THRESHOLD);
            labels.add(sdf.format(g.getFechaHora()));

            // Colorear puntos individualmente
            String pointColor;
            float nivel = g.getNivelGlucosa();
            if (nivel < GLUCOSE_LOW_THRESHOLD) {
                pointColor = "#e53e3e"; // Rojo
            } else if (nivel > GLUCOSE_HIGH_THRESHOLD) {
                pointColor = "#dd6b20"; // Naranja
            } else {
                pointColor = "#38a169"; // Verde
            }
            dataSet.setPointBackgroundColor(pointColor);
        }

        dataSet.setData(values);
        dataSet.setLabel("Nivel Glucosa");
        dataSet.setFill(true);
        dataSet.setBorderColor("#3058a6");
        dataSet.setBackgroundColor("rgba(48,88,166,0.1)");
        dataSet.setPointRadius(5);
        dataSet.setTension(0.4);

        lowThresholdSet.setData(lowThresholdValues);
        lowThresholdSet.setLabel("Min (70)");
        lowThresholdSet.setBorderColor("#e53e3e");
        lowThresholdSet.setBorderDash(Arrays.asList(5, 5));
        lowThresholdSet.setPointRadius(0);
        lowThresholdSet.setFill(false);

        highThresholdSet.setData(highThresholdValues);
        highThresholdSet.setLabel("Max (180)");
        highThresholdSet.setBorderColor("#dd6b20");
        highThresholdSet.setBorderDash(Arrays.asList(5, 5));
        highThresholdSet.setPointRadius(0);
        highThresholdSet.setFill(false);

        data.setLabels(labels);
        data.addChartDataSet(dataSet);
        data.addChartDataSet(lowThresholdSet);
        data.addChartDataSet(highThresholdSet);

        lineChartModel.setData(data);

        // Opciones
        LineChartOptions options = new LineChartOptions();
        CartesianScales scales = new CartesianScales();

        CartesianLinearAxes yAxes = new CartesianLinearAxes();
        yAxes.setBeginAtZero(true);
        scales.addYAxesData(yAxes);

        CartesianCategoryAxes xAxes = new CartesianCategoryAxes();
        xAxes.setOffset(true);
        scales.addXAxesData(xAxes);

        options.setScales(scales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Tendencia de Niveles de Glucosa");
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        options.setLegend(legend);

        options.setResponsive(true);
        options.setMaintainAspectRatio(false);

        lineChartModel.setOptions(options);
    }

    private void calcularEstadisticas() {
        if (glucosaRegistros.isEmpty()) {
            estadisticas = new GlucosaStats(0, 0, 0, 0, 0, 0);
            return;
        }

        double suma = 0;
        float minValue = Float.MAX_VALUE;
        float maxValue = Float.MIN_VALUE;
        int altos = 0, normales = 0, bajos = 0;

        for (Glucosa g : glucosaRegistros) {
            float nivel = g.getNivelGlucosa();
            suma += nivel;
            minValue = Math.min(minValue, nivel);
            maxValue = Math.max(maxValue, nivel);

            if (nivel < GLUCOSE_LOW_THRESHOLD) {
                bajos++;
            } else if (nivel > GLUCOSE_HIGH_THRESHOLD) {
                altos++;
            } else {
                normales++;
            }
        }

        double promedio = suma / glucosaRegistros.size();
        estadisticas = new GlucosaStats(promedio, minValue, maxValue, altos, normales, bajos);
    }

    public void cambiarPeriodo() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        fechaFin = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        switch (rangoPeriodo) {
            case "semana":
                cal.add(Calendar.DAY_OF_YEAR, -7);
                break;
            case "mes":
                cal.add(Calendar.MONTH, -1);
                break;
            case "todos":
            default:
                cal.set(Calendar.YEAR, 1900);
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                break;
        }
        fechaInicio = cal.getTime();
    }

    public void onPeriodoChange() {
        loadData();
    }

    public void onPacienteChange() {
        loadData();
    }

    // Getters y Setters
    public LineChartModel getLineChartModel() {
        return lineChartModel;
    }

    public List<Glucosa> getGlucosaRegistros() {
        return glucosaRegistros;
    }

    public List<Usuario> getPacientes() {
        return pacientes;
    }

    public Integer getPacienteSeleccionado() {
        return pacienteSeleccionado;
    }

    public void setPacienteSeleccionado(Integer pacienteSeleccionado) {
        this.pacienteSeleccionado = pacienteSeleccionado;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getRangoPeriodo() {
        return rangoPeriodo;
    }

    public void setRangoPeriodo(String rangoPeriodo) {
        this.rangoPeriodo = rangoPeriodo;
    }

    public GlucosaStats getEstadisticas() {
        return estadisticas;
    }
}
