    -- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Linux (x86_64)
    --
    -- Host: localhost    Database: cinemax
    -- ------------------------------------------------------
    -- Server version	10.4.32-MariaDB

    /*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
    /*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
    /*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
    /*!40101 SET NAMES utf8mb4 */;
    /*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
    /*!40103 SET TIME_ZONE='+00:00' */;
    /*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
    /*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
    /*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
    /*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

    --
    -- Table structure for table `Asiento`
    --

    DROP TABLE IF EXISTS `Asiento`;
    /*!40101 SET @saved_cs_client     = @@character_set_client */;
    /*!40101 SET character_set_client = utf8 */;
    CREATE TABLE `Asiento` (
      `ID_Asiento` int(10) unsigned NOT NULL AUTO_INCREMENT,
      `Fila` int(11) NOT NULL,
      `Numero` int(11) NOT NULL,
      `Tipo` varchar(20) NOT NULL,
      `Sala_ID_Sala` int(10) unsigned NOT NULL,
      PRIMARY KEY (`ID_Asiento`),
      UNIQUE KEY `uq_asiento_sala_fila_numero` (`Sala_ID_Sala`,`Fila`,`Numero`),
      KEY `idx_asiento_sala` (`Sala_ID_Sala`),
      CONSTRAINT `Asiento_Sala_FK` FOREIGN KEY (`Sala_ID_Sala`) REFERENCES `Sala` (`ID_Sala`) ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
    /*!40101 SET character_set_client = @saved_cs_client */;

    --
    -- Dumping data for table `Asiento`
    --

    LOCK TABLES `Asiento` WRITE;
    /*!40000 ALTER TABLE `Asiento` DISABLE KEYS */;
    /*!40000 ALTER TABLE `Asiento` ENABLE KEYS */;
    UNLOCK TABLES;

    --
    -- Table structure for table `Cine`
    --

    DROP TABLE IF EXISTS `Cine`;
    /*!40101 SET @saved_cs_client     = @@character_set_client */;
    /*!40101 SET character_set_client = utf8 */;
    CREATE TABLE `Cine` (
      `ID_Cine` int(10) unsigned NOT NULL AUTO_INCREMENT,
      `Nombre` varchar(50) NOT NULL,
      `Direccion` varchar(100) NOT NULL,
      `Ciudad` varchar(50) NOT NULL,
      PRIMARY KEY (`ID_Cine`)
    ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
    /*!40101 SET character_set_client = @saved_cs_client */;

    --
    -- Dumping data for table `Cine`
    --

    LOCK TABLES `Cine` WRITE;
    /*!40000 ALTER TABLE `Cine` DISABLE KEYS */;
    INSERT INTO `Cine` VALUES (1,'CineMax Centro','Av. Principal #123, Centro','Ciudad Victoria');
    /*!40000 ALTER TABLE `Cine` ENABLE KEYS */;
    UNLOCK TABLES;

    --
    -- Table structure for table `Funcion`
    --

    DROP TABLE IF EXISTS `Funcion`;
    /*!40101 SET @saved_cs_client     = @@character_set_client */;
    /*!40101 SET character_set_client = utf8 */;
    CREATE TABLE `Funcion` (
      `ID_Funcion` int(10) unsigned NOT NULL AUTO_INCREMENT,
      `Fecha` date NOT NULL,
      `Hora` time NOT NULL,
      `Pelicula_ID_Pelicula` int(10) unsigned NOT NULL,
      `Sala_ID_Sala` int(10) unsigned NOT NULL,
      PRIMARY KEY (`ID_Funcion`),
      UNIQUE KEY `uq_funcion_sala_fecha_hora` (`Sala_ID_Sala`,`Fecha`,`Hora`),
      KEY `idx_funcion_pelicula` (`Pelicula_ID_Pelicula`),
      KEY `idx_funcion_sala` (`Sala_ID_Sala`),
      CONSTRAINT `Funcion_Pelicula_FK` FOREIGN KEY (`Pelicula_ID_Pelicula`) REFERENCES `Pelicula` (`ID_Pelicula`) ON UPDATE CASCADE,
      CONSTRAINT `Funcion_Sala_FK` FOREIGN KEY (`Sala_ID_Sala`) REFERENCES `Sala` (`ID_Sala`) ON UPDATE CASCADE
    ) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
    /*!40101 SET character_set_client = @saved_cs_client */;

    --
    -- Dumping data for table `Funcion`
    --

    LOCK TABLES `Funcion` WRITE;
    /*!40000 ALTER TABLE `Funcion` DISABLE KEYS */;
    INSERT INTO `Funcion` VALUES (2,'2025-08-11','14:30:00',1,2),(3,'2025-08-11','17:00:00',1,3),(4,'2025-08-11','19:45:00',1,2),(5,'2025-08-11','15:00:00',2,4),(6,'2025-08-11','18:30:00',2,5),(7,'2025-08-11','21:00:00',2,3),(8,'2025-08-11','16:00:00',3,5),(9,'2025-08-11','18:00:00',3,2),(10,'2025-08-11','20:30:00',3,4),(11,'2025-08-11','15:30:00',4,3),(12,'2025-08-11','19:00:00',4,5),(13,'2025-08-11','22:30:00',4,2);
    /*!40000 ALTER TABLE `Funcion` ENABLE KEYS */;
    UNLOCK TABLES;

    --
    -- Table structure for table `Pelicula`
    --

    DROP TABLE IF EXISTS `Pelicula`;
    /*!40101 SET @saved_cs_client     = @@character_set_client */;
    /*!40101 SET character_set_client = utf8 */;
    CREATE TABLE `Pelicula` (
      `ID_Pelicula` int(10) unsigned NOT NULL AUTO_INCREMENT,
      `Titulo` varchar(50) NOT NULL,
      `Genero` varchar(20) NOT NULL,
      `Clasificacion` varchar(20) NOT NULL,
      `Duracion` int(10) unsigned NOT NULL,
      `Idioma` varchar(20) NOT NULL,
      `Sinopsis` text DEFAULT NULL,
      PRIMARY KEY (`ID_Pelicula`)
    ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
    /*!40101 SET character_set_client = @saved_cs_client */;

    --
    -- Dumping data for table `Pelicula`
    --

    LOCK TABLES `Pelicula` WRITE;
    /*!40000 ALTER TABLE `Pelicula` DISABLE KEYS */;
    INSERT INTO `Pelicula` VALUES (1,'El Viaje del Héroe','Aventura','PG-13',120,'Español','Un joven aventurero emprende un viaje épico para descubrir su verdadero destino y salvar su reino de una antigua maldición.'),(2,'Sombras del Pasado','Thriller','R',105,'Español','Un detective retirado se ve obligado a confrontar los demonios de su pasado cuando un asesino en serie comienza a imitar los crímenes de un caso que nunca pudo resolver.'),(3,'La Ciudad Olvidada','Fantasía','PG',95,'Español','Una arqueóloga descubre una ciudad perdida llena de magia y criaturas fantásticas. Debe proteger este lugar mágico de quienes buscan explotar sus poderes.'),(4,'Código Secreto','Acción','R',135,'Español','Un agente especial debe infiltrarse en una organización criminal internacional para desactivar una amenaza que podría cambiar el curso de la historia mundial.');
    /*!40000 ALTER TABLE `Pelicula` ENABLE KEYS */;
    UNLOCK TABLES;

    --
    -- Table structure for table `Reserva`
    --

    DROP TABLE IF EXISTS `Reserva`;
    /*!40101 SET @saved_cs_client     = @@character_set_client */;
    /*!40101 SET character_set_client = utf8 */;
    CREATE TABLE `Reserva` (
      `ID_Reserva` int(10) unsigned NOT NULL AUTO_INCREMENT,
      `Fecha_Reserva` datetime NOT NULL,
      `Estado` varchar(20) NOT NULL,
      `Usuario_ID_Usuario` int(10) unsigned NOT NULL,
      `Funcion_ID_Funcion` int(10) unsigned NOT NULL,
      PRIMARY KEY (`ID_Reserva`),
      KEY `idx_reserva_usuario` (`Usuario_ID_Usuario`),
      KEY `idx_reserva_funcion` (`Funcion_ID_Funcion`),
      CONSTRAINT `Reserva_Funcion_FK` FOREIGN KEY (`Funcion_ID_Funcion`) REFERENCES `Funcion` (`ID_Funcion`) ON UPDATE CASCADE,
      CONSTRAINT `Reserva_Usuario_FK` FOREIGN KEY (`Usuario_ID_Usuario`) REFERENCES `Usuario` (`ID_Usuario`) ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
    /*!40101 SET character_set_client = @saved_cs_client */;

    --
    -- Dumping data for table `Reserva`
    --

    LOCK TABLES `Reserva` WRITE;
    /*!40000 ALTER TABLE `Reserva` DISABLE KEYS */;
    /*!40000 ALTER TABLE `Reserva` ENABLE KEYS */;
    UNLOCK TABLES;

    --
    -- Table structure for table `Reserva_Asiento`
    --

    DROP TABLE IF EXISTS `Reserva_Asiento`;
    /*!40101 SET @saved_cs_client     = @@character_set_client */;
    /*!40101 SET character_set_client = utf8 */;
    CREATE TABLE `Reserva_Asiento` (
      `ID_ReservasAsiento` int(10) unsigned NOT NULL AUTO_INCREMENT,
      `Reserva_ID_Reserva` int(10) unsigned NOT NULL,
      `Asiento_ID_Asiento` int(10) unsigned NOT NULL,
      PRIMARY KEY (`ID_ReservasAsiento`),
      UNIQUE KEY `uq_reserva_asiento` (`Reserva_ID_Reserva`,`Asiento_ID_Asiento`),
      KEY `idx_reservaasiento_reserva` (`Reserva_ID_Reserva`),
      KEY `idx_reservaasiento_asiento` (`Asiento_ID_Asiento`),
      CONSTRAINT `Reserva_Asiento_Asiento_FK` FOREIGN KEY (`Asiento_ID_Asiento`) REFERENCES `Asiento` (`ID_Asiento`) ON UPDATE CASCADE,
      CONSTRAINT `Reserva_Asiento_Reserva_FK` FOREIGN KEY (`Reserva_ID_Reserva`) REFERENCES `Reserva` (`ID_Reserva`) ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
    /*!40101 SET character_set_client = @saved_cs_client */;

    --
    -- Dumping data for table `Reserva_Asiento`
    --

    LOCK TABLES `Reserva_Asiento` WRITE;
    /*!40000 ALTER TABLE `Reserva_Asiento` DISABLE KEYS */;
    /*!40000 ALTER TABLE `Reserva_Asiento` ENABLE KEYS */;
    UNLOCK TABLES;

    --
    -- Table structure for table `Reserva_Snack`
    --

    DROP TABLE IF EXISTS `Reserva_Snack`;
    /*!40101 SET @saved_cs_client     = @@character_set_client */;
    /*!40101 SET character_set_client = utf8 */;
    CREATE TABLE `Reserva_Snack` (
      `ID_ReservaSnack` int(10) unsigned NOT NULL AUTO_INCREMENT,
      `Cantidad` int(10) unsigned NOT NULL DEFAULT 1,
      `Snack_ID_Snack` int(10) unsigned NOT NULL,
      `Reserva_ID_Reserva` int(10) unsigned NOT NULL,
      PRIMARY KEY (`ID_ReservaSnack`),
      UNIQUE KEY `uq_reservasnack_reserva_snack` (`Reserva_ID_Reserva`,`Snack_ID_Snack`),
      KEY `idx_reservasnack_snack` (`Snack_ID_Snack`),
      KEY `idx_reservasnack_reserva` (`Reserva_ID_Reserva`),
      CONSTRAINT `Reserva_Snack_Reserva_FK` FOREIGN KEY (`Reserva_ID_Reserva`) REFERENCES `Reserva` (`ID_Reserva`) ON DELETE CASCADE ON UPDATE CASCADE,
      CONSTRAINT `Reserva_Snack_Snack_FK` FOREIGN KEY (`Snack_ID_Snack`) REFERENCES `Snack` (`ID_Snack`) ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
    /*!40101 SET character_set_client = @saved_cs_client */;

    --
    -- Dumping data for table `Reserva_Snack`
    --

    LOCK TABLES `Reserva_Snack` WRITE;
    /*!40000 ALTER TABLE `Reserva_Snack` DISABLE KEYS */;
    /*!40000 ALTER TABLE `Reserva_Snack` ENABLE KEYS */;
    UNLOCK TABLES;

    --
    -- Table structure for table `Sala`
    --

    DROP TABLE IF EXISTS `Sala`;
    /*!40101 SET @saved_cs_client     = @@character_set_client */;
    /*!40101 SET character_set_client = utf8 */;
    CREATE TABLE `Sala` (
      `ID_Sala` int(10) unsigned NOT NULL AUTO_INCREMENT,
      `Nombre_Sala` varchar(100) NOT NULL,
      `Capacidad` int(10) unsigned NOT NULL,
      `Tipo` varchar(20) NOT NULL,
      `Cine_ID_Cine` int(10) unsigned NOT NULL,
      PRIMARY KEY (`ID_Sala`),
      KEY `idx_sala_cine` (`Cine_ID_Cine`),
      CONSTRAINT `Sala_Cine_FK` FOREIGN KEY (`Cine_ID_Cine`) REFERENCES `Cine` (`ID_Cine`) ON UPDATE CASCADE
    ) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
    /*!40101 SET character_set_client = @saved_cs_client */;

    --
    -- Dumping data for table `Sala`
    --

    LOCK TABLES `Sala` WRITE;
    /*!40000 ALTER TABLE `Sala` DISABLE KEYS */;
    INSERT INTO `Sala` VALUES (2,'Sala 1',150,'Estándar',1),(3,'Sala 2',200,'Premium',1),(4,'Sala 3',120,'Estándar',1),(5,'Sala VIP',80,'VIP',1);
    /*!40000 ALTER TABLE `Sala` ENABLE KEYS */;
    UNLOCK TABLES;

    --
    -- Table structure for table `Snack`
    --

    DROP TABLE IF EXISTS `Snack`;
    /*!40101 SET @saved_cs_client     = @@character_set_client */;
    /*!40101 SET character_set_client = utf8 */;
    CREATE TABLE `Snack` (
      `ID_Snack` int(10) unsigned NOT NULL AUTO_INCREMENT,
      `Nombre` varchar(50) NOT NULL,
      `Precio` decimal(10,2) NOT NULL,
      `Descripcion` text DEFAULT NULL,
      `Tipo` varchar(20) DEFAULT NULL,
      PRIMARY KEY (`ID_Snack`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
    /*!40101 SET character_set_client = @saved_cs_client */;

    --
    -- Dumping data for table `Snack`
    --

    LOCK TABLES `Snack` WRITE;
    /*!40000 ALTER TABLE `Snack` DISABLE KEYS */;
    /*!40000 ALTER TABLE `Snack` ENABLE KEYS */;
    UNLOCK TABLES;

    --
    -- Table structure for table `Usuario`
    --

    DROP TABLE IF EXISTS `Usuario`;
    /*!40101 SET @saved_cs_client     = @@character_set_client */;
    /*!40101 SET character_set_client = utf8 */;
    CREATE TABLE `Usuario` (
      `ID_Usuario` int(10) unsigned NOT NULL AUTO_INCREMENT,
      `Nombre` varchar(50) NOT NULL,
      `Apellido` varchar(50) DEFAULT NULL,
      `Correo_Electronico` varchar(100) NOT NULL,
      `Contrasena` varchar(255) NOT NULL,
      `Telefono` varchar(20) NOT NULL,
      PRIMARY KEY (`ID_Usuario`),
      UNIQUE KEY `uq_usuario_correo` (`Correo_Electronico`)
    ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
    /*!40101 SET character_set_client = @saved_cs_client */;

    --
    -- Dumping data for table `Usuario`
    --

    LOCK TABLES `Usuario` WRITE;
    /*!40000 ALTER TABLE `Usuario` DISABLE KEYS */;
    INSERT INTO `Usuario` VALUES (1,'Arturo','Rosales V','rosalesvelazquezarturo@gmail.com','443556','Arthur'),(2,'Ricardo','Maximiliano Trejo Tobias','2430180@gmail.com','levely345','levelyy'),(4,'Efrain','Abdiel Villanueva Balboa','upv@gmail.com','test1234','cinefann1');
    /*!40000 ALTER TABLE `Usuario` ENABLE KEYS */;
    UNLOCK TABLES;
    /*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

    /*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
    /*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
    /*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
    /*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
    /*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
    /*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
    /*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

    -- Dump completed on 2025-08-12 20:52:58
