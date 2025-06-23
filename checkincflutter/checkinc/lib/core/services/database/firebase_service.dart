import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart' as firebase_auth;
import '../../constants/app_constants.dart';
import '../../../data/models/user_model.dart';
import '../../../data/models/glucose_model.dart';

class FirebaseService {
  static final FirebaseFirestore _firestore = FirebaseFirestore.instance;
  static final firebase_auth.FirebaseAuth _auth = firebase_auth.FirebaseAuth.instance;
  static final FirebaseService _instance = FirebaseService._internal();
  
  factory FirebaseService() => _instance;
  FirebaseService._internal();

  // MÉTODOS DE AUTENTICACIÓN

  Future<firebase_auth.User?> getCurrentUser() async {
    return _auth.currentUser;
  }

  Future<firebase_auth.UserCredential?> signInWithEmailAndPassword(
    String email, 
    String password
  ) async {
    try {
      return await _auth.signInWithEmailAndPassword(
        email: email,
        password: password,
      );
    } catch (e) {
      throw Exception('Error al iniciar sesión: $e');
    }
  }

  Future<firebase_auth.UserCredential?> createUserWithEmailAndPassword(
    String email, 
    String password
  ) async {
    try {
      return await _auth.createUserWithEmailAndPassword(
        email: email,
        password: password,
      );
    } catch (e) {
      throw Exception('Error al crear usuario: $e');
    }
  }

  Future<void> signOut() async {
    await _auth.signOut();
  }

  // MÉTODOS PARA USUARIOS

  Future<String> createUser(UserModel user) async {
    try {
      final docRef = await _firestore
          .collection(AppConstants.usersCollection)
          .add(user.toJson());
      return docRef.id;
    } catch (e) {
      throw Exception('Error al crear usuario en Firebase: $e');
    }
  }

  Future<UserModel?> getUserById(String id) async {
    try {
      final doc = await _firestore
          .collection(AppConstants.usersCollection)
          .doc(id)
          .get();
      
      if (doc.exists && doc.data() != null) {
        return UserModel.fromFirebase(doc.data()!, doc.id);
      }
      return null;
    } catch (e) {
      throw Exception('Error al obtener usuario de Firebase: $e');
    }
  }

  Future<UserModel?> getUserByEmail(String email) async {
    try {
      final querySnapshot = await _firestore
          .collection(AppConstants.usersCollection)
          .where('correo', isEqualTo: email)
          .limit(1)
          .get();
      
      if (querySnapshot.docs.isNotEmpty) {
        final doc = querySnapshot.docs.first;
        return UserModel.fromFirebase(doc.data(), doc.id);
      }
      return null;
    } catch (e) {
      throw Exception('Error al buscar usuario por email: $e');
    }
  }

  Future<UserModel?> getUserByUsername(String username) async {
    try {
      final querySnapshot = await _firestore
          .collection(AppConstants.usersCollection)
          .where('user', isEqualTo: username)
          .limit(1)
          .get();
      
      if (querySnapshot.docs.isNotEmpty) {
        final doc = querySnapshot.docs.first;
        return UserModel.fromFirebase(doc.data(), doc.id);
      }
      return null;
    } catch (e) {
      throw Exception('Error al buscar usuario por username: $e');
    }
  }

  Future<List<UserModel>> getAllUsers() async {
    try {
      final querySnapshot = await _firestore
          .collection(AppConstants.usersCollection)
          .get();
      
      return querySnapshot.docs
          .map((doc) => UserModel.fromFirebase(doc.data(), doc.id))
          .toList();
    } catch (e) {
      throw Exception('Error al obtener todos los usuarios: $e');
    }
  }

  Future<void> updateUser(String id, UserModel user) async {
    try {
      await _firestore
          .collection(AppConstants.usersCollection)
          .doc(id)
          .update(user.toJson());
    } catch (e) {
      throw Exception('Error al actualizar usuario en Firebase: $e');
    }
  }

  Future<void> deleteUser(String id) async {
    try {
      await _firestore
          .collection(AppConstants.usersCollection)
          .doc(id)
          .delete();
    } catch (e) {
      throw Exception('Error al eliminar usuario de Firebase: $e');
    }
  }

  // MÉTODOS PARA GLUCOSA

  Future<String> createGlucose(GlucoseModel glucose) async {
    try {
      final docRef = await _firestore
          .collection(AppConstants.glucoseCollection)
          .add(glucose.toJson());
      return docRef.id;
    } catch (e) {
      throw Exception('Error al crear registro de glucosa en Firebase: $e');
    }
  }

  Future<GlucoseModel?> getGlucoseById(String id) async {
    try {
      final doc = await _firestore
          .collection(AppConstants.glucoseCollection)
          .doc(id)
          .get();
      
      if (doc.exists && doc.data() != null) {
        return GlucoseModel.fromFirebase(doc.data()!, doc.id);
      }
      return null;
    } catch (e) {
      throw Exception('Error al obtener registro de glucosa: $e');
    }
  }

  Future<List<GlucoseModel>> getGlucoseByUserId(int userId) async {
    try {
      final querySnapshot = await _firestore
          .collection(AppConstants.glucoseCollection)
          .where('idUsuario', isEqualTo: userId)
          .orderBy('fechaHora', descending: true)
          .get();
      
      return querySnapshot.docs
          .map((doc) => GlucoseModel.fromFirebase(doc.data(), doc.id))
          .toList();
    } catch (e) {
      throw Exception('Error al obtener registros de glucosa del usuario: $e');
    }
  }

  Future<List<GlucoseModel>> getGlucoseByUserIdAndDateRange(
    int userId,
    DateTime startDate,
    DateTime endDate,
  ) async {
    try {
      final querySnapshot = await _firestore
          .collection(AppConstants.glucoseCollection)
          .where('idUsuario', isEqualTo: userId)
          .where('fechaHora', isGreaterThanOrEqualTo: startDate.toIso8601String())
          .where('fechaHora', isLessThanOrEqualTo: endDate.toIso8601String())
          .orderBy('fechaHora', descending: true)
          .get();
      
      return querySnapshot.docs
          .map((doc) => GlucoseModel.fromFirebase(doc.data(), doc.id))
          .toList();
    } catch (e) {
      throw Exception('Error al obtener registros de glucosa por rango de fechas: $e');
    }
  }

  Future<void> updateGlucose(String id, GlucoseModel glucose) async {
    try {
      await _firestore
          .collection(AppConstants.glucoseCollection)
          .doc(id)
          .update(glucose.toJson());
    } catch (e) {
      throw Exception('Error al actualizar registro de glucosa: $e');
    }
  }

  Future<void> deleteGlucose(String id) async {
    try {
      await _firestore
          .collection(AppConstants.glucoseCollection)
          .doc(id)
          .delete();
    } catch (e) {
      throw Exception('Error al eliminar registro de glucosa: $e');
    }
  }

  // MÉTODOS DE SINCRONIZACIÓN

  Future<void> syncUserToFirebase(UserModel user) async {
    try {
      // Buscar si el usuario ya existe en Firebase
      final existingUser = await getUserByEmail(user.correo);
      
      if (existingUser != null) {
        // Actualizar usuario existente
        await updateUser(existingUser.idUsuario.toString(), user);
      } else {
        // Crear nuevo usuario
        await createUser(user);
      }
    } catch (e) {
      throw Exception('Error al sincronizar usuario: $e');
    }
  }

  Future<void> syncGlucoseToFirebase(GlucoseModel glucose) async {
    try {
      await createGlucose(glucose);
    } catch (e) {
      throw Exception('Error al sincronizar registro de glucosa: $e');
    }
  }

  // MÉTODOS DE BATCH PARA SINCRONIZACIÓN MASIVA

  Future<void> batchSyncUsers(List<UserModel> users) async {
    try {
      final batch = _firestore.batch();
      
      for (final user in users) {
        final docRef = _firestore
            .collection(AppConstants.usersCollection)
            .doc();
        batch.set(docRef, user.toJson());
      }
      
      await batch.commit();
    } catch (e) {
      throw Exception('Error en sincronización masiva de usuarios: $e');
    }
  }

  Future<void> batchSyncGlucose(List<GlucoseModel> glucoseList) async {
    try {
      final batch = _firestore.batch();
      
      for (final glucose in glucoseList) {
        final docRef = _firestore
            .collection(AppConstants.glucoseCollection)
            .doc();
        batch.set(docRef, glucose.toJson());
      }
      
      await batch.commit();
    } catch (e) {
      throw Exception('Error en sincronización masiva de glucosa: $e');
    }
  }

  // MÉTODOS DE UTILIDAD

  Future<bool> isConnected() async {
    try {
      await _firestore.enableNetwork();
      return true;
    } catch (e) {
      return false;
    }
  }

  Stream<QuerySnapshot> getUsersStream() {
    return _firestore
        .collection(AppConstants.usersCollection)
        .snapshots();
  }

  Stream<QuerySnapshot> getGlucoseStreamByUserId(int userId) {
    return _firestore
        .collection(AppConstants.glucoseCollection)
        .where('idUsuario', isEqualTo: userId)
        .orderBy('fechaHora', descending: true)
        .snapshots();
  }
}
