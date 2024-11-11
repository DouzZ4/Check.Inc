// dashboard.js
document.addEventListener('DOMContentLoaded', function () {
    const ctxGlucosa = document.getElementById('grafico-glucosa').getContext('2d');
    const graficoGlucosa = new Chart(ctxGlucosa, {
        type: 'bar',
        data: {
            labels: ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'],
            datasets: [{
                label: 'Niveles de Glucosa (mg/dL)',
                data: [110, 120, 130, 125, 115, 140, 135],
                backgroundColor: 'rgba(54, 162, 235, 0.6)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });

    // Gráfico de Peso
    const ctxPeso = document.getElementById('grafico-peso').getContext('2d');
    const graficoPeso = new Chart(ctxPeso, {
        type: 'line',
        data: {
            labels: ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'],
            datasets: [{
                label: 'Peso (kg)',
                data: [82, 81.5, 81, 80.5, 80, 79.8, 79.5],
                fill: false,
                borderColor: 'rgba(255, 99, 132, 1)',
                tension: 0.1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: false
                }
            }
        }
    });
});
