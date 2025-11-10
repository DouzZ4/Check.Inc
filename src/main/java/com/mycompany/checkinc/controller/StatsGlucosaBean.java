package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Glucosa;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.GlucosaFacadeLocal;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.fasterxml.jackson.databind.ObjectMapper;

@ManagedBean(name = "statsGlucosaBean")
@ViewScoped
public class StatsGlucosaBean implements Serializable {

    @EJB
    private GlucosaFacadeLocal glucosaFacade;

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    private String chartDataJson;
    private List<Glucosa> glucosaRegistros;
    private List<Usuario> pacientes;
    private Integer pacienteSeleccionado;
    private Date fechaInicio;
    private Date fechaFin;
    private String rangoPeriodo = "todos"; // todos, semana, mes
    private GlucosaStats estadisticas;

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

        public double getPromedio() { return promedio; }
        public float getMinimo() { return minimo; }
        public float getMaximo() { return maximo; }
        public int getRegistrosAltos() { return registrosAltos; }
        public int getRegistrosNormales() { return registrosNormales; }
        public int getRegistrosBajos() { return registrosBajos; }
        public String getConclusion() { return conclusion; }
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

            // Generar datos para el gráfico
            generarDatosGrafico();
            
            // Calcular estadísticas
            calcularEstadisticas();
        } catch (Exception ex) {
            ex.printStackTrace();
            glucosaRegistros = new ArrayList<>();
            chartDataJson = "";
            estadisticas = new GlucosaStats(0, 0, 0, 0, 0, 0);
        }
    }

    private void generarDatosGrafico() {
        // Agrupar por fecha (dd/MM)
        Map<String, List<Float>> byDate = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        for (Glucosa g : glucosaRegistros) {
            String key = sdf.format(g.getFechaHora());
            byDate.computeIfAbsent(key, k -> new ArrayList<>()).add(g.getNivelGlucosa());
        }

        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Map.Entry<String, List<Float>> e : byDate.entrySet()) {
            labels.add(e.getKey());
            List<Float> vals = e.getValue();
            double sum = 0;
            for (Float v : vals) sum += v;
            double avg = vals.isEmpty() ? 0 : (sum / vals.size());
            values.add(Math.round(avg * 100.0) / 100.0);
        }

        try {
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("labels", labels);
            chartData.put("values", values);
            ObjectMapper mapper = new ObjectMapper();
            chartDataJson = mapper.writeValueAsString(chartData);
        } catch (Exception ex) {
            chartDataJson = "";
        }
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

        // Rangos considerados (pueden ajustarse según protocolos médicos)
        final float LIMITE_BAJO = 70f;
        final float LIMITE_ALTO = 180f;

        for (Glucosa g : glucosaRegistros) {
            float nivel = g.getNivelGlucosa();
            suma += nivel;
            minValue = Math.min(minValue, nivel);
            maxValue = Math.max(maxValue, nivel);

            if (nivel < LIMITE_BAJO) {
                bajos++;
            } else if (nivel > LIMITE_ALTO) {
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

    // Getters y Setters
    public String getChartDataJson() {
        return chartDataJson;
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
