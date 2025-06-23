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
@Table(name = "recordatorio")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Recordatorio.findAll", query = "SELECT r FROM Recordatorio r"),
    @NamedQuery(name = "Recordatorio.findByIdRecordatorio", query = "SELECT r FROM Recordatorio r WHERE r.idRecordatorio = :idRecordatorio"),
    @NamedQuery(name = "Recordatorio.findByTipo", query = "SELECT r FROM Recordatorio r WHERE r.tipo = :tipo"),
    @NamedQuery(name = "Recordatorio.findByFechaHora", query = "SELECT r FROM Recordatorio r WHERE r.fechaHora = :fechaHora")})
public class Recordatorio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idRecordatorio")
    private Integer idRecordatorio;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "tipo")
    private String tipo;
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
    @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario idUsuario;

    public Recordatorio() {
    }

    public Recordatorio(Integer idRecordatorio) {
        this.idRecordatorio = idRecordatorio;
    }

    public Recordatorio(Integer idRecordatorio, String tipo, String descripcion, Date fechaHora) {
        this.idRecordatorio = idRecordatorio;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
    }

    public Integer getIdRecordatorio() {
        return idRecordatorio;
    }

    public void setIdRecordatorio(Integer idRecordatorio) {
        this.idRecordatorio = idRecordatorio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRecordatorio != null ? idRecordatorio.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Recordatorio)) {
            return false;
        }
        Recordatorio other = (Recordatorio) object;
        if ((this.idRecordatorio == null && other.idRecordatorio != null) || (this.idRecordatorio != null && !this.idRecordatorio.equals(other.idRecordatorio))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.checkinc.entities.Recordatorio[ idRecordatorio=" + idRecordatorio + " ]";
    }
    
}
