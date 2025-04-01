<?php
// models/GlucosaModel.php

class Glucosa {
    private $idGlucosa;
    private $nivelGlucosa;
    private $fechaHora;
    private $idUsuario;

    public function __construct($idUsuario = null, $nivelGlucosa = null, $fechaHora = null) {
        $this->idUsuario = $idUsuario;
        $this->nivelGlucosa = $nivelGlucosa;
        $this->fechaHora = $fechaHora;
    }

    // Getters
    public function getIdGlucosa() {
        return $this->idGlucosa;
    }

    public function getIdUsuario() {
        return $this->idUsuario;
    }

    public function getNivelGlucosa() {
        return $this->nivelGlucosa;
    }

    public function getFechaHora() {
        return $this->fechaHora;
    }

    // Setters
    public function setIdUsuario($idUsuario) {
        if (is_numeric($idUsuario) && $idUsuario > 0) {
            $this->idUsuario = $idUsuario;
        } else {
            throw new Exception("❌ El ID del usuario no es válido.");
        }
    }

    public function setNivelGlucosa($nivelGlucosa) {
        if ($nivelGlucosa > 0) {
            $this->nivelGlucosa = $nivelGlucosa;
        } else {
            throw new Exception("❌ El nivel de glucosa debe ser un valor positivo.");
        }
    }

    public function setFechaHora($fechaHora) {
        if (strtotime($fechaHora) !== false) {
            $this->fechaHora = $fechaHora;
        } else {
            throw new Exception("❌ La fecha y hora no tienen un formato válido.");
        }
    }
}

class GlucosaFactory {
    public static function crearGlucosa($idUsuario, $nivelGlucosa, $fechaHora) {
        return new Glucosa($idUsuario, $nivelGlucosa, $fechaHora);
    }
}
?>