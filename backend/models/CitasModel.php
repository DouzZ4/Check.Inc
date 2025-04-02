<?php
// models/CitasModel.php

require_once __DIR__ . '/Usuario.php';

class Citas {
    protected $idCita;
    protected $fecha;
    protected $hora;
    protected $motivo;
    protected $idUsuario; // ID del usuario que tiene la sesión activa

    // Constructor para crear una cita
    public function __construct($fecha = null, $hora = null, $motivo = null, $idUsuario = null) {
        $this->fecha = $fecha;
        $this->hora = $hora;
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
        return $this->hora;
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

    public function setHora($hora) {
        if (strtotime($hora) !== false) {
            $this->hora = $hora;
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
    public static function crearCita($fecha, $hora, $motivo, $idUsuario) {
        return new Citas($fecha, $hora, $motivo, $idUsuario);
    }
}
?>