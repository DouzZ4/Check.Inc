let alarms = [];

// Reloj en tiempo real
function actualizarReloj() {
    const ahora = new Date();
    const horas = ahora.getHours().toString().padStart(2, '0');
    const minutos = ahora.getMinutes().toString().padStart(2, '0');
    const segundos = ahora.getSeconds().toString().padStart(2, '0');
    document.getElementById('clock').textContent = `${horas}:${minutos}:${segundos}`;
    actualizarCuentaRegresivaGlobal();
    requestAnimationFrame(actualizarReloj);
}

document.addEventListener('DOMContentLoaded', function() {
    actualizarReloj();

    const calendarEl = document.getElementById('calendar');
    if (calendarEl) {
        const calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth,timeGridWeek,timeGridDay'
            },
            events: [],
            eventClick: function(info) {
                alert('Evento: ' + info.event.title + '\nDescripción: ' + info.event.extendedProps.description);
            }
        });
        calendar.render();

        const crearAlarmaBtn = document.getElementById('crearAlarmaBtn');
        if (crearAlarmaBtn) {
            crearAlarmaBtn.addEventListener('click', function() {
                crearAlarma(calendar);
            });
        }
    }
});

function crearAlarma(calendar) {
    const alarmContainer = document.getElementById('alarm-container');
    if (alarmContainer) {
        const alarmElement = document.createElement('div');
        alarmElement.className = 'alarm card p-3 mb-3';
        alarmElement.innerHTML = `
            <div class="form-group">
                <input type="text" class="form-control alarm-name" placeholder="Nombre de la alarma">
            </div>
            <div class="form-row">
                <div class="form-group col-md-6">
                    <input type="time" class="form-control time-picker">
                </div>
                <div class="form-group col-md-6">
                    <input type="date" class="form-control date-picker">
                </div>
            </div>
            <span class="countdown">00:00:00</span>
            <button class="btn btn-danger mt-2" onclick="eliminarAlarma(this)">Eliminar</button>
            <button class="btn btn-success mt-2" onclick="guardarAlarma(this, calendar)">Guardar</button>
        `;
        alarmContainer.appendChild(alarmElement);
        actualizarCuentaRegresiva(alarmElement);
    }
}

function eliminarAlarma(button) {
    const alarmElement = button.parentElement;
    if (alarmElement) {
        alarmElement.remove();
    }
}

function guardarAlarma(button, calendar) {
    const alarmElement = button.parentElement;
    if (alarmElement) {
        const alarmName = alarmElement.querySelector('.alarm-name').value;
        const datePicker = alarmElement.querySelector('.date-picker').value;
        const timePicker = alarmElement.querySelector('.time-picker').value;

        if (alarmName && datePicker && timePicker) {
            const alarmDateTime = `${datePicker}T${timePicker}`;
            alarms.push({
                title: alarmName,
                start: alarmDateTime
            });

            if (calendar) {
                calendar.addEvent({
                    title: alarmName,
                    start: alarmDateTime,
                    description: 'Alarma programada'
                });
            }

            const guardarBtn = alarmElement.querySelector('.btn-success');
            if (guardarBtn) {
                guardarBtn.textContent = 'Guardado';
                guardarBtn.disabled = true;
            }
        } else {
            alert('Por favor, completa todos los campos.');
        }
    }
}

function actualizarCuentaRegresiva(alarmElement) {
    const timePicker = alarmElement.querySelector('.time-picker');
    const datePicker = alarmElement.querySelector('.date-picker');
    const countdown = alarmElement.querySelector('.countdown');

    function update() {
        const now = new Date();
        const targetTime = new Date(datePicker.value + 'T' + timePicker.value);
        
        const diff = targetTime - now;
        if (diff >= 0) {
            const hoursLeft = Math.floor(diff / (1000 * 60 * 60));
            const minutesLeft = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
            const secondsLeft = Math.floor((diff % (1000 * 60)) / 1000);
            countdown.textContent = `${hoursLeft.toString().padStart(2, '0')}:${minutesLeft.toString().padStart(2, '0')}:${secondsLeft.toString().padStart(2, '0')}`;
        } else {
            countdown.textContent = '00:00:00';
        }

        requestAnimationFrame(update);
    }

    update();
}

function actualizarCuentaRegresivaGlobal() {
    const now = new Date();
    let nextAlarmTime = null;
    let nextAlarmName = '';

    alarms.forEach(alarmElement => {
        const timePicker = alarmElement.querySelector('.time-picker');
        const datePicker = alarmElement.querySelector('.date-picker');
        const alarmName = alarmElement.querySelector('.alarm-name').value || 'Sin nombre';

        const targetTime = new Date(datePicker.value + 'T' + timePicker.value);

        const diff = targetTime - now;
        if (diff >= 0 && (nextAlarmTime === null || diff < nextAlarmTime - now)) {
            nextAlarmTime = targetTime;
            nextAlarmName = alarmName;
        }
    });

    if (nextAlarmTime) {
        const diff = nextAlarmTime - now;
        const hoursLeft = Math.floor(diff / (1000 * 60 * 60));
        const minutesLeft = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        const secondsLeft = Math.floor((diff % (1000 * 60)) / 1000);
        document.getElementById('next-alarm').textContent = `Próxima alarma: ${nextAlarmName} en ${hoursLeft.toString().padStart(2, '0')}:${minutesLeft.toString().padStart(2, '0')}:${secondsLeft.toString().padStart(2, '0')}`;
    } else {
        document.getElementById('next-alarm').textContent = 'No hay alarmas programadas.';
    }
}

document.addEventListener('input', event => {
    if (event.target.matches('.time-picker') || event.target.matches('.date-picker') || event.target.matches('.alarm-name')) {
        actualizarCuentaRegresiva(event.target.closest('.alarm'));
    }
});
