-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 172.30.16.49    Database: gym_db
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `documento` varchar(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `edad` int(11) DEFAULT NULL,
  `sexo` varchar(20) DEFAULT NULL,
  `peso` decimal(6,2) DEFAULT NULL,
  `altura` decimal(6,2) DEFAULT NULL,
  `objetivo` varchar(100) DEFAULT NULL,
  `calorias` decimal(10,2) DEFAULT NULL,
  `tipo_membresia` varchar(50) DEFAULT 'Basica',
  `abandonado` tinyint(1) DEFAULT 0,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `documento` (`documento`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'1025528936','Kevin',20,'Masculino',76.00,174.00,'Ganar masa muscular',2754.00,'Basica',1,'2026-04-22 17:38:16'),(2,'1938475620','Daniel',22,'Masculino',65.00,180.00,'Ganar masa muscular',2404.00,'Basica',0,'2026-04-22 17:38:16'),(3,'5673829104','Sebastian',19,'Masculino',50.00,184.00,'Ganar masa muscular',1917.00,'Basica',0,'2026-04-22 17:38:16'),(4,'9081726354','Nicolas',21,'Masculino',71.00,174.00,'Ganar masa muscular',2776.00,'Basica',0,'2026-04-22 17:38:16'),(5,'3748291056','Andres',23,'Masculino',90.00,178.00,'Perder grasa',2541.00,'Basica',0,'2026-04-22 17:38:16'),(6,'6519203847','Camila',22,'Femenino',58.00,162.00,'Mantener peso',1980.00,'Basica',0,'2026-04-22 17:38:16'),(7,'2847561930','Valeria',20,'Femenino',52.00,158.00,'Ganar masa muscular',2150.00,'Basica',0,'2026-04-22 17:38:16'),(8,'7192038456','Miguel',25,'Masculino',78.00,176.00,'Mantener peso',2620.00,'Basica',0,'2026-04-22 17:38:16'),(9,'4829105736','Laura',19,'Femenino',61.00,165.00,'Perder grasa',1750.00,'Basica',0,'2026-04-22 17:38:16'),(10,'9301758462','Santiago',24,'Masculino',95.00,182.00,'Perder grasa',2890.00,'Basica',0,'2026-04-22 17:38:16'),(11,'2758491036','Isabella',21,'Femenino',55.00,160.00,'Mantener peso',1870.00,'Basica',0,'2026-04-22 17:38:16'),(12,'8162039475','Juan',28,'Masculino',82.00,179.00,'Ganar masa muscular',3100.00,'Basica',0,'2026-04-22 17:38:16'),(13,'5039182746','Daniela',22,'Femenino',67.00,168.00,'Perder grasa',1920.00,'Basica',0,'2026-04-22 17:38:16'),(14,'6928471503','Carlos',30,'Masculino',88.00,175.00,'Mantener peso',2710.00,'Basica',0,'2026-04-22 17:38:16'),(15,'1475938206','Sofia',18,'Femenino',49.00,155.00,'Ganar masa muscular',2040.00,'Basica',0,'2026-04-22 17:38:16'),(16,'8745201936','Mateo',26,'Masculino',73.00,177.00,'Mantener peso',2560.00,'Basica',0,'2026-04-22 17:38:16'),(17,'3910284756','Alejandra',23,'Femenino',70.00,170.00,'Perder grasa',1830.00,'Basica',0,'2026-04-22 17:38:16'),(18,'6283749105','Natalia',20,'Femenino',54.00,161.00,'Mantener peso',1790.00,'Basica',0,'2026-04-22 17:38:16'),(19,'9502847613','Manuel',30,'Masculino',80.00,170.00,'Perder grasa',1661.00,'Basica',0,'2026-04-22 17:38:16'),(20,'2047591836','Ana',22,'Femenino',57.00,160.00,'Ganar masa muscular',2186.00,'Basica',0,'2026-04-22 17:38:16'),(21,'7639182504','Luis',20,'Masculino',79.00,179.00,'Ganar masa muscular',3211.00,'Basica',0,'2026-04-22 17:38:16'),(22,'1234567','joan',20,'Masculino',76.00,178.00,'Ganar masa muscular',3155.00,'VIP',0,'2026-04-22 18:13:30'),(23,'548454612','Santiago',22,'Masculino',56.00,160.00,'Perder grasa',1601.00,'Premium',0,'2026-04-22 18:26:37'),(24,'4564213','Isabella',18,'Femenino',56.00,159.00,'Ganar masa muscular',2419.00,'Premium',0,'2026-05-08 17:42:44');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-20 13:25:14
