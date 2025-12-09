package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Alerta;
import com.mycompany.checkinc.services.AlertaFacadeLocal;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "alertaBean")
@RequestScoped
public class AlertaBean implements Serializable {

    @EJB
    private AlertaFacadeLocal alertaFacade;

    private List<Alerta> alertas;

    @PostConstruct
    public void init() {
        // Por defecto cargamos todas, pero idealmente deberíamos cargar solo las del
        // usuario en sesión si no es ADMIN
        // Para este ejemplo simple, intentaremos obtener el usuario de la sesión
        javax.faces.context.FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        if (context != null) {
            Object usuarioObj = context.getExternalContext().getSessionMap().get("usuario");
            if (usuarioObj instanceof com.mycompany.checkinc.entities.Usuario) {
                com.mycompany.checkinc.entities.Usuario u = (com.mycompany.checkinc.entities.Usuario) usuarioObj;
                // Asumiendo que agregamos un findByUsuario en AlertaFacade, si no existe,
                // filtramos en memoria
                // Para hacerlo rápido, filtraremos en memoria o usaremos una query nueva si es
                // posible.
                // Dado que no puedo editar AlertaFacade ahora mismo facil, cargo todas y
                // filtro.
                List<Alerta> todas = alertaFacade.findAll();
                alertas = new java.util.ArrayList<>();
                for (Alerta a : todas) {
                    if (a.getIdUsuario() != null && a.getIdUsuario().equals(u)) {
                        alertas.add(a);
                    }
                }
                // Ordenar por fecha descendente
                java.util.Collections.sort(alertas, new java.util.Comparator<Alerta>() {
                    @Override
                    public int compare(Alerta o1, Alerta o2) {
                        return o2.getFechaHora().compareTo(o1.getFechaHora());
                    }
                });
                return;
            }
        }
        alertas = alertaFacade.findAll();
    }

    public void marcarVisto(Alerta a) {
        try {
            if (a != null) {
                a.setVisto(Boolean.TRUE);
                alertaFacade.update(a);
                // Recargar
                init();
            }
        } catch (Exception e) {
            System.err.println("⚠️ [WARN] Error marcando alerta como vista: " + e.getMessage());
        }
    }

    public List<Alerta> getAlertas() {
        return alertas;
    }

    // ========== MÉTODOS DE ESTADÍSTICAS ==========

    public int getTotalAlertas() {
        return alertas != null ? alertas.size() : 0;
    }

    public int getAlertasPendientes() {
        if (alertas == null)
            return 0;
        int count = 0;
        for (Alerta a : alertas) {
            if (a.getVisto() == null || !a.getVisto()) {
                count++;
            }
        }
        return count;
    }

    public int getAlertasGlucosa() {
        if (alertas == null)
            return 0;
        int count = 0;
        for (Alerta a : alertas) {
            if ("ALERTA_GLUCOSA".equals(a.getTipo())) {
                count++;
            }
        }
        return count;
    }

    public int getAlertasAnomalia() {
        if (alertas == null)
            return 0;
        int count = 0;
        for (Alerta a : alertas) {
            if ("ALERTA_ANOMALIA".equals(a.getTipo())) {
                count++;
            }
        }
        return count;
    }

    // ========== FORMATEO DE CONTENIDO ==========

    /**
     * Formatea el contenido de la alerta para mejor visualización.
     * Extrae email, códigos HTTP y mensajes de forma amigable.
     */
    public String formatearContenido(String contenido) {
        if (contenido == null || contenido.trim().isEmpty()) {
            return "<span style='color:#94a3b8;font-style:italic;'>Sin detalles</span>";
        }

        StringBuilder html = new StringBuilder();

        // Extraer email si existe
        if (contenido.contains("Envio a:")) {
            String email = extraerValor(contenido, "Envio a:", "|");
            if (email != null && !email.isEmpty()) {
                html.append("<div class='alert-detail-email'>");
                html.append("<i class='pi pi-envelope'></i>");
                html.append("<span>").append(escapeHtml(email.trim())).append("</span>");
                html.append("</div>");
            }
        }

        // Extraer código HTTP y mostrar mensaje amigable
        String codigo = extraerValor(contenido, "Code=", "|");
        if (codigo != null && !codigo.isEmpty()) {
            String codigoTrim = codigo.trim();
            boolean esExito = codigoTrim.startsWith("2"); // 2xx = éxito

            // Mensaje amigable según el código
            String mensajeEstado;
            String iconoClase;
            if (esExito) {
                mensajeEstado = "Entregado";
                iconoClase = "pi-check-circle";
            } else if (codigoTrim.startsWith("4")) {
                mensajeEstado = "Error de envío";
                iconoClase = "pi-times-circle";
            } else if (codigoTrim.startsWith("5")) {
                mensajeEstado = "Error del servidor";
                iconoClase = "pi-exclamation-circle";
            } else {
                mensajeEstado = "Pendiente";
                iconoClase = "pi-clock";
            }

            html.append("<span class='alert-detail-code ").append(esExito ? "code-success" : "code-error").append("'>");
            html.append("<i class='pi ").append(iconoClase).append("'></i>");
            html.append(" ").append(mensajeEstado);
            html.append("</span> ");
        }

        // Mostrar intentos si existen (solo si hubo error)
        String intentos = extraerValor(contenido, "Attempts=", "|");
        if (intentos != null && !intentos.isEmpty()) {
            String codigoCheck = extraerValor(contenido, "Code=", "|");
            boolean mostrarIntentos = codigoCheck == null || !codigoCheck.trim().startsWith("2");
            if (mostrarIntentos) {
                html.append("<span style='color:#64748b;font-size:0.8rem;margin-left:8px;'>");
                html.append("(Intento ").append(escapeHtml(intentos.trim())).append(")");
                html.append("</span>");
            }
        }

        // Si no se pudo parsear nada, mostrar texto simple
        if (html.length() == 0) {
            // Limpiar y mostrar texto resumido
            String textoLimpio = contenido.replaceAll("\\s+", " ").trim();
            if (textoLimpio.length() > 150) {
                textoLimpio = textoLimpio.substring(0, 147) + "...";
            }
            html.append(escapeHtml(textoLimpio));
        }

        return html.toString();
    }

    private String extraerValor(String texto, String inicio, String fin) {
        try {
            int startIdx = texto.indexOf(inicio);
            if (startIdx == -1)
                return null;
            startIdx += inicio.length();

            int endIdx = texto.indexOf(fin, startIdx);
            if (endIdx == -1) {
                // Si no hay fin, tomar hasta el final o un máximo
                endIdx = Math.min(startIdx + 100, texto.length());
            }

            return texto.substring(startIdx, endIdx);
        } catch (Exception e) {
            return null;
        }
    }

    private String escapeHtml(String text) {
        if (text == null)
            return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
