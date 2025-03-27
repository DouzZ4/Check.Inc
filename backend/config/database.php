<?php
class Conexion {
    private $host = "localhost";
    private $dbname = "checks";
    private $username = "root";
    private $password = "";
    private $conn;

    public function conectar() {
        try {
            $this->conn = new PDO("mysql:host=" . $this->host . ";dbname=" . $this->dbname, $this->username, $this->password);
            $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            return $this->conn;
        } catch (PDOException $e) {
            echo "❌ Error de conexión: " . $e->getMessage();
            return null;
        }
    }
}
?>
