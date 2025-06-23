package com.mycompany.checkinc.controller;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "registroUsuario")
@RequestScoped
public class RegistroUsuario implements Serializable {
    private String nombres;
    private String apellidos;
    private int edad;
    private String correo;
    private String username;
    private String documento;
    private String password;
    
    // Getters y Setters
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    // Método para registrar usuario
    public String registrar() {
        // TODO: Implementar la lógica de registro con la base de datos
        
        // Por ahora solo mostraremos un mensaje de éxito
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, 
                           "Registro exitoso", 
                           "El usuario ha sido registrado correctamente"));
        
        return "login?faces-redirect=true";
    }
}
