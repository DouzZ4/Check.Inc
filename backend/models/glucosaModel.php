<?php
class GlucosaModel {
    private $idGlucosa;
    private $nivelGlucosa;
    private $fechaHora;
    private $idUsuario;
    private $conn;

    // Constructor para inicializar la conexión y los atributos opcionales
    public function __construct($db, $idGlucosa = null, $nivelGlucosa = null, $fechaHora = null, $idUsuario = null) {
        $this->conn = $db;
        $this->idGlucosa = $idGlucosa;
        $this->nivelGlucosa = $nivelGlucosa;
        $this->fechaHora = $fechaHora;
        $this->idUsuario = $idUsuario;
    }

    // Getters y setters para cada propiedad
    public function getIdGlucosa() {
        return $this->idGlucosa;
    }

    public function setIdGlucosa($idGlucosa) {
        $this->idGlucosa = $idGlucosa;
    }

    public function getNivelGlucosa() {
        return $this->nivelGlucosa;
    }

    public function setNivelGlucosa($nivelGlucosa) {
        $this->nivelGlucosa = $nivelGlucosa;
    }

    public function getFechaHora() {
        return $this->fechaHora;
    }

    public function setFechaHora($fechaHora) {
        $this->fechaHora = $fechaHora;
    }

    public function getIdUsuario() {
        return $this->idUsuario;
    }

    public function setIdUsuario($idUsuario) {
        $this->idUsuario = $idUsuario;
    }

    // Método para crear un nuevo registro usando los atributos
    public function crearRegistro($idUsuario, $nivelGlucosa, $fechaHora) {
        $sql = "INSERT INTO glucosa (idUsuario, nivelGlucosa, fechaHora) VALUES (?, ?, ?)";
        $stmt = $this->conn->prepare($sql);
        return $stmt->execute([$idUsuario, $nivelGlucosa, $fechaHora]);
    }

    // Método para obtener registros por usuario
    public function obtenerRegistros($idUsuario) {
        $query = "SELECT * FROM glucosa WHERE idUsuario = ? ORDER BY fechaHora DESC";
        $stmt = $this->conn->prepare($query);
        $stmt->execute([$idUsuario]);
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }
}
?>