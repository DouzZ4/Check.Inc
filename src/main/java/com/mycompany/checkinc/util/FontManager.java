package com.mycompany.checkinc.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;

/**
 * Gestor de fuentes Unicode para generación de PDFs.
 * Proporciona fuentes que soportan caracteres especiales del español (tildes,
 * ñ, etc.)
 */
public class FontManager {

    // Colores estandarizados
    public static final BaseColor COLOR_AZUL = new BaseColor(48, 88, 166);
    public static final BaseColor COLOR_NARANJA = new BaseColor(244, 85, 1);
    public static final BaseColor COLOR_GRIS = new BaseColor(127, 127, 127);

    // BaseFont con encoding completo para caracteres especiales
    private static BaseFont baseFontNormal;
    private static BaseFont baseFontBold;

    static {
        try {
            // Usar Helvetica con encoding CP1252 que soporta caracteres del español
            baseFontNormal = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
            baseFontBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback a fuentes sin encoding si falla
            try {
                baseFontNormal = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
                baseFontBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Obtiene fuente para títulos principales
     */
    public static Font getFontTitulo() {
        return new Font(baseFontBold, 18, Font.BOLD, COLOR_AZUL);
    }

    /**
     * Obtiene fuente para subtítulos
     */
    public static Font getFontSubtitulo() {
        return new Font(baseFontBold, 14, Font.BOLD, COLOR_NARANJA);
    }

    /**
     * Obtiene fuente para headers de tabla
     */
    public static Font getFontHeader() {
        return new Font(baseFontBold, 12, Font.BOLD, BaseColor.WHITE);
    }

    /**
     * Obtiene fuente para celdas de datos
     */
    public static Font getFontCelda() {
        return new Font(baseFontNormal, 11, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * Obtiene fuente para texto de énfasis
     */
    public static Font getFontEnfasis() {
        return new Font(baseFontNormal, 10, Font.ITALIC, COLOR_GRIS);
    }

    /**
     * Obtiene fuente para texto normal/paquete
     */
    public static Font getFontNormal() {
        return new Font(baseFontNormal, 10, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * Obtiene fuente para fechas en el header
     */
    public static Font getFontFecha() {
        return new Font(baseFontNormal, 10, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * Obtiene fuente personalizada con tamaño y color específicos
     */
    public static Font getCustomFont(float size, int style, BaseColor color) {
        BaseFont bf = (style == Font.BOLD || style == Font.BOLDITALIC) ? baseFontBold : baseFontNormal;
        return new Font(bf, size, style, color);
    }
}
