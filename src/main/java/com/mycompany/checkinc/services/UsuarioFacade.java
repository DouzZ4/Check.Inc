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
import javax.persistence.NoResultException;

/**
 *
 * @author davidalonso
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> implements UsuarioFacadeLocal {

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
            System.out.println("UsuarioFacade: Intentando login para usuario: " + username);
            Query query = em.createQuery("SELECT u FROM Usuario u WHERE u.user = :username");
            query.setParameter("username", username);
            Usuario usuario = (Usuario) query.getSingleResult();
            System.out.println("UsuarioFacade: Usuario encontrado: " + usuario.getUser());
            System.out.println("UsuarioFacade: Password almacenado: " + usuario.getPassword());
            String storedPassword = usuario.getPassword();
            boolean passwordMatch;
            if (!storedPassword.startsWith("$2")) {
                System.out.println("UsuarioFacade: Contraseña en texto plano, comparando directamente");
                password = password.trim();
                storedPassword = storedPassword.trim();
                passwordMatch = password.equals(storedPassword);
            } else {
                System.out.println("UsuarioFacade: Contraseña hasheada, verificando con bcrypt");
                passwordMatch = PasswordUtils.verifyPassword(password, storedPassword);
            }
            System.out.println("UsuarioFacade: Verificación de contraseña: " + passwordMatch);
            if (passwordMatch) {
                if (!storedPassword.startsWith("$2")) {
                    System.out.println("UsuarioFacade: Actualizando contraseña a formato hash");
                    usuario.setPassword(PasswordUtils.hashPassword(password));
                    em.merge(usuario);
                }
                return usuario;
            }
        } catch (NoResultException e) {
            System.out.println("UsuarioFacade: Usuario no encontrado: " + username);
            return null;
        } catch (Exception e) {
            System.out.println("UsuarioFacade: Error durante el login: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error inesperado durante el login: " + e.getMessage());
        }
        System.out.println("UsuarioFacade: Contraseña incorrecta para usuario: " + username);
        return null;
    }

    @Override
    public void create(Usuario usuario) {
        // Hashear la contraseña antes de guardar
        usuario.setPassword(PasswordUtils.hashPassword(usuario.getPassword()));
        super.create(usuario);
    }

    @Override
    public void edit(Usuario usuario) {
        // Si la contraseña cambió (no es un hash), hashearla
        String password = usuario.getPassword();
        if (password != null && !password.startsWith("$2")) {
            usuario.setPassword(PasswordUtils.hashPassword(password));
        }
        super.edit(usuario);
    }

    @Override
public Usuario findByUser(String user) {
    try {
        Query query = em.createQuery("SELECT u FROM Usuario u WHERE u.user = :user");
        query.setParameter("user", user);
        return (Usuario) query.getSingleResult();
    } catch (NoResultException e) {
        return null;
    }
}

}