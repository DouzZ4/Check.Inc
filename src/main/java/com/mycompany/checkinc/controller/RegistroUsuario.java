package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.entities.Rol;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import com.mycompany.checkinc.util.PasswordUtils;
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
    private int edad;
    private String correo;
    private String username;
    private int documento;
    private String password;

    // Getters y setters omitidos por brevedad...

    public String registrar() {
    try {
        if (usuarioFacade.findByUser(username) != null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario ya existe", "El nombre de usuario ya está registrado"));
            return null;
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombres(nombres);
        nuevo.setApellidos(apellidos);
        nuevo.setEdad(edad);
        nuevo.setCorreo(correo);
        nuevo.setUser(username);
        nuevo.setDocumento(documento);
        nuevo.setPassword(PasswordUtils.hashPassword(password)); // Aquí se aplica el hash
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

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getDocumento() { return documento; }
    public void setDocumento(int documento) { this.documento = documento; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

