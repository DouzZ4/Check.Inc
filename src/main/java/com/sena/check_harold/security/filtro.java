/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sena.check_harold.security;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author davidalonso
 */
public class filtro implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest solicitud;
        solicitud = (HttpServletRequest)request;
        HttpServletResponse respuesta = (HttpServletResponse)response;
        
        respuesta.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        respuesta.setHeader("Pragma", "no-cache");
        respuesta.setDateHeader("Expires", 0);
        
        //Tomando valores validaciones
        HttpSession sesion = solicitud.getSession(false);
        String rutaSolicitud = solicitud.getRequestURI();
        String raiz = solicitud.getContextPath();
        
        //Validaciones
        //1. validar sesion
        boolean validarsesion =((sesion!=null) && (sesion.getAttribute("Usuario")!=null));
        
        //2. Solicitud Login 
        boolean validarRutaLogin = ((rutaSolicitud.equals(raiz + "/")) || (rutaSolicitud.equals(raiz + "/login.xhtml")));
        
        //Cargue contenido estatico 
        boolean validarContenido = rutaSolicitud.contains("/resources/");
        
        if (validarsesion || validarRutaLogin || validarContenido){
            chain.doFilter(request, response);
        }else {
            respuesta.sendRedirect(raiz);
        }
    }

    @Override
    public void destroy() {
    }
    
}
