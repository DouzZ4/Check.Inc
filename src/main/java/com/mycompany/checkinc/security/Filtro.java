/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.checkinc.security;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class Filtro implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No se requiere inicialización
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Cabeceras anti-caché para páginas protegidas
        if (!isPublicResource(path)) {
            res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            res.setHeader("Pragma", "no-cache");
            res.setDateHeader("Expires", 0);
        }

        // Si es un recurso estático o página pública, permitir acceso
        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Verificar autenticación para el resto de páginas
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            chain.doFilter(request, response);
        } else {
            if (session != null) {
                session.invalidate(); // Cierra la sesión si existe
            }
            res.sendRedirect(req.getContextPath() + "/index.xhtml");
        }
    }

    private boolean isPublicResource(String path) {
        // Recursos públicos (CSS, JS, imágenes, etc.)
        if (path.startsWith("/resources/") ||
            path.startsWith("/javax.faces.resource/")) {
            return true;
        }

        // Páginas públicas
        String[] publicPages = {
            "/views/usuarios/login.xhtml",
            "/views/usuarios/registrousuario.xhtml",
            "/index.xhtml",
            "/error/404.xhtml",
            "/error/500.xhtml"
        };

        for (String page : publicPages) {
            if (path.equals(page)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void destroy() {
        // No se requieren acciones de limpieza
    }
}
