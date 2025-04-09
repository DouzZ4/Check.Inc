<?php
// Iniciar sesión para manejar mensajes flash
session_start();
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro de Paciente - Control Glucosa</title>
    <link rel="stylesheet" href="./css/form.css">
    <link rel="stylesheet" href="./css/navbar.css">

    </head>
<body>
<?php include __DIR__ . '/../includes/navbar.php'; ?>
    

    <main class="form-container"> <h2>Registro de Nuevo Paciente</h2>

        <?php
        // Mostrar mensaje flash de sesión, si existe (para feedback post-registro)
        if (isset($_SESSION['message'])) {
            // Determinar la clase basada en el éxito/error (necesitarías guardar el tipo en sesión también)
            $message_type = $_SESSION['message_type'] ?? 'info'; // 'success', 'error', 'info'
            $message_class = 'message-' . htmlspecialchars($message_type);
            $msg = htmlspecialchars($_SESSION['message']);
            echo "<div class='message-box {$message_class}'>{$msg}</div>";
            unset($_SESSION['message']);
            unset($_SESSION['message_type']);
        }
        ?>

        <form id="registroForm" action="../routes/usuarioRoutes.php?action=register" method="POST"> <div class="form-group">
                <label for="nombres">Nombres:</label>
                <input type="text" id="nombres" name="nombres" placeholder="Ej: Ana María"
                       required pattern="[A-Za-zÀ-ÿ\s]+"
                       title="Ingrese solo letras y espacios" />
            </div>

            <div class="form-group">
                 <label for="apellidos">Apellidos:</label>
                 <input type="text" id="apellidos" name="apellidos" placeholder="Ej: Pérez Gómez"
                        required pattern="[A-Za-zÀ-ÿ\s]+"
                        title="Ingrese solo letras y espacios" />
            </div>

            <div class="form-group">
                 <label for="edad">Edad:</label>
                 <input type="number" id="edad" name="edad" placeholder="Ej: 30"
                        required min="1" max="120"
                        title="Ingrese una edad válida" />
            </div>

            <div class="form-group">
                 <label for="correo">Correo Electrónico:</label>
                 <input type="email" id="correo" name="correo" placeholder="Ej: ana.perez@email.com" required pattern=".+@.+\.com$" title="Por favor, ingresa una dirección de correo válida que termine en .com" />
            </div>

            <div class="form-group">
                  <label for="user">Nombre de Usuario:</label>
                  <input type="text" id="user" name="user" placeholder="Ej: aperezg"
                         required minlength="5"
                         title="El nombre de usuario debe tener al menos 5 caracteres" />
            </div>

            <div class="form-group">
                 <label for="documento">Documento (Cédula):</label>
                 <input type="text" id="documento" name="documento" placeholder="Ej: 1098765432"
                        required pattern="[0-9]{6,20}"
                        title="Debe ser un número entre 6 y 20 dígitos, sin comas ni puntos" />
            </div>

             <div class="form-group">
                 <label for="password">Contraseña:</label>
                 <input type="password" id="password" name="password" placeholder="Crea una contraseña segura"
                        required minlength="8"
                        pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,}$"
                        title="Mínimo 8 caracteres, con mayúscula, minúscula y símbolo especial" />
                 </div>

             <button type="submit" class="submit-button">Registrarme</button>

             <div class="login-link">
                 ¿Ya tienes cuenta? <a href="login.php">Inicia Sesión aquí</a>
             </div>

        </form>
    </main>

</body>
</html>