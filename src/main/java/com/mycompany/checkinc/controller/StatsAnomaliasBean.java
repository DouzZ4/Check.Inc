package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Anomalia;
import com.mycompany.checkinc.services.AnomaliaFacadeLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.fasterxml.jackson.databind.ObjectMapper;

@ManagedBean(name = "statsAnomaliasBean")
@ViewScoped
public class StatsAnomaliasBean implements Serializable {

    @EJB
    private AnomaliaFacadeLocal anomaliaFacade;

    private String chartDataJson;
    private List<Anomalia> anomaliasDetalle;

    @PostConstruct
    public void init() {
        loadData();
    }

    public void loadData() {
        try {
            List<Anomalia> regs = anomaliaFacade.findAll();
            anomaliasDetalle = regs;
            
            Map<String, Integer> byGravedad = new HashMap<>();
            for (Anomalia a : regs) {
                String g = a.getGravedad() != null ? a.getGravedad() : "Desconocida";
                byGravedad.put(g, byGravedad.getOrDefault(g, 0) + 1);
            }

            List<String> labels = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            List<String> colors = new ArrayList<>();
            String[] palette = new String[]{"#3058a6","#f45501","#dd6b20","#e53e3e"};
            int i = 0;

            for (Map.Entry<String, Integer> e : byGravedad.entrySet()) {
                labels.add(e.getKey());
                values.add(e.getValue());
                colors.add(palette[i % palette.length]);
                i++;
            }

            // Crear JSON para Chart.js
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("labels", labels);
            chartData.put("values", values);
            chartData.put("colors", colors);
            ObjectMapper mapper = new ObjectMapper();
            chartDataJson = mapper.writeValueAsString(chartData);
        } catch (Exception ex) {
            chartDataJson = "";
            anomaliasDetalle = new ArrayList<>();
        }
    }

    public String getChartDataJson() {
        return chartDataJson;
    }

    public List<Anomalia> getAnomaliasDetalle() {
        return anomaliasDetalle;
    }
}
