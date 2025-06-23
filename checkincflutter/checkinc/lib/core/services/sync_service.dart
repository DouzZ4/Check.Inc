import 'package:shared_preferences/shared_preferences.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import '../constants/app_constants.dart';
import 'database/sqlite_service.dart';
import 'database/firebase_service.dart';
import '../../data/models/user_model.dart';
import '../../data/models/glucose_model.dart';

class SyncService {
  final SQLiteService _sqliteService = SQLiteService();
  final FirebaseService _firebaseService = FirebaseService();
  SharedPreferences? _prefs;
  
  static final SyncService _instance = SyncService._internal();
  
  factory SyncService() => _instance;
  
  SyncService._internal();

  Future<SharedPreferences> get prefs async {
    _prefs ??= await SharedPreferences.getInstance();
    return _prefs!;
  }

  // Verificar conectividad
  Future<bool> _hasInternetConnection() async {
    final connectivityResult = await Connectivity().checkConnectivity();
    return connectivityResult != ConnectivityResult.none;
  }

  // Obtener última fecha de sincronización
  Future<DateTime?> _getLastSyncTime() async {
    final preferences = await prefs;
    final lastSyncStr = preferences.getString(AppConstants.lastSyncKey);
    return lastSyncStr != null ? DateTime.parse(lastSyncStr) : null;
  }

  // Actualizar fecha de última sincronización
  Future<void> _updateLastSyncTime() async {
    final preferences = await prefs;
    await preferences.setString(
      AppConstants.lastSyncKey,
      DateTime.now().toIso8601String(),
    );
  }

  // Sincronizar usuarios
  Future<void> syncUsers() async {
    try {
      if (!await _hasInternetConnection()) {
        throw Exception('No hay conexión a internet');
      }

      // Obtener usuarios no sincronizados de SQLite
      final unsyncedUsers = await _sqliteService.getUnsyncedUsers();
      
      if (unsyncedUsers.isEmpty) return;

      // Sincronizar usuarios en lotes
      await _firebaseService.batchSyncUsers(unsyncedUsers);

      // Marcar usuarios como sincronizados en SQLite
      for (var user in unsyncedUsers) {
        if (user.idUsuario != null) {
          await _sqliteService.markUserAsSynced(user.idUsuario!);
        }
      }
    } catch (e) {
      throw Exception('Error en sincronización de usuarios: $e');
    }
  }

  // Sincronizar registros de glucosa
  Future<void> syncGlucose() async {
    try {
      if (!await _hasInternetConnection()) {
        throw Exception('No hay conexión a internet');
      }

      // Obtener registros no sincronizados de SQLite
      final unsyncedGlucose = await _sqliteService.getUnsyncedGlucose();
      
      if (unsyncedGlucose.isEmpty) return;

      // Sincronizar registros en lotes
      await _firebaseService.batchSyncGlucose(unsyncedGlucose);

      // Marcar registros como sincronizados en SQLite
      for (var glucose in unsyncedGlucose) {
        if (glucose.idGlucosa != null) {
          await _sqliteService.markGlucoseAsSynced(glucose.idGlucosa!);
        }
      }
    } catch (e) {
      throw Exception('Error en sincronización de registros de glucosa: $e');
    }
  }

  // Sincronizar datos desde Firebase a SQLite
  Future<void> syncFromFirebase() async {
    try {
      if (!await _hasInternetConnection()) {
        throw Exception('No hay conexión a internet');
      }

      final lastSync = await _getLastSyncTime();

      // Sincronizar usuarios
      final users = await _firebaseService.getAllUsers();
      for (var user in users) {
        final localUser = await _sqliteService.getUserById(user.idUsuario!);
        if (localUser == null) {
          // Usuario nuevo, insertar en SQLite
          await _sqliteService.insertUser(user);
        } else if (user.updatedAt != null && 
                  (lastSync == null || user.updatedAt!.isAfter(lastSync))) {
          // Usuario actualizado, actualizar en SQLite
          await _sqliteService.updateUser(user);
        }
      }

      // Sincronizar registros de glucosa por usuario
      for (var user in users) {
        final glucoseRecords = await _firebaseService.getGlucoseByUserId(user.idUsuario!);
        for (var glucose in glucoseRecords) {
          final localGlucose = await _sqliteService.getGlucoseById(glucose.idGlucosa!);
          if (localGlucose == null) {
            // Registro nuevo, insertar en SQLite
            await _sqliteService.insertGlucose(glucose);
          } else if (glucose.updatedAt != null && 
                    (lastSync == null || glucose.updatedAt!.isAfter(lastSync))) {
            // Registro actualizado, actualizar en SQLite
            await _sqliteService.updateGlucose(glucose);
          }
        }
      }

      await _updateLastSyncTime();
    } catch (e) {
      throw Exception('Error en sincronización desde Firebase: $e');
    }
  }

  // Sincronización bidireccional completa
  Future<void> performFullSync() async {
    try {
      // Primero sincronizar datos locales a Firebase
      await syncUsers();
      await syncGlucose();

      // Luego sincronizar datos de Firebase a local
      await syncFromFirebase();

      await _updateLastSyncTime();
    } catch (e) {
      throw Exception('Error en sincronización completa: $e');
    }
  }

  // Sincronización automática periódica
  Future<void> startAutoSync(Duration interval) async {
    while (true) {
      try {
        await performFullSync();
      } catch (e) {
        print('Error en sincronización automática: $e');
      }
      await Future.delayed(interval);
    }
  }

  // Verificar si es necesario sincronizar
  Future<bool> needsSync() async {
    final lastSync = await _getLastSyncTime();
    if (lastSync == null) return true;

    final unsyncedUsers = await _sqliteService.getUnsyncedUsers();
    if (unsyncedUsers.isNotEmpty) return true;

    final unsyncedGlucose = await _sqliteService.getUnsyncedGlucose();
    if (unsyncedGlucose.isNotEmpty) return true;

    // Verificar si han pasado más de 15 minutos desde la última sincronización
    final fifteenMinutesAgo = DateTime.now().subtract(Duration(minutes: 15));
    return lastSync.isBefore(fifteenMinutesAgo);
  }

  // Limpiar datos de sincronización
  Future<void> clearSyncData() async {
    final preferences = await prefs;
    await preferences.remove(AppConstants.lastSyncKey);
  }
}
