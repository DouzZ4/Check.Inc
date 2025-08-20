package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.entities.Rol;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "registroUsuario")
@RequestScoped
public class RegistroUsuario implements Serializable {

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    // Atributos del formulario
    private String nombres;
    private String apellidos;
    private Integer edad;
    private String correo;
    private String username;
    private String documento;
    private String password;
    private String tipoDiabetes; // Valor del select (Tipo 1, Tipo 2, Gestacional, Otro)
    private String tipoDiabetesOtro; // Solo si selecciona 'Otro'
    private String detalleTipoDiabetes; // Para guardar en la entidad
    private Boolean esInsulodependiente;

    public String registrar() {
        try {
            // Validación: usuario existente
            if (usuarioFacade.findByUser(username) != null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario ya existe", "El nombre de usuario ya está registrado"));
                return null;
            }

            // Validación: documento existente
            try {
                int docIntCheck = Integer.parseInt(documento);
                if (usuarioFacade.findByDocumento(docIntCheck) != null) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Documento ya existe", "El número de documento ya está registrado"));
                    return null;
                }
            } catch (NumberFormatException e) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Documento inválido", "El documento debe ser numérico"));
                return null;
            }

            // Validación: nombres y apellidos (solo letras y espacios)
            if (nombres == null || !nombres.matches("[A-Za-zÀ-ÿ\\s]+")) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nombres inválidos", "Ingrese solo letras y espacios para nombres"));
                return null;
            }
            if (apellidos == null || !apellidos.matches("[A-Za-zÀ-ÿ\\s]+")) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Apellidos inválidos", "Ingrese solo letras y espacios para apellidos"));
                return null;
            }

            // Validación: edad
            if (edad == null || edad < 1 || edad > 120) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Edad inválida", "Ingrese una edad válida entre 1 y 120"));
                return null;
            }

            // Validación: correo
            if (correo == null || !correo.matches(".+@.+\\.com$")) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Correo inválido", "Ingrese un correo electrónico válido que termine en .com"));
                return null;
            }

            // Validación: username
            if (username == null || username.length() < 5) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario inválido", "El nombre de usuario debe tener al menos 5 caracteres"));
                return null;
            }

            // Validación: documento (solo números, 6-20 dígitos)
            if (documento == null || !documento.matches("[0-9]{6,20}")) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Documento inválido", "El documento debe tener entre 6 y 20 dígitos"));
                return null;
            }
            int docInt;
            try {
                docInt = Integer.parseInt(documento);
            } catch (NumberFormatException e) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Documento inválido", "El documento debe ser numérico"));
                return null;
            }

            // Validación: contraseña (mínimo 8, mayúscula, minúscula, símbolo)
            if (password == null || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}$")) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contraseña inválida", "La contraseña debe tener al menos 8 caracteres, incluyendo mayúscula, minúscula y símbolo especial"));
                return null;
            }

            // Validación: tipoDiabetes
            String tipoDiabetesFinal = null;
            String detalleTipoDiabetesFinal = null;
            if ("Otro".equals(tipoDiabetes)) {
                if (tipoDiabetesOtro == null || tipoDiabetesOtro.trim().isEmpty() || tipoDiabetesOtro.length() > 50) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tipo de diabetes inválido", "Si selecciona 'Otro', debe especificar el tipo (máximo 50 caracteres)"));
                    return null;
                }
                tipoDiabetesFinal = "Otro";
                detalleTipoDiabetesFinal = tipoDiabetesOtro.trim();
            } else if (tipoDiabetes != null && !tipoDiabetes.isEmpty()) {
                tipoDiabetesFinal = tipoDiabetes;
                detalleTipoDiabetesFinal = null;
            }

            // Validación: esInsulodependiente (no nulo)
            if (esInsulodependiente == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo requerido", "Debe indicar si es insulodependiente"));
                return null;
            }

            Usuario nuevo = new Usuario();
            nuevo.setNombres(nombres);
            nuevo.setApellidos(apellidos);
            nuevo.setEdad(edad);
            nuevo.setCorreo(correo);
            nuevo.setUser(username);
            nuevo.setDocumento(docInt);
            nuevo.setPassword(password);
            nuevo.setTipoDiabetes(tipoDiabetesFinal);
            nuevo.setDetalleTipoDiabetes(detalleTipoDiabetesFinal);
            nuevo.setEsInsulodependiente(esInsulodependiente);
            nuevo.setIdRol(new Rol(2)); // Rol 2: paciente/usuario

            usuarioFacade.create(nuevo);

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro exitoso", "El usuario ha sido registrado correctamente"));

            return "/views/usuarios/login.xhtml?faces-redirect=true";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en el registro", e.getMessage()));
            return null;
        }
    }


    // Getters y Setters completos
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTipoDiabetes() { return tipoDiabetes; }
    public void setTipoDiabetes(String tipoDiabetes) { this.tipoDiabetes = tipoDiabetes; }
    public String getTipoDiabetesOtro() { return tipoDiabetesOtro; }
    public void setTipoDiabetesOtro(String tipoDiabetesOtro) { this.tipoDiabetesOtro = tipoDiabetesOtro; }
    public String getDetalleTipoDiabetes() { return detalleTipoDiabetes; }
    public void setDetalleTipoDiabetes(String detalleTipoDiabetes) { this.detalleTipoDiabetes = detalleTipoDiabetes; }

    public Boolean getEsInsulodependiente() { return esInsulodependiente; }
    public void setEsInsulodependiente(Boolean esInsulodependiente) { this.esInsulodependiente = esInsulodependiente; }

    // Método para validación AJAX: verifica si el documento ya existe
    public boolean existeDocumento() {
        if (documento == null || documento.isEmpty()) {
            return false;
        }
        try {
            int docInt = Integer.parseInt(documento);
            Usuario u = usuarioFacade.findByDocumento(docInt);
            return u != null;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

