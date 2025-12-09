package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.entities.Glucosa;
import com.mycompany.checkinc.util.Config;
import org.junit.jupiter.api.Disabled;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PruebaCompletaEnvioTest {

    private final String TARGET_EMAIL_1 = "angeleduardomen@hotmail.com";
    private final String TARGET_EMAIL_2 = "angeleduardomen@gmail.com";

    @Disabled("Esperando configuración de API Key")
    @Test
    public void testModuloCompletoCorreos() {
        System.out.println("🚀 [INICIO] Prueba completa del módulo de correos");

        // 0. Verificar API Key
        String apiKey = Config.get("SENDGRID_API_KEY");
        if (apiKey == null || apiKey.length() < 10) {
            throw new RuntimeException("❌ ERROR: No se encontró la API Key en config.properties");
        }
        System.out.println("🔑 API Key detectada correctamente.");

        ServicioCorreo servicio = new ServicioCorreo();

        // 1. Prueba de Envío Masivo (Comunicado)
        System.out.println("\n--- 1. Probando Envío Masivo ---");
        List<String> externos = Arrays.asList(TARGET_EMAIL_1, TARGET_EMAIL_2);
        boolean resMasivo = servicio.enviarComunicadoMasivo(
                "Prueba Masiva CheckInc",
                "Este es un correo de prueba del módulo de envío masivo.",
                Collections.emptyList(),
                externos);
        if (resMasivo)
            System.out.println("✅ Envío Masivo: EXITOSO");
        else
            System.err.println("❌ Envío Masivo: FALLIDO");

        // 2. Prueba de Alerta de Glucosa
        System.out.println("\n--- 2. Probando Alerta de Glucosa ---");
        Usuario u = new Usuario();
        u.setCorreo(TARGET_EMAIL_1);
        u.setNombres("Angel");
        u.setApellidos("Eduardo");
        u.setTipoDiabetes("Tipo 1");

        Glucosa g = new Glucosa();
        g.setNivelGlucosa(350.5f);
        g.setFechaHora(new Date());

        // Simular alerta CRITICA ALTA
        boolean resAlerta = servicio.enviarAlertaGlucosaHTML(
                u, g,
                "CRITICO_ALTO",
                "70-140 mg/dL",
                "Acudir a urgencias inmediatamente.");
        if (resAlerta)
            System.out.println("✅ Alerta Glucosa: EXITOSA");
        else
            System.err.println("❌ Alerta Glucosa: FALLIDA");

        // 3. Prueba de Correo de Registro
        System.out.println("\n--- 3. Probando Correo de Registro ---");
        boolean resRegistro = servicio.enviarCorreoRegistro(
                TARGET_EMAIL_1,
                "Angel",
                "Eduardo",
                "angelUser",
                30,
                "Tipo 2",
                true);
        if (resRegistro)
            System.out.println("✅ Correo Registro: EXITOSO");
        else
            System.err.println("❌ Correo Registro: FALLIDO");

        System.out.println("\n🏁 [FIN] Pruebas finalizadas.");

        if (!resMasivo || !resAlerta || !resRegistro) {
            throw new RuntimeException("Al menos una prueba falló.");
        }
    }
}
