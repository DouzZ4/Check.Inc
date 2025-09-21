package com.mycompany.checkinc.entities;

// Medicamento.java
// Ojo: Los nombres de las variables deben coincidir con los nombres de las claves en el JSON
// o usar anotaciones si usas una librería como Jackson.
public class MedicamentoInvima {

    private String expediente;
    private String producto;
    private String titular;
    private String descripcioncomercial;
    // Agrega aquí otros campos que necesites de la API

    // Getters y Setters
    public String getExpediente() {
        return expediente;
    }

    public void setExpediente(String expediente) {
        this.expediente = expediente;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }
    
    public String getDescripcioncomercial() {
        return descripcioncomercial;
    }

    public void setDescripcioncomercial(String descripcioncomercial) {
        this.descripcioncomercial = descripcioncomercial;
    }
}