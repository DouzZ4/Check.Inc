package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Glucosa;
import com.mycompany.checkinc.entities.Usuario;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ImportarGlucosaService {

    private static final Logger logger = Logger.getLogger(ImportarGlucosaService.class.getName());

    @PersistenceContext(unitName = "com.mycompany_CheckInc_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public ImportResult importarCSV(InputStream fileStream, Integer idUsuario) {
        ImportResult result = new ImportResult();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, "UTF-8"))) {
            
            // Buscar el usuario
            Usuario usuario = em.find(Usuario.class, idUsuario);
            if (usuario == null) {
                result.setError("Usuario no encontrado con ID: " + idUsuario);
                logger.log(Level.WARNING, "Usuario no encontrado: {0}", idUsuario);
                return result;
            }

            logger.log(Level.INFO, "Usuario encontrado: ID {0}", usuario.getIdUsuario());

            // Crear parser CSV
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            List<Glucosa> registrosValidos = new ArrayList<>();
            List<String> errores = new ArrayList<>();

            int lineNumber = 1;
            for (CSVRecord csvRecord : csvParser) {
                lineNumber++;
                try {
                    Glucosa glucosa = parseGlucosaFromCSV(csvRecord, usuario, dateFormat);
                    if (glucosa != null) {
                        registrosValidos.add(glucosa);
                        logger.log(Level.FINE, "Registro válido línea {0}: {1}", new Object[]{lineNumber, glucosa.getNivelGlucosa()});
                    }
                } catch (Exception e) {
                    String errorMsg = "Línea " + lineNumber + ": " + e.getMessage();
                    errores.add(errorMsg);
                    logger.log(Level.WARNING, errorMsg);
                }
            }

            csvParser.close();

            // Persistir registros válidos
            if (!registrosValidos.isEmpty()) {
                for (Glucosa glucosa : registrosValidos) {
                    em.persist(glucosa);
                }
                em.flush(); // Forzar la escritura en la base de datos
                logger.log(Level.INFO, "Persistidos {0} registros", registrosValidos.size());
            }

            result.setRegistrosImportados(registrosValidos.size());
            result.setErrores(errores);
            result.setExitoso(true);

            logger.log(Level.INFO, "Importación completada: {0} registros, {1} errores",
                    new Object[]{registrosValidos.size(), errores.size()});

        } catch (Exception e) {
            String errorMsg = "Error procesando archivo: " + e.getMessage();
            result.setError(errorMsg);
            logger.log(Level.SEVERE, errorMsg, e);
        }

        return result;
    }

    private Glucosa parseGlucosaFromCSV(CSVRecord record, Usuario usuario, SimpleDateFormat dateFormat) {
        try {
            // Validar campos requeridos - usar nombres de columna en minúscula
            String nivelStr = record.get("nivel");
            String fechaStr = record.get("fecha");
            String momento = record.get("momento");

            // Log para debugging
            logger.log(Level.FINE, "Procesando: nivel={0}, fecha={1}, momento={2}", 
                      new Object[]{nivelStr, fechaStr, momento});

            if (nivelStr == null || nivelStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Campo 'nivel' está vacío");
            }
            if (fechaStr == null || fechaStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Campo 'fecha' está vacío");
            }
            if (momento == null || momento.trim().isEmpty()) {
                throw new IllegalArgumentException("Campo 'momento' está vacío");
            }

            // Parsear nivel de glucosa
            float nivel;
            try {
                nivel = Float.parseFloat(nivelStr.trim().replace(",", "."));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Nivel no es numérico: " + nivelStr);
            }
            
            // Validar rango razonable de glucosa
            if (nivel < 20 || nivel > 1000) {
                throw new IllegalArgumentException("Nivel de glucosa fuera de rango (20-1000): " + nivel);
            }

            // Parsear fecha (formato: yyyy-MM-dd)
            Date fechaHora;
            try {
                fechaHora = dateFormat.parse(fechaStr.trim());
            } catch (Exception e) {
                throw new IllegalArgumentException("Formato de fecha inválido. Use AAAA-MM-DD: " + fechaStr);
            }

            // Crear entidad Glucosa
            Glucosa glucosa = new Glucosa();
            glucosa.setNivelGlucosa(nivel);
            glucosa.setFechaHora(fechaHora);
            glucosa.setMomentoDia(momento.trim());
            glucosa.setIdUsuario(usuario);

            return glucosa;

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    // Clase para manejar resultados de importación
    public static class ImportResult {
        private boolean exitoso;
        private int registrosImportados;
        private List<String> errores = new ArrayList<>();
        private String error;

        // Getters y Setters
        public boolean isExitoso() { return exitoso; }
        public void setExitoso(boolean exitoso) { this.exitoso = exitoso; }
        
        public int getRegistrosImportados() { return registrosImportados; }
        public void setRegistrosImportados(int registrosImportados) { this.registrosImportados = registrosImportados; }
        
        public List<String> getErrores() { return errores; }
        public void setErrores(List<String> errores) { this.errores = errores; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public String getMensajeResumen() {
            if (!exitoso) {
                return error != null ? error : "Error en la importación";
            }
            if (errores.isEmpty()) {
                return String.format("✅ Importación completada exitosamente: %d registros importados.", registrosImportados);
            } else {
                return String.format("⚠️ Importación completada: %d registros importados, %d errores.", registrosImportados, errores.size());
            }
        }
    }
}