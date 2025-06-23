import 'package:flutter/foundation.dart';
import '../../data/models/glucose_model.dart';
import '../../data/repositories/glucose_repository.dart';

enum GlucoseViewState { idle, loading, success, error }

class GlucoseViewModel extends ChangeNotifier {
  final GlucoseRepository _glucoseRepository = GlucoseRepository();
  
  // Estado del ViewModel
  GlucoseViewState _state = GlucoseViewState.idle;
  String? _errorMessage;
  List<GlucoseModel> _glucoseRecords = [];
  GlucoseModel? _lastRecord;
  Map<String, dynamic>? _statistics;
  List<Map<String, dynamic>> _chartData = [];

  // Getters
  GlucoseViewState get state => _state;
  String? get errorMessage => _errorMessage;
  List<GlucoseModel> get glucoseRecords => _glucoseRecords;
  GlucoseModel? get lastRecord => _lastRecord;
  Map<String, dynamic>? get statistics => _statistics;
  List<Map<String, dynamic>> get chartData => _chartData;
  
  bool get isLoading => _state == GlucoseViewState.loading;
  bool get hasError => _state == GlucoseViewState.error;
  bool get hasData => _glucoseRecords.isNotEmpty;

  // Crear nuevo registro de glucosa
  Future<bool> addGlucoseRecord({
    required int userId,
    required double glucoseLevel,
    required DateTime dateTime,
  }) async {
    _setState(GlucoseViewState.loading);
    _clearError();

    try {
      // Validaciones
      if (glucoseLevel <= 0) {
        _setError('El nivel de glucosa debe ser mayor a 0');
        return false;
      }

      if (glucoseLevel > 600) {
        _setError('El nivel de glucosa parece demasiado alto. Verifique el valor.');
        return false;
      }

      if (dateTime.isAfter(DateTime.now())) {
        _setError('La fecha no puede ser futura');
        return false;
      }

      final newRecord = GlucoseModel(
        nivelGlucosa: glucoseLevel,
        fechaHora: dateTime,
        idUsuario: userId,
        createdAt: DateTime.now(),
        updatedAt: DateTime.now(),
      );

      final createdRecord = await _glucoseRepository.createGlucoseRecord(newRecord);
      
      // Actualizar la lista local
      _glucoseRecords.insert(0, createdRecord);
      _lastRecord = createdRecord;
      
      _setState(GlucoseViewState.success);
      return true;

    } catch (e) {
      _setError('Error al guardar registro: $e');
      return false;
    }
  }

  // Cargar registros de glucosa por usuario
  Future<void> loadGlucoseRecords(int userId) async {
    _setState(GlucoseViewState.loading);
    _clearError();

    try {
      final records = await _glucoseRepository.getGlucoseByUserId(userId);
      _glucoseRecords = records;
      
      if (records.isNotEmpty) {
        _lastRecord = records.first;
      }
      
      _setState(GlucoseViewState.success);
    } catch (e) {
      _setError('Error al cargar registros: $e');
    }
  }

  // Cargar registros de hoy
  Future<void> loadTodayRecords(int userId) async {
    _setState(GlucoseViewState.loading);
    _clearError();

    try {
      final records = await _glucoseRepository.getTodayGlucoseRecords(userId);
      _glucoseRecords = records;
      _setState(GlucoseViewState.success);
    } catch (e) {
      _setError('Error al cargar registros de hoy: $e');
    }
  }

  // Cargar registros de la semana
  Future<void> loadWeekRecords(int userId) async {
    _setState(GlucoseViewState.loading);
    _clearError();

    try {
      final records = await _glucoseRepository.getWeekGlucoseRecords(userId);
      _glucoseRecords = records;
      _setState(GlucoseViewState.success);
    } catch (e) {
      _setError('Error al cargar registros de la semana: $e');
    }
  }

  // Cargar registros del mes
  Future<void> loadMonthRecords(int userId) async {
    _setState(GlucoseViewState.loading);
    _clearError();

    try {
      final records = await _glucoseRepository.getMonthGlucoseRecords(userId);
      _glucoseRecords = records;
      _setState(GlucoseViewState.success);
    } catch (e) {
      _setError('Error al cargar registros del mes: $e');
    }
  }

  // Cargar registros por rango de fechas
  Future<void> loadRecordsByDateRange(
    int userId,
    DateTime startDate,
    DateTime endDate,
  ) async {
    _setState(GlucoseViewState.loading);
    _clearError();

    try {
      if (startDate.isAfter(endDate)) {
        _setError('La fecha de inicio no puede ser posterior a la fecha final');
        return;
      }

      final records = await _glucoseRepository.getGlucoseByDateRange(
        userId,
        startDate,
        endDate,
      );
      _glucoseRecords = records;
      _setState(GlucoseViewState.success);
    } catch (e) {
      _setError('Error al cargar registros por rango de fechas: $e');
    }
  }

  // Actualizar registro de glucosa
  Future<bool> updateGlucoseRecord({
    required int recordId,
    required double glucoseLevel,
    required DateTime dateTime,
  }) async {
    _setState(GlucoseViewState.loading);
    _clearError();

    try {
      // Validaciones
      if (glucoseLevel <= 0) {
        _setError('El nivel de glucosa debe ser mayor a 0');
        return false;
      }

      if (glucoseLevel > 600) {
        _setError('El nivel de glucosa parece demasiado alto. Verifique el valor.');
        return false;
      }

      if (dateTime.isAfter(DateTime.now())) {
        _setError('La fecha no puede ser futura');
        return false;
      }

      // Buscar el registro en la lista local
      final recordIndex = _glucoseRecords.indexWhere((r) => r.idGlucosa == recordId);
      if (recordIndex == -1) {
        _setError('Registro no encontrado');
        return false;
      }

      final originalRecord = _glucoseRecords[recordIndex];
      final updatedRecord = originalRecord.copyWith(
        nivelGlucosa: glucoseLevel,
        fechaHora: dateTime,
        updatedAt: DateTime.now(),
      );

      final result = await _glucoseRepository.updateGlucoseRecord(updatedRecord);
      
      // Actualizar la lista local
      _glucoseRecords[recordIndex] = result;
      
      // Reordenar la lista por fecha
      _glucoseRecords.sort((a, b) => b.fechaHora.compareTo(a.fechaHora));
      
      _setState(GlucoseViewState.success);
      return true;

    } catch (e) {
      _setError('Error al actualizar registro: $e');
      return false;
    }
  }

  // Eliminar registro de glucosa
  Future<bool> deleteGlucoseRecord(int recordId) async {
    _setState(GlucoseViewState.loading);
    _clearError();

    try {
      await _glucoseRepository.deleteGlucoseRecord(recordId);
      
      // Remover de la lista local
      _glucoseRecords.removeWhere((r) => r.idGlucosa == recordId);
      
      // Actualizar último registro si es necesario
      if (_lastRecord?.idGlucosa == recordId) {
        _lastRecord = _glucoseRecords.isNotEmpty ? _glucoseRecords.first : null;
      }
      
      _setState(GlucoseViewState.success);
      return true;

    } catch (e) {
      _setError('Error al eliminar registro: $e');
      return false;
    }
  }

  // Cargar estadísticas
  Future<void> loadStatistics(int userId, DateTime startDate, DateTime endDate) async {
    try {
      final stats = await _glucoseRepository.getGlucoseStatistics(userId, startDate, endDate);
      _statistics = stats;
      notifyListeners();
    } catch (e) {
      _setError('Error al cargar estadísticas: $e');
    }
  }

  // Cargar datos para gráficos
  Future<void> loadChartData(int userId, DateTime startDate, DateTime endDate) async {
    try {
      final data = await _glucoseRepository.getGlucoseChartData(userId, startDate, endDate);
      _chartData = data;
      notifyListeners();
    } catch (e) {
      _setError('Error al cargar datos del gráfico: $e');
    }
  }

  // Obtener último registro
  Future<void> loadLastRecord(int userId) async {
    try {
      final record = await _glucoseRepository.getLastGlucoseRecord(userId);
      _lastRecord = record;
      notifyListeners();
    } catch (e) {
      _setError('Error al cargar último registro: $e');
    }
  }

  // Forzar sincronización
  Future<void> forceSyncRecords() async {
    try {
      await _glucoseRepository.forceSyncGlucose();
      notifyListeners();
    } catch (e) {
      _setError('Error al sincronizar: $e');
    }
  }

  // Verificar si hay registros pendientes de sincronización
  Future<bool> hasUnsyncedRecords() async {
    try {
      return await _glucoseRepository.hasUnsyncedRecords();
    } catch (e) {
      return false;
    }
  }

  // Métodos de utilidad para análisis

  // Obtener promedio de glucosa
  double getAverageGlucose() {
    if (_glucoseRecords.isEmpty) return 0.0;
    final total = _glucoseRecords.fold(0.0, (sum, record) => sum + record.nivelGlucosa);
    return total / _glucoseRecords.length;
  }

  // Obtener conteo por estado
  Map<String, int> getGlucoseStatusCount() {
    final counts = <String, int>{
      'Normal': 0,
      'Alto': 0,
      'Bajo': 0,
      'Muy Alto': 0,
      'Muy Bajo': 0,
    };

    for (final record in _glucoseRecords) {
      counts[record.status] = (counts[record.status] ?? 0) + 1;
    }

    return counts;
  }

  // Obtener tendencia (últimos 7 días vs 7 días anteriores)
  String getTrend(int userId) {
    if (_glucoseRecords.length < 14) return 'Datos insuficientes';

    final now = DateTime.now();
    final sevenDaysAgo = now.subtract(Duration(days: 7));
    final fourteenDaysAgo = now.subtract(Duration(days: 14));

    final recentRecords = _glucoseRecords
        .where((r) => r.fechaHora.isAfter(sevenDaysAgo))
        .toList();
    
    final previousRecords = _glucoseRecords
        .where((r) => r.fechaHora.isAfter(fourteenDaysAgo) && r.fechaHora.isBefore(sevenDaysAgo))
        .toList();

    if (recentRecords.isEmpty || previousRecords.isEmpty) {
      return 'Datos insuficientes';
    }

    final recentAvg = recentRecords.fold(0.0, (sum, r) => sum + r.nivelGlucosa) / recentRecords.length;
    final previousAvg = previousRecords.fold(0.0, (sum, r) => sum + r.nivelGlucosa) / previousRecords.length;

    final difference = recentAvg - previousAvg;
    
    if (difference > 10) return 'Tendencia al alza';
    if (difference < -10) return 'Tendencia a la baja';
    return 'Estable';
  }

  // Métodos privados

  void _setState(GlucoseViewState newState) {
    _state = newState;
    notifyListeners();
  }

  void _setError(String error) {
    _errorMessage = error;
    _state = GlucoseViewState.error;
    notifyListeners();
  }

  void _clearError() {
    _errorMessage = null;
    notifyListeners();
  }

  // Limpiar datos
  void clearData() {
    _glucoseRecords.clear();
    _lastRecord = null;
    _statistics = null;
    _chartData.clear();
    _state = GlucoseViewState.idle;
    _clearError();
  }

  // Limpiar errores manualmente
  void clearError() {
    _clearError();
  }
}
