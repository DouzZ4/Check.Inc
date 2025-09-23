package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.ServicioCorreo;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "correoBean")
@ViewScoped
public class CorreoBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private ServicioCorreo servicioCorreo;

    // Campo que recibe los destinatarios desde el modal (String)
    private String destinatariosString;

    // Lista interna de destinatarios convertida desde la cadena
    private List<String> destinatariosSeleccionados = new ArrayList<>();
    private String asunto;
    private String mensaje;

    // =======================
    // GETTERS Y SETTERS
    // =======================
    public String getDestinatariosString() { return destinatariosString; }
    public void setDestinatariosString(String destinatariosString) { this.destinatariosString = destinatariosString; }

    public List<String> getDestinatariosSeleccionados() { return destinatariosSeleccionados; }
    public void setDestinatariosSeleccionados(List<String> destinatariosSeleccionados) { this.destinatariosSeleccionados = destinatariosSeleccionados; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    // =======================
    // ENVIAR CORREO MASIVO
    // =======================
    public void enviarCorreoMasivo() {
        try {
            if (destinatariosString == null || destinatariosString.trim().isEmpty()) {
                System.out.println("No hay destinatarios seleccionados.");
                return;
            }

            destinatariosSeleccionados.clear();
            for (String correo : destinatariosString.split(",")) {
                if (!correo.trim().isEmpty()) destinatariosSeleccionados.add(correo.trim());
            }

            List<Usuario> usuarios = new ArrayList<>();
            for (String correo : destinatariosSeleccionados) {
                Usuario u = servicioCorreo.obtenerUsuarioPorCorreo(correo);
                if (u != null) usuarios.add(u);
            }

            boolean exito = servicioCorreo.enviarComunicadoMasivo(asunto, mensaje, usuarios);
            if (exito) {
                limpiarFormulario();
                PrimeFaces.current().executeScript("alert('El correo se envió correctamente.');");
            } else {
                PrimeFaces.current().executeScript("alert('Hubo un problema al enviar el correo.');");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =======================
    // LIMPIAR FORMULARIO
    // =======================
    private void limpiarFormulario() {
        destinatariosString = "";
        destinatariosSeleccionados.clear();
        asunto = "";
        mensaje = "";
    }
}