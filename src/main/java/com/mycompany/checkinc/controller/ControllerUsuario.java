/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author angel
 */
@Named(value = "controllerUsuario")
@ViewScoped
public class ControllerUsuario implements Serializable {

    Usuario users = new Usuario();
    @EJB
    UsuarioFacadeLocal cfl;

    public Usuario getUsers() {
        return users;
    }

    public void setUsers(Usuario users) {
        this.users = users;
    }
    
    public List<Usuario> obtenerUsuario(){
        try {
            return this.cfl.findAll();
        } catch (Exception e){
            
        }
        return null;
    }
    
    public ControllerUsuario() {
    }
    
}
