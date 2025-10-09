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

    public static String get(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            System.err.println("⚠️ No se encontró la clave: " + key);
        }
        return value;
    }
}
