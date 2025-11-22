package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.NivelesGlucosa;
import com.mycompany.checkinc.entities.Usuario;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Facade para la entidad NivelesGlucosa.
 * Proporciona métodos para gestionar los rangos de glucosa por usuario y tipo de diabetes.
 */
@Stateless
public class NivelesGlucosaFacade extends AbstractFacade<NivelesGlucosa> implements NivelesGlucosaFacadeLocal {

    @PersistenceContext(unitName = "com.mycompany_CheckInc_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NivelesGlucosaFacade() {
        super(NivelesGlucosa.class);
    }

    /**
     * Obtiene los niveles de glucosa personalizados del usuario.
     * Si no existen, retorna null (se usarán valores por defecto del sistema).
     */
    public NivelesGlucosa findByUsuario(Usuario usuario) {
        try {
            Query q = em.createNamedQuery("NivelesGlucosa.findActivoByUsuario");
            q.setParameter("idUsuario", usuario);
            List<NivelesGlucosa> resultados = q.getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtiene los niveles de glucosa por defecto del sistema para un tipo de diabetes.
     * Ejemplo: "Tipo 1", "Tipo 2", "Gestacional"
     */
    public NivelesGlucosa findByTipoDiabetes(String tipoDiabetes) {
        try {
            Query q = em.createNamedQuery("NivelesGlucosa.findByTipoDiabedesAndActivo");
            q.setParameter("tipoDiabetes", tipoDiabetes);
            List<NivelesGlucosa> resultados = q.getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtiene los niveles por defecto para usuarios sin tipo de diabetes especificado.
     */
    public NivelesGlucosa findDefaultNiveles() {
        return findByTipoDiabetes("No especificado");
    }

    /**
     * Determina el estado de un nivel de glucosa respecto a los rangos del usuario.
     * Retorna: "NORMAL", "BAJO", "ALTO", "CRITICO_BAJO", "CRITICO_ALTO"
     */
    public String determinarEstadoGlucosa(Float nivelGlucosa, Usuario usuario) {
        if (nivelGlucosa == null) {
            return "DESCONOCIDO";
        }

        // Intentar obtener niveles personalizados del usuario
        NivelesGlucosa niveles = findByUsuario(usuario);

        // Si no existen, usar niveles por defecto del tipo de diabetes
        if (niveles == null && usuario.getTipoDiabetes() != null) {
            niveles = findByTipoDiabetes(usuario.getTipoDiabetes());
        }

        // Si sigue siendo null, usar niveles genéricos
        if (niveles == null) {
            niveles = findDefaultNiveles();
        }

        // Si aún así es null, usar umbrales duros como fallback
        if (niveles == null) {
            niveles = new NivelesGlucosa();
            niveles.setNivelMinimo(80.0f);
            niveles.setNivelMaximo(130.0f);
            niveles.setNivelBajoCritico(70.0f);
            niveles.setNivelAltoCritico(250.0f);
        }

        // Evaluar el estado
        if (nivelGlucosa < niveles.getNivelBajoCritico()) {
            return "CRITICO_BAJO";
        } else if (nivelGlucosa < niveles.getNivelMinimo()) {
            return "BAJO";
        } else if (nivelGlucosa <= niveles.getNivelMaximo()) {
            return "NORMAL";
        } else if (nivelGlucosa <= niveles.getNivelAltoCritico()) {
            return "ALTO";
        } else {
            return "CRITICO_ALTO";
        }
    }

    /**
     * Retorna el rango de glucosa para mostrar al usuario (ej: "80-130 mg/dL").
     */
    public String obtenerRangoTexto(Usuario usuario) {
        NivelesGlucosa niveles = findByUsuario(usuario);

        if (niveles == null && usuario.getTipoDiabetes() != null) {
            niveles = findByTipoDiabetes(usuario.getTipoDiabetes());
        }

        if (niveles == null) {
            niveles = findDefaultNiveles();
        }

        if (niveles == null) {
            return "80-130 mg/dL";
        }

        return Math.round(niveles.getNivelMinimo()) + "-" + Math.round(niveles.getNivelMaximo()) + " mg/dL";
    }

    /**
     * Obtiene una recomendación texto basada en el estado de glucosa.
     */
    public String obtenerRecomendacion(String estado) {
        switch (estado) {
            case "CRITICO_BAJO":
                return "⚠️  HIPOGLUCEMIA SEVERA - Consume azúcar rápido (jugo, caramelo). Busca atención médica inmediatamente.";
            case "BAJO":
                return "📍 Nivel bajo. Consume algo con carbohidratos (fruta, jugo). Monitorea en 15 minutos.";
            case "NORMAL":
                return "✅ Nivel normal. Mantén tu rutina de cuidados.";
            case "ALTO":
                return "📍 Nivel elevado. Aumenta actividad física, bebe agua. Revisa tu medicación.";
            case "CRITICO_ALTO":
                return "⚠️  HIPERGLUCEMIA SEVERA - Busca atención médica. Monitorea cetona si tienes Tipo 1.";
            default:
                return "Monitorea tu nivel de glucosa.";
        }
    }

}
