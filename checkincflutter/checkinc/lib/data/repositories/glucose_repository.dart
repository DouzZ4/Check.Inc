import '../models/glucose_model.dart';
import '../../core/services/database/sqlite_service.dart';
import '../../core/services/database/firebase_service.dart';
import '../../core/services/sync_service.dart';

class GlucoseRepository {
  final SQLiteService _sqliteService = SQLiteService();
  final FirebaseService _firebaseService = FirebaseService();
  final SyncService _syncService = SyncService();
  
  static final GlucoseRepository _instance = GlucoseRepository._internal();
  
  factory GlucoseRepository() => _instance;
  
  GlucoseRepository._internal();

  // Crear nuevo registro de glucosa
  Future<GlucoseModel> createGlucoseRecord(GlucoseModel glucose) async {
    try {
      // Crear registro en SQLite
      final localId = await _sqliteService.insertGlucose(glucose);
      
      // Intentar sincronizar con Firebase si hay conexión
      try {
        await _syncService.syncGlucose();
      } catch (e) {
        print('Error al sincronizar con Firebase: $e');
        // No lanzamos el error ya que el registro se guardó localmente
      }

      // Obtener el registro creado con su ID
      final createdRecord = await _sqliteService.getGlucoseById(localId);
      if (createdRecord == null) {
        throw Exception('Error al recuperar el registro creado');
      }
      
      return createdRecord;
    } catch (e) {
      throw Exception('Error al crear registro de glucosa: $e');
    }
  }

  // Obtener registros de glucosa por usuario
  Future<List<GlucoseModel>> getGlucoseByUserId(int userId) async {
    try {
      // Intentar sincronizar primero
      try {
        await _syncService.syncGlucose();
      } catch (e) {
        print('Error al sincronizar con Firebase: $e');
      }

      return await _sqliteService.getGlucoseByUserId(userId);
    } catch (e) {
      throw Exception('Error al obtener registros de glucosa: $e');
    }
  }

  // Obtener registros de glucosa por rango de fechas
  Future<List<GlucoseModel>> getGlucoseByDateRange(
    int userId,
    DateTime startDate,
    DateTime endDate,
  ) async {
    try {
      // Intentar sincronizar primero
      try {
        await _syncService.syncGlucose();
      } catch (e) {
        print('Error al sincronizar con Firebase: $e');
      }

      return await _sqliteService.getGlucoseByUserIdAndDateRange(
        userId,
        startDate,
        endDate,
      );
    } catch (e) {
      throw Exception('Error al obtener registros por rango de fechas: $e');
    }
  }

  // Obtener último registro de glucosa
  Future<GlucoseModel?> getLastGlucoseRecord(int userId) async {
    try {
      final records = await getGlucoseByUserId(userId);
      if (records.isNotEmpty) {
        return records.first; // Ya están ordenados por fecha descendente
      }
      return null;
    } catch (e) {
      throw Exception('Error al obtener último registro: $e');
    }
  }

  // Obtener registros de hoy
  Future<List<GlucoseModel>> getTodayGlucoseRecords(int userId) async {
    try {
      final now = DateTime.now();
      final startOfDay = DateTime(now.year, now.month, now.day);
      final endOfDay = DateTime(now.year, now.month, now.day, 23, 59, 59);
      
      return await getGlucoseByDateRange(userId, startOfDay, endOfDay);
    } catch (e) {
      throw Exception('Error al obtener registros de hoy: $e');
    }
  }

  // Obtener registros de la semana actual
  Future<List<GlucoseModel>> getWeekGlucoseRecords(int userId) async {
    try {
      final now = DateTime.now();
      final startOfWeek = now.subtract(Duration(days: now.weekday - 1));
      final startOfWeekDay = DateTime(startOfWeek.year, startOfWeek.month, startOfWeek.day);
      final endOfWeek = startOfWeekDay.add(Duration(days: 6, hours: 23, minutes: 59, seconds: 59));
      
      return await getGlucoseByDateRange(userId, startOfWeekDay, endOfWeek);
    } catch (e) {
      throw Exception('Error al obtener registros de la semana: $e');
    }
  }

  // Obtener registros del mes actual
  Future<List<GlucoseModel>> getMonthGlucoseRecords(int userId) async {
    try {
      final now = DateTime.now();
      final startOfMonth = DateTime(now.year, now.month, 1);
      final endOfMonth = DateTime(now.year, now.month + 1, 0, 23, 59, 59);
      
      return await getGlucoseByDateRange(userId, startOfMonth, endOfMonth);
    } catch (e) {
      throw Exception('Error al obtener registros del mes: $e');
    }
  }

  // Actualizar registro de glucosa
  Future<GlucoseModel> updateGlucoseRecord(GlucoseModel glucose) async {
    try {
      // Actualizar en SQLite
      await _sqliteService.updateGlucose(glucose);
      
      // Intentar sincronizar con Firebase
      try {
        await _syncService.syncGlucose();
      } catch (e) {
        print('Error al sincronizar con Firebase: $e');
      }

      // Obtener registro actualizado
      final updatedRecord = await _sqliteService.getGlucoseById(glucose.idGlucosa!);
      if (updatedRecord == null) {
        throw Exception('Error al recuperar el registro actualizado');
      }
      
      return updatedRecord;
    } catch (e) {
      throw Exception('Error al actualizar registro de glucosa: $e');
    }
  }

  // Eliminar registro de glucosa
  Future<bool> deleteGlucoseRecord(int glucoseId) async {
    try {
      await _sqliteService.deleteGlucose(glucoseId);
      
      // Intentar sincronizar con Firebase
      try {
        await _syncService.syncGlucose();
      } catch (e) {
        print('Error al sincronizar con Firebase: $e');
      }

      return true;
    } catch (e) {
      throw Exception('Error al eliminar registro de glucosa: $e');
    }
  }

  // Obtener estadísticas de glucosa
  Future<Map<String, dynamic>> getGlucoseStatistics(int userId, DateTime startDate, DateTime endDate) async {
    try {
      final records = await getGlucoseByDateRange(userId, startDate, endDate);
      
      if (records.isEmpty) {
        return {
          'count': 0,
          'average': 0.0,
          'min': 0.0,
          'max': 0.0,
          'normalCount': 0,
          'highCount': 0,
          'lowCount': 0,
        };
      }

      final levels = records.map((r) => r.nivelGlucosa).toList();
      final average = levels.reduce((a, b) => a + b) / levels.length;
      final min = levels.reduce((a, b) => a < b ? a : b);
      final max = levels.reduce((a, b) => a > b ? a : b);
      
      final normalCount = records.where((r) => r.isNormal).length;
      final highCount = records.where((r) => r.isHigh).length;
      final lowCount = records.where((r) => r.isLow).length;

      return {
        'count': records.length,
        'average': average,
        'min': min,
        'max': max,
        'normalCount': normalCount,
        'highCount': highCount,
        'lowCount': lowCount,
      };
    } catch (e) {
      throw Exception('Error al calcular estadísticas: $e');
    }
  }

  // Obtener registros para gráficos
  Future<List<Map<String, dynamic>>> getGlucoseChartData(
    int userId,
    DateTime startDate,
    DateTime endDate,
  ) async {
    try {
      final records = await getGlucoseByDateRange(userId, startDate, endDate);
      
      return records.map((record) => {
        'date': record.fechaHora,
        'value': record.nivelGlucosa,
        'status': record.status,
      }).toList();
    } catch (e) {
      throw Exception('Error al obtener datos para gráfico: $e');
    }
  }

  // Verificar si hay registros pendientes de sincronización
  Future<bool> hasUnsyncedRecords() async {
    try {
      final unsyncedRecords = await _sqliteService.getUnsyncedGlucose();
      return unsyncedRecords.isNotEmpty;
    } catch (e) {
      throw Exception('Error al verificar registros no sincronizados: $e');
    }
  }

  // Forzar sincronización
  Future<void> forceSyncGlucose() async {
    try {
      await _syncService.syncGlucose();
    } catch (e) {
      throw Exception('Error al forzar sincronización: $e');
    }
  }
}
