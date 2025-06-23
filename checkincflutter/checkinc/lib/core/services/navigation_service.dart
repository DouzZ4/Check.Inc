import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../presentation/views/auth/login_view.dart';
import '../../presentation/views/auth/register_view.dart';
import '../../presentation/views/glucose/glucose_list_view.dart';
import '../../presentation/views/glucose/glucose_form_view.dart';
import '../../presentation/viewmodels/auth_viewmodel.dart';

class NavigationService {
  static final NavigationService _instance = NavigationService._internal();
  factory NavigationService() => _instance;
  NavigationService._internal();

  static const String loginRoute = '/login';
  static const String registerRoute = '/register';
  static const String homeRoute = '/home';
  static const String glucoseListRoute = '/glucose';
  static const String glucoseFormRoute = '/glucose/form';

  late final GoRouter router = GoRouter(
    initialLocation: loginRoute,
    debugLogDiagnostics: true,
    routes: [
      GoRoute(
        path: loginRoute,
        builder: (context, state) => const LoginView(),
      ),
      GoRoute(
        path: registerRoute,
        builder: (context, state) => const RegisterView(),
      ),
      GoRoute(
        path: glucoseListRoute,
        builder: (context, state) => const GlucoseListView(),
      ),
      GoRoute(
        path: glucoseFormRoute,
        builder: (context, state) {
          final Map<String, dynamic> extra = state.extra as Map<String, dynamic>? ?? {};
          return GlucoseFormView(
            glucoseId: extra['glucoseId'] as int?,
            initialDate: extra['initialDate'] as DateTime?,
          );
        },
      ),
    ],
    redirect: (context, state) async {
      final authViewModel = AuthViewModel();
      final isLoggedIn = authViewModel.isLoggedIn;
      final isLoginRoute = state.matchedLocation == loginRoute;
      final isRegisterRoute = state.matchedLocation == registerRoute;

      // Si el usuario no está logueado y no está en login/register, redirigir a login
      if (!isLoggedIn && !isLoginRoute && !isRegisterRoute) {
        return loginRoute;
      }

      // Si el usuario está logueado y está en login/register, redirigir a home
      if (isLoggedIn && (isLoginRoute || isRegisterRoute)) {
        return glucoseListRoute;
      }

      // En cualquier otro caso, permitir la navegación normal
      return null;
    },
    errorBuilder: (context, state) => Material(
      child: Center(
        child: Text('Error: ${state.error}'),
      ),
    ),
  );

  // Métodos de navegación
  void goToLogin() {
    router.go(loginRoute);
  }

  void goToRegister() {
    router.go(registerRoute);
  }

  void goToGlucoseList() {
    router.go(glucoseListRoute);
  }

  void goToGlucoseForm({int? glucoseId, DateTime? initialDate}) {
    router.go(
      glucoseFormRoute,
      extra: {
        'glucoseId': glucoseId,
        'initialDate': initialDate,
      },
    );
  }

  void pop() {
    router.pop();
  }

  void popUntil(String route) {
    while (router.canPop()) {
      router.pop();
    }
  }

  // Métodos de diálogo
  Future<bool?> showConfirmationDialog(
    BuildContext context, {
    required String title,
    required String message,
    String confirmText = 'Aceptar',
    String cancelText = 'Cancelar',
  }) {
    return showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(title),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: Text(cancelText),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: Text(confirmText),
          ),
        ],
      ),
    );
  }

  Future<void> showErrorDialog(
    BuildContext context, {
    required String title,
    required String message,
  }) {
    return showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(title),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Aceptar'),
          ),
        ],
      ),
    );
  }

  Future<void> showSuccessDialog(
    BuildContext context, {
    required String title,
    required String message,
  }) {
    return showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(title),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Aceptar'),
          ),
        ],
      ),
    );
  }

  ScaffoldFeatureController<SnackBar, SnackBarClosedReason> showSnackBar(
    BuildContext context, {
    required String message,
    Duration duration = const Duration(seconds: 2),
  }) {
    return ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        duration: duration,
      ),
    );
  }
}
