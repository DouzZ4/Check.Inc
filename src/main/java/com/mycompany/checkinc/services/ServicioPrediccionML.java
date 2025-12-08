package com.mycompany.checkinc.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.checkinc.entities.Glucosa;
import okhttp3.*;

import javax.ejb.Stateless;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para comunicación con el microservicio Python de Machine Learning.
 * Utiliza OkHttp para realizar peticiones HTTP REST.
 * 
 * @author Check.Inc Team
 */
@Stateless
public class ServicioPrediccionML {

    private static final Logger LOGGER = Logger.getLogger(ServicioPrediccionML.class.getName());

    // URL del microservicio Python (cambiar según environment)
    private static final String ML_SERVICE_URL = "https://checkinc-ml-service.onrender.com";
    // Para desarrollo local: http://localhost:8000

    private static final String API_PREFIX = "/api/v1";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public ServicioPrediccionML() {
        // Configurar cliente HTTP con timeouts apropiados
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS) // Predicciones pueden tardar
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.setTimeZone(TimeZone.getDefault());
    }

    /**
     * Sincroniza una lectura de glucosa con el microservicio Python.
     * Llamar después de guardar cada nueva lectura en la BD local.
     * 
     * @param glucosa Lectura de glucosa a sincronizar
     * @return true si la sincronización fue exitosa
     */
    public boolean sincronizarLecturaGlucosa(Glucosa glucosa) {
        try {
            // Formatear fecha en ISO 8601
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String timestamp = sdf.format(glucosa.getFechaHora());

            // Construir JSON request
            String json = String.format(
                    "{\"user_id\": %d, \"glucose_level\": %.2f, \"timestamp\": \"%s\", \"moment_of_day\": \"%s\"}",
                    glucosa.getIdUsuario().getIdUsuario(),
                    glucosa.getNivelGlucosa(),
                    timestamp,
                    glucosa.getMomentoDia() != null ? glucosa.getMomentoDia() : "");

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(ML_SERVICE_URL + API_PREFIX + "/sync/reading")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    LOGGER.log(Level.INFO, "Glucosa sincronizada con ML service: ID {0}", glucosa.getIdGlucosa());
                    return true;
                } else {
                    LOGGER.log(Level.WARNING, "Error al sincronizar glucosa. Status: {0}", response.code());
                    return false;
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error de conexión con ML service", e);
            return false;
        }
    }

    /**
     * Obtiene predicciones de niveles de glucosa para las próximas horas.
     * 
     * @param idUsuario     ID del usuario
     * @param horasAdelante Número de horas a predecir (1-24)
     * @return JSON con predicciones o null si hay error
     */
    public JsonNode obtenerPredicciones(int idUsuario, int horasAdelante) {
        try {
            String json = String.format(
                    "{\"user_id\": %d, \"hours_ahead\": %d}",
                    idUsuario, horasAdelante);

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(ML_SERVICE_URL + API_PREFIX + "/predictions/next-hours")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return objectMapper.readTree(responseBody);
                } else {
                    LOGGER.log(Level.WARNING, "Error obteniendo predicciones. Status: {0}", response.code());
                    return null;
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener predicciones", e);
            return null;
        }
    }

    /**
     * Evalúa el nivel de riesgo actual del usuario basado en lecturas recientes.
     * 
     * @param idUsuario ID del usuario
     * @return JSON con evaluación de riesgo o null si hay error
     */
    public JsonNode evaluarRiesgo(int idUsuario) {
        try {
            String json = String.format("{\"user_id\": %d}", idUsuario);

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(ML_SERVICE_URL + API_PREFIX + "/predictions/risk-assessment")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return objectMapper.readTree(responseBody);
                } else {
                    LOGGER.log(Level.WARNING, "Error evaluando riesgo. Status: {0}", response.code());
                    return null;
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al evaluar riesgo", e);
            return null;
        }
    }

    /**
     * Obtiene recomendaciones personalizadas para el usuario.
     * 
     * @param idUsuario ID del usuario
     * @return JSON con recomendaciones o null si hay error
     */
    public JsonNode obtenerRecomendaciones(int idUsuario) {
        try {
            Request request = new Request.Builder()
                    .url(ML_SERVICE_URL + API_PREFIX + "/predictions/recommendations/" + idUsuario)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return objectMapper.readTree(responseBody);
                } else {
                    LOGGER.log(Level.WARNING, "Error obteniendo recomendaciones. Status: {0}", response.code());
                    return null;
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener recomendaciones", e);
            return null;
        }
    }

    /**
     * Verifica si el servicio ML está disponible.
     * 
     * @return true si el servicio responde
     */
    public boolean verificarDisponibilidad() {
        try {
            Request request = new Request.Builder()
                    .url(ML_SERVICE_URL + "/health")
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            }

        } catch (IOException e) {
            return false;
        }
    }
}
