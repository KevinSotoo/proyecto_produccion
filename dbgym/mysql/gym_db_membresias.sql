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
-- Table structure for table `membresias`
--

DROP TABLE IF EXISTS `membresias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membresias` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) NOT NULL,
  `tipo_membresia` varchar(50) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `estado` varchar(30) NOT NULL,
  `fecha_registro` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_membresias_usuario` (`usuario_id`),
  CONSTRAINT `fk_membresias_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membresias`
--

LOCK TABLES `membresias` WRITE;
/*!40000 ALTER TABLE `membresias` DISABLE KEYS */;
INSERT INTO `membresias` VALUES (1,1,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(2,15,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(3,2,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(4,20,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(5,11,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(6,7,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(7,5,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(8,17,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(9,9,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(10,13,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(11,3,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(12,18,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(13,6,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(14,14,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(15,8,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(16,21,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(17,12,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(18,16,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(19,4,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(20,10,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00'),(21,19,'Basica','2024-01-01','2026-06-19',50000.00,'activa','2024-01-01 00:00:00');
/*!40000 ALTER TABLE `membresias` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-20 13:25:13
