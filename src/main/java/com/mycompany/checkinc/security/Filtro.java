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
        // No initialization required
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        // 1. Solo procesar peticiones .xhtml (como ya lo tienes)
        if (!req.getRequestURI().contains(".xhtml")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Aplicar encabezados de seguridad (no requiere cambios)
        res.setHeader("X-Frame-Options", "SAMEORIGIN");
        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        res.setHeader("Content-Security-Policy",
                "default-src 'self'; "
                + "script-src 'self' 'unsafe-inline' 'unsafe-eval'; "
                + "style-src 'self' 'unsafe-inline'; "
                + "img-src 'self' data:; "
                + "font-src 'self' data:; "
                + "connect-src 'self' https://www.datos.gov.co");
        res.setHeader("X-Permitted-Cross-Domain-Policies", "none");

        String path = req.getRequestURI();
        String context = req.getContextPath();
        if (context != null && context.length() > 0 && path.startsWith(context)) {
            path = path.substring(context.length());
        }

        // 3. Control de caché para páginas protegidas
        if (!isPublicResource(path)) {
            res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            res.setHeader("Pragma", "no-cache");
            res.setDateHeader("Expires", 0);
        }

        // 4. Permitir recursos públicos y eludir la verificación de sesión
        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return; // Detener el filtro aquí para páginas públicas (incluida la de restablecimiento)
        }

        // 5. Verificación de autenticación (Solo para páginas NO públicas)
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            chain.doFilter(request, response); // Usuario autenticado
        } else {
            // Usuario no autenticado intentando acceder a una página protegida
            if (session != null) {
                session.invalidate();
            }
            res.sendRedirect(req.getContextPath() + "/index.xhtml"); // Redirigir al login/inicio
        }
    }

    private boolean isPublicResource(String path) {
        if (path == null) {
            return true;
        }

        // Permitir recursos de PrimeFaces y JSF
        if (path.startsWith("/resources/") || path.startsWith("/javax.faces.resource/")) {
            return true;
        }

        // Páginas de acceso público (LOGIN, REGISTRO, RESTABLECER)
        String[] publicPages = {
            "/views/usuarios/login.xhtml",
            "/views/usuarios/registrousuario.xhtml",
            // Páginas de restablecimiento:
            "/views/usuarios/reset.xhtml",        // La página donde pides el correo
            "/views/usuarios/restablecer.xhtml",  // La página donde usas el token (¡CRÍTICA!)
            "/index.xhtml",
            "/error/404.xhtml",
            "/error/500.xhtml"
        };

        for (String page : publicPages) {
            // Utilizamos 'path.startsWith(page)' para incluir cualquier parámetro (como ?token=...)
            if (path.startsWith(page)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        // No cleanup required
    }
}