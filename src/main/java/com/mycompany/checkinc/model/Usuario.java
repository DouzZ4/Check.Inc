package com.mycompany.checkinc.model;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String nombre;

    // Constructor vacío
    public Usuario() {}

    // Getter y Setter
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
