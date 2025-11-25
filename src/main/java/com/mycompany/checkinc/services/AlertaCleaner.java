package com.mycompany.checkinc.services;

import java.util.Calendar;
import java.util.Date;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.EJB;

/**
 * Tarea programada para marcar como vistas las alertas antiguas (por defecto > 2 días).
 */
@Singleton
@Startup
public class AlertaCleaner {

    @EJB
    private AlertaFacadeLocal alertaFacade;

    // Ejecuta diariamente a las 02:00 AM
    @Schedule(hour = "2", minute = "0", second = "0", persistent = false)
    public void dailyMarkOldAlerts() {
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -2);
            Date threshold = cal.getTime();
            int updated = alertaFacade.markOlderThan(threshold);
            System.out.println("🧹 [CLEANER] Marcadas como vistas " + updated + " alertas anteriores a " + threshold);
        } catch (Exception e) {
            System.err.println("⚠️ [CLEANER] Error marcando alertas antiguas: " + e.getMessage());
        }
    }
}
