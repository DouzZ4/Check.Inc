import '../models/user_model.dart';
import '../../core/services/database/sqlite_service.dart';
import '../../core/services/database/firebase_service.dart';
import '../../core/services/sync_service.dart';

class UserRepository {
  final SQLiteService _sqliteService = SQLiteService();
  final FirebaseService _firebaseService = FirebaseService();
  final SyncService _syncService = SyncService();
  
  static final UserRepository _instance = UserRepository._internal();
  
  factory UserRepository() => _instance;
  
  UserRepository._internal();

  // Registro de usuario
  Future<UserModel> registerUser(UserModel user) async {
    try {
      // Crear usuario en SQLite
      final localId = await _sqliteService.insertUser(user);
      
      // Intentar sincronizar con Firebase si hay conexión
      try {
        await _syncService.syncUsers();
      } catch (e) {
        print('Error al sincronizar con Firebase: $e');
        // No lanzamos el error ya que el usuario se guardó localmente
      }

      // Obtener el usuario creado con su ID
      final createdUser = await _sqliteService.getUserById(localId);
      if (createdUser == null) {
        throw Exception('Error al recuperar el usuario creado');
      }
      
      return createdUser;
    } catch (e) {
      throw Exception('Error al registrar usuario: $e');
    }
  }

  // Login de usuario
  Future<UserModel?> loginUser(String username, String password) async {
    try {
      // Buscar usuario en SQLite
      final user = await _sqliteService.getUserByUsername(username);
      
      if (user != null && user.verifyPassword(password)) {
        // Intentar sincronizar con Firebase si hay conexión
        try {
          await _syncService.syncUsers();
        } catch (e) {
          print('Error al sincronizar con Firebase: $e');
          // No lanzamos el error ya que podemos continuar con los datos locales
        }
        
        return user;
      }
      
      return null;
    } catch (e) {
      throw Exception('Error al iniciar sesión: $e');
    }
  }

  // Actualizar usuario
  Future<UserModel> updateUser(UserModel user) async {
    try {
      // Actualizar en SQLite
      await _sqliteService.updateUser(user);
      
      // Intentar sincronizar con Firebase
      try {
        await _syncService.syncUsers();
      } catch (e) {
        print('Error al sincronizar con Firebase: $e');
      }

      // Obtener usuario actualizado
      final updatedUser = await _sqliteService.getUserById(user.idUsuario!);
      if (updatedUser == null) {
        throw Exception('Error al recuperar el usuario actualizado');
      }
      
      return updatedUser;
    } catch (e) {
      throw Exception('Error al actualizar usuario: $e');
    }
  }

  // Obtener usuario por ID
  Future<UserModel?> getUserById(int id) async {
    try {
      return await _sqliteService.getUserById(id);
    } catch (e) {
      throw Exception('Error al obtener usuario: $e');
    }
  }

  // Verificar si existe un usuario
  Future<bool> userExists(String username) async {
    try {
      final user = await _sqliteService.getUserByUsername(username);
      return user != null;
    } catch (e) {
      throw Exception('Error al verificar usuario: $e');
    }
  }

  // Verificar si existe un email
  Future<bool> emailExists(String email) async {
    try {
      final user = await _sqliteService.getUserByEmail(email);
      return user != null;
    } catch (e) {
      throw Exception('Error al verificar email: $e');
    }
  }

  // Cambiar contraseña
  Future<bool> changePassword(int userId, String currentPassword, String newPassword) async {
    try {
      final user = await _sqliteService.getUserById(userId);
      if (user == null) {
        throw Exception('Usuario no encontrado');
      }

      if (!user.verifyPassword(currentPassword)) {
        return false;
      }

      final updatedUser = user.copyWith(
        password: UserModel.hashPassword(newPassword),
        updatedAt: DateTime.now(),
      );

      await _sqliteService.updateUser(updatedUser);
      
      // Intentar sincronizar con Firebase
      try {
        await _syncService.syncUsers();
      } catch (e) {
        print('Error al sincronizar con Firebase: $e');
      }

      return true;
    } catch (e) {
      throw Exception('Error al cambiar contraseña: $e');
    }
  }

  // Recuperar contraseña (enviar email)
  Future<bool> requestPasswordReset(String email) async {
    try {
      final user = await _sqliteService.getUserByEmail(email);
      if (user == null) {
        return false;
      }

      // Aquí se implementaría la lógica para enviar el email de recuperación
      // Por ahora solo verificamos que el usuario existe
      return true;
    } catch (e) {
      throw Exception('Error al solicitar recuperación de contraseña: $e');
    }
  }

  // Obtener todos los usuarios (solo para admin)
  Future<List<UserModel>> getAllUsers() async {
    try {
      return await _sqliteService.getAllUsers();
    } catch (e) {
      throw Exception('Error al obtener usuarios: $e');
    }
  }

  // Eliminar usuario (solo para admin o el propio usuario)
  Future<bool> deleteUser(int userId) async {
    try {
      await _sqliteService.deleteUser(userId);
      
      // Intentar sincronizar con Firebase
      try {
        await _syncService.syncUsers();
      } catch (e) {
        print('Error al sincronizar con Firebase: $e');
      }

      return true;
    } catch (e) {
      throw Exception('Error al eliminar usuario: $e');
    }
  }
}
