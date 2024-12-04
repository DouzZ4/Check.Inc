// Validación del formulario de inicio de sesión
function validateForm() {
    const email = document.forms["loginForm"]["email"].value;
    const password = document.forms["loginForm"]["password"].value;
    const errorMessage = document.getElementById("error-message");

    // Validar email
    const emailPattern = /^[^ ]+@[^ ]+\.[a-z]{2,3}$/;
    if (!emailPattern.test(email)) {
        errorMessage.textContent = "Por favor, ingrese una dirección de correo válida.";
        return false;
    }

    // Validar contraseña (mínimo 6 caracteres)
    if (password.length < 6) {
        errorMessage.textContent = "La contraseña debe tener al menos 6 caracteres.";
        return false;
    }

    // Si todas las validaciones pasan, enviar el formulario
    errorMessage.textContent = "";
    return true;
}
