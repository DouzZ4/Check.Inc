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

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;

@Stateless
public class ServicioCorreo {

    private static String SENDGRID_API_KEY = "";


    @PersistenceContext
    private EntityManager em;

    // Cargar la API key desde el archivo de propiedades
    static {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("src/main/resources/sendgrid.properties"));
            SENDGRID_API_KEY = props.getProperty("SENDGRID_API_KEY", "");
        } catch (IOException e) {
            System.err.println("No se pudo cargar la clave de SendGrid: " + e.getMessage());
        }
    }

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
            if (usuarios.isEmpty()) {
                return false;
            }

            int exitosos = 0;
            for (Usuario usuario : usuarios) {
                boolean enviado = enviarCorreoIndividual(usuario.getCorreo(),
                        usuario.getNombres(),
                        usuario.getApellidos(),
                        asunto,
                        mensaje);
                if (enviado) {
                    exitosos++;
                }
                Thread.sleep(500);
            }
            return exitosos > 0;
        } catch (Exception e) {
            e.printStackTrace();
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
            tempFile = File.createTempFile("sendgrid_masivo", ".json");

            String mensajePersonalizado = "==============================\\n"
                    + "   SISTEMA DE GESTION CHECKINC\\n"
                    + "==============================\\n\\n"
                    + "Hola " + nombres +",\\n\\n"
                    + mensaje + "\\n\\n"
                    + "==============================\\n\\n\\n"
                    + "Si tiene alguna duda o consulta, no dude en contactarnos.\\n\\n"
                    + "Saludos cordiales,\\n"
                    + "Equipo CheckInc\\n"
                    + "==============================";

            String json = "{"
                    + "\"personalizations\": [{"
                    + "\"to\": [{\"email\": \"" + correo + "\",\"name\": \"" + nombres + " " + apellidos + "\"}]"
                    + "}],"
                    + "\"from\": {\"email\": \"a-cmoreno@hotmail.com\",\"name\": \"CheckInc - Sistema de Diabetes\"},"
                    + "\"subject\": \"" + asunto.replace("\"", "\\\"") + "\","
                    + "\"content\": [{\"type\": \"text/plain\",\"value\": \"" + mensajePersonalizado.replace("\"", "\\\"") + "\"}]"
                    + "}";

            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(json);
            }

            return ejecutarEnvioCorreo(tempFile);

        } catch (Exception e) {
            e.printStackTrace();
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
            String[] comando = {
                "curl", "-X", "POST",
                "https://api.sendgrid.com/v3/mail/send",
                "-H", "Authorization: Bearer " + SENDGRID_API_KEY,
                "-H", "Content-Type: application/json",
                "-d", "@" + tempFile.getAbsolutePath(),
                "--max-time", "30",
                "-s"
            };
            ProcessBuilder pb = new ProcessBuilder(comando);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================================
    // NUEVO MÉTODO PÚBLICO PARA BUSCAR USUARIO
    // ================================
    public Usuario obtenerUsuarioPorCorreo(String correo) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.correo = :correo", Usuario.class)
                    .setParameter("correo", correo)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
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

        String mensaje = "   BIENVENIDO/A A CHECKINC!\\n\\n"
                + "Hola " + nombres + " " + apellidos + ",\\n\\n"
                + "Tu registro en nuestro sistema de gestion de diabetes ha sido exitoso.\\n\\n"
                + "Informacion de registro:\\n"
                + "==============================\\n"
                + "Usuario: " + username + "\\n"
                + "Correo: " + correo + "\\n"
                + "Edad: " + edad + " anos\\n"
                + "Tipo Diabetes: " + tipoDiabetes + "\\n"
                + "Insulodependiente: " + insulodependiente + "\\n"
                + "Fecha Registro: " + fecha + "\\n\\n"
                + "==============================\\n"
                + "Proximos pasos:\\n"
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
