<?php session_start(); ?>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Registro de Paciente</title>
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
          max-width: 500px;
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
      form input, form select, form button {
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
      <h1>Registro de Paciente</h1>
  </header>
  <main>
      <?php
      // Mostrar mensaje flash almacenado en la sesión si existe.
      if (isset($_SESSION['message'])) {
          $msg = htmlspecialchars($_SESSION['message']);
          echo "<div class='mensaje'>{$msg}</div>";
          unset($_SESSION['message']);
      }
      ?>
      <form action="../routes/usuarioRoutes.php?register" method="POST">
          <input type="text" name="nombres" placeholder="Nombres" required>
          <input type="text" name="apellidos" placeholder="Apellidos" required>
          <input type="number" name="edad" placeholder="Edad" required>
          <input type="email" name="correo" placeholder="Correo Electrónico" required>
          <input type="text" name="username" placeholder="Nombre de Usuario" required>
          <input type="password" name="password" placeholder="Contraseña" required>
          <button type="submit">Registrar</button>
      </form>
  </main>
</body>
</html>