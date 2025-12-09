package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Cita;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class RecordatorioCitaTimer {

    @EJB
    private CitaFacadeLocal citaFacade;

    @EJB
    private ServicioCorreo servicioCorreo;

    // Se ejecuta todos los dias a las 8:00 AM
    @Schedule(hour = "8", minute = "0", second = "0", persistent = false)
    public void verificarCitasManana() {
        System.out.println("⏰ [TIMER] Iniciando verificación de citas para mañana...");
        try {
            List<Cita> citas = citaFacade.findCitasManana();
            if (citas != null && !citas.isEmpty()) {
                System.out.println("📅 Se encontraron " + citas.size() + " citas para mañana.");
                for (Cita cita : citas) {
                    servicioCorreo.enviarRecordatorioCita(cita);
                }
            } else {
                System.out.println("ℹ️ No hay citas programadas para mañana.");
            }
        } catch (Exception e) {
            System.err.println("❌ [TIMER ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }
}
