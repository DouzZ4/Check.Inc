/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sena.check_harold.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author davidalonso
 */
@Entity
@Table(name = "medicamento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Medicamento.findAll", query = "SELECT m FROM Medicamento m"),
    @NamedQuery(name = "Medicamento.findByIdMedicamento", query = "SELECT m FROM Medicamento m WHERE m.idMedicamento = :idMedicamento"),
    @NamedQuery(name = "Medicamento.findByNombre", query = "SELECT m FROM Medicamento m WHERE m.nombre = :nombre"),
    @NamedQuery(name = "Medicamento.findByDosis", query = "SELECT m FROM Medicamento m WHERE m.dosis = :dosis"),
    @NamedQuery(name = "Medicamento.findByFrecuencia", query = "SELECT m FROM Medicamento m WHERE m.frecuencia = :frecuencia"),
    @NamedQuery(name = "Medicamento.findByFechaInicio", query = "SELECT m FROM Medicamento m WHERE m.fechaInicio = :fechaInicio"),
    @NamedQuery(name = "Medicamento.findByFechaFin", query = "SELECT m FROM Medicamento m WHERE m.fechaFin = :fechaFin")})
public class Medicamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idMedicamento")
    private Integer idMedicamento;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "dosis")
    private String dosis;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "frecuencia")
    private String frecuencia;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fechaInicio")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fechaFin")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
    @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario idUsuario;

    public Medicamento() {
    }

    public Medicamento(Integer idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public Medicamento(Integer idMedicamento, String nombre, String dosis, String frecuencia, Date fechaInicio, Date fechaFin) {
        this.idMedicamento = idMedicamento;
        this.nombre = nombre;
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public Integer getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(Integer idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMedicamento != null ? idMedicamento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Medicamento)) {
            return false;
        }
        Medicamento other = (Medicamento) object;
        if ((this.idMedicamento == null && other.idMedicamento != null) || (this.idMedicamento != null && !this.idMedicamento.equals(other.idMedicamento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sena.check_harold.entities.Medicamento[ idMedicamento=" + idMedicamento + " ]";
    }
    
}
