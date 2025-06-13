/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.checkinc.entities;

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
import javax.persistence.Lob;
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
@Table(name = "anomalia")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Anomalia.findAll", query = "SELECT a FROM Anomalia a"),
    @NamedQuery(name = "Anomalia.findByIdAnomalia", query = "SELECT a FROM Anomalia a WHERE a.idAnomalia = :idAnomalia"),
    @NamedQuery(name = "Anomalia.findByFechaHora", query = "SELECT a FROM Anomalia a WHERE a.fechaHora = :fechaHora")})
public class Anomalia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idAnomalia")
    private Integer idAnomalia;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fechaHora")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaHora;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "sintomas")
    private String sintomas;
    @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario idUsuario;

    public Anomalia() {
    }

    public Anomalia(Integer idAnomalia) {
        this.idAnomalia = idAnomalia;
    }

    public Anomalia(Integer idAnomalia, String descripcion, Date fechaHora, String sintomas) {
        this.idAnomalia = idAnomalia;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
        this.sintomas = sintomas;
    }

    public Integer getIdAnomalia() {
        return idAnomalia;
    }

    public void setIdAnomalia(Integer idAnomalia) {
        this.idAnomalia = idAnomalia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
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
        hash += (idAnomalia != null ? idAnomalia.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Anomalia)) {
            return false;
        }
        Anomalia other = (Anomalia) object;
        if ((this.idAnomalia == null && other.idAnomalia != null) || (this.idAnomalia != null && !this.idAnomalia.equals(other.idAnomalia))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.checkinc.entities.Anomalia[ idAnomalia=" + idAnomalia + " ]";
    }
    
}
