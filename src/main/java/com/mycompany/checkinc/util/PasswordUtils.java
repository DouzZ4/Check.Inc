package com.mycompany.checkinc.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    
    private PasswordUtils() {
        // Constructor privado para clase de utilidad
    }
    
    /**
     * Verifica si una contraseña coincide con su hash
     * @param password La contraseña en texto plano
     * @param hashedPassword El hash de la contraseña almacenado
     * @return true si la contraseña coincide, false en caso contrario
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        if (hashedPassword == null || password == null) {
            return false;
        }
        
        // Si la contraseña está hasheada con bcrypt
        if (hashedPassword.startsWith("$2y$") || hashedPassword.startsWith("$2a$")) {
            try {
                // Convertir hash de PHP a formato compatible con jBCrypt
                String jbcryptHash = hashedPassword.replace("$2y$", "$2a$");
                return BCrypt.checkpw(password, jbcryptHash);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        
        // Si la contraseña no está hasheada, comparación directa
        return hashedPassword.equals(password);
    }
    
    /**
     * Genera un hash bcrypt para una contraseña
     * @param password La contraseña a hashear
     * @return El hash de la contraseña
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }
}
