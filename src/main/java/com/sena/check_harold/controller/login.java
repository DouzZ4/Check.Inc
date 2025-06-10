/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.sena.check_harold.controller;

import com.sena.check_harold.entities.Usuario;
import com.sena.check_harold.services.UsuarioFacadeLocal;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author davidalonso
 */
@Named(value = "login")
@SessionScoped
public class login implements Serializable {

    private static final long serialVersionUID = 1L;

    private String usuario;
    private String contrasenna;
    private Usuario user = new Usuario();
    @EJB
    private UsuarioFacadeLocal ufl;

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenna() {
        return contrasenna;
    }

    public void setContrasenna(String contrasenna) {
        this.contrasenna = contrasenna;
    }
    
    public String iniciarSesion(){
        this.user = this.ufl.iniciarSesion(usuario, contrasenna);
        if(user.getIdUsuario()!=null){
            HttpSession sesion = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            sesion.setAttribute("usuario", usuario);
            return "inicio.xhtml?faces-redirect=true";
        }else {
            FacesContext fc = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Usuario y/o contraseña incorrectos","MSG_INFO");
            fc.addMessage(null, fm);
            return null;
        }
    }
            
    public login() {
    }
    
}
