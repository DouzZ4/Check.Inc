Instrucciones para Chart.js

Este proyecto carga Chart.js desde `/resources/js/chart.min.js` por razones de seguridad (CSP).

Para habilitar los gráficos reales:
1. Descarga Chart.js versión 3.9.1 (archivo minificado) desde el sitio oficial o CDN.
   Ejemplo: https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js
2. Copia el contenido en `src/main/webapp/resources/js/chart.min.js`.
3. Vuelve a compilar/desplegar la aplicación.

Alternativa: si prefieres usar el CDN, modifica la política CSP en `src/main/java/com/mycompany/checkinc/security/Filtro.java`
    - Añade `https://cdn.jsdelivr.net` en `script-src`.
    - Evita añadir dominios sospechosos como `overbridgenet.com`.

Nota: si ves conexiones a `overbridgenet.com`, revisa extensiones del navegador o scripts de terceros; no añadas ese dominio a CSP sin confirmar su legitimidad.
