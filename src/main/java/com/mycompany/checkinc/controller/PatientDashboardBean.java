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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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

    private Integer pacienteId;
    private Usuario paciente;
    private List<Glucosa> glucosaReciente;
    private List<Anomalia> anomaliasRecientes;
    private List<Cita> proximasCitas;
    private List<Medicamento> medicamentos;
    private String chartDataJson;

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
            
            // Si no hay usuario en sesión, intentar obtener del Request param (para caso de acceso directo)
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
        if (paciente == null) return;

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
        } catch (Exception ex) {
            ex.printStackTrace();
            glucosaReciente = new ArrayList<>();
            anomaliasRecientes = new ArrayList<>();
            proximasCitas = new ArrayList<>();
            medicamentos = new ArrayList<>();
            chartDataJson = "";
        }
    }

    private void generarDatosGrafico() {
        Map<String, List<Float>> byDate = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        
        for (Glucosa g : glucosaReciente) {
            String key = sdf.format(g.getFechaHora());
            byDate.computeIfAbsent(key, k -> new ArrayList<>()).add(g.getNivelGlucosa());
        }

        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        
        // Ordenar por fecha en el mapa
        byDate.entrySet().stream()
            .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
            .forEach(e -> {
                labels.add(e.getKey());
                List<Float> vals = e.getValue();
                double sum = 0;
                for (Float v : vals) sum += v;
                double avg = vals.isEmpty() ? 0 : (sum / vals.size());
                values.add(Math.round(avg * 100.0) / 100.0);
            });

        try {
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("labels", labels);
            chartData.put("values", values);
            ObjectMapper mapper = new ObjectMapper();
            chartDataJson = mapper.writeValueAsString(chartData);
        } catch (Exception ex) {
            ex.printStackTrace();
            chartDataJson = "";
        }
    }

    // Método para calcular promedio general
    public double getPromedioGlucosa() {
        if (glucosaReciente == null || glucosaReciente.isEmpty()) return 0.0;
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
}
