package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Usuario;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Properties;
import java.util.UUID;
import java.io.*;
import java.util.Properties;

@Stateless
public class ResetService {

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    private static final String CONFIG_FILE = "/config.properties";

    private Properties cargarConfig() throws Exception {
        Properties prop = new Properties();
        prop.load(getClass().getResourceAsStream(CONFIG_FILE));
        return prop;
    }

    public void enviarTokenPorCorreo(String correo) throws Exception {

        Usuario usuario = usuarioFacade.findByCorreo(correo);
        if (usuario == null) {
            throw new Exception("No existe una cuenta con este correo");
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expira = LocalDateTime.now().plusMinutes(30);

        usuarioFacade.actualizarTokenRecuperacion(
                usuario,
                token,
                Timestamp.valueOf(expira)
        );

        enviarCorreo(correo, token);
    }

    public void enviarCorreo(String correo, String token) throws Exception {
        // 1️⃣ Cargar propiedades
        Properties prop = cargarConfig();
        String apiKey = prop.getProperty("sendgrid.api_key").trim();
        String from = prop.getProperty("sendgrid.from");
        String baseUrl = prop.getProperty("app.reset_base_url");

        // 2️⃣ Construir URL de restablecimiento
        String urlReset = baseUrl + "?token=" + token;

        // 3️⃣ Texto del correo
        String texto = "Hola,\n\nUse este enlace para restablecer su contraseña: " + urlReset + "\n\nSi no solicitó este cambio, ignore este correo.";
        texto = texto.replace("\"", "\\\""); // escapar comillas

        // 4️⃣ Construir JSON
        String json = "{"
                + "\"personalizations\":[{\"to\":[{\"email\":\"" + correo + "\"}]}],"
                + "\"from\":{\"email\":\"" + from + "\"},"
                + "\"subject\":\"Recuperación de contraseña - CheckInc\","
                + "\"content\":[{\"type\":\"text/plain\",\"value\":\"" + texto + "\"}]"
                + "}";

        // 5️⃣ Guardar JSON UTF-8 en archivo temporal
        File tempFile = File.createTempFile("email", ".json");
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8")) {
            writer.write(json);
        }

        System.out.println("JSON para SendGrid creado en: " + tempFile.getAbsolutePath());

        // 6️⃣ Ejecutar curl
        String[] cmd = {
            "curl",
            "-s", // silent
            "-o", "-", // enviar salida a stdout
            "-w", "%{http_code}", // obtener código HTTP al final
            "-X", "POST",
            "https://api.sendgrid.com/v3/mail/send",
            "-H", "Authorization: Bearer " + apiKey,
            "-H", "Content-Type: application/json",
            "-d", "@" + tempFile.getAbsolutePath()
        };

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = pb.start();

        // Leer stdout (incluye código HTTP al final)
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        // Leer stderr
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.err.println("[CURL STDERR] " + line);
            }
        }

        int exitCode = process.waitFor();
        String result = output.toString().trim();
        System.out.println("[CURL OUTPUT] " + result);

        // Eliminar archivo temporal
        if (tempFile.exists()) {
            tempFile.delete();
        }

        // 7️⃣ Analizar código HTTP
        // El último valor de output es el http code (gracias a -w %{http_code})
        String httpCodeStr = result.length() >= 3 ? result.substring(result.length() - 3) : "000";
        int httpCode = Integer.parseInt(httpCodeStr);

        if (httpCode >= 200 && httpCode < 300) {
            System.out.println("✅ Correo enviado correctamente a " + correo);
        } else {
            throw new Exception("Error enviando correo a " + correo + ". Código HTTP: " + httpCode + ". Respuesta: " + result);
        }
    }

    public Usuario validarToken(String token) {
        Usuario u = usuarioFacade.findByToken(token);

        if (u == null) {
            return null;
        }
        if (u.getTokenExpira() == null) {
            return null;
        }

        // Convertir java.util.Date → LocalDateTime
        LocalDateTime fechaExpira = u.getTokenExpira()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        if (fechaExpira.isBefore(LocalDateTime.now())) {
            return null; // token expirado
        }

        return u;
    }

    public void actualizarPassword(Usuario usuario, String nuevaPassword) {
        usuarioFacade.actualizarPasswordRecuperacion(usuario, nuevaPassword);
    }
}
