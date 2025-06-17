/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Usuario;
import com.mycompany.checkinc.util.PasswordUtils;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;

/**
 *
 * @author davidalonso
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> implements UsuarioFacadeLocal {
    private static final Logger logger = Logger.getLogger(UsuarioFacade.class.getName());

    @PersistenceContext(unitName = "com.mycompany_CheckInc_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }
    
    @Override
    public Usuario iniciarSesion(String username, String password) {
        try {
            // Buscar usuario por nombre de usuario
            Query query = em.createQuery("SELECT u FROM Usuario u WHERE u.user = :username");
            query.setParameter("username", username);
            
            Usuario usuario = (Usuario) query.getSingleResult();
            
            // Verificar la contraseña usando la utilidad que maneja tanto bcrypt como texto plano
            if (usuario != null && PasswordUtils.verifyPassword(password, usuario.getPassword())) {
                // Si la contraseña no está hasheada, actualizarla
                if (!usuario.getPassword().startsWith("$2")) {
                    usuario.setPassword(PasswordUtils.hashPassword(password));
                    em.merge(usuario);
                }
                return usuario;
            }
            
        } catch (NoResultException e) {
            logger.log(Level.INFO, "Intento de inicio de sesión fallido para usuario no existente: {0}", username);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error durante la verificación de inicio de sesión", e);
        }
        
        return null;
    }
    
    @Override
    public void create(Usuario usuario) {
        try {
            // Hashear la contraseña antes de guardar
            String hashedPassword = PasswordUtils.hashPassword(usuario.getPassword());
            usuario.setPassword(hashedPassword);
            
            super.create(usuario);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al crear usuario", e);
            throw e;
        }
    }
    
    @Override
    public void edit(Usuario usuario) {
        try {
            // Si la contraseña ha cambiado y no está hasheada, hashearla
            String currentPassword = usuario.getPassword();
            if (currentPassword != null && !currentPassword.startsWith("$2")) {
                String hashedPassword = PasswordUtils.hashPassword(currentPassword);
                usuario.setPassword(hashedPassword);
            }
            
            super.edit(usuario);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al actualizar usuario", e);
            throw e;
        }
    }
}