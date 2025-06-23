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

        }
        return null;
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
                mfl.edit(med);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Medicamento actualizado", null));
            } else {
                // Asignar usuario autenticado si aplica
                HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
                Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
                if (usuarioSesion != null) {
                    med.setIdUsuario(usuarioSesion);
                }
                mfl.create(med);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Medicamento registrado", null));
            }
            med = new Medicamento();
            editando = false;
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar", e.getMessage()));
        }
    }

    public void editar(Medicamento m) {
        this.med = m;
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
