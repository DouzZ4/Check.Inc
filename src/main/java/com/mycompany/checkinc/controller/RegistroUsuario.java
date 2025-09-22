package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.entities.Rol;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import com.mycompany.checkinc.services.ServicioCorreo;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@ManagedBean(name = "registroUsuario")
@SessionScoped
public class RegistroUsuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    @EJB
    private ServicioCorreo servicioCorreo;

    // Atributos del formulario
    private String nombres;
    private String apellidos;
    private Integer edad;
    private String correo;
    private String username;
    private String documento;
    private String password;
    private String tipoDiabetes;
    private String tipoDiabetesOtro;
    private String detalleTipoDiabetes;
    private Boolean esInsulodependiente;

    public RegistroUsuario() {
        System.out.println("BEAN RegistroUsuario INICIALIZADO");
    }

    public String registrar() {
        try {
            // Validación simple
            if (usuarioFacade.findByUser(username) != null) {
                mostrarError("Usuario ya existe", "El nombre de usuario ya está registrado");
                return null;
            }

            // Crear usuario
            Usuario nuevo = new Usuario();
            nuevo.setNombres(nombres);
            nuevo.setApellidos(apellidos);
            nuevo.setEdad(edad);
            nuevo.setCorreo(correo);
            nuevo.setUser(username);
            nuevo.setDocumento(Integer.parseInt(documento));
            nuevo.setPassword(password);
            nuevo.setTipoDiabetes("Otro".equals(tipoDiabetes) ? tipoDiabetesOtro : tipoDiabetes);
            nuevo.setDetalleTipoDiabetes(detalleTipoDiabetes);
            nuevo.setEsInsulodependiente(esInsulodependiente);
            nuevo.setIdRol(new Rol(2));

            usuarioFacade.create(nuevo);
            System.out.println("Usuario creado en la base de datos");

            // ENVIAR CORREO USANDO EL SERVICIO
            boolean correoEnviado = servicioCorreo.enviarCorreoRegistro(
                correo, 
                nombres, 
                apellidos, 
                username, 
                edad, 
                "Otro".equals(tipoDiabetes) ? tipoDiabetesOtro : tipoDiabetes, 
                esInsulodependiente
            );

            if (correoEnviado) {
                mostrarInfo("Registro exitoso", "Usuario registrado y correo enviado exitosamente");
            } else {
                mostrarInfo("Registro exitoso", "Usuario registrado correctamente");
            }

            limpiarFormulario();
            return "/views/usuarios/login.xhtml?faces-redirect=true";

        } catch (Exception e) {
            mostrarError("Error", "Error en registro: " + e.getMessage());
            return null;
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, titulo, mensaje));
    }

    private void mostrarInfo(String titulo, String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, titulo, mensaje));
    }

    private void limpiarFormulario() {
        this.nombres = null;
        this.apellidos = null;
        this.edad = null;
        this.correo = null;
        this.username = null;
        this.documento = null;
        this.password = null;
        this.tipoDiabetes = null;
        this.tipoDiabetesOtro = null;
        this.detalleTipoDiabetes = null;
        this.esInsulodependiente = null;
    }

    // Getters y Setters
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
}