package com.mycompany.checkinc.services;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class MedicamentoApiService {
    private static final String API_URL = "https://www.datos.gov.co/resource/i7cb-raxc.json";
    private static final String APP_TOKEN = "-QkxAuASMPxzY8No3t9zsDIEhZjwVh47H3qY";

    private Client createJerseyClient() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        return ClientBuilder.newBuilder()
                .sslContext(sslContext)
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }

    public List<MedicamentoInvimaDTO> buscarPorNombreONumero(String nombre, String numeroRegistro) {
        List<MedicamentoInvimaDTO> resultados = new ArrayList<>();
        try {
            Client client = createJerseyClient();
            WebTarget target = client.target(API_URL)
                .queryParam("$$app_token", APP_TOKEN);

            if (nombre != null && !nombre.isEmpty()) {
                target = target.queryParam("$where", "lower(producto) LIKE '%" + nombre.toLowerCase() + "%' OR lower(descripcioncomercial) LIKE '%" + nombre.toLowerCase() + "%'");
            }

            if (numeroRegistro != null && !numeroRegistro.isEmpty()) {
                target = target.queryParam("$where", "registrosanitario='" + numeroRegistro + "'");
            }

            target = target.queryParam("$limit", 100);

            Response response = target.request(MediaType.APPLICATION_JSON).get();
            if (response.getStatus() == 200) {
                String jsonResponse = response.readEntity(String.class);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(jsonResponse);

                if (root.isArray()) {
                    for (JsonNode item : root) {
                        resultados.add(crearDTO(item));
                    }
                }
            } else {
                System.err.println("Error en la API: " + response.getStatus());
            }
        } catch (Exception e) {
            System.err.println("Error en la conexión: " + e.getMessage());
            e.printStackTrace();
        }
        return resultados;
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
