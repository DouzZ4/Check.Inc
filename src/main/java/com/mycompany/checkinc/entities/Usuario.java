/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.checkinc.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author angel
 */
@Entity
@Table(name = "usuario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findByIdUsuario", query = "SELECT u FROM Usuario u WHERE u.idUsuario = :idUsuario"),
    @NamedQuery(name = "Usuario.findByUser", query = "SELECT u FROM Usuario u WHERE u.user = :user"),
    @NamedQuery(name = "Usuario.findByPassword", query = "SELECT u FROM Usuario u WHERE u.password = :password"),
    @NamedQuery(name = "Usuario.findByDocumento", query = "SELECT u FROM Usuario u WHERE u.documento = :documento"),
    @NamedQuery(name = "Usuario.findByNombres", query = "SELECT u FROM Usuario u WHERE u.nombres = :nombres"),
    @NamedQuery(name = "Usuario.findByApellidos", query = "SELECT u FROM Usuario u WHERE u.apellidos = :apellidos"),
    @NamedQuery(name = "Usuario.findByCorreo", query = "SELECT u FROM Usuario u WHERE u.correo = :correo"),
    @NamedQuery(name = "Usuario.findByEdad", query = "SELECT u FROM Usuario u WHERE u.edad = :edad"),
    @NamedQuery(name = "Usuario.findByTipoDiabetes", query = "SELECT u FROM Usuario u WHERE u.tipoDiabetes = :tipoDiabetes"),
    @NamedQuery(name = "Usuario.findByDetalleTipoDiabetes", query = "SELECT u FROM Usuario u WHERE u.detalleTipoDiabetes = :detalleTipoDiabetes"),
    @NamedQuery(name = "Usuario.findByEsInsulodependiente", query = "SELECT u FROM Usuario u WHERE u.esInsulodependiente = :esInsulodependiente"),
    @NamedQuery(name = "Usuario.findByTelefonoEmergencia", query = "SELECT u FROM Usuario u WHERE u.telefonoEmergencia = :telefonoEmergencia"),
    @NamedQuery(name = "Usuario.findByCorreoEmergencia", query = "SELECT u FROM Usuario u WHERE u.correoEmergencia = :correoEmergencia"),
    @NamedQuery(name = "Usuario.findByNombreContactoEmergencia", query = "SELECT u FROM Usuario u WHERE u.nombreContactoEmergencia = :nombreContactoEmergencia"),
    @NamedQuery(name = "Usuario.findByParentescoContacto", query = "SELECT u FROM Usuario u WHERE u.parentescoContacto = :parentescoContacto")})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idUsuario")
    private Integer idUsuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "user")
    private String user;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "password")
    private String password;
    @Basic(optional = false)
    @NotNull
    @Column(name = "documento")
    private int documento;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "nombres")
    private String nombres;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "apellidos")
    private String apellidos;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "correo")
    private String correo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "edad")
    private int edad;
    @Size(max = 11)
    @Column(name = "tipoDiabetes")
    private String tipoDiabetes;
    @Size(max = 50)
    @Column(name = "detalleTipoDiabetes")
    private String detalleTipoDiabetes;
    @Column(name = "esInsulodependiente")
    private Boolean esInsulodependiente;
    @Size(max = 20)
    @Column(name = "telefonoEmergencia")
    private String telefonoEmergencia;
    @Size(max = 100)
    @Column(name = "correoEmergencia")
    private String correoEmergencia;
    @Size(max = 100)
    @Column(name = "nombreContactoEmergencia")
    private String nombreContactoEmergencia;
    @Size(max = 50)
    @Column(name = "parentescoContacto")
    private String parentescoContacto;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUsuario", fetch = FetchType.LAZY)
    private List<Alerta> alertaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUsuario", fetch = FetchType.LAZY)
    private List<Glucosa> glucosaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUsuario", fetch = FetchType.LAZY)
    private List<Anomalia> anomaliaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUsuario", fetch = FetchType.LAZY)
    private List<Medicamento> medicamentoList;
    @JoinColumn(name = "idRol", referencedColumnName = "idRol")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Rol idRol;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUsuario", fetch = FetchType.LAZY)
    private List<Cita> citaList;

    public Usuario() {
    }

    public Usuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Usuario(Integer idUsuario, String user, String password, int documento, String nombres, String apellidos, String correo, int edad) {
        this.idUsuario = idUsuario;
        this.user = user;
        this.password = password;
        this.documento = documento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.edad = edad;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDocumento() {
        return documento;
    }

    public void setDocumento(int documento) {
        this.documento = documento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getTipoDiabetes() {
        return tipoDiabetes;
    }

    public void setTipoDiabetes(String tipoDiabetes) {
        this.tipoDiabetes = tipoDiabetes;
    }

    public String getDetalleTipoDiabetes() {
        return detalleTipoDiabetes;
    }

    public void setDetalleTipoDiabetes(String detalleTipoDiabetes) {
        this.detalleTipoDiabetes = detalleTipoDiabetes;
    }

    public Boolean getEsInsulodependiente() {
        return esInsulodependiente;
    }

    public void setEsInsulodependiente(Boolean esInsulodependiente) {
        this.esInsulodependiente = esInsulodependiente;
    }

    public String getTelefonoEmergencia() {
        return telefonoEmergencia;
    }

    public void setTelefonoEmergencia(String telefonoEmergencia) {
        this.telefonoEmergencia = telefonoEmergencia;
    }

    public String getCorreoEmergencia() {
        return correoEmergencia;
    }

    public void setCorreoEmergencia(String correoEmergencia) {
        this.correoEmergencia = correoEmergencia;
    }

    public String getNombreContactoEmergencia() {
        return nombreContactoEmergencia;
    }

    public void setNombreContactoEmergencia(String nombreContactoEmergencia) {
        this.nombreContactoEmergencia = nombreContactoEmergencia;
    }

    public String getParentescoContacto() {
        return parentescoContacto;
    }

    public void setParentescoContacto(String parentescoContacto) {
        this.parentescoContacto = parentescoContacto;
    }

    @XmlTransient
    public List<Alerta> getAlertaList() {
        return alertaList;
    }

    public void setAlertaList(List<Alerta> alertaList) {
        this.alertaList = alertaList;
    }

    @XmlTransient
    public List<Glucosa> getGlucosaList() {
        return glucosaList;
    }

    public void setGlucosaList(List<Glucosa> glucosaList) {
        this.glucosaList = glucosaList;
    }

    @XmlTransient
    public List<Anomalia> getAnomaliaList() {
        return anomaliaList;
    }

    public void setAnomaliaList(List<Anomalia> anomaliaList) {
        this.anomaliaList = anomaliaList;
    }

    @XmlTransient
    public List<Medicamento> getMedicamentoList() {
        return medicamentoList;
    }

    public void setMedicamentoList(List<Medicamento> medicamentoList) {
        this.medicamentoList = medicamentoList;
    }

    public Rol getIdRol() {
        return idRol;
    }

    public void setIdRol(Rol idRol) {
        this.idRol = idRol;
    }

    @XmlTransient
    public List<Cita> getCitaList() {
        return citaList;
    }

    public void setCitaList(List<Cita> citaList) {
        this.citaList = citaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUsuario != null ? idUsuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.idUsuario == null && other.idUsuario != null) || (this.idUsuario != null && !this.idUsuario.equals(other.idUsuario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.checkinc.entities.Usuario[ idUsuario=" + idUsuario + " ]";
    }
    
}
