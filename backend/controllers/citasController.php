<?php

require_once '../models/CitasModel.php';

class CitasController {
    private $citasModel;

    public function __construct(CitaModel $model) {
        $this->citasModel = $model;
    }

    public function crearCita($data): array {
        try {
            if (empty($data['idUsuario']) || empty($data['fecha']) || empty($data['hora']) || empty($data['motivo'])) {
                return ['status' => 'error', 'message' => 'idUsuario, fecha, hora y motivo son obligatorios.'];
            }

            $cita = CitaFactory::crearCita($data['idUsuario'], $data['fecha'], $data['hora'], $data['motivo']);
            $nuevoId = $this->citasModel->InsertarCita($cita);

            if ($nuevoId !== false) {
                return ['status' => 'success', 'message' => 'Cita creada exitosamente.', 'id' => $nuevoId];
            } else {
                return ['status' => 'error', 'message' => 'Error del servidor al intentar guardar la cita.'];
            }
        } catch (Exception $e) {
            return ['status' => 'error', 'message' => 'Error al crear la cita: ' . $e->getMessage()];
        }
    }

    public function obtenerCitas($idUsuario): array {
        try {
            $citas = $this->citasModel->ConsultarCita($idUsuario);
            return ['status' => 'success', 'data' => $citas];
        } catch (Exception $e) {
            error_log("Error inesperado en citasController::obtenerCitas: " . $e->getMessage());
            return ['status' => 'error', 'message' => 'Error inesperado del servidor al obtener registros.'];
        }
    }

    public function actualizarCita($idCita, $data): array {
        try {
            if (empty($data)) {
                return ['status' => 'error', 'message' => 'Datos incompletos.'];
            }

            $cita = new Cita();
            $camposParaActualizar = [];
            if (isset($data['fecha'])) {
                $cita->setFecha($data['fecha']);
                $camposParaActualizar['fecha'] = $data['fecha'];
            }
            if (isset($data['hora'])) {
                $cita->setHora($data['hora']);
                $camposParaActualizar['hora'] = $data['hora'];
            }
            if (isset($data['motivo'])) {
                $cita->setMotivo($data['motivo']);
                $camposParaActualizar['motivo'] = $data['motivo'];
            }

            if (empty($camposParaActualizar)) {
                return ['status' => 'error', 'message' => 'No se proporcionaron datos para actualizar.'];
            }

            $actualizado = $this->citasModel->ActualizarCita($idCita, $camposParaActualizar);
            if ($actualizado) {
                return ['status' => 'success', 'message' => 'Cita actualizada exitosamente.'];
            } else {
                return ['status' => 'error', 'message' => 'Error del servidor al intentar actualizar la cita.'];
            }
        } catch (Exception $e) {
            return ['status' => 'error', 'message' => $e->getMessage()];
        }
    }

    public function eliminarCita($idCita): array {
        try {
            $eliminado = $this->citasModel->EliminarCita($idCita);
            if ($eliminado) {
                return ['status' => 'success', 'message' => 'Cita eliminada exitosamente.'];
            } else {
                return ['status' => 'error', 'message' => 'Error del servidor al intentar eliminar la cita.'];
            }
        } catch (Exception $e) {
            return ['status' => 'error', 'message' => $e->getMessage()];
        }
    }

}
?>