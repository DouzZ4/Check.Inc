/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Medicamento;
import com.mycompany.checkinc.services.MedicamentoFacadeLocal;
import com.mycompany.checkinc.services.UsuarioFacadeLocal;
import com.mycompany.checkinc.entities.Usuario;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author davidalonso
 */
@ManagedBean(name = "controllerMedicamento")
@ViewScoped
public class controllerMedicamento implements Serializable {

    private static final long serialVersionUID = 1L;

    Medicamento med = new Medicamento();
    @EJB
    MedicamentoFacadeLocal mfl;
    @EJB
    UsuarioFacadeLocal ufl;

    private boolean editando = false;

    public Medicamento getMed() {
        return med;
    }

    public void setMed(Medicamento med) {
        this.med = med;
    }

    public boolean isEditando() {
        return editando;
    }

    public void setEditando(boolean editando) {
        this.editando = editando;
    }

    public List<Usuario> getUsuarios() {
        try {
            return ufl.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Medicamento> obtenerMedicamento() {
        try {
            return this.mfl.findAll();
        } catch (Exception e) {
            System.err.println("Error al obtener medicamentos: " + e.getMessage());
        }
        return java.util.Collections.emptyList();
    }

    public controllerMedicamento() {
    }

    public String crearMedicamentoP1() {
        med = new Medicamento();
        return "/views/registros/Medicamentos/crearMedicamentos.xhtml?faces-redirect=true";
    }

    public void crearMedicamentoP2() {

    }

    public void registrar() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (editando) {
                if (med.getIdMedicamento() == null) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se puede actualizar: ID nulo", null));
                    System.err.println("Intento de actualizar medicamento sin ID");
                    return;
                }
                System.out.println("Actualizando medicamento: " + med);
                mfl.edit(med);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Medicamento actualizado", null));
            } else {
                // Asignar usuario autenticado si aplica
                HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
                Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
                if (usuarioSesion != null) {
                    med.setIdUsuario(usuarioSesion);
                }
                System.out.println("Registrando nuevo medicamento: " + med);
                mfl.create(med);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Medicamento registrado", null));
            }
            med = new Medicamento();
            editando = false;
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar", e.getMessage()));
            System.err.println("Error al guardar medicamento: " + e.getMessage());
        }
    }

    public void editar(Medicamento m) {
        // Copia profunda para evitar problemas de referencia y asegurar que el ID se mantenga
        Medicamento copia = new Medicamento();
        copia.setIdMedicamento(m.getIdMedicamento());
        copia.setNombre(m.getNombre());
        copia.setDosis(m.getDosis());
        copia.setFrecuencia(m.getFrecuencia());
        copia.setFechaInicio(m.getFechaInicio());
        copia.setFechaFin(m.getFechaFin());
        copia.setIdUsuario(m.getIdUsuario());
        this.med = copia;
        this.editando = true;
    }

    public void eliminar(Medicamento m) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            mfl.remove(m);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Medicamento eliminado", null));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar", e.getMessage()));
        }
    }

    public void cancelarEdicion() {
        this.med = new Medicamento();
        this.editando = false;
    }
}
