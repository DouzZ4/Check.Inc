import '../../domain/entities/glucose.dart';

class GlucoseModel extends Glucose {
  GlucoseModel({
    super.idGlucosa,
    required super.nivelGlucosa,
    required super.fechaHora,
    required super.idUsuario,
    super.createdAt,
    super.updatedAt,
    super.synced,
  });

  // Factory constructor para crear desde JSON
  factory GlucoseModel.fromJson(Map<String, dynamic> json) {
    return GlucoseModel(
      idGlucosa: json['idGlucosa'],
      nivelGlucosa: json['nivelGlucosa']?.toDouble() ?? 0.0,
      fechaHora: json['fechaHora'] != null 
          ? DateTime.parse(json['fechaHora']) 
          : DateTime.now(),
      idUsuario: json['idUsuario'] ?? 0,
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
  factory GlucoseModel.fromSQLite(Map<String, dynamic> map) {
    return GlucoseModel(
      idGlucosa: map['idGlucosa'],
      nivelGlucosa: map['nivelGlucosa']?.toDouble() ?? 0.0,
      fechaHora: DateTime.parse(map['fechaHora']),
      idUsuario: map['idUsuario'],
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
  factory GlucoseModel.fromFirebase(Map<String, dynamic> data, String id) {
    return GlucoseModel(
      idGlucosa: int.tryParse(id),
      nivelGlucosa: data['nivelGlucosa']?.toDouble() ?? 0.0,
      fechaHora: data['fechaHora'] != null 
          ? DateTime.parse(data['fechaHora']) 
          : DateTime.now(),
      idUsuario: data['idUsuario'] ?? 0,
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
      'nivelGlucosa': nivelGlucosa,
      'fechaHora': fechaHora.toIso8601String(),
      'idUsuario': idUsuario,
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }

  // Convertir a Map para SQLite
  Map<String, dynamic> toSQLite() {
    return {
      'idGlucosa': idGlucosa,
      'nivelGlucosa': nivelGlucosa,
      'fechaHora': fechaHora.toIso8601String(),
      'idUsuario': idUsuario,
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
      'synced': synced == true ? 1 : 0,
    };
  }

  // Crear copia con nuevos valores
  @override
  GlucoseModel copyWith({
    int? idGlucosa,
    double? nivelGlucosa,
    DateTime? fechaHora,
    int? idUsuario,
    DateTime? createdAt,
    DateTime? updatedAt,
    bool? synced,
  }) {
    return GlucoseModel(
      idGlucosa: idGlucosa ?? this.idGlucosa,
      nivelGlucosa: nivelGlucosa ?? this.nivelGlucosa,
      fechaHora: fechaHora ?? this.fechaHora,
      idUsuario: idUsuario ?? this.idUsuario,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      synced: synced ?? this.synced,
    );
  }
}
