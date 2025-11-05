package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.services.ImportarGlucosaService;
import com.mycompany.checkinc.entities.Usuario;
import org.primefaces.model.file.UploadedFile;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class ImportarGlucosaBean implements Serializable {

    private static final Logger logger = Logger.getLogger(ImportarGlucosaBean.class.getName());

    @Inject
    private ImportarGlucosaService importarGlucosaService;

    private UploadedFile file;
    private String mensaje;
    private boolean mostrarResultado;
    private int registrosImportados;
    private int totalErrores;

    public void importar() {
        logger.log(Level.INFO, "=== INICIANDO IMPORTACIÓN ===");
        
        if (file == null) {
            mensaje = "❌ Por favor seleccione un archivo CSV";
            logger.log(Level.WARNING, "Archivo no seleccionado");
            return;
        }

        if (file.getContent().length == 0) {
            mensaje = "❌ El archivo seleccionado está vacío";
            logger.log(Level.WARNING, "Archivo vacío");
            return;
        }

        // Obtener ID de usuario automáticamente desde la sesión
        Integer idUsuario = obtenerIdUsuarioDesdeSesion();
        if (idUsuario == null) {
            mensaje = "❌ No hay usuario autenticado. Por favor inicie sesión.";
            logger.log(Level.WARNING, "Usuario no autenticado");
            return;
        }

        logger.log(Level.INFO, "Procesando archivo: {0} ({1} bytes), Usuario ID: {2}", 
                  new Object[]{file.getFileName(), file.getContent().length, idUsuario});

        try {
            ImportarGlucosaService.ImportResult result = importarGlucosaService
                    .importarCSV(file.getInputStream(), idUsuario);

            mostrarResultado = true;
            registrosImportados = result.getRegistrosImportados();
            totalErrores = result.getErrores().size();
            
            mensaje = result.getMensajeResumen();

            // Log detallado de errores
            if (!result.getErrores().isEmpty()) {
                logger.log(Level.WARNING, "Errores encontrados en importación:");
                for (String error : result.getErrores()) {
                    logger.log(Level.WARNING, " - {0}", error);
                }
            }

            logger.log(Level.INFO, "=== IMPORTACIÓN FINALIZADA: {0} ===", mensaje);

        } catch (Exception e) {
            mensaje = "❌ Error al procesar el archivo: " + e.getMessage();
            logger.log(Level.SEVERE, "Error en importación", e);
        }
    }

    private Integer obtenerIdUsuarioDesdeSesion() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext != null) {
                HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
                
                if (session != null) {
                    // Intenta obtener el usuario de diferentes maneras
                    Object usuario = session.getAttribute("usuario");
                    if (usuario instanceof Usuario) {
                        return ((Usuario) usuario).getIdUsuario();
                    }
                    
                    // O busca el ID directamente
                    Object usuarioId = session.getAttribute("usuarioId");
                    if (usuarioId != null) {
                        return Integer.parseInt(usuarioId.toString());
                    }
                    
                    // O busca en atributos comunes de sesión
                    Object id = session.getAttribute("idUsuario");
                    if (id != null) {
                        return Integer.parseInt(id.toString());
                    }
                    
                    // Último intento: busca cualquier atributo que contenga "usuario"
                    java.util.Enumeration<String> attributeNames = session.getAttributeNames();
                    while (attributeNames.hasMoreElements()) {
                        String attributeName = attributeNames.nextElement();
                        if (attributeName.toLowerCase().contains("usuario") || 
                            attributeName.toLowerCase().contains("user")) {
                            Object attrValue = session.getAttribute(attributeName);
                            if (attrValue instanceof Usuario) {
                                return ((Usuario) attrValue).getIdUsuario();
                            } else if (attrValue instanceof Integer) {
                                return (Integer) attrValue;
                            } else if (attrValue instanceof String) {
                                try {
                                    return Integer.parseInt((String) attrValue);
                                } catch (NumberFormatException e) {
                                    // Continuar buscando
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error obteniendo ID de usuario de sesión", e);
        }
        
        logger.log(Level.WARNING, "No se pudo obtener el ID de usuario de la sesión");
        return null;
    }

    public void limpiar() {
        file = null;
        mensaje = null;
        mostrarResultado = false;
        registrosImportados = 0;
        totalErrores = 0;
        logger.log(Level.INFO, "Formulario limpiado");
    }

    // Getters y Setters
    public UploadedFile getFile() { return file; }
    public void setFile(UploadedFile file) { this.file = file; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public boolean isMostrarResultado() { return mostrarResultado; }
    public int getRegistrosImportados() { return registrosImportados; }
    public int getTotalErrores() { return totalErrores; }
}