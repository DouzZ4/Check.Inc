import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../../viewmodels/glucose_viewmodel.dart';
import '../../viewmodels/auth_viewmodel.dart';
import '../../../core/services/navigation_service.dart';

class GlucoseFormView extends StatefulWidget {
  final int? glucoseId;
  final DateTime? initialDate;

  const GlucoseFormView({
    super.key,
    this.glucoseId,
    this.initialDate,
  });

  @override
  State<GlucoseFormView> createState() => _GlucoseFormViewState();
}

class _GlucoseFormViewState extends State<GlucoseFormView> {
  final _formKey = GlobalKey<FormState>();
  final _glucoseLevelController = TextEditingController();
  DateTime _selectedDateTime = DateTime.now();
  bool _isEditing = false;

  @override
  void initState() {
    super.initState();
    _isEditing = widget.glucoseId != null;
    _selectedDateTime = widget.initialDate ?? DateTime.now();
    
    if (_isEditing) {
      _loadExistingRecord();
    }
  }

  void _loadExistingRecord() {
    final glucoseViewModel = context.read<GlucoseViewModel>();
    final record = glucoseViewModel.glucoseRecords
        .where((r) => r.idGlucosa == widget.glucoseId)
        .firstOrNull;
    
    if (record != null) {
      _glucoseLevelController.text = record.nivelGlucosa.toString();
      _selectedDateTime = record.fechaHora;
    }
  }

  @override
  void dispose() {
    _glucoseLevelController.dispose();
    super.dispose();
  }

  Future<void> _selectDateTime() async {
    final date = await showDatePicker(
      context: context,
      initialDate: _selectedDateTime,
      firstDate: DateTime.now().subtract(const Duration(days: 365)),
      lastDate: DateTime.now(),
    );

    if (date != null && mounted) {
      final time = await showTimePicker(
        context: context,
        initialTime: TimeOfDay.fromDateTime(_selectedDateTime),
      );

      if (time != null && mounted) {
        setState(() {
          _selectedDateTime = DateTime(
            date.year,
            date.month,
            date.day,
            time.hour,
            time.minute,
          );
        });
      }
    }
  }

  Future<void> _saveRecord() async {
    if (!_formKey.currentState!.validate()) return;

    final userId = context.read<AuthViewModel>().currentUser?.idUsuario;
    if (userId == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Error: Usuario no encontrado')),
      );
      return;
    }

    final glucoseLevel = double.tryParse(_glucoseLevelController.text);
    if (glucoseLevel == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Error: Nivel de glucosa inválido')),
      );
      return;
    }

    final glucoseViewModel = context.read<GlucoseViewModel>();
    bool success;

    if (_isEditing) {
      success = await glucoseViewModel.updateGlucoseRecord(
        recordId: widget.glucoseId!,
        glucoseLevel: glucoseLevel,
        dateTime: _selectedDateTime,
      );
    } else {
      success = await glucoseViewModel.addGlucoseRecord(
        userId: userId,
        glucoseLevel: glucoseLevel,
        dateTime: _selectedDateTime,
      );
    }

    if (!mounted) return;

    if (success) {
      NavigationService().pop();
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            _isEditing 
                ? 'Registro actualizado exitosamente'
                : 'Registro guardado exitosamente',
          ),
        ),
      );
    } else if (glucoseViewModel.errorMessage != null) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(glucoseViewModel.errorMessage!)),
      );
    }
  }

  String _getGlucoseStatus(double level) {
    if (level > 180) return 'Muy Alto';
    if (level > 140) return 'Alto';
    if (level < 50) return 'Muy Bajo';
    if (level < 70) return 'Bajo';
    return 'Normal';
  }

  Color _getStatusColor(String status) {
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
        title: Text(_isEditing ? 'Editar Registro' : 'Nuevo Registro'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              TextFormField(
                controller: _glucoseLevelController,
                decoration: const InputDecoration(
                  labelText: 'Nivel de Glucosa (mg/dL)',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.bloodtype),
                  helperText: 'Ingrese el nivel de glucosa en mg/dL',
                ),
                keyboardType: const TextInputType.numberWithOptions(decimal: true),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Por favor ingrese el nivel de glucosa';
                  }
                  final level = double.tryParse(value);
                  if (level == null) {
                    return 'Por favor ingrese un número válido';
                  }
                  if (level <= 0) {
                    return 'El nivel debe ser mayor a 0';
                  }
                  if (level > 600) {
                    return 'El nivel parece demasiado alto. Verifique el valor.';
                  }
                  return null;
                },
                onChanged: (value) {
                  setState(() {}); // Para actualizar el indicador de estado
                },
              ),
              const SizedBox(height: 16),
              
              // Indicador de estado de glucosa
              if (_glucoseLevelController.text.isNotEmpty)
                Builder(
                  builder: (context) {
                    final level = double.tryParse(_glucoseLevelController.text);
                    if (level != null) {
                      final status = _getGlucoseStatus(level);
                      final color = _getStatusColor(status);
                      return Container(
                        padding: const EdgeInsets.all(12),
                        decoration: BoxDecoration(
                          color: color.withOpacity(0.1),
                          border: Border.all(color: color),
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Row(
                          children: [
                            Icon(Icons.info, color: color),
                            const SizedBox(width: 8),
                            Text(
                              'Estado: $status',
                              style: TextStyle(
                                color: color,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ],
                        ),
                      );
                    }
                    return const SizedBox.shrink();
                  },
                ),
              
              const SizedBox(height: 16),
              
              InkWell(
                onTap: _selectDateTime,
                child: InputDecorator(
                  decoration: const InputDecoration(
                    labelText: 'Fecha y Hora',
                    border: OutlineInputBorder(),
                    prefixIcon: Icon(Icons.calendar_today),
                  ),
                  child: Text(
                    DateFormat('dd/MM/yyyy HH:mm').format(_selectedDateTime),
                    style: const TextStyle(fontSize: 16),
                  ),
                ),
              ),
              
              const SizedBox(height: 24),
              
              // Información de referencia
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.blue.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'Valores de Referencia:',
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 16,
                      ),
                    ),
                    const SizedBox(height: 8),
                    _buildReferenceRow('Normal', '70 - 140 mg/dL', Colors.green),
                    _buildReferenceRow('Alto', '141 - 180 mg/dL', Colors.orange),
                    _buildReferenceRow('Muy Alto', '> 180 mg/dL', Colors.red),
                    _buildReferenceRow('Bajo', '50 - 69 mg/dL', Colors.blue),
                    _buildReferenceRow('Muy Bajo', '< 50 mg/dL', Colors.purple),
                  ],
                ),
              ),
              
              const SizedBox(height: 24),
              
              Consumer<GlucoseViewModel>(
                builder: (context, glucoseViewModel, child) {
                  return ElevatedButton(
                    onPressed: glucoseViewModel.isLoading ? null : _saveRecord,
                    style: ElevatedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(vertical: 16),
                    ),
                    child: glucoseViewModel.isLoading
                        ? const CircularProgressIndicator()
                        : Text(_isEditing ? 'Actualizar' : 'Guardar'),
                  );
                },
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildReferenceRow(String label, String range, Color color) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        children: [
          Container(
            width: 12,
            height: 12,
            decoration: BoxDecoration(
              color: color,
              shape: BoxShape.circle,
            ),
          ),
          const SizedBox(width: 8),
          Text('$label: $range'),
        ],
      ),
    );
  }
}
