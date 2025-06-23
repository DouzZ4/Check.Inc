import 'dart:convert';
import 'package:crypto/crypto.dart';
import '../../domain/entities/user.dart';

class UserModel extends User {
  UserModel({
    super.idUsuario,
    required super.user,
    required super.password,
    required super.documento,
    required super.nombres,
    required super.apellidos,
    required super.correo,
    required super.edad,
    required super.idRol,
    super.createdAt,
    super.updatedAt,
    super.synced,
  });

  // Factory constructor para crear desde JSON
  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      idUsuario: json['idUsuario'],
      user: json['user'] ?? '',
      password: json['password'] ?? '',
      documento: json['documento'] ?? 0,
      nombres: json['nombres'] ?? '',
      apellidos: json['apellidos'] ?? '',
      correo: json['correo'] ?? '',
      edad: json['edad'] ?? 0,
      idRol: json['idRol'] ?? 2,
      createdAt: json['createdAt'] != null 
          ? DateTime.parse(json['createdAt']) 
          : null,
      updatedAt: json['updatedAt'] != null 
          ? DateTime.parse(json['updatedAt']) 
          : null,
      synced: json['synced'] == 1 || json['synced'] == true,
    );
  }

  // Factory constructor para crear desde SQLite
  factory UserModel.fromSQLite(Map<String, dynamic> map) {
    return UserModel(
      idUsuario: map['idUsuario'],
      user: map['user'],
      password: map['password'],
      documento: map['documento'],
      nombres: map['nombres'],
      apellidos: map['apellidos'],
      correo: map['correo'],
      edad: map['edad'],
      idRol: map['idRol'],
      createdAt: map['createdAt'] != null 
          ? DateTime.parse(map['createdAt']) 
          : null,
      updatedAt: map['updatedAt'] != null 
          ? DateTime.parse(map['updatedAt']) 
          : null,
      synced: map['synced'] == 1,
    );
  }

  // Factory constructor para crear desde Firebase
  factory UserModel.fromFirebase(Map<String, dynamic> data, String id) {
    return UserModel(
      idUsuario: int.tryParse(id),
      user: data['user'] ?? '',
      password: data['password'] ?? '',
      documento: data['documento'] ?? 0,
      nombres: data['nombres'] ?? '',
      apellidos: data['apellidos'] ?? '',
      correo: data['correo'] ?? '',
      edad: data['edad'] ?? 0,
      idRol: data['idRol'] ?? 2,
      createdAt: data['createdAt'] != null 
          ? DateTime.parse(data['createdAt']) 
          : null,
      updatedAt: data['updatedAt'] != null 
          ? DateTime.parse(data['updatedAt']) 
          : null,
      synced: true,
    );
  }

  // Convertir a JSON para Firebase
  Map<String, dynamic> toJson() {
    return {
      'user': user,
      'password': password,
      'documento': documento,
      'nombres': nombres,
      'apellidos': apellidos,
      'correo': correo,
      'edad': edad,
      'idRol': idRol,
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }

  // Convertir a Map para SQLite
  Map<String, dynamic> toSQLite() {
    return {
      'idUsuario': idUsuario,
      'user': user,
      'password': password,
      'documento': documento,
      'nombres': nombres,
      'apellidos': apellidos,
      'correo': correo,
      'edad': edad,
      'idRol': idRol,
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
      'synced': synced == true ? 1 : 0,
    };
  }

  // Método para hashear la contraseña
  static String hashPassword(String password) {
    var bytes = utf8.encode(password);
    var digest = sha256.convert(bytes);
    return digest.toString();
  }

  // Método para verificar contraseña
  bool verifyPassword(String inputPassword) {
    return password == hashPassword(inputPassword);
  }

  // Crear copia con nuevos valores
  @override
  UserModel copyWith({
    int? idUsuario,
    String? user,
    String? password,
    int? documento,
    String? nombres,
    String? apellidos,
    String? correo,
    int? edad,
    int? idRol,
    DateTime? createdAt,
    DateTime? updatedAt,
    bool? synced,
  }) {
    return UserModel(
      idUsuario: idUsuario ?? this.idUsuario,
      user: user ?? this.user,
      password: password ?? this.password,
      documento: documento ?? this.documento,
      nombres: nombres ?? this.nombres,
      apellidos: apellidos ?? this.apellidos,
      correo: correo ?? this.correo,
      edad: edad ?? this.edad,
      idRol: idRol ?? this.idRol,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      synced: synced ?? this.synced,
    );
  }
}
