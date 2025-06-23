import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';
import '../../constants/app_constants.dart';
import '../../../data/models/user_model.dart';
import '../../../data/models/glucose_model.dart';

class SQLiteService {
  static Database? _database;
  static final SQLiteService _instance = SQLiteService._internal();
  
  factory SQLiteService() => _instance;
  SQLiteService._internal();

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDatabase();
    return _database!;
  }

  Future<Database> _initDatabase() async {
    String path = join(await getDatabasesPath(), AppConstants.databaseName);
    
    return await openDatabase(
      path,
      version: AppConstants.databaseVersion,
      onCreate: _createDatabase,
      onUpgrade: _upgradeDatabase,
    );
  }

  Future<void> _createDatabase(Database db, int version) async {
    // Crear tabla de roles
    await db.execute('''
      CREATE TABLE rol (
        idRol INTEGER PRIMARY KEY,
        nombre TEXT NOT NULL UNIQUE CHECK (nombre IN ('admin', 'paciente'))
      )
    ''');

    // Insertar roles por defecto
    await db.insert('rol', {'idRol': 1, 'nombre': 'admin'});
    await db.insert('rol', {'idRol': 2, 'nombre': 'paciente'});

    // Crear tabla de usuarios
    await db.execute('''
      CREATE TABLE usuario (
        idUsuario INTEGER PRIMARY KEY AUTOINCREMENT,
        user TEXT NOT NULL UNIQUE,
        password TEXT NOT NULL,
        documento INTEGER NOT NULL UNIQUE,
        nombres TEXT NOT NULL,
        apellidos TEXT NOT NULL,
        correo TEXT NOT NULL,
        edad INTEGER NOT NULL,
        idRol INTEGER NOT NULL,
        createdAt TEXT,
        updatedAt TEXT,
        synced INTEGER DEFAULT 0,
        FOREIGN KEY (idRol) REFERENCES rol (idRol)
      )
    ''');

    // Crear tabla de glucosa
    await db.execute('''
      CREATE TABLE glucosa (
        idGlucosa INTEGER PRIMARY KEY AUTOINCREMENT,
        nivelGlucosa REAL NOT NULL,
        fechaHora TEXT NOT NULL,
        idUsuario INTEGER NOT NULL,
        createdAt TEXT,
        updatedAt TEXT,
        synced INTEGER DEFAULT 0,
        FOREIGN KEY (idUsuario) REFERENCES usuario (idUsuario)
      )
    ''');

    // Crear índices para mejorar el rendimiento
    await db.execute('CREATE INDEX idx_usuario_user ON usuario(user)');
    await db.execute('CREATE INDEX idx_usuario_documento ON usuario(documento)');
    await db.execute('CREATE INDEX idx_glucosa_usuario ON glucosa(idUsuario)');
    await db.execute('CREATE INDEX idx_glucosa_fecha ON glucosa(fechaHora)');
    await db.execute('CREATE INDEX idx_glucosa_synced ON glucosa(synced)');
    await db.execute('CREATE INDEX idx_usuario_synced ON usuario(synced)');
  }

  Future<void> _upgradeDatabase(Database db, int oldVersion, int newVersion) async {
    // Aquí se pueden agregar migraciones futuras
    if (oldVersion < 2) {
      // Ejemplo de migración futura
      // await db.execute('ALTER TABLE usuario ADD COLUMN newColumn TEXT');
    }
  }

  // MÉTODOS PARA USUARIOS

  Future<int> insertUser(UserModel user) async {
    final db = await database;
    final userWithTimestamp = user.copyWith(
      createdAt: DateTime.now(),
      updatedAt: DateTime.now(),
    );
    return await db.insert('usuario', userWithTimestamp.toSQLite());
  }

  Future<UserModel?> getUserById(int id) async {
    final db = await database;
    final List<Map<String, dynamic>> maps = await db.query(
      'usuario',
      where: 'idUsuario = ?',
      whereArgs: [id],
    );

    if (maps.isNotEmpty) {
      return UserModel.fromSQLite(maps.first);
    }
    return null;
  }

  Future<UserModel?> getUserByUsername(String username) async {
    final db = await database;
    final List<Map<String, dynamic>> maps = await db.query(
      'usuario',
      where: 'user = ?',
      whereArgs: [username],
    );

    if (maps.isNotEmpty) {
      return UserModel.fromSQLite(maps.first);
    }
    return null;
  }

  Future<UserModel?> getUserByEmail(String email) async {
    final db = await database;
    final List<Map<String, dynamic>> maps = await db.query(
      'usuario',
      where: 'correo = ?',
      whereArgs: [email],
    );

    if (maps.isNotEmpty) {
      return UserModel.fromSQLite(maps.first);
    }
    return null;
  }

  Future<List<UserModel>> getAllUsers() async {
    final db = await database;
    final List<Map<String, dynamic>> maps = await db.query('usuario');
    return List.generate(maps.length, (i) => UserModel.fromSQLite(maps[i]));
  }

  Future<List<UserModel>> getUnsyncedUsers() async {
    final db = await database;
    final List<Map<String, dynamic>> maps = await db.query(
      'usuario',
      where: 'synced = ?',
      whereArgs: [0],
    );
    return List.generate(maps.length, (i) => UserModel.fromSQLite(maps[i]));
  }

  Future<int> updateUser(UserModel user) async {
    final db = await database;
    final userWithTimestamp = user.copyWith(
      updatedAt: DateTime.now(),
      synced: false,
    );
    return await db.update(
      'usuario',
      userWithTimestamp.toSQLite(),
      where: 'idUsuario = ?',
      whereArgs: [user.idUsuario],
    );
  }

  Future<int> markUserAsSynced(int userId) async {
    final db = await database;
    return await db.update(
      'usuario',
      {'synced': 1, 'updatedAt': DateTime.now().toIso8601String()},
      where: 'idUsuario = ?',
      whereArgs: [userId],
    );
  }

  Future<int> deleteUser(int id) async {
    final db = await database;
    return await db.delete(
      'usuario',
      where: 'idUsuario = ?',
      whereArgs: [id],
    );
  }

  // MÉTODOS PARA GLUCOSA

  Future<int> insertGlucose(GlucoseModel glucose) async {
    final db = await database;
    final glucoseWithTimestamp = glucose.copyWith(
      createdAt: DateTime.now(),
      updatedAt: DateTime.now(),
    );
    return await db.insert('glucosa', glucoseWithTimestamp.toSQLite());
  }

  Future<GlucoseModel?> getGlucoseById(int id) async {
    final db = await database;
    final List<Map<String, dynamic>> maps = await db.query(
      'glucosa',
      where: 'idGlucosa = ?',
      whereArgs: [id],
    );

    if (maps.isNotEmpty) {
      return GlucoseModel.fromSQLite(maps.first);
    }
    return null;
  }

  Future<List<GlucoseModel>> getGlucoseByUserId(int userId) async {
    final db = await database;
    final List<Map<String, dynamic>> maps = await db.query(
      'glucosa',
      where: 'idUsuario = ?',
      whereArgs: [userId],
      orderBy: 'fechaHora DESC',
    );
    return List.generate(maps.length, (i) => GlucoseModel.fromSQLite(maps[i]));
  }

  Future<List<GlucoseModel>> getGlucoseByUserIdAndDateRange(
    int userId, 
    DateTime startDate, 
    DateTime endDate
  ) async {
    final db = await database;
    final List<Map<String, dynamic>> maps = await db.query(
      'glucosa',
      where: 'idUsuario = ? AND fechaHora BETWEEN ? AND ?',
      whereArgs: [
        userId,
        startDate.toIso8601String(),
        endDate.toIso8601String(),
      ],
      orderBy: 'fechaHora DESC',
    );
    return List.generate(maps.length, (i) => GlucoseModel.fromSQLite(maps[i]));
  }

  Future<List<GlucoseModel>> getUnsyncedGlucose() async {
    final db = await database;
    final List<Map<String, dynamic>> maps = await db.query(
      'glucosa',
      where: 'synced = ?',
      whereArgs: [0],
    );
    return List.generate(maps.length, (i) => GlucoseModel.fromSQLite(maps[i]));
  }

  Future<int> updateGlucose(GlucoseModel glucose) async {
    final db = await database;
    final glucoseWithTimestamp = glucose.copyWith(
      updatedAt: DateTime.now(),
      synced: false,
    );
    return await db.update(
      'glucosa',
      glucoseWithTimestamp.toSQLite(),
      where: 'idGlucosa = ?',
      whereArgs: [glucose.idGlucosa],
    );
  }

  Future<int> markGlucoseAsSynced(int glucoseId) async {
    final db = await database;
    return await db.update(
      'glucosa',
      {'synced': 1, 'updatedAt': DateTime.now().toIso8601String()},
      where: 'idGlucosa = ?',
      whereArgs: [glucoseId],
    );
  }

  Future<int> deleteGlucose(int id) async {
    final db = await database;
    return await db.delete(
      'glucosa',
      where: 'idGlucosa = ?',
      whereArgs: [id],
    );
  }

  // MÉTODOS GENERALES

  Future<void> clearAllData() async {
    final db = await database;
    await db.delete('glucosa');
    await db.delete('usuario');
  }

  Future<void> closeDatabase() async {
    final db = await database;
    await db.close();
    _database = null;
  }
}
