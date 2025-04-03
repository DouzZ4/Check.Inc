<?php
// models/AnomaliaModel.php

class Anomalia {
    private $idAnomalia;
    private $descripcion;
    private $fechaHora;
    private $sintomas;
    private $idUsuario;

    public function __construct($idUsuario = null, $descripcion = null, $fechaHora = null, $sintomas = null) {
        $this->idUsuario = $idUsuario;
        $this->descripcion = $descripcion;
        $this->fechaHora = $fechaHora;
        $this->sintomas = $sintomas;
    }

    //getters
    public function getIdAnomalia() {
        return $this->idAnomalia;
    }
    public function getIdUsuario() {
        return $this->idUsuario;
    }
    public function getDescripcion() {
        return $this->descripcion;
    }
    public function getFechaHora() {
        return $this->fechaHora;
    }
    public function getsintomas() {
        return $this->sintomas;
    }

    //setters
    public function setIdUsuario($idUsuario) {
        if (is_numeric($idUsuario) && $idUsuario > 0) {
            $this->idUsuario = $idUsuario;
        } else {
            throw new Exception("❌ El ID del usuario no es válido.");
        }
    }
    public function setDescripcion($descripcion) {
        if (!empty($descripcion)) {
            $this->descripcion = $descripcion;
        } else {
            throw new Exception("❌ La descripción no puede estar vacía.");
        }
    }
    public function setFechaHora($fechaHora) {
        if (strtotime($fechaHora) !== false) {
            $this->fechaHora = $fechaHora;
        } else {
            throw new Exception("❌ La fecha y hora no tienen un formato válido.");
        }
    }
    
    public function setSintomas($sintomas) {
        if (!empty($sintomas)) {
            $this->sintomas = $sintomas;
        } else {
            throw new Exception("❌ Los síntomas no pueden estar vacíos.");
        }
    }
}

class AnomaliaFactory {
    public static function crearAnomalia($idUsuario, $descripcion, $fechaHora, $sintomas) {
        return new Anomalia($idUsuario, $descripcion, $fechaHora, $sintomas);
    }
}
?>