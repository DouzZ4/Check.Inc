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

        // Apply security-related headers for all responses
        res.setHeader("X-Frame-Options", "SAMEORIGIN");
        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        // HSTS should only be enabled when serving over HTTPS in production
        res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        // Content Security Policy: only allow resources from same origin.
        res.setHeader("Content-Security-Policy",
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
            "style-src 'self' 'unsafe-inline'; " +
            "img-src 'self' data:; " +
            "font-src 'self' data:; " +
            "connect-src 'self' https://www.datos.gov.co");
        res.setHeader("X-Permitted-Cross-Domain-Policies", "none");

        String path = req.getRequestURI();
        String context = req.getContextPath();
        if (context != null && context.length() > 0 && path.startsWith(context)) {
            path = path.substring(context.length());
        }

        // Anti-cache headers for protected pages
        if (!isPublicResource(path)) {
            res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            res.setHeader("Pragma", "no-cache");
            res.setDateHeader("Expires", 0);
        }

        // Allow public resources without authentication
        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Authentication check for protected pages
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            chain.doFilter(request, response);
        } else {
            if (session != null) {
                session.invalidate();
            }
            res.sendRedirect(req.getContextPath() + "/index.xhtml");
        }
    }

    private boolean isPublicResource(String path) {
        if (path == null) {
            return true;
        }

        if (path.startsWith("/resources/") || path.startsWith("/javax.faces.resource/")) {
            return true;
        }

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
        // No cleanup required
    }
}
