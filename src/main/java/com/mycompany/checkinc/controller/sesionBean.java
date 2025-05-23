package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.model.Usuario;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean(name = "sesionBean")
@SessionScoped
public class sesionBean implements Serializable {

    private Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String cerrarSesion() {
        usuario = null;
        return "index?faces-redirect=true";
    }
}
