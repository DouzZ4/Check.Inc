class AppConstants {
  // Database
  static const String databaseName = 'checkinc.db';
  static const int databaseVersion = 1;
  
  // Tables
  static const String userTable = 'usuario';
  static const String glucoseTable = 'glucosa';
  static const String roleTable = 'rol';
  
  // Firebase Collections
  static const String usersCollection = 'users';
  static const String glucoseCollection = 'glucose';
  
  // Shared Preferences Keys
  static const String isLoggedInKey = 'isLoggedIn';
  static const String currentUserIdKey = 'currentUserId';
  static const String lastSyncKey = 'lastSync';
  
  // App Info
  static const String appName = 'Check Inc';
  static const String appVersion = '1.0.0';
  
  // Glucose Levels (mg/dL)
  static const double normalGlucoseMin = 70.0;
  static const double normalGlucoseMax = 140.0;
  static const double highGlucoseThreshold = 180.0;
  static const double lowGlucoseThreshold = 70.0;
}
