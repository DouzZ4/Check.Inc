package com.mycompany.checkinc.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.checkinc.entities.Glucosa;
import okhttp3.*;

import javax.ejb.Stateless;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private static final String ML_SERVICE_URL = "https://checkinc-ml-service-production.up.railway.app";
    // Para desarrollo local: http://localhost:8000

    private static final String API_PREFIX = "/api/v1";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public ServicioPrediccionML() {
        // Configurar cliente HTTP con TLS robusto para Java 8
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS); // Predicciones pueden tardar

        // Configurar SSLContext con máxima compatibilidad
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            if (trustManagers.length == 0 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("No se encontró X509TrustManager");
            }

            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            // Intentar TLS 1.3 primero, luego 1.2 como fallback
            SSLContext sslContext = null;
            String[] protocolosIntentados = { "TLSv1.3", "TLSv1.2", "TLS" };

            for (String protocolo : protocolosIntentados) {
                try {
                    sslContext = SSLContext.getInstance(protocolo);
                    sslContext.init(null, new TrustManager[] { trustManager }, new SecureRandom());
                    LOGGER.log(Level.INFO, "SSL/TLS configurado con protocolo: {0}", protocolo);
                    break;
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, "Protocolo {0} no disponible, intentando siguiente...", protocolo);
                }
            }

            if (sslContext == null) {
                sslContext = SSLContext.getDefault();
                LOGGER.log(Level.WARNING, "Usando SSLContext por defecto");
            }

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory, trustManager);

            // Configurar specs de conexión más permisivas para máxima compatibilidad
            ConnectionSpec modernTLS = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .allEnabledTlsVersions()
                    .allEnabledCipherSuites()
                    .build();

            ConnectionSpec compatibleTLS = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                    .allEnabledTlsVersions()
                    .allEnabledCipherSuites()
                    .build();

            // Permitir también conexiones sin cifrar para desarrollo local
            ConnectionSpec cleartext = new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build();

            builder.connectionSpecs(Arrays.asList(modernTLS, compatibleTLS, cleartext));

            LOGGER.log(Level.INFO, "Cliente OkHttp configurado correctamente para HTTPS");

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo configurar TLS personalizado, usando defaults: " + e.getMessage());
        }

        this.client = builder.build();

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
     * Sincroniza múltiples lecturas de glucosa en una sola petición (Batch Sync).
     * Ideal para carga inicial de historial.
     */
    public boolean sincronizarLecturasMasivas(java.util.List<Glucosa> lecturas) {
        if (lecturas == null || lecturas.isEmpty())
            return true;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            StringBuilder jsonArray = new StringBuilder("[");

            for (int i = 0; i < lecturas.size(); i++) {
                Glucosa g = lecturas.get(i);
                String timestamp = sdf.format(g.getFechaHora());

                // Construir objeto registro simple compatible con GlucoseReadingCreate
                String item = String.format(
                        "{\"user_id\": %d, \"glucose_level\": %.2f, \"timestamp\": \"%s\", \"moment_of_day\": \"%s\"}",
                        g.getIdUsuario().getIdUsuario(),
                        g.getNivelGlucosa(),
                        timestamp,
                        g.getMomentoDia() != null ? g.getMomentoDia() : "");

                jsonArray.append(item);
                if (i < lecturas.size() - 1)
                    jsonArray.append(",");
            }
            jsonArray.append("]");

            String jsonBody = "{\"readings\": " + jsonArray.toString() + "}";

            RequestBody body = RequestBody.create(jsonBody, JSON);
            Request request = new Request.Builder()
                    .url(ML_SERVICE_URL + API_PREFIX + "/sync/batch")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    LOGGER.log(Level.INFO, "Sincronización masiva exitosa: {0} registros", lecturas.size());
                    return true;
                } else {
                    LOGGER.log(Level.WARNING, "Error en batch sync. Status: {0}, Body: {1}",
                            new Object[] { response.code(), response.body() != null ? response.body().string() : "" });
                    return false;
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error de conexión en batch sync", e);
            return false;
        }
    }

    /**
     * Solicita al microservicio que entrene el modelo inmediatamente para un
     * usuario.
     */
    public boolean entrenarModelo(int idUsuario) {
        try {
            // El endpoint espera POST pero sin body obligatorio o con user_id query param
            // Según nuestra API Python: POST /sync/train-model?user_id=X

            Request request = new Request.Builder()
                    .url(ML_SERVICE_URL + API_PREFIX + "/sync/train-model?user_id=" + idUsuario)
                    .post(RequestBody.create(new byte[0], null)) // Empty body
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error solicitando entrenamiento", e);
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
