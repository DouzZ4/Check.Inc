<?php session_start(); ?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inicio de Sesión</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f0f0f0;
            margin: 0;
            padding: 0;
        }
        header {
            background-color: #3058a6;
            color: white;
            text-align: center;
            padding: 15px 0;
        }
        main {
            max-width: 400px;
            margin: 50px auto;
            padding: 20px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .mensaje {
            text-align: center;
            font-weight: bold;
            margin-bottom: 15px;
        }
        form label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        form input, form button {
            width: 100%;
            padding: 10px;
            margin: 8px 0;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        form button {
            background-color: #3058a6;
            color: white;
            border: none;
            cursor: pointer;
        }
        form button:hover {
            background-color: #f45501;
        }
    </style>
</head>
<body>
    <header>
        <h1>Inicio de Sesión</h1>
    </header>
    <main>
        <?php
        // Verifica si existe un mensaje en la sesión.
        if (isset($_SESSION['message'])) {
            // Escapa el mensaje para evitar XSS.
            $msg = htmlspecialchars($_SESSION['message']);
            // Muestra el mensaje dentro de un div con la clase 'mensaje'.
            echo "<div class='mensaje'>{$msg}</div>";
            // Elimina el mensaje de la sesión para que no se muestre nuevamente.
            unset($_SESSION['message']);
        }
        ?>
        <form action="../routes/usuarioRoutes.php?login" method="POST">
            <label for="user">Usuario:</label>
            <input type="text" id="user" name="username" required>
            <label for="password">Contraseña:</label>
            <input type="password" id="password" name="password" required>
            <button type="submit">Iniciar Sesión</button>
        </form>
    </main>
</body>
</html>