document.getElementById('changePasswordForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (newPassword === confirmPassword) {
        if (newPassword.length >= 8) {
            alert('Contraseña actualizada con éxito.');
            this.reset();
        } else {
            alert('La contraseña debe tener al menos 8 caracteres.');
        }
    } else {
        alert('Las contraseñas no coinciden. Inténtalo de nuevo.');
    }
});

const newPassword = document.getElementById('newPassword');
const confirmPassword = document.getElementById('confirmPassword');
const newPasswordFeedback = document.getElementById('newPasswordFeedback');
const confirmPasswordFeedback = document.getElementById('confirmPasswordFeedback');

// Validación en tiempo real para la nueva contraseña
newPassword.addEventListener('input', () => {
    if (newPassword.value.length < 8) {
        newPassword.classList.add('invalid');
        newPassword.classList.remove('valid');
        newPasswordFeedback.textContent = 'La contraseña debe tener al menos 8 caracteres.';
        newPasswordFeedback.style.display = 'block';
    } else {
        newPassword.classList.add('valid');
        newPassword.classList.remove('invalid');
        newPasswordFeedback.textContent = '';
        newPasswordFeedback.style.display = 'none';
    }
});

// Validación en tiempo real para confirmar contraseña
confirmPassword.addEventListener('input', () => {
    if (confirmPassword.value !== newPassword.value) {
        confirmPassword.classList.add('invalid');
        confirmPassword.classList.remove('valid');
        confirmPasswordFeedback.textContent = 'Las contraseñas no coinciden.';
        confirmPasswordFeedback.style.display = 'block';
    } else {
        confirmPassword.classList.add('valid');
        confirmPassword.classList.remove('invalid');
        confirmPasswordFeedback.textContent = '';
        confirmPasswordFeedback.style.display = 'none';
    }
});

// Validación final al enviar el formulario
document.getElementById('changePasswordForm').addEventListener('submit', function (e) {
    e.preventDefault();

    if (newPassword.classList.contains('valid') && confirmPassword.classList.contains('valid')) {
        alert('Contraseña actualizada con éxito.');
        this.reset();
        newPassword.classList.remove('valid');
        confirmPassword.classList.remove('valid');
    } else {
        alert('Por favor, corrige los errores antes de continuar.');
    }
});
