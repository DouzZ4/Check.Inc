package com.mycompany.checkinc.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entidad NivelesGlucosa: almacena rangos normales de glucosa por tipo de diabetes
 * y permite personalizaciones por usuario.
 * 
 * Basada en estándares ADA (American Diabetes Association) y OMS.
 */
@Entity
@Table(name = "nivelesGlucosa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NivelesGlucosa.findAll", query = "SELECT n FROM NivelesGlucosa n"),
    @NamedQuery(name = "NivelesGlucosa.findByIdNivelGlucosa", query = "SELECT n FROM NivelesGlucosa n WHERE n.idNivelGlucosa = :idNivelGlucosa"),
    @NamedQuery(name = "NivelesGlucosa.findByIdUsuario", query = "SELECT n FROM NivelesGlucosa n WHERE n.idUsuario = :idUsuario"),
    @NamedQuery(name = "NivelesGlucosa.findByTipoDiabetes", query = "SELECT n FROM NivelesGlucosa n WHERE n.tipoDiabetes = :tipoDiabetes AND n.idUsuario IS NULL"),
    @NamedQuery(name = "NivelesGlucosa.findByTipoDiabedesAndActivo", query = "SELECT n FROM NivelesGlucosa n WHERE n.tipoDiabetes = :tipoDiabetes AND n.activo = TRUE"),
    @NamedQuery(name = "NivelesGlucosa.findActivoByUsuario", query = "SELECT n FROM NivelesGlucosa n WHERE n.idUsuario = :idUsuario AND n.activo = TRUE")
})
public class NivelesGlucosa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idNivelGlucosa")
    private Integer idNivelGlucosa;

    @Column(name = "tipoDiabetes", length = 50)
    private String tipoDiabetes;

    @Basic(optional = false)
    @Column(name = "nivelMinimo")
    private Float nivelMinimo;

    @Basic(optional = false)
    @Column(name = "nivelMaximo")
    private Float nivelMaximo;

    @Basic(optional = false)
    @Column(name = "nivelBajoCritico")
    private Float nivelBajoCritico;

    @Basic(optional = false)
    @Column(name = "nivelAltoCritico")
    private Float nivelAltoCritico;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "fechaCreacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "fechaActualizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;

    @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario")
    @ManyToOne
    private Usuario idUsuario;

    public NivelesGlucosa() {
    }

    public NivelesGlucosa(Integer idNivelGlucosa) {
        this.idNivelGlucosa = idNivelGlucosa;
    }

    public NivelesGlucosa(Float nivelMinimo, Float nivelMaximo, Float nivelBajoCritico, Float nivelAltoCritico) {
        this.nivelMinimo = nivelMinimo;
        this.nivelMaximo = nivelMaximo;
        this.nivelBajoCritico = nivelBajoCritico;
        this.nivelAltoCritico = nivelAltoCritico;
    }

    public Integer getIdNivelGlucosa() {
        return idNivelGlucosa;
    }

    public void setIdNivelGlucosa(Integer idNivelGlucosa) {
        this.idNivelGlucosa = idNivelGlucosa;
    }

    public String getTipoDiabetes() {
        return tipoDiabetes;
    }

    public void setTipoDiabetes(String tipoDiabetes) {
        this.tipoDiabetes = tipoDiabetes;
    }

    public Float getNivelMinimo() {
        return nivelMinimo;
    }

    public void setNivelMinimo(Float nivelMinimo) {
        this.nivelMinimo = nivelMinimo;
    }

    public Float getNivelMaximo() {
        return nivelMaximo;
    }

    public void setNivelMaximo(Float nivelMaximo) {
        this.nivelMaximo = nivelMaximo;
    }

    public Float getNivelBajoCritico() {
        return nivelBajoCritico;
    }

    public void setNivelBajoCritico(Float nivelBajoCritico) {
        this.nivelBajoCritico = nivelBajoCritico;
    }

    public Float getNivelAltoCritico() {
        return nivelAltoCritico;
    }

    public void setNivelAltoCritico(Float nivelAltoCritico) {
        this.nivelAltoCritico = nivelAltoCritico;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
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
        hash += (idNivelGlucosa != null ? idNivelGlucosa.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof NivelesGlucosa)) {
            return false;
        }
        NivelesGlucosa other = (NivelesGlucosa) object;
        if ((this.idNivelGlucosa == null && other.idNivelGlucosa != null) || 
            (this.idNivelGlucosa != null && !this.idNivelGlucosa.equals(other.idNivelGlucosa))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NivelesGlucosa[ idNivelGlucosa=" + idNivelGlucosa + " ]";
    }

}
