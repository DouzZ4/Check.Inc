package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.entities.Glucosa;
import com.mycompany.checkinc.util.Config;
import java.io.BufferedReader;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ServicioCorreo {

    private static final String SENDGRID = Config.get("SENDGRID_API_KEY");

    @PersistenceContext
    private EntityManager em;

    // ======================================================
    // ✅ MÉTODO 1: Enviar correo de registro
    // ======================================================
    public boolean enviarCorreoRegistro(String correo, String nombres, String apellidos,
            String username, Integer edad, String tipoDiabetes,
            Boolean esInsulodependiente) {

        File tempFile = null;
        try {
            System.out.println("📤 [INFO] Enviando correo de registro a: " + correo);

            tempFile = File.createTempFile("sendgrid_registro", ".json");
            String json = crearJsonRegistro(correo, nombres, apellidos, username, edad, tipoDiabetes, esInsulodependiente);

            // ✅ Escritura con UTF-8 explícito
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
                writer.write(json.replace("\\\\n", "\\n"));
            }

            boolean enviado = ejecutarEnvioCorreo(tempFile);

            if (enviado) {
                System.out.println("✅ [OK] Correo de registro enviado correctamente a " + correo);
            } else {
                System.err.println("⚠️ [WARN] El correo no pudo ser enviado a " + correo);
            }

            return enviado;

        } catch (Exception e) {
            System.err.println("❌ [ERROR] Falló el envío de correo de registro: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (tempFile != null && tempFile.exists()) tempFile.delete();
        }
    }
/**
 * Envía un comunicado a usuarios registrados y correos externos.
 * Retorna true si al menos 1 envío fue exitoso.
 */
public boolean enviarComunicadoMasivo(String asunto, String mensaje, List<Usuario> usuariosRegistrados, List<String> correosExternos) {
    // Construir lista de destinatarios como strings (email + opcional nombre)
    List<String> todos = new ArrayList<>();

    // Agregar usuarios registrados (puedes usar su nombre para personalizar)
    for (Usuario u : usuariosRegistrados) {
        if (u != null && u.getCorreo() != null && !u.getCorreo().trim().isEmpty()) {
            todos.add(u.getCorreo().trim());
        }
    }

    // Agregar correos externos
    for (String c : correosExternos) {
        if (c != null && !c.trim().isEmpty()) {
            todos.add(c.trim());
        }
    }

    // Enviar uno por uno (puedes optimizar para batches si quieres)
    int exitosos = 0;
    int fallidos = 0;
    for (String destinatario : todos) {
        boolean sent = enviarCorreoIndividual(destinatario, "", "", asunto, mensaje);
        if (sent) exitosos++; else fallidos++;
        try { Thread.sleep(300); } catch (InterruptedException ex) { /* ignore */ }
    }

    System.out.println("📊 [RESULTADO] Exitosos: " + exitosos + " Fallidos: " + fallidos);
    return exitosos > 0;
}


    // ======================================================
    // ✅ MÉTODO AUXILIAR: Enviar correo individual
    // ======================================================
    private boolean enviarCorreoIndividual(String correo, String nombres, String apellidos,
            String asunto, String mensaje) {

        File tempFile = null;
        try {
            tempFile = File.createTempFile("sendgrid_masivo", ".json");

            String mensajePersonalizado = "==============================\\n"
                    + "   SISTEMA DE GESTIÓN CHECKINC\\n"
                    + "==============================\\n\\n"
                    + "Hola " + nombres + ",\\n\\n"
                    + mensaje + "\\n\\n"
                    + "==============================\\n"
                    + "Si tienes alguna duda o consulta, no dudes en contactarnos.\\n\\n"
                    + "Saludos,\\n"
                    + "Equipo CheckInc";

            String json = "{"
                    + "\"personalizations\": [{"
                    + "\"to\": [{\"email\": \"" + correo + "\",\"name\": \"" + nombres + " " + apellidos + "\"}]"
                    + "}],"
                    + "\"from\": {\"email\": \"a-cmoreno@hotmail.com\",\"name\": \"CheckInc - Sistema de Diabetes\"},"
                    + "\"subject\": \"" + asunto.replace("\"", "\\\"") + "\","
                    + "\"content\": [{\"type\": \"text/plain\",\"value\": \"" + mensajePersonalizado.replace("\"", "\\\"") + "\"}]"
                    + "}";

            // ✅ Escritura segura con UTF-8
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
                writer.write(json.replace("\\\\n", "\\n"));
            }

            boolean enviado = ejecutarEnvioCorreo(tempFile);
            if (!enviado) {
                System.err.println("⚠️ [WARN] No se pudo enviar correo a " + correo);
            }

            return enviado;

        } catch (Exception e) {
            System.err.println("❌ [ERROR] Falló envío individual a " + correo + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (tempFile != null && tempFile.exists()) tempFile.delete();
        }
    }

    public boolean enviarCorreoAnomalia(String correoDestino, String asunto, String mensaje) {
    File tempFile = null;
    try {
        tempFile = File.createTempFile("sendgrid_anomalia", ".json");

        String json = "{"
                + "\"personalizations\": [{\"to\": [{\"email\": \"" + correoDestino + "\"}]}],"
                + "\"from\": {\"email\": \"a-cmoreno@hotmail.com\",\"name\": \"CheckInc - Sistema de Diabetes\"},"
                + "\"subject\": \"" + asunto.replace("\"", "\\\"") + "\","
                + "\"content\": [{\"type\": \"text/plain\",\"value\": \"" + mensaje.replace("\"", "\\\"") + "\"}]"
                + "}";

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
            writer.write(json);
        }

        return ejecutarEnvioCorreo(tempFile);

    } catch (Exception e) {
        System.err.println("❌ [ERROR] No se pudo enviar correo de anomalía: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        if (tempFile != null && tempFile.exists()) tempFile.delete();
    }
}


    // ======================================================
    // ✅ MÉTODO COMÚN: Ejecutar envío con cURL
    // ======================================================
    private boolean ejecutarEnvioCorreo(File tempFile) {
        try {
            String[] comando = {
                "curl", "-X", "POST",
                "https://api.sendgrid.com/v3/mail/send",
                "-H", "Authorization: Bearer " + SENDGRID,
                "-H", "Content-Type: application/json; charset=utf-8",
                "-d", "@" + tempFile.getAbsolutePath(),
                "--max-time", "30",
                "-s", "-i"
            };

            System.out.println("🔧 [INFO] Ejecutando comando CURL:");
            System.out.println(String.join(" ", comando));

            ProcessBuilder pb = new ProcessBuilder(comando);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            System.out.println("📜 [RESPUESTA CURL]");
            System.out.println(output.toString());

            if (exitCode != 0) {
                System.err.println("❌ [ERROR] Falló la ejecución de cURL. Código: " + exitCode);
                return false;
            }

            if (output.toString().contains("202 Accepted")) {
                System.out.println("📨 [OK] Correo enviado correctamente ✅");
                return true;
            } else if (output.toString().contains("401")) {
                System.err.println("🚫 [ERROR] Autenticación fallida. Verifica tu API Key de SendGrid.");
            } else if (output.toString().contains("400")) {
                System.err.println("⚠️ [ERROR] Petición incorrecta. Revisa el formato del JSON enviado.");
            } else if (output.toString().contains("403")) {
                System.err.println("🚷 [ERROR] No tienes permisos para usar la API de SendGrid.");
            } else if (output.toString().contains("415")) {
                System.err.println("⚠️ [ERROR] El servidor rechazó la codificación. Verifica UTF-8 o caracteres especiales en el mensaje.");
            } else {
                System.err.println("❌ [ERROR] Respuesta desconocida del servidor:");
                System.err.println(output.toString());
            }

            return false;

        } catch (Exception e) {
            System.err.println("💥 [ERROR] Excepción al ejecutar cURL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ======================================================
    // ✅ Buscar usuario por correo
    // ======================================================
    public Usuario obtenerUsuarioPorCorreo(String correo) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.correo = :correo", Usuario.class)
                    .setParameter("correo", correo)
                    .getSingleResult();
        } catch (Exception e) {
            System.err.println("⚠️ [WARN] No se encontró usuario con correo: " + correo);
            return null;
        }
    }

    // ======================================================
    // ✅ MÉTODO: Enviar alerta de glucosa con HTML
    // ======================================================
    public boolean enviarAlertaGlucosaHTML(Usuario usuario, Glucosa glucosa, String estado, String rango, String recomendacion) {
        File tempFile = null;
        try {
            System.out.println("📤 [INFO] Enviando alerta de glucosa a: " + usuario.getCorreo());

            tempFile = File.createTempFile("sendgrid_alerta_glucosa", ".json");

            // Formatear datos para el HTML
            String nombreUsuario = usuario.getNombres() != null ? usuario.getNombres() : "Usuario";
            String correoDestino = usuario.getCorreo();
            String tipoDiabetes = usuario.getTipoDiabetes() != null ? usuario.getTipoDiabetes() : "No especificado";
            String nivelGlucosaStr = String.format("%.1f", glucosa.getNivelGlucosa());
            String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(glucosa.getFechaHora());

            // Determinar emoji según el estado
            String emoji = "❓";
            String colorBorde = "#FFA500";
            switch (estado) {
                case "CRITICO_BAJO":
                    emoji = "🚨";
                    colorBorde = "#FF0000";
                    break;
                case "CRITICO_ALTO":
                    emoji = "🚨";
                    colorBorde = "#FF0000";
                    break;
                case "BAJO":
                    emoji = "⚠️";
                    colorBorde = "#FF6B6B";
                    break;
                case "ALTO":
                    emoji = "⚠️";
                    colorBorde = "#FF6B6B";
                    break;
                case "NORMAL":
                    emoji = "✅";
                    colorBorde = "#28A745";
                    break;
            }

            // Construir HTML del correo
            String htmlBody = "<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                    + "<div style='max-width: 600px; margin: 0 auto; background-color: #f9f9f9; border: 3px solid " + colorBorde + "; border-radius: 8px; padding: 20px;'>"
                    + "<div style='text-align: center; margin-bottom: 20px;'>"
                    + "<h2 style='color: " + colorBorde + "; margin: 0;'>" + emoji + " ALERTA DE GLUCOSA</h2>"
                    + "</div>"
                    + "<hr style='border: none; border-top: 2px solid " + colorBorde + "; margin: 20px 0;'>"
                    + "<p><strong>Hola " + nombreUsuario + ",</strong></p>"
                    + "<p>Se ha detectado un nivel de glucosa fuera del rango normal:</p>"
                    + "<div style='background-color: white; border-left: 4px solid " + colorBorde + "; padding: 15px; margin: 15px 0; border-radius: 4px;'>"
                    + "<p><strong>📊 Nivel actual:</strong> <span style='font-size: 24px; color: " + colorBorde + "; font-weight: bold;'>" + nivelGlucosaStr + " mg/dL</span></p>"
                    + "<p><strong>📍 Estado:</strong> <span style='color: " + colorBorde + "; font-weight: bold;'>" + estado + "</span></p>"
                    + "<p><strong>🎯 Rango recomendado:</strong> " + rango + "</p>"
                    + "<p><strong>⏰ Hora del registro:</strong> " + fecha + "</p>"
                    + "<p><strong>🏥 Tipo de diabetes:</strong> " + tipoDiabetes + "</p>"
                    + "</div>"
                    + "<div style='background-color: #FFF3CD; border-left: 4px solid #FF9800; padding: 15px; margin: 15px 0; border-radius: 4px;'>"
                    + "<p><strong>💡 RECOMENDACIÓN:</strong></p>"
                    + "<p>" + recomendacion + "</p>"
                    + "</div>"
                    + "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>"
                    + "<p style='font-size: 12px; color: #999;'>"
                    + "Este es un mensaje automático del sistema CheckInc. Por favor, no respondas a este correo. "
                    + "Si necesitas contactar con soporte, accede a tu panel de CheckInc."
                    + "</p>"
                    + "<p style='text-align: center; margin-top: 20px;'>"
                    + "<a href='http://localhost:8080/CheckInc' style='background-color: " + colorBorde + "; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; display: inline-block;'>Ir al Dashboard</a>"
                    + "</p>"
                    + "</div>"
                    + "</body></html>";

            // Escapar comillas y newlines para JSON
            htmlBody = htmlBody.replace("\"", "\\\"").replace("\n", "");

            // Construir JSON para SendGrid con soporte para HTML
            String json = "{"
                    + "\"personalizations\": [{\"to\": [{\"email\": \"" + correoDestino + "\",\"name\": \"" + nombreUsuario + "\"}]}],"
                    + "\"from\": {\"email\": \"a-cmoreno@hotmail.com\",\"name\": \"CheckInc - Sistema de Diabetes\"},"
                    + "\"subject\": \"" + emoji + " ALERTA DE GLUCOSA - Nivel " + estado + "\","
                    + "\"content\": [{\"type\": \"text/html\",\"value\": \"" + htmlBody + "\"}]"
                    + "}";

            // Escritura segura con UTF-8
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
                writer.write(json.replace("\\\\n", "\\n"));
            }

            boolean enviado = ejecutarEnvioCorreo(tempFile);

            if (enviado) {
                System.out.println("✅ [OK] Alerta de glucosa enviada correctamente a " + correoDestino);
            } else {
                System.err.println("⚠️ [WARN] La alerta de glucosa no pudo ser enviada a " + correoDestino);
            }

            return enviado;

        } catch (Exception e) {
            System.err.println("❌ [ERROR] Falló el envío de alerta de glucosa: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (tempFile != null && tempFile.exists()) tempFile.delete();
        }
    }

    // ======================================================
    // ✅ Crear JSON de correo de registro
    // ======================================================
    private String crearJsonRegistro(String correo, String nombres, String apellidos,
            String username, Integer edad, String tipoDiabetes,
            Boolean esInsulodependiente) {

        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String insulodependiente = esInsulodependiente ? "Sí" : "No";

        String mensaje = "BIENVENIDO/A A CHECKINC!\\n\\n"
                + "Hola " + nombres + " " + apellidos + ",\\n\\n"
                + "Tu registro en el sistema ha sido exitoso.\\n\\n"
                + "==============================\\n"
                + "Información de registro:\\n"
                + "Usuario: " + username + "\\n"
                + "Correo: " + correo + "\\n"
                + "Edad: " + edad + " años\\n"
                + "Tipo de Diabetes: " + tipoDiabetes + "\\n"
                + "Insulodependiente: " + insulodependiente + "\\n"
                + "Fecha de registro: " + fecha + "\\n"
                + "==============================\\n\\n"
                + "Gracias por unirte a CheckInc.";

        mensaje = mensaje.replace("\"", "\\\"");

        return "{"
                + "\"personalizations\": [{"
                + "\"to\": [{"
                + "\"email\": \"" + correo + "\","
                + "\"name\": \"" + nombres + " " + apellidos + "\""
                + "}]"
                + "}],"
                + "\"from\": {"
                + "\"email\": \"a-cmoreno@hotmail.com\","
                + "\"name\": \"CheckInc - Sistema de Diabetes\""
                + "},"
                + "\"subject\": \"Bienvenido a CheckInc - Registro Exitoso\","
                + "\"content\": [{"
                + "\"type\": \"text/plain\","
                + "\"value\": \"" + mensaje + "\""
                + "}]"
                + "}";
    }
}
