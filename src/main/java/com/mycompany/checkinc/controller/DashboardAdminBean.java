package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.entities.Rol;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import com.mycompany.checkinc.services.GlucosaFacadeLocal;
import com.mycompany.checkinc.services.CitaFacadeLocal;
import com.mycompany.checkinc.services.RolFacadeLocal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@ManagedBean(name = "dashboardAdminBean")
@ViewScoped
public class DashboardAdminBean implements Serializable {

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    @EJB
    private GlucosaFacadeLocal glucosaFacade;

    @EJB
    private CitaFacadeLocal citaFacade;

    @EJB
    private RolFacadeLocal rolFacade;

    @EJB
    private com.mycompany.checkinc.services.AnomaliaFacadeLocal anomaliaFacade;

    @EJB
    private com.mycompany.checkinc.services.AlertaFacadeLocal alertaFacade;

    private int totalUsuarios;
    private int totalRegistrosGlucosa;
    private int totalCitas;
    private int totalAnomalias;
    private int totalAlertas;
    private int anomaliasSinResolver;
    private String errorStats;

    private List<UsuarioEditable> listaUsuarios;
    private List<Rol> listaRoles;

    @PostConstruct
    public void init() {
        try {
            totalUsuarios = usuarioFacade.count();
            totalRegistrosGlucosa = glucosaFacade.count();
            totalCitas = citaFacade.count();
            // Anomalias (totales y sin resolver)
            try {
                totalAnomalias = anomaliaFacade.count();
                int sinResolver = 0;
                for (com.mycompany.checkinc.entities.Anomalia a : anomaliaFacade.findAll()) {
                    if (a.getResuelto() == null || !a.getResuelto()) {
                        sinResolver++;
                    }
                }
                anomaliasSinResolver = sinResolver;
            } catch (Exception e) {
                totalAnomalias = 0;
                anomaliasSinResolver = 0;
            }
            // Alertas
            try {
                totalAlertas = alertaFacade.findAll().size();
            } catch (Exception e) {
                totalAlertas = 0;
            }
            cargarUsuarios();
            listaRoles = rolFacade.findAll();
        } catch (Exception e) {
            totalUsuarios = 0;
            totalRegistrosGlucosa = 0;
            totalCitas = 0;
            errorStats = "Error al cargar estadísticas o usuarios: " + e.getMessage();
        }
    }

    public void cargarUsuarios() {
        listaUsuarios = new ArrayList<>();
        for (Usuario u : usuarioFacade.findAll()) {
            listaUsuarios.add(new UsuarioEditable(u));
        }
    }

    public List<UsuarioEditable> getListaUsuarios() {
        return listaUsuarios;
    }

    public List<Rol> getListaRoles() {
        return listaRoles;
    }

    public void editarUsuario(UsuarioEditable usuario) {
        usuario.setEditable(true);
    }

    public void guardarUsuario(UsuarioEditable usuarioEditable) {
        try {
            Usuario usuario = usuarioEditable.getUsuario();
            // Actualizar el rol si cambió
            if (usuarioEditable.getNuevoRolId() != null) {
                Rol nuevoRol = rolFacade.find(usuarioEditable.getNuevoRolId());
                usuario.setIdRol(nuevoRol);
            }
            usuarioFacade.edit(usuario);
            usuarioEditable.setEditable(false);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Usuario actualizado correctamente"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al actualizar usuario: " + e.getMessage(), null));
        }
    }

    // Getters
    public int getTotalUsuarios() {
        return totalUsuarios;
    }

    public int getTotalRegistrosGlucosa() {
        return totalRegistrosGlucosa;
    }

    public int getTotalAnomalias() {
        return totalAnomalias;
    }

    public int getAnomaliasSinResolver() {
        return anomaliasSinResolver;
    }

    public int getTotalCitas() {
        return totalCitas;
    }

    public int getTotalAlertas() {
        return totalAlertas;
    }

    public String getErrorStats() {
        return errorStats;
    }

    // Clase interna para manejar el estado editable y el cambio de rol
    public static class UsuarioEditable implements Serializable {

        private Usuario usuario;
        private boolean editable = false;
        private Integer nuevoRolId;

        public UsuarioEditable(Usuario usuario) {
            this.usuario = usuario;
            if (usuario.getIdRol() != null) {
                this.nuevoRolId = usuario.getIdRol().getIdRol();
            }
        }

        public Usuario getUsuario() {
            return usuario;
        }

        public void setUsuario(Usuario usuario) {
            this.usuario = usuario;
        }

        public boolean isEditable() {
            return editable;
        }

        public void setEditable(boolean editable) {
            this.editable = editable;
        }

        public Integer getNuevoRolId() {
            return nuevoRolId;
        }

        public void setNuevoRolId(Integer nuevoRolId) {
            this.nuevoRolId = nuevoRolId;
        }

        public Integer getIdUsuario() {
            return usuario.getIdUsuario();
        }

        public void setIdUsuario(Integer id) {
            usuario.setIdUsuario(id);
        }

        // Getters delegados para JSF
        public String getNombres() {
            return usuario.getNombres();
        }

        public void setNombres(String n) {
            usuario.setNombres(n);
        }

        public String getApellidos() {
            return usuario.getApellidos();
        }

        public void setApellidos(String a) {
            usuario.setApellidos(a);
        }

        public String getCorreo() {
            return usuario.getCorreo();
        }

        public void setCorreo(String c) {
            usuario.setCorreo(c);
        }

        public String getUser() {
            return usuario.getUser();
        }

        public void setUser(String u) {
            usuario.setUser(u);
        }

        public int getDocumento() {
            return usuario.getDocumento();
        }

        public void setDocumento(int d) {
            usuario.setDocumento(d);
        }

        public int getEdad() {
            return usuario.getEdad();
        }

        public void setEdad(int e) {
            usuario.setEdad(e);
        }

        public String getRolNombre() {
            return usuario.getIdRol() != null ? usuario.getIdRol().getNombre() : "";
        }
    }
}
