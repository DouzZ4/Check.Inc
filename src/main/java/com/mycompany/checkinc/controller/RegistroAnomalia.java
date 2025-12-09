package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Anomalia;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.AnomaliaFacadeLocal;
import com.mycompany.checkinc.services.ServicioCorreo;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author davidalonso
 */
@ManagedBean(name = "registroAnomalia")
@ViewScoped
public class RegistroAnomalia implements Serializable {

    private static final String USUARIO_SESSION_KEY = "usuario";
    private static final String ERROR = "Error";

    @EJB
    private AnomaliaFacadeLocal anomaliaFacade;

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    @EJB
    private ServicioCorreo servicioCorreo;

    private Integer idAnomalia;
    private String descripcion;
    private Date fechaHora;
    private String sintomas;
    private String gravedad;
    private Boolean resuelto = false; // Cambiado a Boolean con inicialización
    private List<Anomalia> registros;
    private Boolean editando = false; // Cambiado a Boolean con inicialización

    // -- Filtrar por resuelto--//
    private String filtroResuelto;

    public RegistroAnomalia() {
        this.fechaHora = new Date();
    }

    @PostConstruct
    public void init() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get(USUARIO_SESSION_KEY);
        if (usuario != null) {
            cargarRegistros(usuario);
        }
    }

    private void cargarRegistros(Usuario usuario) {
        this.registros = anomaliaFacade.findByUsuario(usuario);
    }

    // Getters y Setters
    public Integer getIdAnomalia() {
        return idAnomalia;
    }

    public void setIdAnomalia(Integer idAnomalia) {
        this.idAnomalia = idAnomalia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getGravedad() {
        return gravedad;
    }

    public void setGravedad(String gravedad) {
        this.gravedad = gravedad;
    }

    public Boolean getResuelto() {
        return resuelto;
    }

    public void setResuelto(Boolean resuelto) {
        this.resuelto = resuelto;
    }

    public List<Anomalia> getRegistros() {
        return registros;
    }

    public void setRegistros(List<Anomalia> registros) {
        this.registros = registros;
    }

    public Boolean getEditando() {
        return editando;
    }

    public void setEditando(Boolean editando) {
        this.editando = editando;
    }

    public boolean isEditando() { // Método extra para compatibilidad
        return editando != null && editando;
    }

    public String getFiltroResuelto() {
        return filtroResuelto;
    }

    public void setFiltroResuelto(String filtroResuelto) {
        this.filtroResuelto = filtroResuelto;
    }

    public AnomaliaFacadeLocal getAnomaliaFacade() {
        return anomaliaFacade;
    }

    public void setAnomaliaFacade(AnomaliaFacadeLocal anomaliaFacade) {
        this.anomaliaFacade = anomaliaFacade;
    }

    public void registrar() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get(USUARIO_SESSION_KEY);
        if (usuario == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "Debe iniciar sesión para registrar una anomalía.");
            return;
        }

        try {
            Anomalia anomalia;
            if (editando != null && editando) {
                // DEBUG: Verificar que el idAnomalia no es null
                System.out.println("Editando anomalía ID: " + idAnomalia);

                anomalia = anomaliaFacade.find(idAnomalia);
                if (anomalia == null) {
                    addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "Registro no encontrado.");
                    return;
                }

                // Verificar que la anomalía pertenece al usuario actual
                if (!anomalia.getIdUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                    addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "No tiene permisos para editar esta anomalía.");
                    return;
                }

                anomalia.setDescripcion(descripcion);
                anomalia.setFechaHora(fechaHora);
                anomalia.setSintomas(sintomas);
                anomalia.setGravedad(gravedad);
                anomalia.setResuelto(resuelto);

                anomaliaFacade.edit(anomalia);
                addMessage(FacesMessage.SEVERITY_INFO, "Registro actualizado",
                        "La anomalía ha sido actualizada correctamente.");
                editando = false;

            } else {
                anomalia = new Anomalia();
                anomalia.setDescripcion(descripcion);
                anomalia.setFechaHora(fechaHora);
                anomalia.setSintomas(sintomas);
                anomalia.setGravedad(gravedad);
                anomalia.setIdUsuario(usuario);
                anomalia.setResuelto(resuelto != null ? resuelto : false);

                anomaliaFacade.create(anomalia);
                addMessage(FacesMessage.SEVERITY_INFO, "Registro guardado",
                        "La anomalía ha sido registrada correctamente.");

                if ("moderada".equalsIgnoreCase(gravedad) || "grave".equalsIgnoreCase(gravedad)) {
                    enviarAlertaEmergencia(usuario, anomalia);
                }
            }

            cargarRegistros(usuario);
            limpiarFormulario();

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "Error al registrar la anomalía: " + e.getMessage());
        }
    }

    private void enviarAlertaEmergencia(Usuario usuario, Anomalia anomalia) {
        try {
            String correoDestino = usuario.getCorreoEmergencia();
            if (correoDestino == null || correoDestino.trim().isEmpty()) {
                System.out.println("⚠️ No hay correo de emergencia configurado para este usuario.");
                return;
            }

            String asunto = "⚠️ Alerta de salud: " + usuario.getNombres() + " " + usuario.getApellidos();
            String mensaje = String.format(
                    "Hola %s,\n\n"
                            + "Se ha registrado una anomalía %s en el sistema Check Inc para tu familiar %s %s.\n\n"
                            + "🕒 Fecha y hora: %s\n"
                            + "💬 Descripción: %s\n"
                            + "🤒 Síntomas: %s\n\n"
                            + "Por favor comunícate con él/ella o busca asistencia médica si es necesario.\n\n"
                            + "-- Sistema Check Inc --",
                    usuario.getNombreContactoEmergencia(),
                    anomalia.getGravedad(),
                    usuario.getNombres(),
                    usuario.getApellidos(),
                    anomalia.getFechaHora(),
                    anomalia.getDescripcion(),
                    anomalia.getSintomas());

            boolean enviado = servicioCorreo.enviarCorreoAnomalia(usuario, correoDestino, asunto, mensaje);

            if (enviado) {
                System.out.println("📧 Correo de emergencia enviado a " + correoDestino);
                addMessage(FacesMessage.SEVERITY_INFO, "Alerta enviada", "Se notificó al contacto de emergencia.");
            } else {
                System.err.println("❌ Error al enviar el correo a " + correoDestino);
                addMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No se pudo enviar la alerta al familiar.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "Ocurrió un error al enviar la alerta: " + e.getMessage());
        }
    }

    public void editar(Anomalia anomalia) {
        System.out.println("Iniciando edición de anomalía ID: " + anomalia.getIdAnomalia());
        this.idAnomalia = anomalia.getIdAnomalia();
        this.descripcion = anomalia.getDescripcion();
        this.fechaHora = anomalia.getFechaHora();
        this.sintomas = anomalia.getSintomas();
        this.gravedad = anomalia.getGravedad();
        this.resuelto = anomalia.getResuelto() != null ? anomalia.getResuelto() : false;
        this.editando = true;
        System.out.println("Editando activado: " + editando + ", ID guardado: " + idAnomalia);
    }

    public void eliminar(Anomalia anomalia) {
        try {
            anomaliaFacade.remove(anomalia);
            Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .get(USUARIO_SESSION_KEY);
            if (usuario != null) {
                cargarRegistros(usuario);
            }
            addMessage(FacesMessage.SEVERITY_INFO, "Registro eliminado", "La Anomalia ha sido eliminado correctamente");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "No se pudo eliminar el registro: " + e.getMessage());
        }
    }

    public void cancelarEdicion() {
        limpiarFormulario();
        editando = false;
        System.out.println("Edición cancelada");
    }

    private void limpiarFormulario() {
        this.idAnomalia = null;
        this.descripcion = null;
        this.fechaHora = new Date();
        this.sintomas = null;
        this.gravedad = null;
        this.resuelto = false;
        this.editando = false;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }
}