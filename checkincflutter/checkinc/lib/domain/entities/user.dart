class User {
  final int? idUsuario;
  final String user;
  final String password;
  final int documento;
  final String nombres;
  final String apellidos;
  final String correo;
  final int edad;
  final int idRol;
  final DateTime? createdAt;
  final DateTime? updatedAt;
  final bool? synced;

  User({
    this.idUsuario,
    required this.user,
    required this.password,
    required this.documento,
    required this.nombres,
    required this.apellidos,
    required this.correo,
    required this.edad,
    required this.idRol,
    this.createdAt,
    this.updatedAt,
    this.synced = false,
  });

  User copyWith({
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
    return User(
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

  String get fullName => '$nombres $apellidos';
  
  bool get isAdmin => idRol == 1;
  bool get isPaciente => idRol == 2;
}
