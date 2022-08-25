/*
SQLyog Community v13.1.5  (64 bit)
MySQL - 8.0.27 : Database - cc_subscription
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`cc_subscription` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `cc_subscription`;

/*Table structure for table `allowed_no_of_institutional_members` */

DROP TABLE IF EXISTS `allowed_no_of_institutional_members`;

CREATE TABLE `allowed_no_of_institutional_members` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `no_of_members` int DEFAULT NULL,
  `institution_id` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `allowed_no_of_institutional_members` */

/*Table structure for table `feature` */

DROP TABLE IF EXISTS `feature`;

CREATE TABLE `feature` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `display_text` varchar(255) DEFAULT NULL,
  `mark_enabled` tinyint(1) DEFAULT NULL,
  `show_in_display` tinyint(1) DEFAULT NULL,
  `is_required` tinyint(1) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Data for the table `feature` */

insert  into `feature`(`id`,`name`,`display_text`,`mark_enabled`,`show_in_display`,`is_required`,`created_at`,`created_by`,`updated_at`,`updated_by`,`status`) values 
(1,'Standard','It is basic feature of this plan',1,1,1,'2022-04-14 09:24:27',1,'2022-04-14 09:24:27',1,'Active'),
(2,'string','Standard',1,1,1,'2022-04-14 10:12:18',1,'2022-04-14 10:13:14',1,'Deleted');

/*Table structure for table `order_request` */

DROP TABLE IF EXISTS `order_request`;

CREATE TABLE `order_request` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `order_date` date DEFAULT NULL,
  `no_of_user` int DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `price` double DEFAULT NULL,
  `upgrade` tinyint(1) DEFAULT NULL,
  `discount` double DEFAULT NULL,
  `coupon_code` varchar(255) DEFAULT NULL,
  `plan` bigint unsigned DEFAULT NULL,
  `discount_percentage` double DEFAULT NULL,
  `unit_price` double DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `plan` (`plan`),
  CONSTRAINT `order_request_ibfk_1` FOREIGN KEY (`plan`) REFERENCES `plan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Data for the table `order_request` */

insert  into `order_request`(`id`,`order_date`,`no_of_user`,`duration`,`price`,`upgrade`,`discount`,`coupon_code`,`plan`,`discount_percentage`,`unit_price`,`created_at`,`created_by`,`updated_at`,`updated_by`,`status`) values 
(1,'2022-04-02',5,15,1299,1,20,'JDHBD-HJFHJ-UIUIU',1,1,1299,'2022-04-14 10:29:41',1,'2022-04-14 10:29:41',1,NULL),
(2,'2022-04-02',2,10,1299,1,10,'dsasdasdaszxczcz',1,1,1299,'2022-04-14 10:31:44',1,'2022-04-14 11:00:57',1,'Deleted');

/*Table structure for table `payment_history` */

DROP TABLE IF EXISTS `payment_history`;

CREATE TABLE `payment_history` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `transaction` varchar(255) DEFAULT NULL,
  `payment_date` date DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `payment_type` varchar(100) DEFAULT NULL,
  `payment_status` varchar(100) DEFAULT NULL,
  `plan` bigint unsigned DEFAULT NULL,
  `order_request` bigint unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `plan` (`plan`),
  KEY `order_request` (`order_request`),
  CONSTRAINT `payment_history_ibfk_1` FOREIGN KEY (`plan`) REFERENCES `plan` (`id`),
  CONSTRAINT `payment_history_ibfk_2` FOREIGN KEY (`order_request`) REFERENCES `order_request` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

/*Data for the table `payment_history` */

insert  into `payment_history`(`id`,`transaction`,`payment_date`,`amount`,`payment_type`,`payment_status`,`plan`,`order_request`) values 
(1,'10CDHJDERDDF234','2022-04-19',599,'UPI','Completed',1,1),
(2,'XVDGV13SDS','2022-04-18',699,'UPI','Completed',1,1),
(3,'QWERTYUIOPLKJHGFDSA','2022-04-15',1199,'UPI','Completed',1,1);

/*Table structure for table `plan` */

DROP TABLE IF EXISTS `plan`;

CREATE TABLE `plan` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(200) DEFAULT NULL,
  `code` varchar(200) DEFAULT NULL,
  `description` longtext,
  `price` double DEFAULT NULL,
  `is_free` tinyint(1) DEFAULT NULL,
  `weight` int DEFAULT NULL,
  `frequency` varchar(200) DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Data for the table `plan` */

insert  into `plan`(`id`,`name`,`code`,`description`,`price`,`is_free`,`weight`,`frequency`,`duration`,`created_at`,`created_by`,`updated_at`,`updated_by`,`status`) values 
(1,'Basic','PL001','It is a very Basic Plan.',0,1,1,'1 Month',30,'2022-04-14 10:28:09',1,'2022-04-14 11:08:39',1,'Active'),
(2,'Standard','PL002','It is a Standard Plan.',499,0,2,'3 Months',90,'2022-04-18 16:02:11',1,NULL,1,'Active');

/*Table structure for table `plan_config` */

DROP TABLE IF EXISTS `plan_config`;

CREATE TABLE `plan_config` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `plan` bigint unsigned DEFAULT NULL,
  `feature` bigint unsigned DEFAULT NULL,
  `count` int DEFAULT NULL,
  `display_order` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `plan` (`plan`),
  KEY `feature` (`feature`),
  CONSTRAINT `plan_config_ibfk_1` FOREIGN KEY (`plan`) REFERENCES `plan` (`id`),
  CONSTRAINT `plan_config_ibfk_2` FOREIGN KEY (`feature`) REFERENCES `feature` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Data for the table `plan_config` */

insert  into `plan_config`(`id`,`plan`,`feature`,`count`,`display_order`,`created_at`,`created_by`,`updated_at`,`updated_by`,`status`) values 
(1,1,1,2,2,'2022-04-14 11:41:14',1,'2022-04-14 12:20:49',1,NULL),
(2,1,1,5,5,'2022-04-14 12:26:43',1,'2022-04-14 12:27:11',1,'Deleted');

/*Table structure for table `subscription` */

DROP TABLE IF EXISTS `subscription`;

CREATE TABLE `subscription` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `total_price` double DEFAULT NULL,
  `no_of_student` int DEFAULT NULL,
  `is_trial` tinyint(1) DEFAULT NULL,
  `order_request` bigint unsigned DEFAULT NULL,
  `institution_id` bigint DEFAULT NULL,
  `plan` bigint unsigned DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_request` (`order_request`),
  KEY `plan` (`plan`),
  CONSTRAINT `subscription_ibfk_1` FOREIGN KEY (`order_request`) REFERENCES `order_request` (`id`),
  CONSTRAINT `subscription_ibfk_2` FOREIGN KEY (`plan`) REFERENCES `plan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

/*Data for the table `subscription` */

insert  into `subscription`(`id`,`start_date`,`end_date`,`total_price`,`no_of_student`,`is_trial`,`order_request`,`institution_id`,`plan`,`created_at`,`created_by`,`updated_at`,`updated_by`,`status`) values 
(5,'2011-01-01','2012-04-11',199,9,1,1,1,1,'2022-04-18 06:58:44',1,'2022-04-18 07:09:53',1,NULL);

/*Table structure for table `subscription_history` */

DROP TABLE IF EXISTS `subscription_history`;

CREATE TABLE `subscription_history` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `plan` bigint DEFAULT NULL,
  `institution_id` bigint DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `total_price` double DEFAULT NULL,
  `no_of_student` int DEFAULT NULL,
  `order_request` bigint unsigned DEFAULT NULL,
  `subscription` bigint unsigned DEFAULT NULL,
  `is_trial` tinyint(1) DEFAULT NULL,
  `action` varchar(10) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_request` (`order_request`),
  KEY `subscription` (`subscription`),
  CONSTRAINT `subscription_history_ibfk_1` FOREIGN KEY (`order_request`) REFERENCES `order_request` (`id`),
  CONSTRAINT `subscription_history_ibfk_2` FOREIGN KEY (`subscription`) REFERENCES `subscription` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Data for the table `subscription_history` */

insert  into `subscription_history`(`id`,`plan`,`institution_id`,`start_date`,`end_date`,`total_price`,`no_of_student`,`order_request`,`subscription`,`is_trial`,`action`,`created_at`,`created_by`,`updated_at`,`updated_by`,`status`) values 
(1,1,1,'2020-09-09','2011-07-02',399,10,1,5,0,'Action','2022-04-18 07:23:59',1,'2022-04-18 07:55:08',1,'Active');

/*Table structure for table `trial_history` */

DROP TABLE IF EXISTS `trial_history`;

CREATE TABLE `trial_history` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `institution_id` bigint unsigned DEFAULT NULL,
  `plan` bigint unsigned DEFAULT NULL,
  `subscription_history` bigint unsigned DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `plan` (`plan`),
  KEY `subscription_history` (`subscription_history`),
  CONSTRAINT `trial_history_ibfk_1` FOREIGN KEY (`plan`) REFERENCES `plan` (`id`),
  CONSTRAINT `trial_history_ibfk_2` FOREIGN KEY (`subscription_history`) REFERENCES `subscription_history` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Data for the table `trial_history` */

insert  into `trial_history`(`id`,`institution_id`,`plan`,`subscription_history`,`created_at`,`created_by`,`updated_at`,`updated_by`,`status`) values 
(1,1,1,1,'2022-04-19 07:40:21',1,'2022-04-19 07:59:57',1,'Active');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
