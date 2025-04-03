<?php
// backend/public/logout.php
session_start();
session_unset(); // Limpia todas las variables de sesión
session_destroy(); // Destruye la sesión

// --- CORRECCIÓN DE REDIRECCIÓN ---
// Redirige a la página de inicio principal (index.php en la raíz)
// Ajusta '/check.inc/' si tu proyecto no está en esa subcarpeta
$baseUrl = '/check.inc'; // O define esto en un config global
header('Location: ' . $baseUrl . '/backend/public/index.php');
// Alternativa si $baseUrl no está definido aquí:
// header('Location: /check.inc/index.php');
// O relativo (menos robusto):
// header('Location: ../../index.php');
// --- FIN CORRECCIÓN ---

exit; // Detiene la ejecución del script
?>