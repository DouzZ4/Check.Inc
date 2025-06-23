import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../data/models/user_model.dart';
import '../../data/repositories/user_repository.dart';
import '../../core/constants/app_constants.dart';

class AuthViewModel extends ChangeNotifier {
  final UserRepository _userRepository = UserRepository();
  
  // Estado del ViewModel
  bool _isLoading = false;
  String? _errorMessage;
  UserModel? _currentUser;
  bool _isLoggedIn = false;

  // Getters
  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;
  UserModel? get currentUser => _currentUser;
  bool get isLoggedIn => _isLoggedIn;

  // Constructor
  AuthViewModel() {
    _checkLoginStatus();
  }

  // Verificar si el usuario ya está logueado
  Future<void> _checkLoginStatus() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final isLoggedIn = prefs.getBool(AppConstants.isLoggedInKey) ?? false;
      final userId = prefs.getInt(AppConstants.currentUserIdKey);

      if (isLoggedIn && userId != null) {
        final user = await _userRepository.getUserById(userId);
        if (user != null) {
          _currentUser = user;
          _isLoggedIn = true;
          notifyListeners();
        } else {
          await _clearLoginData();
        }
      }
    } catch (e) {
      _errorMessage = 'Error al verificar estado de sesión: $e';
      notifyListeners();
    }
  }

  // Login
  Future<bool> login(String username, String password) async {
    _setLoading(true);
    _clearError();

    try {
      // Validaciones básicas
      if (username.trim().isEmpty || password.trim().isEmpty) {
        _setError('Usuario y contraseña son requeridos');
        return false;
      }

      final user = await _userRepository.loginUser(username.trim(), password);
      
      if (user != null) {
        _currentUser = user;
        _isLoggedIn = true;
        await _saveLoginData(user);
        _setLoading(false);
        return true;
      } else {
        _setError('Usuario o contraseña incorrectos');
        return false;
      }
    } catch (e) {
      _setError('Error al iniciar sesión: $e');
      return false;
    } finally {
      _setLoading(false);
    }
  }

  // Registro
  Future<bool> register({
    required String username,
    required String password,
    required String confirmPassword,
    required int documento,
    required String nombres,
    required String apellidos,
    required String correo,
    required int edad,
  }) async {
    _setLoading(true);
    _clearError();

    try {
      // Validaciones
      final validationError = _validateRegistrationData(
        username: username,
        password: password,
        confirmPassword: confirmPassword,
        documento: documento,
        nombres: nombres,
        apellidos: apellidos,
        correo: correo,
        edad: edad,
      );

      if (validationError != null) {
        _setError(validationError);
        return false;
      }

      // Verificar si el usuario ya existe
      if (await _userRepository.userExists(username.trim())) {
        _setError('El nombre de usuario ya existe');
        return false;
      }

      // Verificar si el email ya existe
      if (await _userRepository.emailExists(correo.trim())) {
        _setError('El correo electrónico ya está registrado');
        return false;
      }

      // Crear nuevo usuario
      final newUser = UserModel(
        user: username.trim(),
        password: UserModel.hashPassword(password),
        documento: documento,
        nombres: nombres.trim(),
        apellidos: apellidos.trim(),
        correo: correo.trim().toLowerCase(),
        edad: edad,
        idRol: 2, // Paciente por defecto
        createdAt: DateTime.now(),
        updatedAt: DateTime.now(),
      );

      final createdUser = await _userRepository.registerUser(newUser);
      
      _currentUser = createdUser;
      _isLoggedIn = true;
      await _saveLoginData(createdUser);
      _setLoading(false);
      return true;

    } catch (e) {
      _setError('Error al registrar usuario: $e');
      return false;
    } finally {
      _setLoading(false);
    }
  }

  // Logout
  Future<void> logout() async {
    _setLoading(true);
    
    try {
      await _clearLoginData();
      _currentUser = null;
      _isLoggedIn = false;
      _clearError();
    } catch (e) {
      _setError('Error al cerrar sesión: $e');
    } finally {
      _setLoading(false);
    }
  }

  // Actualizar perfil
  Future<bool> updateProfile({
    required String nombres,
    required String apellidos,
    required String correo,
    required int edad,
  }) async {
    if (_currentUser == null) return false;

    _setLoading(true);
    _clearError();

    try {
      // Validaciones básicas
      if (nombres.trim().isEmpty || apellidos.trim().isEmpty || correo.trim().isEmpty) {
        _setError('Todos los campos son requeridos');
        return false;
      }

      if (edad < 1 || edad > 120) {
        _setError('La edad debe estar entre 1 y 120 años');
        return false;
      }

      if (!_isValidEmail(correo.trim())) {
        _setError('Formato de correo electrónico inválido');
        return false;
      }

      // Verificar si el nuevo email ya existe (si es diferente al actual)
      if (correo.trim().toLowerCase() != _currentUser!.correo.toLowerCase()) {
        if (await _userRepository.emailExists(correo.trim())) {
          _setError('El correo electrónico ya está registrado');
          return false;
        }
      }

      final updatedUser = _currentUser!.copyWith(
        nombres: nombres.trim(),
        apellidos: apellidos.trim(),
        correo: correo.trim().toLowerCase(),
        edad: edad,
        updatedAt: DateTime.now(),
      );

      final result = await _userRepository.updateUser(updatedUser);
      _currentUser = result;
      await _saveLoginData(result);
      _setLoading(false);
      return true;

    } catch (e) {
      _setError('Error al actualizar perfil: $e');
      return false;
    } finally {
      _setLoading(false);
    }
  }

  // Cambiar contraseña
  Future<bool> changePassword(String currentPassword, String newPassword, String confirmPassword) async {
    if (_currentUser == null) return false;

    _setLoading(true);
    _clearError();

    try {
      // Validaciones
      if (currentPassword.isEmpty || newPassword.isEmpty || confirmPassword.isEmpty) {
        _setError('Todos los campos son requeridos');
        return false;
      }

      if (newPassword != confirmPassword) {
        _setError('Las contraseñas no coinciden');
        return false;
      }

      if (newPassword.length < 6) {
        _setError('La nueva contraseña debe tener al menos 6 caracteres');
        return false;
      }

      final success = await _userRepository.changePassword(
        _currentUser!.idUsuario!,
        currentPassword,
        newPassword,
      );

      if (success) {
        _setLoading(false);
        return true;
      } else {
        _setError('Contraseña actual incorrecta');
        return false;
      }

    } catch (e) {
      _setError('Error al cambiar contraseña: $e');
      return false;
    } finally {
      _setLoading(false);
    }
  }

  // Solicitar recuperación de contraseña
  Future<bool> requestPasswordReset(String email) async {
    _setLoading(true);
    _clearError();

    try {
      if (email.trim().isEmpty) {
        _setError('El correo electrónico es requerido');
        return false;
      }

      if (!_isValidEmail(email.trim())) {
        _setError('Formato de correo electrónico inválido');
        return false;
      }

      final success = await _userRepository.requestPasswordReset(email.trim());
      
      if (success) {
        _setLoading(false);
        return true;
      } else {
        _setError('No se encontró una cuenta con ese correo electrónico');
        return false;
      }

    } catch (e) {
      _setError('Error al solicitar recuperación de contraseña: $e');
      return false;
    } finally {
      _setLoading(false);
    }
  }

  // Métodos privados de utilidad

  void _setLoading(bool loading) {
    _isLoading = loading;
    notifyListeners();
  }

  void _setError(String error) {
    _errorMessage = error;
    _isLoading = false;
    notifyListeners();
  }

  void _clearError() {
    _errorMessage = null;
    notifyListeners();
  }

  Future<void> _saveLoginData(UserModel user) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(AppConstants.isLoggedInKey, true);
    await prefs.setInt(AppConstants.currentUserIdKey, user.idUsuario!);
  }

  Future<void> _clearLoginData() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(AppConstants.isLoggedInKey);
    await prefs.remove(AppConstants.currentUserIdKey);
  }

  String? _validateRegistrationData({
    required String username,
    required String password,
    required String confirmPassword,
    required int documento,
    required String nombres,
    required String apellidos,
    required String correo,
    required int edad,
  }) {
    if (username.trim().isEmpty) return 'El nombre de usuario es requerido';
    if (username.trim().length < 3) return 'El nombre de usuario debe tener al menos 3 caracteres';
    if (password.isEmpty) return 'La contraseña es requerida';
    if (password.length < 6) return 'La contraseña debe tener al menos 6 caracteres';
    if (password != confirmPassword) return 'Las contraseñas no coinciden';
    if (documento <= 0) return 'El documento es requerido';
    if (nombres.trim().isEmpty) return 'Los nombres son requeridos';
    if (apellidos.trim().isEmpty) return 'Los apellidos son requeridos';
    if (correo.trim().isEmpty) return 'El correo electrónico es requerido';
    if (!_isValidEmail(correo.trim())) return 'Formato de correo electrónico inválido';
    if (edad < 1 || edad > 120) return 'La edad debe estar entre 1 y 120 años';
    
    return null;
  }

  bool _isValidEmail(String email) {
    return RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(email);
  }

  // Limpiar errores manualmente
  void clearError() {
    _clearError();
  }
}
