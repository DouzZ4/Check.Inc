// Espera a que el contenido del DOM se haya cargado para ejecutar el código
document.addEventListener('DOMContentLoaded', function () {

    // Gráfico de Glucosa
    const ctxGlucosa = document.getElementById('grafico-glucosa').getContext('2d');
    const graficoGlucosa = new Chart(ctxGlucosa, {
        type: 'bar', // Tipo de gráfico: barras
        data: {
            labels: ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'], // Etiquetas de los días de la semana
            datasets: [{
                label: 'Niveles de Glucosa (mg/dL)', // Etiqueta del conjunto de datos
                data: [110, 120, 130, 125, 115, 140, 135], // Valores de glucosa de cada día
                backgroundColor: 'rgba(54, 162, 235, 0.6)', // Color de fondo de las barras
                borderColor: 'rgba(54, 162, 235, 1)', // Color de borde de las barras
                borderWidth: 1 // Ancho del borde de las barras
            }]
        },
        options: {
            responsive: true, // Hace que el gráfico sea responsivo
            scales: {
                y: {
                    beginAtZero: true // Asegura que el eje Y comience en cero
                }
            }
        }
    });

    // Gráfico de Peso
    const ctxPeso = document.getElementById('grafico-peso').getContext('2d');
    const graficoPeso = new Chart(ctxPeso, {
        type: 'line', // Tipo de gráfico: línea
        data: {
            labels: ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'], // Etiquetas de los días de la semana
            datasets: [{
                label: 'Peso (kg)', // Etiqueta del conjunto de datos
                data: [82, 81.5, 81, 80.5, 80, 79.8, 79.5], // Valores de peso de cada día
                fill: false, // No llenar el área bajo la línea
                borderColor: 'rgba(255, 99, 132, 1)', // Color de la línea
                tension: 0.1 // Controla la curvatura de la línea
            }]
        },
        options: {
            responsive: true, // Hace que el gráfico sea responsivo
            scales: {
                y: {
                    beginAtZero: false // El eje Y no comienza en cero
                }
            }
        }
    });
});

// Datos de citas de ejemplo (se podrían cargar dinámicamente desde una base de datos o API)
const citas = [
    { id: 1, fecha: "2023-01-01", detalles: "Control general, medición de glucosa, presión arterial." },
    { id: 2, fecha: "2023-02-15", detalles: "Evaluación de medicamentos, recomendaciones de dieta." },
    { id: 3, fecha: "2023-03-20", detalles: "Revisión de niveles de azúcar y presión." },
    { id: 4, fecha: "2023-04-10", detalles: "Actualización de tratamiento y ejercicios." },
    { id: 5, fecha: "2023-05-05", detalles: "Chequeo de rutina, ajuste de dosis." }
];

// Función para abrir el modal de citas
function abrirModalCitas() {
    document.getElementById("modalCitas").style.display = "block"; // Muestra el modal
}

// Función para cerrar el modal de citas
function cerrarModalCitas() {
    document.getElementById("modalCitas").style.display = "none"; // Oculta el modal
}

// Función para mostrar los detalles de una cita seleccionada
function mostrarDetallesCita(id) {
    const cita = citas.find(c => c.id === id); // Busca la cita por ID
    const detallesCitaDiv = document.getElementById("detalles-cita"); // Selecciona el contenedor donde se mostrarán los detalles

    if (cita) {
        detallesCitaDiv.innerHTML = `
            <h3>Detalles de la Cita del ${cita.fecha}</h3>
            <p>${cita.detalles}</p>
        `; // Muestra los detalles de la cita en el contenedor
    }
}

// Cierra el modal si se hace clic fuera de él
window.onclick = function(event) {
    const modal = document.getElementById("modalCitas"); // Selecciona el modal
    if (event.target === modal) {
        cerrarModalCitas(); // Cierra el modal si se hace clic fuera de él
    }
};

// Función para mostrar el resumen de salud
function mostrarResumenSalud() {
    // Datos de ejemplo, en un caso real estos deben ser obtenidos de una base de datos o API
    const glucosaPromedio = 110;  // mg/dL
    const presionArterial = "125/85"; // mmHg
    const frecuenciaCardiaca = 78;  // bpm

    // Objetivos de salud
    const objetivoGlucosa = { min: 70, max: 100 };  // mg/dL
    const objetivoPresion = { min: 120, max: 80 };  // mmHg
    const objetivoFC = { max: 80 };  // bpm

    // Cálculo del progreso de glucosa
    let progresoGlucosa = 0;
    if (glucosaPromedio >= objetivoGlucosa.min && glucosaPromedio <= objetivoGlucosa.max) {
        progresoGlucosa = 100; // Objetivo alcanzado
    } else if (glucosaPromedio < objetivoGlucosa.min) {
        progresoGlucosa = (glucosaPromedio / objetivoGlucosa.min) * 100; // Calcula el porcentaje de progreso si está por debajo del objetivo
    } else {
        progresoGlucosa = (100 - (glucosaPromedio - objetivoGlucosa.max)) * 100; // Calcula el progreso si está por encima del objetivo
    }

    // Cálculo del progreso de presión arterial
    let progresoPresion = 0;
    const presionValores = presionArterial.split('/'); // Divide la presión en sistólica y diastólica
    const presionSistolica = parseInt(presionValores[0]);
    const presionDiastolica = parseInt(presionValores[1]);

    if (presionSistolica <= objetivoPresion.min && presionDiastolica <= objetivoPresion.max) {
        progresoPresion = 100; // Objetivo alcanzado
    } else {
        progresoPresion = 0; // Objetivo no alcanzado
    }

    // Cálculo del progreso de frecuencia cardíaca
    let progresoFC = 0;
    if (frecuenciaCardiaca <= objetivoFC.max) {
        progresoFC = 100; // Objetivo alcanzado
    } else {
        progresoFC = 0; // Objetivo no alcanzado
    }

    // Actualiza las barras de progreso en el HTML
    document.getElementById("progreso-glucosa").style.width = progresoGlucosa + "%";
    document.getElementById("progreso-presion").style.width = progresoPresion + "%";
    document.getElementById("progreso-fc").style.width = progresoFC + "%";

    // Muestra los mensajes de progreso
    document.getElementById("mensaje-glucosa").innerText = 
        `Glucosa: ${glucosaPromedio} mg/dL (${progresoGlucosa}% del objetivo alcanzado)`;
    document.getElementById("mensaje-presion").innerText = 
        `Presión Arterial: ${presionArterial} mmHg (${progresoPresion}% del objetivo alcanzado)`;
    document.getElementById("mensaje-fc").innerText = 
        `Frecuencia Cardíaca: ${frecuenciaCardiaca} bpm (${progresoFC}% del objetivo alcanzado)`;
}

// Función para mostrar u ocultar la sección de Objetivos de Salud
function toggleObjetivosSalud() {
    const objetivosSalud = document.getElementById("objetivosSalud");

    // Alterna la visibilidad
    if (objetivosSalud.style.display === "none" || objetivosSalud.style.display === "") {
        objetivosSalud.style.display = "block";
    } else {
        objetivosSalud.style.display = "none";
    }
}

// Abrir el modal de citas programadas
function abrirModalCitasProgramadas() {
    document.getElementById("modalCitasProgramadas").style.display = "block";
}

// Cerrar el modal de citas programadas
function cerrarModalCitasProgramadas() {
    document.getElementById("modalCitasProgramadas").style.display = "none";
}

// Obtener elementos del DOM
const modalCitasProgramadas = document.getElementById("modalCitasProgramadas");
const citasProgramadasBtn = document.getElementById("citasProgramadasBtn");
const closeModalCitas = document.querySelector(".modal .close");

// Función para abrir el modal
citasProgramadasBtn.onclick = function() {
    modalCitasProgramadas.style.display = "block";
}

// Función para cerrar el modal
closeModalCitas.onclick = function() {
    modalCitasProgramadas.style.display = "none";
}

// Cerrar el modal si el usuario hace clic fuera de la ventana modal
window.onclick = function(event) {
    if (event.target == modalCitasProgramadas) {
        modalCitasProgramadas.style.display = "none";
    }
}

// Datos para la gráfica de líneas
const lineCtx = document.getElementById('lineChart').getContext('2d');
new Chart(lineCtx, {
    type: 'line',
    data: {
        labels: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio'],
        datasets: [{
            label: 'Nivel de Glucosa (mg/dL)',
            data: [95, 110, 105, 120, 115, 100],
            borderColor: 'rgba(153, 102, 255, 1)',
            backgroundColor: 'rgba(153, 102, 255, 0.2)',
            borderWidth: 2,
            fill: true
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: {
                position: 'top'
            }
        }
    }
});

// Datos para la gráfica de pastel
const pieCtx = document.getElementById('pieChart').getContext('2d');
new Chart(pieCtx, {
    type: 'pie',
    data: {
        labels: ['Medicamento A', 'Medicamento B', 'Medicamento C'],
        datasets: [{
            label: 'Porcentaje',
            data: [40, 35, 25],
            backgroundColor: [
                'rgba(255, 99, 132, 0.6)',
                'rgba(54, 162, 235, 0.6)',
                'rgba(255, 206, 86, 0.6)'
            ],
            borderColor: [
                'rgba(255, 99, 132, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)'
            ],
            borderWidth: 1
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: {
                position: 'top'
            }
        }
    }
});

// Configuración del gráfico de frecuencia cardíaca
const ctxFrecuencia = document.getElementById('grafico-frecuencia-cardiaca').getContext('2d');
const graficoFrecuenciaCardiaca = new Chart(ctxFrecuencia, {
    type: 'line', // Tipo de gráfico: línea
    data: {
        labels: ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'], // Días de la semana
        datasets: [{
            label: 'Frecuencia Cardíaca (bpm)',
            data: [72, 75, 78, 74, 76, 73, 75], // Datos de frecuencia cardíaca
            borderColor: '#4CAF50', // Color de la línea
            backgroundColor: 'rgba(76, 175, 80, 0.2)', // Fondo debajo de la línea
            borderWidth: 2, // Grosor de la línea
            tension: 0.4, // Suavizado de la línea
        }]
    },
    options: {
        responsive: true, // Se ajusta automáticamente al tamaño del contenedor
        plugins: {
            legend: {
                display: true,
                position: 'top'
            },
        },
        scales: {
            y: {
                beginAtZero: false // El eje Y no comienza desde cero
            }
        }
    }
});

