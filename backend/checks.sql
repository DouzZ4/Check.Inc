-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 17-06-2025 a las 16:22:52
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `checkinc`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `anomalia`
--

CREATE TABLE `anomalia` (
  `idAnomalia` int(11) NOT NULL,
  `descripcion` text NOT NULL,
  `fechaHora` datetime NOT NULL,
  `sintomas` text NOT NULL,
  `idUsuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `anomalia`
--

INSERT INTO `anomalia` (`idAnomalia`, `descripcion`, `fechaHora`, `sintomas`, `idUsuario`) VALUES
(1, 'Episodio de hipoglucemia leve', '2025-04-17 10:15:00', 'Mareo leve, sudoración fría. Se corrigió con zumo.', 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cita`
--

CREATE TABLE `cita` (
  `idCita` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `hora` time NOT NULL,
  `motivo` text NOT NULL,
  `idUsuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `cita`
--

INSERT INTO `cita` (`idCita`, `fecha`, `hora`, `motivo`, `idUsuario`) VALUES
(3, '2025-04-02', '18:45:00', 'fgfgfgf', 15),
(4, '2025-04-02', '17:45:00', 'fgfgfgf', 15),
(5, '2025-04-03', '18:16:00', 'nose', 15),
(6, '2025-04-01', '17:55:00', 'cvcvbcv', 15),
(7, '2025-04-07', '21:26:00', 'asdadsdas', 15),
(8, '2025-04-01', '21:15:00', 'nose', 15),
(9, '2025-04-06', '22:48:00', 'nose', 15),
(20, '2025-04-10', '09:00:00', 'Control anual Dr. López', 1),
(21, '2025-04-11', '11:30:00', 'Examen de sangre', 3),
(22, '2025-04-15', '16:00:00', 'Consulta nutricionista', 5),
(23, '2025-04-18', '08:30:00', 'Revisión pies', 1),
(24, '2025-04-22', '10:00:00', 'Control general', 2),
(25, '2025-04-25', '14:15:00', 'Cardiólogo', 4),
(26, '2025-05-02', '11:00:00', 'Oftalmólogo - Fondo de ojo', 7),
(27, '2025-05-05', '09:30:00', 'Entrega resultados análisis', 1),
(28, '2025-05-10', '17:00:00', 'Consulta seguimiento', 8),
(29, '2025-05-15', '10:45:00', 'Podólogo', 7),
(34, '2025-04-01', '22:46:00', 'Revision semanal', 40),
(35, '2025-04-25', '10:00:00', 'Control general', 1),
(36, '2025-04-25', '09:00:00', 'Control trimestral diabetes', 1),
(37, '2025-05-10', '11:30:00', 'Consulta nutricionista', 1),
(38, '2025-05-05', '15:00:00', 'Consulta general', 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `glucosa`
--

CREATE TABLE `glucosa` (
  `idGlucosa` int(11) NOT NULL,
  `nivelGlucosa` float NOT NULL,
  `fechaHora` datetime NOT NULL,
  `idUsuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `glucosa`
--

INSERT INTO `glucosa` (`idGlucosa`, `nivelGlucosa`, `fechaHora`, `idUsuario`) VALUES
(13, 77, '2025-04-01 19:11:00', 1),
(14, 120.5, '2025-04-01 19:16:00', 1),
(15, 70, '2025-03-31 23:10:00', 15),
(16, 70.6, '2025-03-31 23:10:00', 15),
(17, 55, '2025-04-03 17:03:00', 15),
(19, 76.5, '2025-04-03 18:34:00', 15),
(20, 76.5322, '2025-04-06 18:55:00', 15),
(22, 22, '2025-03-31 21:13:00', 15),
(23, 26, '2025-04-02 21:47:00', 15),
(24, 95.5, '2025-04-01 08:00:00', 1),
(25, 110, '2025-04-01 08:30:00', 2),
(26, 145.2, '2025-04-01 12:30:00', 1),
(27, 88, '2025-04-02 07:45:00', 3),
(28, 120.8, '2025-04-02 14:00:00', 4),
(29, 92.1, '2025-04-03 08:15:00', 1),
(30, 160.5, '2025-04-03 19:00:00', 5),
(31, 105, '2025-04-04 09:00:00', 2),
(32, 75, '2025-04-05 11:00:00', 6),
(33, 130, '2025-04-06 13:15:00', 1),
(36, 213, '2025-04-07 22:27:00', 40),
(37, 467, '2025-04-15 22:30:00', 40),
(38, 33, '2025-04-09 19:27:00', 40),
(41, 105.5, '2025-04-18 08:00:00', 1),
(42, 130, '2025-04-18 12:30:00', 1),
(43, 115.8, '2025-04-19 07:45:00', 1),
(44, 145.2, '2025-04-19 13:00:00', 1),
(45, 95, '2025-04-19 09:00:00', 2),
(46, 110.3, '2025-04-19 14:00:00', 2),
(47, 105.5, '2025-04-21 19:30:00', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medicamento`
--

CREATE TABLE `medicamento` (
  `idMedicamento` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `dosis` varchar(50) NOT NULL,
  `frecuencia` varchar(50) NOT NULL,
  `fechaInicio` date NOT NULL,
  `fechaFin` date NOT NULL,
  `idUsuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `medicamento`
--

INSERT INTO `medicamento` (`idMedicamento`, `nombre`, `dosis`, `frecuencia`, `fechaInicio`, `fechaFin`, `idUsuario`) VALUES
(1, 'Metformina', '850 mg', '2 veces al día', '2024-01-15', '2025-12-31', 1),
(2, 'Glibenclamida', '5 mg', '1 vez al día (mañana)', '2024-06-01', '2025-06-01', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `notificacion`
--

CREATE TABLE `notificacion` (
  `idNotificacion` int(11) NOT NULL,
  `mensaje` text NOT NULL,
  `tipo` varchar(50) NOT NULL,
  `fechaHora` datetime NOT NULL,
  `estado` varchar(50) NOT NULL,
  `idUsuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `notificacion`
--

INSERT INTO `notificacion` (`idNotificacion`, `mensaje`, `tipo`, `fechaHora`, `estado`, `idUsuario`) VALUES
(1, 'Recuerda tomar tu Metformina', 'Medicación', '2025-04-19 08:00:00', 'Enviada', 1),
(2, 'Próxima cita: Control trimestral el 2025-04-25 09:00', 'Cita', '2025-04-19 10:00:00', 'Leída', 1),
(3, 'Nivel de glucosa post-almuerzo un poco alto (145.2)', 'Alerta Glucosa', '2025-04-19 13:05:00', 'Enviada', 1),
(4, 'Registro de glucosa pendiente hoy', 'Recordatorio', '2025-04-19 18:00:00', 'Enviada', 2),
(5, 'Nuevo usuario registrado: ana.perez', 'Sistema', '2025-04-16 11:00:00', 'Leída', 3),
(6, 'Actualización de sistema programada para el Lunes', 'Sistema', '2025-04-18 15:30:00', 'Enviada', 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `recordatorio`
--

CREATE TABLE `recordatorio` (
  `idRecordatorio` int(11) NOT NULL,
  `tipo` varchar(50) NOT NULL,
  `descripcion` text NOT NULL,
  `fechaHora` datetime NOT NULL,
  `idUsuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `recordatorio`
--

INSERT INTO `recordatorio` (`idRecordatorio`, `tipo`, `descripcion`, `fechaHora`, `idUsuario`) VALUES
(1, 'Medición Glucosa', 'Medir glucosa antes de cenar', '2025-04-19 19:00:00', 1),
(2, 'Ejercicio', 'Caminata de 30 minutos', '2025-04-20 17:00:00', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reporte`
--

CREATE TABLE `reporte` (
  `idReporte` int(11) NOT NULL,
  `tipoReporte` varchar(100) NOT NULL,
  `fechaGeneracion` date NOT NULL,
  `idUsuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `reporte`
--

INSERT INTO `reporte` (`idReporte`, `tipoReporte`, `fechaGeneracion`, `idUsuario`) VALUES
(1, 'Resumen semanal de glucosa', '2025-04-14', 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol`
--

CREATE TABLE `rol` (
  `idRol` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL CHECK (`nombre` in ('admin','paciente'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `rol`
--

INSERT INTO `rol` (`idRol`, `nombre`) VALUES
(1, 'admin'),
(2, 'paciente');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `idUsuario` int(11) NOT NULL,
  `user` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `documento` int(11) NOT NULL,
  `nombres` varchar(50) NOT NULL,
  `apellidos` varchar(50) NOT NULL,
  `correo` varchar(50) NOT NULL,
  `edad` int(11) NOT NULL,
  `idRol` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`idUsuario`, `user`, `password`, `documento`, `nombres`, `apellidos`, `correo`, `edad`, `idRol`) VALUES
(1, 'carlosg', 'pass1234', 1001234567, 'Carlos', 'Gómez', 'carlos.gomez@example.com', 30, 2),
(2, 'anam', 'clave5678', 1007654321, 'Ana', 'Martínez', 'ana.martinez@example.com', 25, 2),
(3, 'luisf', 'segura9012', 1012345678, 'Luis', 'Fernández', 'luis.fernandez@example.com', 40, 1),
(4, 'marial', 'password2024', 1018765432, 'María', 'López', 'maria.lopez@example.com', 35, 2),
(5, 'javierr', 'passabcd', 1023456789, 'Javier', 'Rodríguez', 'javier.rodriguez@example.com', 28, 1),
(6, 'sofiap', 'claveqwerty', 1029876543, 'Sofía', 'Pérez', 'sofia.perez@example.com', 32, 2),
(7, 'andresh', 'adminpass', 1034567890, 'Andrés', 'Hernández', 'andres.hernandez@example.com', 27, 1),
(8, 'elenad', 'securepass', 1038765432, 'Elena', 'Díaz', 'elena.diaz@example.com', 29, 2),
(9, 'DouzZ', '$2y$10$pOxHJ6dbso0CkuF89TtRR.DuUhUaq7zadxplkLQwhy9.PxrqDWFVm', 0, 'angel mendez ', 'Nendez', 'angeleduardomen@outlook.com', 23, 2),
(13, 'DouzZ4', '$2y$10$9d0BDYqMpTRL/uW8/sYBPu8w622UDm7UCA6HQ1zhlrL0UCtLRxYOK', 1193086738, 'Angel', 'gamez', 'angeleduardomen@hotmail.com', 23, 1),
(14, 'DouzZ42', '$2y$10$YnrBxg5CeeTcFOp98qjxSObWdsccCOKPsCc6tEkb8kzSXL0fLg.hu', 1193086737, 'Angel bbgbg', 'Nendezbggbgbg', 'angeleduardomen@mail.com', 23, 2),
(15, 'DouzZ2', '$2y$10$DZhqqlCjPD0pOPuZUVqsfebujW2iSD.ucaqvFsUYBceOnUWjKxazO', 1193086736, 'angel mende', 'game', 'angeleduardomen@homail.com', 23, 1),
(16, 'DouzZ22', '$2y$10$oU2fVHEcwmlHWmQhEWdTf.Bdnr/PR3dOdKqk/Z8tFse/oteKDR47u', 1193086735, 'angel mendez ', 'gamez', 'angeleduardomen@outlook.com', 25, 2),
(18, 'DouzZ223', '$2y$10$kg5uHJIARePf8ojwKpXfxu9LeI7zX0f242mICM9PffLrOLZp7IlX6', 1193086734, 'Angel', 'gamez', 'angeleduardomen@msail.com', 2323, 2),
(19, 'DouzZ2233', '$2y$10$0MYI1QHjrssH1QQW/g/dB.wW5ACOdGeCfrO.15kDZqmElFyw9k.QC', 2147483647, 'angel mendez ', 'gamez', 'angeleduardomen@hotmail.com', 33, 2),
(28, 'DouzZ29', '$2y$10$rQw00.UbSJn8vE8IuBeS..11xrynmbyKPjkk6YIUd/aXFsHFFNbHe', 2222222, 'angel mendez ', 'Nendez', 'angel@corre2.com', 25, 2),
(29, 'agomez', '$2y$10$fictionalHashExamplePretendItIsUnique123.', 1098765432, 'Ana', 'Gómez', 'ana.gomez@email.com', 34, 2),
(30, 'lmartinez', '$2y$10$fictionalHashExamplePretendItIsUnique123.', 1098765433, 'Luis', 'Martínez', 'luis.martinez@email.com', 45, 2),
(31, 'sperez', '$2y$10$fictionalHashExamplePretendItIsUnique123.', 1098765434, 'Sofía', 'Pérez', 'sofia.perez@email.com', 28, 2),
(32, 'crodriguez', '$2y$10$fictionalHashExamplePretendItIsUnique123.', 1098765435, 'Carlos', 'Rodríguez', 'carlos.r@email.com', 52, 2),
(33, 'mlopez', '$2y$10$fictionalHashExamplePretendItIsUnique123.', 1098765436, 'María', 'López', 'maria.lopez@email.com', 61, 2),
(34, 'jhernandez', '$2y$10$fictionalHashExamplePretendItIsUnique123.', 1098765437, 'Javier', 'Hernández', 'javier.h@email.com', 30, 2),
(35, 'lgarcia', '$2y$10$fictionalHashExamplePretendItIsUnique123.', 1098765438, 'Laura', 'García', 'laura.garcia@email.com', 25, 2),
(36, 'mdiaz', '$2y$10$fictionalHashExamplePretendItIsUnique123.', 1098765439, 'Miguel', 'Díaz', 'miguel.diaz@email.com', 41, 2),
(37, 'esanchez', '$2y$10$fictionalHashExamplePretendItIsUnique123.', 1098765440, 'Elena', 'Sánchez', 'elena.sanchez@email.com', 38, 2),
(38, 'dromero', '$2y$10$fictionalHashExamplePretendItIsUnique123.', 1098765441, 'David', 'Romero', 'david.romero@email.com', 49, 2),
(39, 'Bryalav', '$2y$10$l/BYSbWCpa/195MfHyZzjOrhdaDEOtdWNa/gyEsym74Eeh5JyEIei', 1023027242, 'Bryan', 'Lavao', 'b.lavaol7@gmail.com', 26, 2),
(40, 'HaroldA11', '$2y$10$NHy5KyOznwmIL9takXZU4uilJ77IuWp3DO6/5OoGyD.G9ipHRKkpm', 1030520976, 'Harold', 'Alonso', 'harold@gmail', 21, 2),
(42, 'nosex2', '$2y$10$JwhDxeU.VlWQsI9XQCqxvekItnG6pV2B2kpy9MLeGIvO8AuR4Tq/S', 1111111, 'edward', 'Alonso', 'nose@nose.com', 25, 2),
(43, 'Brayan1', '$2y$10$6I3zjI2LgcXGD9nLWaEB9OA6BKe2YYUor9Tkags5dQgVOZ/J/iuRW', 78354234, 'Brayan', 'Lvao', 'ejemplo3@gmail.com', 23, 2),
(44, 'Miguel1', '$2y$10$2Y25YDHiOrYErc0mj4.KMuj5faPILrwdj4z8j3ifAIzeV2nqh5DrW', 7693609, 'Miguel', 'Cabrera', 'gagsjgh@gmail.com', 20, 2),
(45, 'juanp', 'contraseña_segura', 12345678, 'Juan', 'Perez', 'juan.perez@email.com', 35, 2),
(47, 'carlosgg', 'pass123', 11223344, 'Carlos', 'Gomez', 'carlos.gomez@email.com', 42, 2),
(48, 'anap', 'pass456', 55667788, 'Ana', 'Perez', 'ana.perez@email.com', 28, 2),
(49, 'admin_check', 'adminpass', 99001122, 'Admin', 'Sistema', 'admin@checks.com', 35, 1),
(51, 'Psilva82', '$2y$10$WeONEqE/.YWh4qZqPsDncurIuuwKzUqQKIAvUGAo.VlLL1zg8BX/q', 987654321, 'pedro antonio ', 'silva lagos', 'pedro.silva.test@email.com', 42, 2);

-- --------------------------------------------------------

--
-- Estructura Stand-in para la vista `vista_usuario_glucosa`
-- (Véase abajo para la vista actual)
--
CREATE TABLE `vista_usuario_glucosa` (
`idUsuario` int(11)
,`nombres` varchar(50)
,`apellidos` varchar(50)
,`idGlucosa` int(11)
,`nivelGlucosa` float
,`fechaHora` datetime
);

-- --------------------------------------------------------

--
-- Estructura para la vista `vista_usuario_glucosa`
--
DROP TABLE IF EXISTS `vista_usuario_glucosa`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vista_usuario_glucosa`  AS SELECT `u`.`idUsuario` AS `idUsuario`, `u`.`nombres` AS `nombres`, `u`.`apellidos` AS `apellidos`, `g`.`idGlucosa` AS `idGlucosa`, `g`.`nivelGlucosa` AS `nivelGlucosa`, `g`.`fechaHora` AS `fechaHora` FROM (`usuario` `u` join `glucosa` `g` on(`u`.`idUsuario` = `g`.`idUsuario`)) ;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `anomalia`
--
ALTER TABLE `anomalia`
  ADD PRIMARY KEY (`idAnomalia`),
  ADD KEY `idUsuario` (`idUsuario`);

--
-- Indices de la tabla `cita`
--
ALTER TABLE `cita`
  ADD PRIMARY KEY (`idCita`),
  ADD KEY `idUsuario` (`idUsuario`);

--
-- Indices de la tabla `glucosa`
--
ALTER TABLE `glucosa`
  ADD PRIMARY KEY (`idGlucosa`),
  ADD KEY `idUsuario` (`idUsuario`);

--
-- Indices de la tabla `medicamento`
--
ALTER TABLE `medicamento`
  ADD PRIMARY KEY (`idMedicamento`),
  ADD KEY `idUsuario` (`idUsuario`);

--
-- Indices de la tabla `notificacion`
--
ALTER TABLE `notificacion`
  ADD PRIMARY KEY (`idNotificacion`),
  ADD KEY `idUsuario` (`idUsuario`);

--
-- Indices de la tabla `recordatorio`
--
ALTER TABLE `recordatorio`
  ADD PRIMARY KEY (`idRecordatorio`),
  ADD KEY `idUsuario` (`idUsuario`);

--
-- Indices de la tabla `reporte`
--
ALTER TABLE `reporte`
  ADD PRIMARY KEY (`idReporte`),
  ADD KEY `idUsuario` (`idUsuario`);

--
-- Indices de la tabla `rol`
--
ALTER TABLE `rol`
  ADD PRIMARY KEY (`idRol`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`idUsuario`),
  ADD UNIQUE KEY `user` (`user`),
  ADD UNIQUE KEY `documento` (`documento`),
  ADD KEY `idRol` (`idRol`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `anomalia`
--
ALTER TABLE `anomalia`
  MODIFY `idAnomalia` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `cita`
--
ALTER TABLE `cita`
  MODIFY `idCita` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT de la tabla `glucosa`
--
ALTER TABLE `glucosa`
  MODIFY `idGlucosa` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;

--
-- AUTO_INCREMENT de la tabla `medicamento`
--
ALTER TABLE `medicamento`
  MODIFY `idMedicamento` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `notificacion`
--
ALTER TABLE `notificacion`
  MODIFY `idNotificacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `recordatorio`
--
ALTER TABLE `recordatorio`
  MODIFY `idRecordatorio` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `reporte`
--
ALTER TABLE `reporte`
  MODIFY `idReporte` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `rol`
--
ALTER TABLE `rol`
  MODIFY `idRol` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `idUsuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=52;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `anomalia`
--
ALTER TABLE `anomalia`
  ADD CONSTRAINT `anomalia_ibfk_1` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`);

--
-- Filtros para la tabla `cita`
--
ALTER TABLE `cita`
  ADD CONSTRAINT `cita_ibfk_1` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`);

--
-- Filtros para la tabla `glucosa`
--
ALTER TABLE `glucosa`
  ADD CONSTRAINT `glucosa_ibfk_1` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`);

--
-- Filtros para la tabla `medicamento`
--
ALTER TABLE `medicamento`
  ADD CONSTRAINT `medicamento_ibfk_1` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`);

--
-- Filtros para la tabla `notificacion`
--
ALTER TABLE `notificacion`
  ADD CONSTRAINT `notificacion_ibfk_1` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`);

--
-- Filtros para la tabla `recordatorio`
--
ALTER TABLE `recordatorio`
  ADD CONSTRAINT `recordatorio_ibfk_1` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`);

--
-- Filtros para la tabla `reporte`
--
ALTER TABLE `reporte`
  ADD CONSTRAINT `reporte_ibfk_1` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`);

--
-- Filtros para la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`idRol`) REFERENCES `rol` (`idRol`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
