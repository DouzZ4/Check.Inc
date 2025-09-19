package com.mycompany.checkinc.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

public class MedicamentoApiService {
    private static final String API_URL = "https://www.datos.gov.co/resource/i7cb-raxc.json";
    private static final String APP_TOKEN = "eYzoy7NJVkqnCYmCUNWMHyNj5";
    
    private CloseableHttpClient createHttpClient() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        // Create SSL Socket Factory with our all-trusting manager
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
            sslContext,
            new String[] { "TLSv1.2" },
            null,
            NoopHostnameVerifier.INSTANCE);

        // Create a http client with our SSL settings
        return HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
    }

    public List<MedicamentoInvimaDTO> buscarPorNombreONumero(String nombre, String numeroRegistro) {
        List<MedicamentoInvimaDTO> resultados = new ArrayList<>();
        
        CloseableHttpClient httpClient = null;
        try {
            httpClient = createHttpClient();
            // Construir URL con parámetros de búsqueda
            StringBuilder urlBuilder = new StringBuilder(API_URL);
            StringBuilder queryParams = new StringBuilder();
            
            // Añadir App Token
            queryParams.append("$$app_token=").append(APP_TOKEN);
            
            // Construir la consulta
            if (nombre != null && !nombre.isEmpty() || numeroRegistro != null && !numeroRegistro.isEmpty()) {
                queryParams.append("&$where=");
                List<String> conditions = new ArrayList<>();
                
                if (nombre != null && !nombre.isEmpty()) {
                    // Buscar tanto en producto como en descripcioncomercial
                    conditions.add(String.format("lower(producto) LIKE '%%%s%%' OR lower(descripcioncomercial) LIKE '%%%s%%'", 
                        nombre.toLowerCase(), nombre.toLowerCase()));
                }
                
                if (numeroRegistro != null && !numeroRegistro.isEmpty()) {
                    conditions.add(String.format("registrosanitario='%s'", numeroRegistro));
                }
                
                queryParams.append(URLEncoder.encode(String.join(" OR ", conditions), StandardCharsets.UTF_8.toString()));
            }
            
            // Añadir límite para optimizar la respuesta
            queryParams.append("&$limit=100");
            
            // Construir URL final
            if (queryParams.length() > 0) {
                urlBuilder.append("?").append(queryParams);
            }

            // Crear y configurar la petición HTTP
            HttpGet request = new HttpGet(urlBuilder.toString());
            request.setHeader("Accept", "application/json");

            // Ejecutar la petición
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String jsonResponse = EntityUtils.toString(entity);
                        // Procesar JSON
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(jsonResponse);
                        
                        // La API de Socrata devuelve directamente un array de resultados
                        if (root.isArray()) {
                            for (JsonNode item : root) {
                                try {
                                    String regSanitario = item.path("registrosanitario").asText("");
                                    String nombreMed = item.path("producto").asText("");
                                    
                                    if (coincideConBusqueda(nombre, numeroRegistro, regSanitario, nombreMed)) {
                                        resultados.add(crearDTO(item));
                                    }
                                } catch (Exception e) {
                                    System.err.println("Error procesando item: " + e.getMessage());
                                    continue;
                                }
                            }
                        }
                    }
                } else {
                    System.err.println("Error en la API: " + status);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en la conexión: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    System.err.println("Error cerrando el cliente HTTP: " + e.getMessage());
                }
            }
        }
        
        return resultados;
    }

    private boolean coincideConBusqueda(String nombre, String numeroRegistro, String regSanitario, String nombreMed) {
        boolean coincideNombre = nombre == null || nombre.isEmpty() || 
                               (nombreMed != null && nombreMed.toLowerCase().contains(nombre.toLowerCase()));
        boolean coincideRegistro = numeroRegistro == null || numeroRegistro.isEmpty() || 
                                 (regSanitario != null && regSanitario.equalsIgnoreCase(numeroRegistro));
        return coincideNombre && coincideRegistro;
    }

    private MedicamentoInvimaDTO crearDTO(JsonNode item) {
        MedicamentoInvimaDTO dto = new MedicamentoInvimaDTO();
        dto.setNumeroRegistro(item.path("registrosanitario").asText(""));
        dto.setNombre(item.path("producto").asText(""));
        dto.setTitular(item.path("titular").asText(""));
        dto.setEstado(item.path("estadoregistro").asText(""));
        dto.setFechaVencimiento(item.path("fechavencimiento").asText(""));
        return dto;
    }

    public static class MedicamentoInvimaDTO {
        private String numeroRegistro;
        private String nombre;
        private String titular;
        private String estado;
        private String fechaVencimiento;
        // Getters y setters
        public String getNumeroRegistro() { return numeroRegistro; }
        public void setNumeroRegistro(String numeroRegistro) { this.numeroRegistro = numeroRegistro; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getTitular() { return titular; }
        public void setTitular(String titular) { this.titular = titular; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        public String getFechaVencimiento() { return fechaVencimiento; }
        public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    }
}
