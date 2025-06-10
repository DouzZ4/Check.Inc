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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author davidalonso
 */
@Entity
@Table(name = "glucosa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Glucosa.findAll", query = "SELECT g FROM Glucosa g"),
    @NamedQuery(name = "Glucosa.findByIdGlucosa", query = "SELECT g FROM Glucosa g WHERE g.idGlucosa = :idGlucosa"),
    @NamedQuery(name = "Glucosa.findByNivelGlucosa", query = "SELECT g FROM Glucosa g WHERE g.nivelGlucosa = :nivelGlucosa"),
    @NamedQuery(name = "Glucosa.findByFechaHora", query = "SELECT g FROM Glucosa g WHERE g.fechaHora = :fechaHora")})
public class Glucosa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idGlucosa")
    private Integer idGlucosa;
    @Basic(optional = false)
    @NotNull
    @Column(name = "nivelGlucosa")
    private float nivelGlucosa;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fechaHora")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaHora;
    @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario idUsuario;

    public Glucosa() {
    }

    public Glucosa(Integer idGlucosa) {
        this.idGlucosa = idGlucosa;
    }

    public Glucosa(Integer idGlucosa, float nivelGlucosa, Date fechaHora) {
        this.idGlucosa = idGlucosa;
        this.nivelGlucosa = nivelGlucosa;
        this.fechaHora = fechaHora;
    }

    public Integer getIdGlucosa() {
        return idGlucosa;
    }

    public void setIdGlucosa(Integer idGlucosa) {
        this.idGlucosa = idGlucosa;
    }

    public float getNivelGlucosa() {
        return nivelGlucosa;
    }

    public void setNivelGlucosa(float nivelGlucosa) {
        this.nivelGlucosa = nivelGlucosa;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
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
        hash += (idGlucosa != null ? idGlucosa.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Glucosa)) {
            return false;
        }
        Glucosa other = (Glucosa) object;
        if ((this.idGlucosa == null && other.idGlucosa != null) || (this.idGlucosa != null && !this.idGlucosa.equals(other.idGlucosa))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sena.check_harold.entities.Glucosa[ idGlucosa=" + idGlucosa + " ]";
    }
    
}
