CREATE DATABASE  IF NOT EXISTS `ludo` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ludo`;
-- MySQL dump 10.13  Distrib 5.6.13, for osx10.6 (i386)
--
-- Host: 127.0.0.1    Database: ludo
-- ------------------------------------------------------
-- Server version	5.6.14

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
-- Table structure for table `audit_trail`
--

DROP TABLE IF EXISTS `audit_trail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `audit_trail` (
  `audit_trail_id` int(11) NOT NULL AUTO_INCREMENT,
  `game_id` int(11) NOT NULL,
  `username` varchar(45) NOT NULL,
  `time` datetime NOT NULL,
  `move_type` varchar(10) NOT NULL,
  `dice_number` tinyint(1) DEFAULT NULL,
  `peg_number` varchar(6) DEFAULT NULL,
  `destination` varchar(10) DEFAULT NULL,
  `rank` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`audit_trail_id`),
  KEY `user_idx` (`username`),
  KEY `game_idx` (`game_id`),
  CONSTRAINT `game` FOREIGN KEY (`game_id`) REFERENCES `game` (`game_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=647 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `game`
--

DROP TABLE IF EXISTS `game`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game` (
  `game_id` int(11) NOT NULL AUTO_INCREMENT,
  `state` varchar(10) NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `current_player_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`game_id`),
  UNIQUE KEY `idgame_UNIQUE` (`game_id`),
  KEY `player_fk_idx` (`current_player_id`),
  CONSTRAINT `player_fk` FOREIGN KEY (`current_player_id`) REFERENCES `player` (`player_id`) ON DELETE SET NULL ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player`
--

DROP TABLE IF EXISTS `player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player` (
  `player_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) DEFAULT NULL,
  `game_id` int(11) DEFAULT NULL,
  `rank` varchar(10) DEFAULT NULL,
  `finished` tinyint(1) unsigned DEFAULT NULL,
  `owner` tinyint(1) NOT NULL DEFAULT '0',
  `color` varchar(10) NOT NULL,
  `last_dice_number` tinyint(1) DEFAULT NULL,
  `before_last_dice_number` tinyint(1) DEFAULT NULL,
  `p1` varchar(4) NOT NULL,
  `p2` varchar(4) NOT NULL,
  `p3` varchar(4) NOT NULL,
  `p4` varchar(4) NOT NULL,
  `last_move_time` datetime DEFAULT NULL,
  PRIMARY KEY (`player_id`),
  UNIQUE KEY `idplayer_UNIQUE` (`player_id`),
  KEY `user_fk_idx` (`username`),
  KEY `game_fk_idx` (`game_id`),
  CONSTRAINT `game_fk` FOREIGN KEY (`game_id`) REFERENCES `game` (`game_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `username` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `password` varchar(64) NOT NULL,
  `avatar` tinyint(2) DEFAULT NULL,
  `salt` bigint(20) NOT NULL,
  `failed_attempt` tinyint(2) DEFAULT '0',
  `blocked` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`username`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-12-10 22:05:09
