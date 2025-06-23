class Glucose {
  final int? idGlucosa;
  final double nivelGlucosa;
  final DateTime fechaHora;
  final int idUsuario;
  final DateTime? createdAt;
  final DateTime? updatedAt;
  final bool? synced;

  Glucose({
    this.idGlucosa,
    required this.nivelGlucosa,
    required this.fechaHora,
    required this.idUsuario,
    this.createdAt,
    this.updatedAt,
    this.synced = false,
  });

  Glucose copyWith({
    int? idGlucosa,
    double? nivelGlucosa,
    DateTime? fechaHora,
    int? idUsuario,
    DateTime? createdAt,
    DateTime? updatedAt,
    bool? synced,
  }) {
    return Glucose(
      idGlucosa: idGlucosa ?? this.idGlucosa,
      nivelGlucosa: nivelGlucosa ?? this.nivelGlucosa,
      fechaHora: fechaHora ?? this.fechaHora,
      idUsuario: idUsuario ?? this.idUsuario,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      synced: synced ?? this.synced,
    );
  }

  // Métodos de utilidad para clasificar los niveles de glucosa
  bool get isNormal => 
      nivelGlucosa >= 70.0 && nivelGlucosa <= 140.0;
  
  bool get isHigh => nivelGlucosa > 140.0;
  
  bool get isLow => nivelGlucosa < 70.0;
  
  bool get isVeryHigh => nivelGlucosa > 180.0;
  
  bool get isVeryLow => nivelGlucosa < 50.0;
  
  String get status {
    if (isVeryHigh) return 'Muy Alto';
    if (isHigh) return 'Alto';
    if (isVeryLow) return 'Muy Bajo';
    if (isLow) return 'Bajo';
    return 'Normal';
  }
}
