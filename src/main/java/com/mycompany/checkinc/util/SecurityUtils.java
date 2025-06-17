package com.mycompany.checkinc.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecurityUtils {
    private static final Logger logger = Logger.getLogger(SecurityUtils.class.getName());
    private static final int SALT_LENGTH = 16;
    
    private SecurityUtils() {
        // Constructor privado para utilidad
    }
    
    /**
     * Genera un hash seguro de la contraseña usando SHA-256 y un salt aleatorio
     * @param password La contraseña a hashear
     * @return String con formato "salt:hash" o null si hay error
     */
    public static String hashPassword(String password) {
        try {
            // Generar salt aleatorio
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Crear hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());
            
            // Convertir salt y hash a Base64 para almacenamiento
            String saltStr = Base64.getEncoder().encodeToString(salt);
            String hashStr = Base64.getEncoder().encodeToString(hash);
            
            // Retornar en formato "salt:hash"
            return saltStr + ":" + hashStr;
            
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "Error al generar hash de contraseña", e);
            return null;
        }
    }
    
    /**
     * Verifica si una contraseña coincide con su hash almacenado
     * @param password La contraseña a verificar
     * @param storedHash El hash almacenado en formato "salt:hash"
     * @return true si la contraseña coincide, false en caso contrario
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Separar salt y hash
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash = Base64.getDecoder().decode(parts[1]);
            
            // Generar hash de la contraseña proporcionada
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] newHash = md.digest(password.getBytes());
            
            // Comparar hashes
            return MessageDigest.isEqual(newHash, hash);
            
        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Error al verificar contraseña", e);
            return false;
        }
    }
    
    /**
     * Sanitiza la entrada del usuario para prevenir XSS y otros ataques
     * @param input El texto a sanitizar
     * @return El texto sanitizado
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Eliminar caracteres potencialmente peligrosos
        return input.replaceAll("[<>&'\"]", "");
    }
    
    /**
     * Verifica si una cadena contiene caracteres especiales peligrosos
     * @param input La cadena a verificar
     * @return true si la cadena es segura, false si contiene caracteres peligrosos
     */
    public static boolean isSafeInput(String input) {
        if (input == null) {
            return true;
        }
        
        // Verificar caracteres no permitidos
        return !input.matches(".*[<>&'\"].*");
    }
}
