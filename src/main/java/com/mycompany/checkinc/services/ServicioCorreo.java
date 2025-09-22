package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Usuario;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ServicioCorreo {

    private static final String SENDGRID_API_KEY = "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";

    @PersistenceContext
    private EntityManager em;

    /**
     * ✅ MÉTODO 1: Enviar correo cuando se registra un usuario nuevo
     */
    public boolean enviarCorreoRegistro(String correo, String nombres, String apellidos, 
                                       String username, Integer edad, String tipoDiabetes, 
                                       Boolean esInsulodependiente) {
        File tempFile = null;
        try {
            System.out.println("🔄 Enviando correo de registro a: " + correo);

            // Crear archivo temporal
            tempFile = File.createTempFile("sendgrid_registro", ".json");

            // Escribir JSON en el archivo
            String json = crearJsonRegistro(correo, nombres, apellidos, username, edad, tipoDiabetes, esInsulodependiente);
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(json);
            }

            System.out.println("📄 JSON de registro escrito en: " + tempFile.getAbsolutePath());

            return ejecutarEnvioCorreo(tempFile);

        } catch (Exception e) {
            System.err.println("❌ Error enviando correo de registro: " + e.getMessage());
            return false;
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * ✅ MÉTODO 2: Enviar correo masivo a todos los usuarios registrados
     */
    public boolean enviarComunicadoMasivo(String asunto, String mensaje, List<Usuario> usuarios) {
        try {
            System.out.println("📢 Iniciando envío de comunicado masivo");
            
            if (usuarios.isEmpty()) {
                System.out.println("ℹ️ No hay usuarios registrados");
                return false;
            }

            int exitosos = 0;
            int fallidos = 0;

            for (Usuario usuario : usuarios) {
                boolean enviado = enviarCorreoIndividual(usuario.getCorreo(), 
                                                        usuario.getNombres(), 
                                                        usuario.getApellidos(), 
                                                        asunto, 
                                                        mensaje);
                
                if (enviado) {
                    exitosos++;
                } else {
                    fallidos++;
                }
                
                // Pequeña pausa para no saturar la API
                Thread.sleep(500);
            }

            System.out.println("📊 Resultado comunicado masivo:");
            System.out.println("✅ Envíos exitosos: " + exitosos);
            System.out.println("❌ Envíos fallidos: " + fallidos);
            System.out.println("📧 Total usuarios: " + usuarios.size());

            return exitosos > 0;

        } catch (Exception e) {
            System.err.println("❌ Error en comunicado masivo: " + e.getMessage());
            return false;
        }
    }

    /**
     * ✅ MÉTODO AUXILIAR: Enviar correo individual para comunicados
     */
    private boolean enviarCorreoIndividual(String correo, String nombres, String apellidos, 
                                         String asunto, String mensaje) {
        File tempFile = null;
        try {
            // Crear archivo temporal
            tempFile = File.createTempFile("sendgrid_masivo", ".json");

            // Personalizar mensaje para el usuario
            String mensajePersonalizado = "Hola " + nombres + " " + apellidos + ",\\n\\n" 
                                        + mensaje + "\\n\\n"
                                        + "Saludos cordiales,\\n"
                                        + "Equipo CheckInc";

            String json = "{"
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
                    + "\"subject\": \"" + asunto.replace("\"", "\\\"") + "\","
                    + "\"content\": [{"
                    + "\"type\": \"text/plain\","
                    + "\"value\": \"" + mensajePersonalizado.replace("\"", "\\\"") + "\""
                    + "}]"
                    + "}";

            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(json);
            }

            return ejecutarEnvioCorreo(tempFile);

        } catch (Exception e) {
            System.err.println("❌ Error enviando correo individual a " + correo + ": " + e.getMessage());
            return false;
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * ✅ MÉTODO COMÚN: Ejecutar el envío via cURL
     */
    private boolean ejecutarEnvioCorreo(File tempFile) {
        try {
            // Comando cURL con archivo
            String[] comando = {
                "curl", "-X", "POST",
                "https://api.sendgrid.com/v3/mail/send",
                "-H", "Authorization: Bearer " + SENDGRID_API_KEY,
                "-H", "Content-Type: application/json",
                "-d", "@" + tempFile.getAbsolutePath(),
                "--max-time", "30",
                "-s" // Modo silencioso
            };

            // Ejecutar proceso
            ProcessBuilder pb = new ProcessBuilder(comando);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Leer resultado
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("✅ Correo enviado exitosamente");
                return true;
            } else {
                System.err.println("❌ Error en cURL: " + output.toString());
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ Error ejecutando cURL: " + e.getMessage());
            return false;
        }
    }

    /**
     * ✅ JSON para correo de registro
     */
    private String crearJsonRegistro(String correo, String nombres, String apellidos, 
                                   String username, Integer edad, String tipoDiabetes, 
                                   Boolean esInsulodependiente) {
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String insulodependiente = esInsulodependiente ? "Si" : "No";

        String mensaje = "BIENVENIDO/A A CHECKINC!\\n\\n"
                + "Hola " + nombres + " " + apellidos + ",\\n\\n"
                + "Tu registro en nuestro sistema de gestion de diabetes ha sido exitoso.\\n\\n"
                + "INFORMACION DE TU REGISTRO:\\n"
                + "-----------------------------------------\\n"
                + "Usuario: " + username + "\\n"
                + "Correo: " + correo + "\\n"
                + "Edad: " + edad + " anos\\n"
                + "Tipo Diabetes: " + tipoDiabetes + "\\n"
                + "Insulodependiente: " + insulodependiente + "\\n"
                + "Fecha Registro: " + fecha + "\\n"
                + "-----------------------------------------\\n\\n"
                + "PROXIMOS PASOS:\\n"
                + "- Inicia sesion en la plataforma\\n"
                + "- Registra tus niveles de glucosa\\n"
                + "- Gestiona tu medicacion\\n"
                + "- Consulta tus reportes\\n\\n"
                + "Muchas gracias.";

        // Escapar comillas dobles
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