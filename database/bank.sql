-- MySQL dump 10.13  Distrib 8.0.33, for macos13 (x86_64)
--
-- Host: localhost    Database: bank
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account_types`
--

DROP TABLE IF EXISTS `account_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_types` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_types`
--

LOCK TABLES `account_types` WRITE;
/*!40000 ALTER TABLE `account_types` DISABLE KEYS */;
INSERT INTO `account_types` VALUES (1,'Current'),(2,'Deposit');
/*!40000 ALTER TABLE `account_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` int NOT NULL,
  `iban` varchar(19) NOT NULL,
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00',
  `client` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `iban` (`iban`),
  KEY `type` (`type`),
  KEY `client` (`client`),
  CONSTRAINT `accounts_ibfk_1` FOREIGN KEY (`type`) REFERENCES `account_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `accounts_ibfk_2` FOREIGN KEY (`client`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,1,'0406 4131 1928 1551',995100.00,1),(2,1,'2876 4987 3718 6941',1000.00,3),(3,1,'0677 6021 5206 6526',1500.00,5),(4,1,'7795 0600 9556 7798',100.00,6),(5,1,'5106 3935 2708 3883',0.00,7),(6,1,'7220 1867 0319 4029',0.00,8),(7,1,'0257 6548 3313 7365',0.00,9),(8,1,'9120 8952 8537 8893',0.00,10),(9,1,'2013 2931 0287 7048',100.00,11),(10,1,'9959 4114 6569 3957',500.00,12),(11,1,'0254 3122 5558 6734',0.00,14),(12,1,'3209 0242 4706 1302',1000.00,15),(13,1,'1654 2658 9218 4867',0.00,17),(14,1,'8838 1185 5734 6427',200.00,18),(15,1,'7528 2498 2755 9220',0.00,19),(16,1,'9591 0822 2389 5675',800.00,20),(17,1,'5650 9501 3549 0790',200.00,21),(18,1,'5985 6105 8890 4380',500.00,22),(19,1,'0105 5251 5227 1906',0.00,23),(20,1,'8231 9146 9618 2660',0.00,24),(21,1,'0722 6558 3599 0945',0.00,25),(22,1,'5502 5551 8932 7794',2000.00,27),(23,1,'7584 0468 4528 0362',0.00,28),(24,1,'5899 4608 0153 9198',0.00,29),(25,1,'7060 5139 4004 3615',0.00,30),(26,2,'1491 4098 0684 8673',0.00,3),(27,2,'8405 6670 4538 1926',700.00,14),(28,2,'9215 6390 3277 8769',100.00,11),(29,2,'4892 4924 0673 4929',300.00,17),(30,2,'0675 7635 7286 7370',500.00,12);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loans`
--

DROP TABLE IF EXISTS `loans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `loans` (
  `id` int NOT NULL AUTO_INCREMENT,
  `client` int NOT NULL,
  `start_balance` decimal(10,2) NOT NULL,
  `remaining_balance` decimal(10,2) NOT NULL,
  `months_number` int NOT NULL,
  `payment` decimal(10,2) NOT NULL DEFAULT '0.00',
  `penalty` int NOT NULL DEFAULT '0',
  `date_approved` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `due_date` datetime NOT NULL,
  `last_payment_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `client` (`client`),
  CONSTRAINT `loans_ibfk_1` FOREIGN KEY (`client`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loans`
--

LOCK TABLES `loans` WRITE;
/*!40000 ALTER TABLE `loans` DISABLE KEYS */;
INSERT INTO `loans` VALUES (1,14,1000.00,2000.00,6,200.92,0,'2024-02-10 15:13:21','2024-05-08 00:00:00','2024-02-10 15:18:30'),(2,5,2000.00,2000.00,12,200.92,0,'2024-02-14 11:02:14','2024-05-08 00:00:00','2024-02-15 09:16:26'),(3,15,1500.00,2000.00,12,200.92,0,'2024-02-20 11:53:26','2024-05-08 00:00:00','2024-02-20 12:37:38'),(4,27,2000.00,2000.00,12,200.92,0,'2024-04-08 12:30:17','2024-05-08 00:00:00','2024-04-08 12:30:17');
/*!40000 ALTER TABLE `loans` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `from_acc` int NOT NULL,
  `to_acc` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `withdrawal` tinyint(1) DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `from_acc` (`from_acc`),
  KEY `to_acc` (`to_acc`),
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`from_acc`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `transactions_ibfk_2` FOREIGN KEY (`to_acc`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (1,11,11,300.00,0,'2024-02-10 11:36:18'),(2,11,3,100.00,1,'2024-02-10 14:02:16'),(3,1,11,1000.00,1,'2024-02-10 15:13:21'),(4,11,1,500.00,1,'2024-02-10 15:18:30'),(5,11,27,700.00,1,'2024-02-11 10:21:14'),(6,2,2,1000.00,0,'2024-02-13 12:16:02'),(7,9,9,200.00,0,'2024-02-13 13:02:14'),(8,9,28,100.00,1,'2024-02-13 13:30:12'),(9,1,3,2000.00,1,'2024-02-14 11:02:14'),(10,3,1,600.00,1,'2024-02-15 09:16:26'),(11,16,16,800.00,0,'2024-02-15 12:32:47'),(12,17,17,400.00,0,'2024-02-18 10:06:52'),(13,17,13,200.00,1,'2024-02-18 10:15:36'),(14,14,14,200.00,0,'2024-02-18 12:26:42'),(15,1,12,1500.00,1,'2024-02-20 11:53:26'),(16,12,1,500.00,1,'2024-02-20 12:37:38'),(17,13,4,100.00,1,'2024-03-01 14:53:48'),(18,18,18,500.00,0,'2024-03-07 12:54:38'),(19,13,29,100.00,1,'2024-03-07 13:47:25'),(20,10,10,1000.00,0,'2024-03-10 10:25:38'),(21,10,30,500.00,1,'2024-03-10 10:38:36'),(22,10,29,200.00,1,'2024-03-10 11:04:27'),(23,10,10,200.00,0,'2024-03-10 11:17:26'),(24,1,22,2000.00,1,'2024-04-08 12:30:17');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_types`
--

DROP TABLE IF EXISTS `user_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_types` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_types`
--

LOCK TABLES `user_types` WRITE;
/*!40000 ALTER TABLE `user_types` DISABLE KEYS */;
INSERT INTO `user_types` VALUES (1,'Admin'),(2,'Banker'),(3,'Client');
/*!40000 ALTER TABLE `user_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `email` varchar(70) DEFAULT NULL,
  `phone_number` varchar(10) NOT NULL,
  `pin` varchar(6) NOT NULL,
  `date_of_birth` date NOT NULL,
  `username` varchar(9) NOT NULL,
  `password` varchar(100) NOT NULL,
  `type` int NOT NULL,
  `loan_eligibility` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `phone_number` (`phone_number`),
  UNIQUE KEY `pin` (`pin`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `type` (`type`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`type`) REFERENCES `user_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'John','Smith','jsmith@email.com','0735475687','573526','2008-12-20','jsmith','26dbdf3230ac35d1b2cfa3e0c0af292d',1,0,'2024-01-01 09:16:03'),(2,'Emily','Johnson','ejohnson@email.com','0746587956','657834','2000-02-10','emijoh575','6e32d9f4e2183531a55f41c2bf3cc366',2,0,'2024-01-01 10:02:21'),(3,'Michael','Williams','mwilliams@gmail.com','0783415674','563768','1998-07-09','micwil736','1e0df63d9c25c78446ac6b13223ab75a',3,1,'2024-01-02 10:13:32'),(4,'Sarah','Brown','sbrown@email.com','0794673452','576982','2001-04-23','sarbro967','aa5b654ee2dfd74929d33ea02629a593',2,0,'2024-01-02 10:30:15'),(5,'David','Jones','djones@email.com','0746578345','657934','2000-02-15','davjon993','cf0e1840beb0e20fcb4f88cce02a7c1c',3,0,'2024-01-04 09:20:43'),(6,'Jessica','Davis','jdavis@gmail.com','0784657243','673547','1997-08-10','jesdav554','8ce91c9e5991a7209c4ac3c89afe48ed',3,1,'2024-01-15 13:52:12'),(7,'Robert','Miller','rmiller@gmail.com','0735467243','638254','1999-12-19','robmil434','bc2e26bf721d48d79573a9aae3a445b3',3,1,'2024-01-17 12:05:16'),(8,'Ashley','Wilson','awilson@gmail.com','0735476845','378354','2003-09-14','ashwil447','61ab96559f2e158d234c365042e239f8',3,1,'2024-01-18 14:20:11'),(9,'William','Moore','wmoore@email.com','0793547623','953672','2000-04-27','wilmoo336','130ba14542e342271135ecbbc7da1c22',3,1,'2024-01-20 13:02:13'),(10,'Jennifer','Taylor','jtaylor@gmail.com','0783547683','537845','1998-05-27','jentay557','d79695c54d05bd38c0ca4b72e9e1bb13',3,1,'2024-01-25 15:20:10'),(11,'James','Anderson','janderson@gmail.com','0757354756','568354','2002-10-11','jamand385','aaec3f38bc589ce8a250205591df9c5f',3,1,'2024-01-26 10:07:23'),(12,'Melissa','Thomas','mthomas@email.com','0754768354','578354','2000-03-14','meltho354','6522fd0d5c92e67e36a80a3054580cc9',3,1,'2024-01-30 11:34:18'),(13,'Christopher','Jackson','cjackson@yahoo.com','0763746512','749673','1999-07-20','chrjac446','1a7c4e741940d55f1041ab2387626a19',2,0,'2024-02-05 15:16:29'),(14,'Amanda','White','awhite@gmail.com','0784652438','769354','2004-11-19','amawhi734','e6a6a612aac4390403eccfe68c533133',3,0,'2024-02-08 09:21:05'),(15,'Matthew','Harris','mharris@yahoo.com','0735478363','627945','1996-05-23','mathar766','e513fca5cad8d05a7078539c6e8c2a8f',3,0,'2024-02-13 11:48:21'),(16,'Laura','Martin','lmartin@email.com','0746835126','527894','1992-05-16','laumar422','0006e54487ce05a55e2a355bf7634704',2,0,'2024-02-20 14:15:28'),(17,'Daniel','Thompson','dthompson@gmail.com','0745824367','573548','1995-02-19','dantho574','e6c6a0d788d261438f8507514d23bb1f',3,1,'2024-02-21 12:32:12'),(18,'Elizabeth','Garcia','egarcia@gmail.com','0747893547','263874','1997-08-21','eligar687','a1d407be8ac235f5f731c1de8536139d',3,1,'2024-02-25 09:02:14'),(19,'Kevin','Martinez','kmartinez@email.com','0737845768','735489','2000-06-09','kevmar599','9cfaef0c9e7d65e09fff9e7b59db75e8',3,1,'2024-02-28 12:39:09'),(20,'Megan','Robinson','mrobinson@gmail.com','0735486745','683547','1999-12-03','megrob478','edef3f1c39e976b6a5cc2202bd391cee',3,1,'2024-03-01 16:09:21'),(21,'Brian','Clark','bclark@yahoo.com','0794723156','730573','2000-09-27','bricla703','8aae0bb4c02e93ae5a20214e07e60502',3,1,'2024-03-03 14:56:37'),(22,'Rachel','Rodriguez','rrodriguez@email.com','0735768243','678354','2003-10-19','racrod583','3b12c839fb9777f01f9e6c6fe492ca9f',3,1,'2024-03-07 09:42:08'),(23,'Steven','Lewis','slewis@gmail.com','0783547893','267945','1998-09-27','stelew642','7adbc2248bc6e437d69d267d154aecd5',3,1,'2024-03-10 13:52:12'),(24,'Kimberly','Hall','kimhall@email.com','0794658345','478934','1997-06-01','kimhal347','ddf257235242486181b01d8c46145b78',3,1,'2024-03-15 12:09:27'),(25,'Timothy','Allen','timallen@gmail.com','0794657834','354789','2000-09-13','timall435','594e0b5bae7c1128a113261424e5aea2',3,1,'2024-03-17 14:37:49'),(26,'Michelle','Young','myoung@email.com','0735487945','458243','2001-09-09','micyou444','30fd79807ae8a6db239eec9f66db3294',2,0,'2024-03-20 10:23:05'),(27,'Jason','Hernandez','jhernandez@gmail.com','0793547689','537894','2000-08-04','jasher783','0b56619cc0409146950df7fe071f1e15',3,0,'2024-03-21 15:17:37'),(28,'Rebecca','King','rking@email.com','0793548254','768935','1995-07-11','rebkin638','3dcf73c1ad62e3af99ff3288dbd7d423',3,1,'2024-04-02 11:56:26'),(29,'Eric','Wright','ewright@gmail.com','0793648623','583964','1997-06-23','eriwri454','463461fdc38f7d91df89476675382628',3,1,'2024-04-04 09:27:14'),(30,'Stephanie','Lopez','slopez@email.com','0793547683','563845','1997-03-20','stelop566','83b64c9ed3baccbdfdb383344f060c1f',3,1,'2024-04-08 11:12:26');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-04-09 14:51:03
