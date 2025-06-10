/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sena.check_harold.services;

import com.sena.check_harold.entities.Usuario;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author davidalonso
 */
@Local
public interface UsuarioFacadeLocal {

    void create(Usuario usuario);

    void edit(Usuario usuario);

    void remove(Usuario usuario);

    Usuario find(Object id);

    List<Usuario> findAll();

    List<Usuario> findRange(int[] range);

    int count();
    
    Usuario iniciarSesion(String usuario, String contrasenna);
}
