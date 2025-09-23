package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Cita;
import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.CitaFacadeLocal;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

@ManagedBean(name = "registroCita")
@ViewScoped
public class RegistroCita implements Serializable {
    private static final String USUARIO_SESSION_KEY = "usuario";
    private static final String ERROR = "Error";

    @EJB
    private CitaFacadeLocal citaFacade;
    
    @EJB
    private UsuarioFacadeLocal usuarioFacade;
    
    private Integer id;
    private Date fecha;
    private Date hora;
    private String motivo;
    private List<Cita> registros;
    private boolean editando;
    private ScheduleModel eventModel;
    private ScheduleEvent<?> selectedEvent;
    
    // --- Filtro y método filtrado para motivo ---
    private String filtroMotivo;
    public String getFiltroMotivo() { return filtroMotivo; }
    public void setFiltroMotivo(String filtroMotivo) { this.filtroMotivo = filtroMotivo; }

    // --- Filtros adicionales y orden para la vista ---
    private String filtroFecha;
    private String filtroHora;
    private boolean ascendente = true;
    public String getFiltroFecha() { return filtroFecha; }
    public void setFiltroFecha(String filtroFecha) { this.filtroFecha = filtroFecha; }
    public String getFiltroHora() { return filtroHora; }
    public void setFiltroHora(String filtroHora) { this.filtroHora = filtroHora; }
    public boolean isAscendente() { return ascendente; }
    public void setAscendente(boolean ascendente) { this.ascendente = ascendente; }

    public List<Cita> getRegistrosFiltrados() {
        if (registros == null) return java.util.Collections.emptyList();
        java.util.stream.Stream<Cita> stream = registros.stream();
        if (filtroMotivo != null && !filtroMotivo.isEmpty()) {
            stream = stream.filter(c -> c.getMotivo() != null && c.getMotivo().toLowerCase().contains(filtroMotivo.toLowerCase()));
        }
        if (filtroFecha != null && !filtroFecha.isEmpty()) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            stream = stream.filter(c -> {
                String fecha = c.getFecha() != null ? sdf.format(c.getFecha()) : "";
                return fecha.contains(filtroFecha);
            });
        }
        if (filtroHora != null && !filtroHora.isEmpty()) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
            stream = stream.filter(c -> {
                String hora = c.getHora() != null ? sdf.format(c.getHora()) : "";
                return hora.contains(filtroHora);
            });
        }
        java.util.Comparator<Cita> comparator = java.util.Comparator.comparing(
            c -> {
                // Combina fecha y hora para ordenar correctamente
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(c.getFecha() != null ? c.getFecha() : new java.util.Date(0));
                if (c.getHora() != null) {
                    java.util.Calendar horaCal = java.util.Calendar.getInstance();
                    horaCal.setTime(c.getHora());
                    cal.set(java.util.Calendar.HOUR_OF_DAY, horaCal.get(java.util.Calendar.HOUR_OF_DAY));
                    cal.set(java.util.Calendar.MINUTE, horaCal.get(java.util.Calendar.MINUTE));
                }
                return cal.getTime();
            }
        );
        if (!ascendente) {
            comparator = comparator.reversed();
        }
        return stream.sorted(comparator).collect(java.util.stream.Collectors.toList());
    }
    
    public RegistroCita() {
        this.fecha = new Date();
        this.hora = new Date();
    }
    
    @PostConstruct
    public void init() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USUARIO_SESSION_KEY);
        if (usuario != null) {
            cargarRegistros(usuario);
            inicializarCalendario();
        }
    }
    
    private void inicializarCalendario() {
        eventModel = new DefaultScheduleModel();
        if (registros != null) {
            for (Cita cita : registros) {
                LocalDateTime start = combinarFechaHora(cita.getFecha(), cita.getHora());
                LocalDateTime end = start.plusHours(1); // Asumimos 1 hora de duración

                ScheduleEvent<?> event = DefaultScheduleEvent.builder()
                        .title(cita.getMotivo())
                        .startDate(start)
                        .endDate(end)
                        .description(cita.getMotivo())
                        .build();
                eventModel.addEvent(event);
            }
        }
    }

    private LocalDateTime combinarFechaHora(Date fecha, Date hora) {
        Calendar calFecha = Calendar.getInstance();
        calFecha.setTime(fecha);

        Calendar calHora = Calendar.getInstance();
        calHora.setTime(hora);

        calFecha.set(Calendar.HOUR_OF_DAY, calHora.get(Calendar.HOUR_OF_DAY));
        calFecha.set(Calendar.MINUTE, calHora.get(Calendar.MINUTE));
        calFecha.set(Calendar.SECOND, 0);
        calFecha.set(Calendar.MILLISECOND, 0);

        return calFecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    public void onEventSelect(SelectEvent<ScheduleEvent<?>> selectEvent) {
        selectedEvent = selectEvent.getObject();
    }

    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        selectedEvent = DefaultScheduleEvent.builder()
                .startDate(selectEvent.getObject())
                .endDate(selectEvent.getObject().plusHours(1))
                .build();
    }

    private void cargarRegistros(Usuario usuario) {
        this.registros = citaFacade.findByUsuario(usuario);
    }
    
    // Getters y Setters
    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public ScheduleEvent<?> getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(ScheduleEvent<?> selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    
    public Date getHora() { return hora; }
    public void setHora(Date hora) { this.hora = hora; }
    
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    
    public List<Cita> getRegistros() { return registros; }
    
    public boolean isEditando() { return editando; }
    public void setEditando(boolean editando) { this.editando = editando; }
    
    // Método para el botón 'Ahora' en la vista
    public void setFechaHoraNow() {
        Date now = new Date();
        this.fecha = now;
        this.hora = now;
    }
    
    // Métodos de acción
    public void registrar() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USUARIO_SESSION_KEY);
        if (usuario == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "Debe iniciar sesión para registrar");
            return;
        }
        try {
            if (editando) {
                Cita cita = citaFacade.find(id);
                if (cita == null) {
                    addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "Registro no encontrado");
                    return;
                }
                cita.setFecha(fecha);
                cita.setHora(hora);
                cita.setMotivo(motivo);
                citaFacade.edit(cita);
                addMessage(FacesMessage.SEVERITY_INFO, "Registro actualizado", "La cita ha sido actualizada correctamente");
                editando = false;
            } else {
                Cita cita = new Cita();
                cita.setFecha(fecha);
                cita.setHora(hora);
                cita.setMotivo(motivo);
                cita.setIdUsuario(usuario);
                citaFacade.create(cita);
                addMessage(FacesMessage.SEVERITY_INFO, "Registro guardado", "La cita ha sido guardada correctamente");
            }
            cargarRegistros(usuario);
            limpiarFormulario();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "No se pudo procesar el registro: " + e.getMessage());
        }
    }
    
    public void editar(Cita cita) {
        this.id = cita.getIdCita();
        this.fecha = cita.getFecha();
        this.hora = cita.getHora();
        this.motivo = cita.getMotivo();
        this.editando = true;
    }
    
    public void eliminar(Cita cita) {
        try {
            citaFacade.remove(cita);
            Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USUARIO_SESSION_KEY);
            if (usuario != null) {
                cargarRegistros(usuario);
            }
            addMessage(FacesMessage.SEVERITY_INFO, "Registro eliminado", "La cita ha sido eliminada correctamente");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, ERROR, "No se pudo eliminar el registro: " + e.getMessage());
        }
    }
    
    public void cancelarEdicion() {
        limpiarFormulario();
        editando = false;
    }
    
    private void limpiarFormulario() {
        this.id = null;
        this.fecha = new Date();
        this.hora = new Date();
        this.motivo = null;
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
}
