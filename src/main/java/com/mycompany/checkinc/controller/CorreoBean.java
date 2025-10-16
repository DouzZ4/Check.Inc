package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.ServicioCorreo;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
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

    private String destinatariosString;
    private List<String> destinatariosSeleccionados = new ArrayList<>();
    private String asunto;
    private String mensaje;

    // =======================
    // GETTERS & SETTERS
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
        // 1) Validaciones básicas
        if (destinatariosString == null || destinatariosString.trim().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Atención", "Debes ingresar al menos un destinatario.");
            System.out.println("⚠️ [WARN] No hay destinatarios seleccionados.");
            return;
        }
        if (asunto == null || asunto.trim().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Atención", "Debes ingresar un asunto para el correo.");
            System.out.println("⚠️ [WARN] Asunto vacío.");
            return;
        }
        if (mensaje == null || mensaje.trim().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Atención", "Debes ingresar un mensaje para enviar.");
            System.out.println("⚠️ [WARN] Mensaje vacío.");
            return;
        }

        // 2) Normalizar y separar destinatarios (por comas)
        destinatariosSeleccionados.clear();
        for (String s : destinatariosString.split(",")) {
            String correo = s.trim();
            if (!correo.isEmpty()) destinatariosSeleccionados.add(correo);
        }

        if (destinatariosSeleccionados.isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Atención", "No se detectaron correos válidos.");
            System.out.println("⚠️ [WARN] Ningún correo válido detectado.");
            return;
        }

        // 3) Separar entre usuarios del sistema y correos externos
        List<Usuario> usuariosRegistrados = new ArrayList<>();
        List<String> correosExternos = new ArrayList<>();

        for (String correo : destinatariosSeleccionados) {
            Usuario u = servicioCorreo.obtenerUsuarioPorCorreo(correo); // si vas a eliminar este método, sustituir por otro servicio
            if (u != null) {
                usuariosRegistrados.add(u);
            } else {
                // opcional: validar formato básico de email antes de agregar
                if (correo.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    correosExternos.add(correo);
                } else {
                    System.out.println("⚠️ [WARN] Formato inválido, se omitirá: " + correo);
                }
            }
        }

        if (usuariosRegistrados.isEmpty() && correosExternos.isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Atención", "No hay destinatarios válidos (revisa formatos).");
            System.out.println("⚠️ [WARN] Ningún destinatario válido tras validación.");
            return;
        }

        // 4) Llamada al servicio: ahora aceptamos ambos tipos
        System.out.println("📤 [INFO] Enviando a: registrados=" + usuariosRegistrados.size()
                + " externos=" + correosExternos.size());

        // NUEVO método en ServicioCorreo (ver sección 2 abajo)
        boolean exito = servicioCorreo.enviarComunicadoMasivo(asunto, mensaje, usuariosRegistrados, correosExternos);

        // 5) Resultado y mensajes al usuario
        if (exito) {
            limpiarFormulario();
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "El correo se envió correctamente a los destinatarios.");
            System.out.println("✅ [OK] Correo masivo enviado correctamente.");
        } else {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Hubo un problema al enviar los correos. Revisa el log para más detalles.");
            System.err.println("❌ [ERROR] Falló el envío de correo masivo.");
        }

    } catch (Exception e) {
        e.printStackTrace();
        mostrarMensaje(FacesMessage.SEVERITY_FATAL, "Error inesperado", "Ocurrió un error al enviar los correos: " + e.getMessage());
        System.err.println("💥 [EXCEPCIÓN] " + e.getMessage());
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
        System.out.println("🧹 [INFO] Formulario de correo limpiado.");
    }

    // =======================
    // MÉTODO AUXILIAR PARA MENSAJES
    // =======================
    private void mostrarMensaje(FacesMessage.Severity severidad, String titulo, String detalle) {
        FacesMessage msg = new FacesMessage(severidad, titulo, detalle);
        PrimeFaces.current().dialog().showMessageDynamic(msg);
    }
}
