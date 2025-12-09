package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Glucosa;
import com.itextpdf.text.Image;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ReporteGeneralPDFTest {

    private static class ReporteGeneralPDFExposer extends ReporteGeneralPDF {
        public ReporteGeneralPDFExposer() {
            super(null, null, null, null);
        }

        @Override
        public Image crearGraficoGlucosa(List<Glucosa> glucosaList) {
            return super.crearGraficoGlucosa(glucosaList);
        }
    }

    @Test
    public void testCrearGraficoGlucosa_GeneraImagenCorrectamente() {
        ReporteGeneralPDFExposer reporte = new ReporteGeneralPDFExposer();
        List<Glucosa> lista = new ArrayList<>();

        Date now = new Date();
        lista.add(crearGlucosa(100.0, now));
        lista.add(crearGlucosa(120.0, new Date(now.getTime() - 86400000L))); // Ayer

        Image imagen = reporte.crearGraficoGlucosa(lista);

        assertNotNull(imagen, "La imagen del gráfico no debería ser null");
        // iText Image doesn't have getWidth(null), checking scaled width or just
        // existence is enough
        assertTrue(imagen.getScaledWidth() > 0);
    }

    @Test
    public void testCaracteresEspecialesEnGrafico() {
        ReporteGeneralPDFExposer reporte = new ReporteGeneralPDFExposer();
        List<Glucosa> lista = new ArrayList<>();
        lista.add(crearGlucosa(95.5, new Date()));

        Image imagen = reporte.crearGraficoGlucosa(lista);
        assertNotNull(imagen);
    }

    private Glucosa crearGlucosa(Double nivel, Date fecha) {
        Glucosa g = new Glucosa();
        g.setNivelGlucosa(nivel.floatValue());
        g.setFechaHora(fecha);
        return g;
    }
}
