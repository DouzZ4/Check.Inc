<?php
require_once '../models/glucosaModel.php';

class GlucosaController {
    private $model;

    public function __construct($db) {
        $this->model = new GlucosaModel($db);
    }

    public function crearRegistro($data): array {
        if (!isset($data['idUsuario'], $data['nivelGlucosa'], $data['fechaHora'])) {
            return ['status' => 'error', 'message' => 'Error al crear registro'];
        }
        $resultado = $this->model->crearRegistro($data['idUsuario'], $data['nivelGlucosa'], $data['fechaHora']);
        return $resultado ? ['status' => 'success', 'message' => 'Registro creado'] : ['status' => 'error', 'message' => 'Error al crear registro'];
    }

    public function obtenerRegistros($idUsuario) {
        return $this->model->obtenerRegistros($idUsuario);
    }
}
?>