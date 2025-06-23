import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../../viewmodels/glucose_viewmodel.dart';
import '../../viewmodels/auth_viewmodel.dart';
import '../../../core/services/navigation_service.dart';

class GlucoseListView extends StatefulWidget {
  const GlucoseListView({super.key});

  @override
  State<GlucoseListView> createState() => _GlucoseListViewState();
}

class _GlucoseListViewState extends State<GlucoseListView> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadData();
    });
  }

  Future<void> _loadData() async {
    final userId = context.read<AuthViewModel>().currentUser?.idUsuario;
    if (userId != null) {
      await context.read<GlucoseViewModel>().loadGlucoseRecords(userId);
    }
  }

  String _getStatusColor(double level) {
    if (level > 180) return 'Muy Alto';
    if (level > 140) return 'Alto';
    if (level < 50) return 'Muy Bajo';
    if (level < 70) return 'Bajo';
    return 'Normal';
  }

  Color _getStatusColorValue(String status) {
    switch (status) {
      case 'Muy Alto':
        return Colors.red;
      case 'Alto':
        return Colors.orange;
      case 'Muy Bajo':
        return Colors.purple;
      case 'Bajo':
        return Colors.blue;
      default:
        return Colors.green;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Registros de Glucosa'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () async {
              final confirmed = await NavigationService().showConfirmationDialog(
                context,
                title: 'Cerrar Sesión',
                message: '¿Está seguro que desea cerrar sesión?',
              );

              if (confirmed == true && mounted) {
                await context.read<AuthViewModel>().logout();
                NavigationService().goToLogin();
              }
            },
          ),
        ],
      ),
      body: Consumer<GlucoseViewModel>(
        builder: (context, glucoseViewModel, child) {
          if (glucoseViewModel.isLoading) {
            return const Center(child: CircularProgressIndicator());
          }

          if (glucoseViewModel.hasError) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    glucoseViewModel.errorMessage ?? 'Error desconocido',
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: _loadData,
                    child: const Text('Reintentar'),
                  ),
                ],
              ),
            );
          }

          if (!glucoseViewModel.hasData) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Text(
                    'No hay registros de glucosa',
                    style: TextStyle(fontSize: 18),
                  ),
                  const SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: () => NavigationService().goToGlucoseForm(),
                    child: const Text('Agregar Registro'),
                  ),
                ],
              ),
            );
          }

          return RefreshIndicator(
            onRefresh: _loadData,
            child: ListView.builder(
              itemCount: glucoseViewModel.glucoseRecords.length,
              itemBuilder: (context, index) {
                final record = glucoseViewModel.glucoseRecords[index];
                final status = _getStatusColor(record.nivelGlucosa);
                final statusColor = _getStatusColorValue(status);

                return Dismissible(
                  key: Key(record.idGlucosa.toString()),
                  direction: DismissDirection.endToStart,
                  confirmDismiss: (direction) async {
                    return await NavigationService().showConfirmationDialog(
                      context,
                      title: 'Eliminar Registro',
                      message: '¿Está seguro que desea eliminar este registro?',
                    );
                  },
                  onDismissed: (direction) async {
                    if (record.idGlucosa != null) {
                      await glucoseViewModel.deleteGlucoseRecord(record.idGlucosa!);
                    }
                  },
                  background: Container(
                    color: Colors.red,
                    alignment: Alignment.centerRight,
                    padding: const EdgeInsets.only(right: 16),
                    child: const Icon(
                      Icons.delete,
                      color: Colors.white,
                    ),
                  ),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundColor: statusColor.withOpacity(0.2),
                      child: Text(
                        '${record.nivelGlucosa.round()}',
                        style: TextStyle(
                          color: statusColor,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                    title: Text(
                      DateFormat('dd/MM/yyyy HH:mm').format(record.fechaHora),
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                    subtitle: Text(
                      'Estado: $status',
                      style: TextStyle(color: statusColor),
                    ),
                    trailing: IconButton(
                      icon: const Icon(Icons.edit),
                      onPressed: () {
                        NavigationService().goToGlucoseForm(
                          glucoseId: record.idGlucosa,
                        );
                      },
                    ),
                  ),
                );
              },
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => NavigationService().goToGlucoseForm(),
        child: const Icon(Icons.add),
      ),
    );
  }
}
