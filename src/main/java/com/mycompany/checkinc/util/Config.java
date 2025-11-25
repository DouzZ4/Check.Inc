package com.mycompany.checkinc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Properties properties = new Properties();

    static {
        try {
            // 1️⃣ Primero intenta leer desde la raíz del proyecto (modo NetBeans local)
            File externalFile = new File("config.properties");
            InputStream input;

            if (externalFile.exists()) {
                input = new FileInputStream(externalFile);
                System.out.println("✅ Cargando config.properties desde la raíz del proyecto.");
            } else {
                // 2️⃣ Si no existe, intenta cargarlo desde los recursos del WAR
                input = Config.class.getClassLoader().getResourceAsStream("config.properties");
                if (input != null) {
                    System.out.println("✅ Cargando config.properties desde dentro del WAR.");
                } else {
                    System.err.println("⚠️ No se encontró config.properties en ninguna ubicación.");
                    input = null; // No se encontró el archivo, no se carga nada.
                }
            }

            if (input != null) {
                properties.load(input);
                input.close();
            }
        } catch (IOException e) {
            System.err.println("❌ Error al cargar config.properties: " + e.getMessage());
        }
    }

    /**
     * Obtiene la configuración buscando (en este orden):
     *  1) Variable de entorno
     *  2) System property
     *  3) config.properties
     * Devuelve null si no existe.
     */
    public static String get(String key) {
        // 1) Environment variable
        try {
            String env = System.getenv(key);
            if (env != null && !env.trim().isEmpty()) {
                return env.trim();
            }
        } catch (SecurityException se) {
            // En entornos restringidos puede lanzar excepción
        }

        // 2) System property (-Dkey=value)
        String sys = System.getProperty(key);
        if (sys != null && !sys.trim().isEmpty()) {
            return sys.trim();
        }

        // 3) config.properties file
        String prop = properties.getProperty(key);
        if (prop != null && !prop.trim().isEmpty()) {
            return prop.trim();
        }

        // No encontrado
        return null;
    }
}
