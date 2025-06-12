/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mycompany.checkinc.controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author angel
 */
@Named(value = "login")
@SessionScoped
public class Login implements Serializable {
    private String username;
    private String password;
    private boolean loggedIn;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String iniciarSesion() {
        // Simulación de autenticación
        if ("admin".equals(username) && "1234".equals(password)) {
            loggedIn = true;
            return "/views/dashboard?faces-redirect=true"; // Redirige al dashboard
        } else {
            loggedIn = false;
            return null; // No redirige, el usuario debe intentar de nuevo
        }
    }

    public String cerrarSesion() {
        loggedIn = false;
        username = null;
        password = null;
        return "/index?faces-redirect=true"; // Redirige al inicio
    }
}
