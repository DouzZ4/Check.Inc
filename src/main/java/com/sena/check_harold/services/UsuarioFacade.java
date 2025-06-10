/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sena.check_harold.services;

import com.sena.check_harold.entities.Usuario;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author davidalonso
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> implements UsuarioFacadeLocal {

    @PersistenceContext(unitName = "com.sena_check_harold_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }

    @Override
    public Usuario iniciarSesion(String usuario, String contrasenna) {
        Query query = em.createQuery("SELECT U FROM Usuario U Where U.user=:usuario AND U.password=:contrasenna");
        query.setParameter("usuario", usuario);
        query.setParameter("contrasenna", contrasenna);
        try{
            return (Usuario) query.getSingleResult();
        } catch (Exception e){
            
        }
        Usuario userVacio = new Usuario();
        return userVacio;
           
    }
    
}
