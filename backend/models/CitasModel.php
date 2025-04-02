<?php 
// models/CitasModel.php
class Citas extends Usuario {

    protected $idCita;
    protected $fecha;
    protected $Hora;
    protected $motivo;
    protected $idUsuario; // ID del usuario que tiene la sesion activa
    
    // Constructor para crear una cita
    public function __construct($fecha = null, $Hora = null, $motivo = null, $idUsuario = null) {
        $this->fecha = $fecha;
        $this->Hora = $Hora;
        $this->motivo = $motivo;
        $this->idUsuario = $idUsuario; 
    }

    // Getters
    public function getIdCita() {
        return $this->idCita;
    }
    public function getFecha() {
        return $this->fecha;
    }
    public function getHora() {
        return $this->Hora;
    }
    public function getMotivo() {
        return $this->motivo;
    }
    public function getIdUsuario() {
        return $this->idUsuario;
    }
    
    // Setters
    public function setIdUsuario($idUsuario) {
        if (is_numeric($idUsuario) && $idUsuario > 0) {
            $this->idUsuario = $idUsuario;
        } else {
            throw new Exception("❌ El ID del usuario no es válido.");
        }
    }
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
    public static function crearCita( $fecha, $Hora, $motivo, $idUsuario) {
        return new Citas($fecha, $Hora, $motivo, $idUsuario);
    }
}
?>