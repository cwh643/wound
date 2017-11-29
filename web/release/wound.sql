/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50173
Source Host           : localhost:3306
Source Database       : wound

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2017-06-10 21:23:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `archives_record`
-- ----------------------------
DROP TABLE IF EXISTS `archives_record`;
CREATE TABLE `archives_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` int(11) DEFAULT NULL,
  `doctor_id` int(11) DEFAULT NULL,
  `record_time` datetime DEFAULT NULL,
  `bed` varchar(45) DEFAULT NULL,
  `diagnosis` varchar(45) DEFAULT NULL,
  `department` varchar(45) DEFAULT NULL,
  `is_operation` int(11) DEFAULT NULL,
  `wound_type` int(11) DEFAULT NULL,
  `wound_height` decimal(10,2) DEFAULT NULL,
  `wound_width` decimal(10,2) DEFAULT NULL,
  `wound_deep` decimal(10,2) DEFAULT NULL,
  `wound_area` decimal(10,2) DEFAULT NULL,
  `wound_volume` decimal(10,2) DEFAULT NULL,
  `wound_time` datetime DEFAULT NULL,
  `wound_postion` int(11) DEFAULT NULL,
  `wound_postion_x` int(11) DEFAULT NULL,
  `wound_postion_y` int(11) DEFAULT NULL,
  `wound_measures` int(11) DEFAULT NULL,
  `wound_describe_clean` int(11) DEFAULT NULL,
  `wound_describe_color` int(11) DEFAULT NULL,
  `wound_describe_skin` int(11) DEFAULT NULL,
  `wound_assess_prop` int(11) DEFAULT NULL,
  `wound_assess_infect` int(11) DEFAULT NULL,
  `wound_assess_infect_desc` int(11) DEFAULT NULL,
  `wound_leachate_volume` int(11) DEFAULT NULL,
  `wound_leachate_color` int(11) DEFAULT NULL,
  `wound_leachate_smell` int(11) DEFAULT NULL,
  `wound_heal_all` varchar(255) DEFAULT NULL,
  `wound_heal_position` varchar(255) DEFAULT NULL,
  `wound_doppler` varchar(255) DEFAULT NULL,
  `wound_cta` varchar(255) DEFAULT NULL,
  `wound_mr` varchar(255) DEFAULT NULL,
  `wound_petct` varchar(255) DEFAULT NULL,
  `wound_dressing` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of archives_record
-- ----------------------------

-- ----------------------------
-- Table structure for `doctor_permision`
-- ----------------------------
DROP TABLE IF EXISTS `doctor_permision`;
CREATE TABLE `doctor_permision` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of doctor_permision
-- ----------------------------

-- ----------------------------
-- Table structure for `favorite_patient`
-- ----------------------------
DROP TABLE IF EXISTS `favorite_patient`;
CREATE TABLE `favorite_patient` (
  `id` int(11) NOT NULL,
  `userid` int(11) DEFAULT NULL,
  `patient_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of favorite_patient
-- ----------------------------

-- ----------------------------
-- Table structure for `model_info`
-- ----------------------------
DROP TABLE IF EXISTS `model_info`;
CREATE TABLE `model_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `archives_id` int(11) DEFAULT NULL,
  `patient_id` int(11) DEFAULT NULL,
  `rgb_image` blob,
  `hot_image` blob,
  `model_image` blob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of model_info
-- ----------------------------

-- ----------------------------
-- Table structure for `patient_info`
-- ----------------------------
DROP TABLE IF EXISTS `patient_info`;
CREATE TABLE `patient_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `sex` varchar(45) DEFAULT NULL,
  `birthday` datetime DEFAULT NULL,
  `department` varchar(45) DEFAULT NULL,
  `bed_no` varchar(45) DEFAULT NULL,
  `inpatient_no` varchar(45) DEFAULT NULL,
  `doctor` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of patient_info
-- ----------------------------

-- ----------------------------
-- Table structure for `user_relation`
-- ----------------------------
DROP TABLE IF EXISTS `user_relation`;
CREATE TABLE `user_relation` (
  `id` int(11) NOT NULL,
  `userid` int(11) DEFAULT NULL,
  `parent_userid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_relation
-- ----------------------------

-- ----------------------------
-- Table structure for `woutask`
-- ----------------------------
DROP TABLE IF EXISTS `woutask`;
CREATE TABLE `woutask` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(128) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of woutask
-- ----------------------------
INSERT INTO `woutask` VALUES ('6', 'good', 'OK', '1');

-- ----------------------------
-- Table structure for `wouuser`
-- ----------------------------
DROP TABLE IF EXISTS `wouuser`;
CREATE TABLE `wouuser` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `login_name` varchar(64) NOT NULL DEFAULT '登录名',
  `name` varchar(64) NOT NULL DEFAULT '姓名',
  `password` varchar(255) NOT NULL DEFAULT '密码',
  `salt` varchar(64) NOT NULL,
  `roles` varchar(255) NOT NULL,
  `type` tinyint(4) NOT NULL DEFAULT '1',
  `register_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `department` varchar(64) DEFAULT '' COMMENT '科室',
  `hospital` varchar(255) DEFAULT NULL,
  `login_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login_name` (`login_name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of wouuser
-- ----------------------------
INSERT INTO `wouuser` VALUES ('1', 'admin', 'Admin', 'a71db71af245cc8380fe6e7bde3b8afa194f8990', '43b526276f4d5f9b', 'admin', '0', '2012-06-04 01:00:00', '科室', null, null);
INSERT INTO `wouuser` VALUES ('8', 'user', '测试医生', '9b0b1cc7e0e598868dddbc2cdfa8c01b22192d80', 'ed80d456481e26f6', 'user', '1', '2017-06-10 10:06:32', '创伤科', null, null);
