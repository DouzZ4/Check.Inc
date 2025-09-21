package com.mycompany.checkinc.services;

// DatosGovApiService.java
import com.mycompany.checkinc.entities.MedicamentoInvima;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

// Importaciones de la librería Jackson
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


public class DatosGovApiService {

    // 1. Instancia estática privada (corazón del Singleton)
    private static final DatosGovApiService INSTANCE = new DatosGovApiService();

    private final String API_BASE_URL = "https://www.datos.gov.co/api/v3/views/i7cb-raxc/query.json";
    private final String APP_TOKEN = "-QkxAuASMPxzY8No3t9zsDIEhZjwVh47H3qY"; // ¡IMPORTANTE! Reemplaza esto

    // 2. Constructor privado para evitar que se creen nuevas instancias
    private DatosGovApiService() {
    }

    // 3. Método público estático para obtener la única instancia
    public static DatosGovApiService getInstance() {
        return INSTANCE;
    }

    /**
     * Busca medicamentos en la API usando un término de búsqueda.
     * @param terminoBusqueda El texto para buscar en el campo 'producto'.
     * @return Una lista de objetos Medicamento.
     * @throws Exception Si ocurre un error en la conexión o procesamiento.
     */
    public List<MedicamentoInvima> buscarMedicamentos(String terminoBusqueda) throws Exception {
        // La documentación de Socrata usa SoQL para las consultas.
        // Aquí construimos una consulta simple para buscar en el campo 'producto'.
        String soqlQuery = "SELECT * WHERE lower(producto) LIKE '%" + terminoBusqueda.toLowerCase() + "%' LIMIT 50";
        String encodedQuery = URLEncoder.encode(soqlQuery, "UTF-8");

        URL url = new URL(API_BASE_URL + "?$query=" + encodedQuery);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        
        // Configurar la petición
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("X-App-Token", APP_TOKEN); // Autenticación

        int status = con.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("Fallo la petición HTTP, código de error: " + status);
        }

        // Leer la respuesta
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        // Usar Jackson para convertir el JSON en una lista de objetos Java
        ObjectMapper mapper = new ObjectMapper();
        List<MedicamentoInvima> medicamentosInvima = mapper.readValue(content.toString(), new TypeReference<List<MedicamentoInvima>>(){});

        return medicamentosInvima;
    }
}