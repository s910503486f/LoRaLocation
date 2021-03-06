-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: postman
-- ------------------------------------------------------
-- Server version	5.7.17-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `transport`
--

DROP TABLE IF EXISTS `transport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transport` (
  `_id` int(16) NOT NULL AUTO_INCREMENT,
  `requireTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `arriveTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `sender` int(16) NOT NULL,
  `receiver` int(16) NOT NULL,
  `start_id` int(16) NOT NULL,
  `des_id` int(16) NOT NULL,
  `car_id` int(16) NOT NULL,
  `state` int(2) NOT NULL DEFAULT '0',
  `key` varchar(4) NOT NULL,
  `remark` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`_id`),
  KEY `sender` (`sender`),
  KEY `receiver` (`receiver`),
  KEY `des_id` (`des_id`),
  KEY `car_id` (`car_id`),
  KEY `start_id` (`start_id`),
  CONSTRAINT `transport_ibfk_1` FOREIGN KEY (`sender`) REFERENCES `user` (`_id`),
  CONSTRAINT `transport_ibfk_2` FOREIGN KEY (`receiver`) REFERENCES `user` (`_id`),
  CONSTRAINT `transport_ibfk_3` FOREIGN KEY (`des_id`) REFERENCES `location` (`_id`),
  CONSTRAINT `transport_ibfk_4` FOREIGN KEY (`car_id`) REFERENCES `cars` (`_id`),
  CONSTRAINT `transport_ibfk_5` FOREIGN KEY (`start_id`) REFERENCES `location` (`_id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transport`
--

LOCK TABLES `transport` WRITE;
/*!40000 ALTER TABLE `transport` DISABLE KEYS */;
INSERT INTO `transport` VALUES (2,'2016-12-24 12:00:00','2016-12-25 00:00:00',4,5,1,2,1,1,'',NULL),(4,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,1,'1',NULL),(5,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,1,'1',NULL),(6,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,1,'1',NULL),(7,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,1,'1',NULL),(8,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,2,'1',NULL),(9,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,3,'1',NULL),(10,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,4,'1',NULL),(11,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,1,'1',NULL),(12,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,1,'1',NULL),(13,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,2,'1',NULL),(14,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,2,'1',NULL),(15,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,4,'1',NULL),(16,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,4,'1',NULL),(17,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,4,'1',NULL),(18,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,4,'1',NULL),(19,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,0,'1',NULL),(20,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,0,'1',NULL),(21,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,0,'1',NULL),(22,'0000-00-00 00:00:00','0000-00-00 00:00:00',4,4,1,1,1,0,'1',NULL),(23,'2017-03-17 16:00:00','0000-00-00 00:00:00',4,4,1,1,1,0,'1',NULL),(24,'2017-03-18 11:25:48','0000-00-00 00:00:00',4,4,1,1,1,0,'1',NULL),(25,'2017-03-18 11:26:43','2017-03-18 11:25:48',4,4,1,1,1,0,'1',NULL),(26,'2017-03-18 11:27:15','2017-03-18 11:25:48',4,4,1,1,1,0,'1',NULL),(27,'2017-03-18 11:28:03','2017-03-17 16:00:00',4,4,1,1,1,0,'1',NULL),(28,'2017-03-18 11:55:08','2017-03-18 11:55:08',4,4,1,1,1,0,'1',NULL),(29,'2017-05-04 09:52:00','2017-05-04 09:52:00',4,4,1,1,1,0,'1',NULL),(33,'2017-05-04 10:00:00','2017-05-04 13:09:37',4,4,1,1,1,4,'1',NULL),(39,'2017-05-04 10:00:00','2017-05-06 06:54:14',4,4,1,1,1,0,'1',NULL),(40,'2017-05-04 10:00:00','2017-05-06 06:54:34',4,4,1,1,1,0,'1',NULL),(41,'2017-05-06 10:00:00','2017-05-06 13:04:16',4,4,1,1,1,0,'1',NULL),(42,'2017-05-06 10:00:00','2017-05-06 13:04:17',4,4,1,1,1,0,'1',NULL),(43,'2017-05-06 10:00:00','2017-05-10 17:32:12',4,4,1,1,1,0,'1',NULL),(44,'2017-05-06 10:15:00','2017-05-10 17:33:35',4,4,1,1,1,0,'1',NULL),(45,'2017-05-06 10:15:00','2017-05-10 17:35:25',4,4,1,1,1,0,'1',NULL),(46,'2017-05-06 10:15:00','2017-05-10 17:35:53',4,4,1,1,1,0,'1',NULL),(47,'2017-05-06 10:15:00','0000-00-00 00:00:00',4,4,1,1,1,0,'1',NULL),(48,'2017-05-06 10:15:00','2017-05-11 07:43:42',4,4,1,1,1,0,'1','123'),(49,'2017-05-11 06:30:00','0000-00-00 00:00:00',4,21,2,1,1,0,'5496','Bb'),(50,'2017-05-11 03:30:00','0000-00-00 00:00:00',4,21,1,2,1,0,'5496','Gg'),(51,'2017-05-11 04:30:00','0000-00-00 00:00:00',4,21,1,2,1,0,'5496','DDDD'),(52,'2017-05-11 04:00:00','0000-00-00 00:00:00',4,19,1,2,1,0,'5496','Ff');
/*!40000 ALTER TABLE `transport` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-05-13 15:22:45
