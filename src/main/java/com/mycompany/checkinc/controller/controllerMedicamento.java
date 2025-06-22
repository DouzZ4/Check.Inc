/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package com.mycompany.checkinc.controller;

import com.mycompany.checkinc.entities.Medicamento;
import com.mycompany.checkinc.services.MedicamentoFacadeLocal;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

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

    public Medicamento getMed() {
        return med;
    }

    public void setMed(Medicamento med) {
        this.med = med;
    }
    
    public List<Medicamento> obtenerMedicamento(){
        try {
            return this.mfl.findAll();
        } catch (Exception e){
            
        }
        return null;
    }
    
    public controllerMedicamento() {
    }
    
    public String crearMedicamentoP1(){
        med = new Medicamento();
        return "/views/registros/Medicamentos/crearMedicamentos.xhtml?faces-redirect=true";
    }
    
    public void crearMedicamentoP2(){
        
    }
}
