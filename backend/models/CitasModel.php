<?php 

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../models/Usuario.php';

class Citas extends Usuario {

    protected $idCita;
    protected $fecha;
    protected $hora;
    protected $motivo;
    protected $idUsuario; // ID del usuario que tiene la sesion activa
    
    // Constructor para crear una cita
    public function __construct($idCita, $fecha, $hora, $motivo, $idUsuario) {
        parent::__construct($idUsuario); // Llama al constructor de la clase Usuario
        $this->idCita = $idCita;
        $this->fecha = $fecha;
        $this->hora = $hora;
        $this->motivo = $motivo;
        $this->idUsuario = $idUsuario; // Asigna el ID del usuario a la propiedad de la clase Citas
    }

    // Getters
    public function getFecha() {
        return $this->fecha;
    }
    public function gethora() {
        return $this->hora;
    }
    public function getMotivo() {
        return $this->motivo;
    }
    
    // Setters
    public function setFecha($fecha) {
        if (strtotime($fecha) !== false) {
            $this->fecha = $fecha;
        } else {
            throw new Exception("❌ La fecha no tiene un formato válido.");
        }
    }

    public function setHora($Hora) {
        if (strtotime($Hora) !== false) {
            $this->Hora = $Hora;
        } else {
            throw new Exception("❌ La hora no tiene un formato válido.");
        }
    }
    public function setMotivo($motivo) {
        if (!empty($motivo)) {
            $this->motivo = $motivo;
        } else {
            throw new Exception("❌ El motivo no puede estar vacío.");
        }
    }
}
class CitasFactory {
    public static function crearCita($idCita, $fecha, $Hora, $motivo, $idUsuario) {
        return new Citas($idCita, $fecha, $Hora, $motivo, $idUsuario);
    }
}
?>