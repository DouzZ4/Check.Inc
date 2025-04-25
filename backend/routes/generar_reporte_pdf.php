<?php
// backend/routes/generar_reporte_pdf.php

// Iniciar sesión para obtener datos del usuario logueado
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

// 1. Verificar Autenticación
if (!isset($_SESSION['idUsuario'])) {
    // Si no está logueado, no puede generar reporte
    // Podrías redirigir a login o mostrar un error
    die("Acceso denegado. Debes iniciar sesión para generar reportes.");
}

$idUsuarioLogueado = $_SESSION['idUsuario'];
$nombreUsuarioLogueado = $_SESSION['nombreUsuario'] ?? 'Usuario'; // Para el título

// 2. Incluir dependencias (Ajusta rutas si es necesario)
require_once '../config/database.php';
require_once '../models/Usuario.php';   // Necesario para obtener más detalles si quieres
require_once '../models/GlucosaModel.php';
require_once '../models/CitasModel.php';      // Asegúrate que este existe
require_once '../lib/fpdf/fpdf.php'; // <-- Incluir la librería FPDF

// 3. Conectar a BD e instanciar Modelos
try {
    $db = new Conexion();
    $conn = $db->conectar();

    $usuarioModel = new UsuarioModel($conn); // Podrías usarlo para obtener más datos del usuario
    $glucosaModel = new GlucosaModel($conn);
    $citaModel = new CitaModel($conn);

    // 4. Obtener Datos del Usuario Logueado
    $glucosaData = $glucosaModel->findByUsuario($idUsuarioLogueado);
    $citasData = $citaModel->consultarCita($idUsuarioLogueado); // Asegúrate que el método se llame así

} catch (Exception $e) {
    error_log("Error preparando datos para PDF: " . $e->getMessage());
    die("Error al generar el reporte. No se pudieron obtener los datos.");
}

// --- 5. Generación del PDF con FPDF ---

class PDF extends FPDF {
    // Cabecera de página
    function Header() {
        // Logo (Opcional - asegúrate que la ruta sea accesible desde el servidor)
        // $this->Image('../public/images/ICONO.png', 10, 6, 20); // Ruta, X, Y, Ancho
        $this->SetFont('Arial', 'B', 15);
        $this->Cell(80); // Mover a la derecha
        $this->Cell(30, 10, utf8_decode('Reporte de Salud'), 0, 0, 'C'); // Título
        $this->Ln(15); // Salto de línea
    }

    // Pie de página
    function Footer() {
        $this->SetY(-15); // Posición a 1.5 cm del final
        $this->SetFont('Arial', 'I', 8);
        // Número de página {nb} es un alias especial para el total de páginas
        $this->Cell(0, 10, utf8_decode('Página ') . $this->PageNo() . '/{nb}', 0, 0, 'C');
    }

     // Función para tabla simple (puedes mejorarla)
     function BasicTable($header, $data, $columnWidths) {
         // Cabecera
         $this->SetFillColor(48, 88, 166); // Azul oscuro (RGB)
         $this->SetTextColor(255); // Blanco
         $this->SetDrawColor(128); // Gris para bordes
         $this->SetFont('', 'B'); // Negrita
         $this->SetLineWidth(.3);
         foreach ($header as $i => $col) {
             $this->Cell($columnWidths[$i], 7, utf8_decode($col), 1, 0, 'C', true); // Decodificar UTF8
         }
         $this->Ln();

         // Datos
         $this->SetFillColor(224, 235, 255); // Azul muy claro
         $this->SetTextColor(0); // Negro
         $this->SetFont(''); // Normal
         $fill = false;
         if (empty($data)) {
             $this->Cell(array_sum($columnWidths), 7, utf8_decode('No hay datos para mostrar.'), 'LR', 0, 'C', $fill);
             $this->Ln();
         } else {
             foreach ($data as $row) {
                 $i = 0;
                 foreach ($row as $col) {
                     // Manejo básico de multilinea para columnas anchas (como motivo)
                     $cellWidth = $columnWidths[$i];
                     $text = utf8_decode($col ?? ''); // Decodificar y manejar null
                     // Calcular si el texto necesita multilinea (aproximado)
                      $numLines = ceil($this->GetStringWidth($text) / ($cellWidth - 2)); // Restar un pequeño margen
                      $cellHeight = 6 * $numLines; // Altura base de 6 por línea
                      $x = $this->GetX();
                      $y = $this->GetY();
                      $this->MultiCell($cellWidth, 6, $text, 1, 'L', $fill); // Usar MultiCell para texto largo
                       // Mover posición para la siguiente celda en la misma fila si se usó MultiCell
                      if ($i < count($columnWidths) - 1) {
                          $this->SetXY($x + $cellWidth, $y);
                      }
                      $i++;
                 }
                  $this->Ln($cellHeight > 6 ? $cellHeight : 6); // Salto de línea ajustado a la celda más alta
                  $fill = !$fill;
             }
         }
          // Línea de cierre
          $this->Cell(array_sum($columnWidths), 0, '', 'T');
     }
}

// Crear instancia de PDF
$pdf = new PDF();
$pdf->AliasNbPages(); // Habilitar el alias para número total de páginas
$pdf->AddPage(); // Añadir una página

// --- Información del Paciente ---
$pdf->SetFont('Arial', 'B', 12);
$pdf->Cell(0, 10, utf8_decode('Paciente: ' . $nombreUsuarioLogueado), 0, 1);
$pdf->SetFont('Arial', '', 10);
$pdf->Cell(0, 6, utf8_decode('Fecha de Generación: ') . date('d/m/Y H:i:s'), 0, 1);
$pdf->Ln(8); // Espacio

// --- Tabla de Glucosa ---
$pdf->SetFont('Arial', 'B', 14);
$pdf->Cell(0, 10, utf8_decode('Historial de Glucosa'), 0, 1);

$headerGlucosa = ['Fecha y Hora', 'Nivel (mg/dL)'];
$dataGlucosa = [];
foreach ($glucosaData as $reg) {
     // Formatear fecha/hora si es necesario
     $fechaHoraGlucosa = $reg['fechaHora'] ? date('d/m/Y H:i', strtotime($reg['fechaHora'])) : 'N/A';
    $dataGlucosa[] = [$fechaHoraGlucosa, $reg['nivelGlucosa']];
}
$widthsGlucosa = [50, 50]; // Ancho de columnas
$pdf->BasicTable($headerGlucosa, $dataGlucosa, $widthsGlucosa);
$pdf->Ln(10); // Espacio

// --- Tabla de Citas ---
$pdf->SetFont('Arial', 'B', 14);
$pdf->Cell(0, 10, utf8_decode('Próximas Citas / Historial'), 0, 1);

$headerCitas = ['Fecha', 'Hora', 'Motivo'];
$dataCitas = [];
foreach ($citasData as $cita) {
    // Formatear fecha/hora si es necesario
    $fechaCita = $cita['fecha'] ? date('d/m/Y', strtotime($cita['fecha'])) : 'N/A';
    $horaCita = $cita['hora'] ? date('H:i', strtotime($cita['hora'])) : 'N/A';
    $dataCitas[] = [$fechaCita, $horaCita, $cita['motivo']];
}
// Anchos: Fecha, Hora, Motivo (total debe ser aprox 190 para A4)
$widthsCitas = [40, 30, 120];
$pdf->BasicTable($headerCitas, $dataCitas, $widthsCitas);

// --- 6. Enviar PDF al Navegador ---
$nombreArchivo = 'Reporte_Salud_' . date('Ymd') . '.pdf';
// Limpiar cualquier salida anterior (importante)
ob_end_clean();

// Cabeceras para forzar descarga
header('Content-Type: application/pdf');
header('Content-Disposition: attachment; filename="' . $nombreArchivo . '"');
header('Cache-Control: private, max-age=0, must-revalidate');
header('Pragma: public');

// Salida del PDF ('D' = Forzar descarga, 'I' = Mostrar en navegador)
$pdf->Output('D', $nombreArchivo);
exit; // Terminar script

?>