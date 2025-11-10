package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.entities.Rol;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import com.mycompany.checkinc.services.RolFacadeLocal;
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

@ManagedBean(name = "statsUsuariosBean")
@ViewScoped
public class StatsUsuariosBean implements Serializable {

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    @EJB
    private RolFacadeLocal rolFacade;

    private int totalUsuarios;
    private List<RoleDetail> roleDetails;
    private String chartDataJson;

    public static class RoleDetail {
        private String nombreRol;
        private int cantidad;

        public RoleDetail(String nombreRol, int cantidad) {
            this.nombreRol = nombreRol;
            this.cantidad = cantidad;
        }

        public String getNombreRol() { return nombreRol; }
        public int getCantidad() { return cantidad; }
    }

    @PostConstruct
    public void init() {
        loadData();
    }

    public void loadData() {
        try {
            List<Usuario> usuarios = usuarioFacade.findAll();
            List<Rol> roles = rolFacade.findAll();

            Map<String, Integer> counts = new HashMap<>();
            for (Rol r : roles) counts.put(r.getNombre(), 0);

            for (Usuario u : usuarios) {
                String rolName = (u.getIdRol() != null) ? u.getIdRol().getNombre() : "Sin rol";
                counts.put(rolName, counts.getOrDefault(rolName, 0) + 1);
            }

            totalUsuarios = usuarios.size();
            roleDetails = new ArrayList<>();
            String[] palette = new String[]{"#3058a6","#f45501","#38a169","#dd6b20","#e53e3e","#6b46c1"};
            
            List<String> labels = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            List<String> colors = new ArrayList<>();
            int i = 0;

            for (Map.Entry<String, Integer> e : counts.entrySet()) {
                roleDetails.add(new RoleDetail(e.getKey(), e.getValue()));
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
        } catch (Exception e) {
            totalUsuarios = 0;
            roleDetails = new ArrayList<>();
            chartDataJson = "";
        }
    }

    public int getTotalUsuarios() {
        return totalUsuarios;
    }

    public List<RoleDetail> getRoleDetails() {
        return roleDetails;
    }

    public String getChartDataJson() {
        return chartDataJson;
    }
}
