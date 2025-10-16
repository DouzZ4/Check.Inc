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
            // 1️⃣ Validación de campos obligatorios
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

            // 2️⃣ Procesar lista de destinatarios
            destinatariosSeleccionados.clear();
            for (String correo : destinatariosString.split(",")) {
                if (!correo.trim().isEmpty()) destinatariosSeleccionados.add(correo.trim());
            }

            if (destinatariosSeleccionados.isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Atención", "No se detectaron correos válidos.");
                System.out.println("⚠️ [WARN] Ningún correo válido detectado.");
                return;
            }

            // 3️⃣ Buscar usuarios por correo
            List<Usuario> usuarios = new ArrayList<>();
            for (String correo : destinatariosSeleccionados) {
                Usuario u = servicioCorreo.obtenerUsuarioPorCorreo(correo);
                if (u != null) {
                    usuarios.add(u);
                } else {
                    System.out.println("⚠️ [INFO] Usuario no encontrado en BD para correo: " + correo);
                }
            }

            if (usuarios.isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Atención", "Ninguno de los destinatarios está registrado en el sistema.");
                System.out.println("⚠️ [WARN] Ningún usuario encontrado en la base de datos.");
                return;
            }

            // 4️⃣ Enviar correo masivo
            System.out.println("📤 [INFO] Enviando correo masivo a " + usuarios.size() + " destinatarios...");
            boolean exito = servicioCorreo.enviarComunicadoMasivo(asunto, mensaje, usuarios);

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
