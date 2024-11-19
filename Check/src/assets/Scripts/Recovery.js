document.getElementById('recoveryForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const email = document.getElementById('email').value;

    if (validateEmail(email)) {
        alert('Se enviaron las instrucciones a tu correo.');
        this.reset();
    } else {
        alert('Por favor, ingresa un correo válido.');
    }
});

// Validación básica del correo
function validateEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}
