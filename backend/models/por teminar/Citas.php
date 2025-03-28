<?php 

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../models/Usuario.php';

class Citas extends Usuario {

    protected $idcita;
    protected $fecha;
    protected $Hora;
    protected $motivo;
    protected $idUsuario; // ID del usuario que creó la cita
    
}
?>