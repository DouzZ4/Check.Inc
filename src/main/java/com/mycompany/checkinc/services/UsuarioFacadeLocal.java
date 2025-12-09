/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.checkinc.services;

import com.mycompany.checkinc.entities.Usuario;
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

    Usuario findByUser(String user);

    // Buscar usuario por documento
    Usuario findByDocumento(int documento);

    int count();

    Usuario iniciarSesion(String username, String password);

    Usuario findByCorreo(String correo);

    Usuario findByToken(String token);

    void actualizarTokenRecuperacion(Usuario usuario, String token, java.sql.Timestamp expira);

    void actualizarPasswordRecuperacion(Usuario usuario, String nuevaPassword);
}
