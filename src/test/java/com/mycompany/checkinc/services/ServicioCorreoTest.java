package com.mycompany.checkinc.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.IOException;

public class ServicioCorreoTest {

    private MockWebServer server;

    @BeforeEach
    public void setup() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void enviarCorreoAnomalia_returnsTrue_andRegistersAlert() throws Exception {
        // Arrange: enqueue success response
        server.enqueue(new MockResponse().setResponseCode(202).setBody("Accepted"));

        ServicioCorreo servicio = new ServicioCorreo();
        servicio.setSendgridApiKey("testkey");
        servicio.setSendgridBaseUrl(server.url("/mail/send").toString());
        servicio.setHttpClient(new OkHttpClient.Builder().build());

        // inject mock AlertaFacade
        AlertaFacadeLocal mockFacade = Mockito.mock(AlertaFacadeLocal.class);
        servicio.setAlertaFacade(mockFacade);

        // Act
        boolean result = servicio.enviarCorreoAnomalia("test@example.com", "Prueba", "Mensaje de prueba");

        // Assert
        assertTrue(result, "El envío debe devolver true para 202");
        Mockito.verify(mockFacade, Mockito.atLeastOnce()).create(Mockito.any());
    }
}
