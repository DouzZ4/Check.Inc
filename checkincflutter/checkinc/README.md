# Check Inc - Aplicación de Control de Glucosa

Una aplicación móvil desarrollada en Flutter para el control y seguimiento de niveles de glucosa en sangre, implementando arquitectura MVVM con sincronización local y en la nube.

## 🏗️ Arquitectura

La aplicación sigue el patrón **MVVM (Model-View-ViewModel)** con las siguientes capas:

### 📁 Estructura de Carpetas

```
lib/
├── core/                           # Núcleo de la aplicación
│   ├── constants/                  # Constantes globales
│   │   └── app_constants.dart
│   └── services/                   # Servicios principales
│       ├── database/               # Servicios de base de datos
│       │   ├── sqlite_service.dart # Almacenamiento local
│       │   └── firebase_service.dart # Almacenamiento en la nube
│       ├── sync_service.dart       # Sincronización de datos
│       └── navigation_service.dart # Navegación con go_router
├── domain/                         # Capa de dominio
│   └── entities/                   # Entidades de negocio
│       ├── user.dart
│       └── glucose.dart
├── data/                          # Capa de datos
│   ├── models/                    # Modelos de datos
│   │   ├── user_model.dart
│   │   └── glucose_model.dart
│   └── repositories/              # Repositorios
│       ├── user_repository.dart
│       └── glucose_repository.dart
└── presentation/                  # Capa de presentación
    ├── viewmodels/               # ViewModels (lógica de negocio)
    │   ├── auth_viewmodel.dart
    │   └── glucose_viewmodel.dart
    └── views/                    # Vistas (UI)
        ├── auth/
        │   ├── login_view.dart
        │   └── register_view.dart
        └── glucose/
            ├── glucose_list_view.dart
            └── glucose_form_view.dart
```

## 🚀 Características

### ✅ Funcionalidades Implementadas

- **Autenticación de usuarios**
  - Registro de nuevos usuarios
  - Inicio de sesión
  - Gestión de sesiones con SharedPreferences
  - Validación de datos

- **Gestión de registros de glucosa**
  - Crear nuevos registros
  - Editar registros existentes
  - Eliminar registros
  - Visualización en lista ordenada por fecha

- **Almacenamiento de datos**
  - Base de datos local SQLite
  - Sincronización con Firebase Firestore
  - Funcionamiento offline

- **Navegación**
  - Navegación declarativa con go_router
  - Protección de rutas basada en autenticación

### 📊 Análisis de Glucosa

- **Clasificación automática de niveles:**
  - Normal: 70-140 mg/dL
  - Alto: 141-180 mg/dL
  - Muy Alto: >180 mg/dL
  - Bajo: 50-69 mg/dL
  - Muy Bajo: <50 mg/dL

- **Indicadores visuales por colores**
- **Validación de datos de entrada**

## 🛠️ Tecnologías Utilizadas

### Framework y Lenguaje
- **Flutter 3.x**
- **Dart 3.x**

### Gestión de Estado
- **Provider** - Para la gestión de estado reactivo

### Base de Datos
- **SQLite** (sqflite) - Almacenamiento local
- **Firebase Firestore** - Almacenamiento en la nube

### Navegación
- **go_router** - Navegación declarativa

### Otras Dependencias
- **shared_preferences** - Persistencia de configuraciones
- **connectivity_plus** - Verificación de conectividad
- **crypto** - Encriptación de contraseñas
- **intl** - Formateo de fechas

## 📱 Pantallas

### 🔐 Autenticación
1. **Login** - Inicio de sesión con usuario y contraseña
2. **Registro** - Creación de nueva cuenta con validaciones

### 📈 Gestión de Glucosa
1. **Lista de Registros** - Visualización de todos los registros
2. **Formulario** - Creación/edición de registros

## 🔧 Configuración del Proyecto

### Prerrequisitos
- Flutter SDK 3.x
- Dart SDK 3.x
- Android Studio / VS Code
- Cuenta de Firebase

### Instalación

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd checkinc
```

2. **Instalar dependencias**
```bash
flutter pub get
```

3. **Configurar Firebase**
   - Crear proyecto en Firebase Console
   - Agregar aplicación Android/iOS
   - Descargar `google-services.json` y colocarlo en `android/app/`
   - Habilitar Firestore Database

4. **Ejecutar la aplicación**
```bash
flutter run
```

## 🗄️ Base de Datos

### Esquema SQLite

#### Tabla `usuario`
```sql
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
);
```

#### Tabla `glucosa`
```sql
CREATE TABLE glucosa (
  idGlucosa INTEGER PRIMARY KEY AUTOINCREMENT,
  nivelGlucosa REAL NOT NULL,
  fechaHora TEXT NOT NULL,
  idUsuario INTEGER NOT NULL,
  createdAt TEXT,
  updatedAt TEXT,
  synced INTEGER DEFAULT 0,
  FOREIGN KEY (idUsuario) REFERENCES usuario (idUsuario)
);
```

### Colecciones Firebase
- `users` - Usuarios sincronizados
- `glucose` - Registros de glucosa sincronizados

## 🔄 Sincronización

La aplicación implementa sincronización bidireccional:

1. **Local → Firebase**: Los datos creados offline se sincronizan cuando hay conexión
2. **Firebase → Local**: Los datos de la nube se descargan al dispositivo
3. **Resolución de conflictos**: Basada en timestamps de actualización

## 🧪 Testing

```bash
# Ejecutar tests
flutter test

# Ejecutar tests con cobertura
flutter test --coverage
```

## 📦 Build

### Android
```bash
flutter build apk --release
```

### iOS
```bash
flutter build ios --release
```

## 🤝 Contribución

1. Fork el proyecto
2. Crear una rama para la feature (`git checkout -b feature/AmazingFeature`)
3. Commit los cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

## 👥 Autores

- **Tu Nombre** - *Desarrollo inicial* - [TuGitHub](https://github.com/tuusuario)

## 🙏 Agradecimientos

- Flutter team por el excelente framework
- Firebase por los servicios de backend
- Comunidad de Flutter por los paquetes utilizados
