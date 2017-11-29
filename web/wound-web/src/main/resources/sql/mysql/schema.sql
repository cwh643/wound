/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50173
Source Host           : localhost:3306
Source Database       : wound

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2017-06-23 20:29:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `archives_record`
-- ----------------------------
DROP TABLE IF EXISTS `archives_record`;
CREATE TABLE `archives_record` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`device_id`  varchar(64) NOT NULL ,
`record_id`  int(11) NOT NULL ,
`doctor_id`  int(11) NOT NULL ,
`inpatient_no`  varchar(64) NOT NULL ,
`record_time`  varchar(64) NOT NULL ,
`bed`  varchar(45) NULL DEFAULT NULL ,
`diagnosis`  varchar(45) NULL DEFAULT NULL ,
`department`  varchar(45) NULL DEFAULT NULL ,
`is_operation`  int(11) NULL DEFAULT NULL ,
`wound_type`  int(11) NULL DEFAULT NULL ,
`wound_height`  decimal(10,2) NULL DEFAULT NULL ,
`wound_width`  decimal(10,2) NULL DEFAULT NULL ,
`wound_deep`  decimal(10,2) NULL DEFAULT NULL ,
`wound_area`  decimal(10,2) NULL DEFAULT NULL ,
`wound_volume`  decimal(10,2) NULL DEFAULT NULL ,
`wound_time`  varchar(64) NULL DEFAULT NULL ,
`wound_postion`  int(11) NULL DEFAULT NULL ,
`wound_postionx`  int(11) NULL DEFAULT NULL ,
`wound_postiony`  int(11) NULL DEFAULT NULL ,
`wound_measures`  int(11) NULL DEFAULT NULL ,
`wound_describe_clean`  int(11) NULL DEFAULT NULL ,
`wound_color_red`  decimal(10,2) NULL DEFAULT NULL ,
`wound_color_yellow`  decimal(10,2) NULL DEFAULT NULL ,
`wound_color_black`  decimal(10,2) NULL DEFAULT NULL ,
`wound_describe_color`  int(11) NULL DEFAULT NULL ,
`wound_describe_skin`  int(11) NULL DEFAULT NULL ,
`wound_assess_prop`  int(11) NULL DEFAULT NULL ,
`wound_assess_infect`  int(11) NULL DEFAULT NULL ,
`wound_assess_infect_desc`  varchar(255) NULL DEFAULT NULL ,
`wound_leachate_volume`  int(11) NULL DEFAULT NULL ,
`wound_leachate_color`  int(11) NULL DEFAULT NULL ,
`wound_leachate_smell`  int(11) NULL DEFAULT NULL ,
`wound_heal_all`  varchar(255) NULL DEFAULT NULL ,
`wound_heal_position`  varchar(255) NULL DEFAULT NULL ,
`wound_doppler`  varchar(255) NULL DEFAULT NULL ,
`wound_cta`  varchar(255) NULL DEFAULT NULL ,
`wound_mr`  varchar(255) NULL DEFAULT NULL ,
`wound_petct`  varchar(255) NULL DEFAULT NULL ,
`wound_dressing`  varchar(255) NULL DEFAULT NULL ,
`wound_dressing_type`  int(11) NULL DEFAULT NULL ,
`complains`  VARCHAR(255) NULL DEFAULT NULL ,
`favorites`  int(11) NULL DEFAULT NULL ,
`uuid`  VARCHAR(64) NOT NULL ,
`create_time`  datetime NULL DEFAULT NULL ,
`update_time`  datetime NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- ----------------------------
-- Records of archives_record
-- ----------------------------
-- BEGIN;
-- INSERT INTO `archives_record` VALUES ('1', '8', '123455', '2017-06-19 22:19:32', null, null, null, '0', '1', null, null, null, null, null, '2017-06-12', null, null, null, '1', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
-- COMMIT;

 CREATE TABLE record_image (
	 `id`  int(11) NOT NULL AUTO_INCREMENT ,
	`record_id`  int(11) NOT NULL ,
 	`image_type`  varchar(32) NULL DEFAULT NULL,
 	`image_path`  varchar(255) NULL DEFAULT NULL,
 	`create_time`  datetime NULL DEFAULT NULL,
 	`update_time`  datetime NULL DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- ----------------------------
-- Table structure for `doctor_permision`
-- ----------------------------
DROP TABLE IF EXISTS `doctor_permision`;
CREATE TABLE `doctor_permision` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`patient_id`  int(11) NULL DEFAULT NULL ,
`user_id`  int(11) NULL DEFAULT NULL ,
`type`  int(11) NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- ----------------------------
-- Records of doctor_permision
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for `favorite_patient`
-- ----------------------------
DROP TABLE IF EXISTS `favorite_patient`;
CREATE TABLE `favorite_patient` (
`id`  int(11) NOT NULL ,
`userid`  int(11) NULL DEFAULT NULL ,
`patient_id`  int(11) NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- ----------------------------
-- Records of favorite_patient
-- ----------------------------
BEGIN;
COMMIT;


-- ----------------------------
-- Table structure for `patient_info`
-- ----------------------------
DROP TABLE IF EXISTS `patient_info`;
CREATE TABLE `patient_info` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`device_id`  varchar(64) NOT NULL ,
`patient_id`  int(11) NOT NULL ,
`inpatient_no`  varchar(64) NOT NULL ,
`name`  varchar(64) NOT NULL ,
`sex`  varchar(5) NULL DEFAULT NULL ,
`birthday`  datetime NULL DEFAULT NULL ,
`age`  varchar(5) NULL DEFAULT NULL ,
`department`  varchar(10)  NULL DEFAULT NULL ,
`bed_no`  varchar(5) NULL DEFAULT NULL ,
`diagnosis`  varchar(1024) NULL DEFAULT NULL ,
`doctor`  varchar(64) NULL DEFAULT NULL ,
`doctor_id`  int(11) NOT NULL ,
`uuid`  VARCHAR(64) NOT NULL ,
`create_time`  datetime NULL DEFAULT NULL ,
`update_time`  datetime NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- ----------------------------
-- Table structure for `user_relation`
-- ----------------------------
DROP TABLE IF EXISTS `user_relation`;
CREATE TABLE `user_relation` (
`id`  int(11) NOT NULL ,
`userid`  int(11) NULL DEFAULT NULL ,
`parent_userid`  int(11) NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Records of user_relation
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for `woutask`
-- ----------------------------
DROP TABLE IF EXISTS `woutask`;
CREATE TABLE `woutask` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`title`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`description`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`user_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=7

;

-- ----------------------------
-- Records of woutask
-- ----------------------------
BEGIN;
INSERT INTO `woutask` VALUES ('6', 'good', 'OK', '1');
COMMIT;

-- ----------------------------
-- Table structure for `wouuser`
-- ----------------------------
DROP TABLE IF EXISTS `wouuser`;
CREATE TABLE `wouuser` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`login_name`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '登录名' ,
`name`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '姓名' ,
`password`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '密码' ,
`salt`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`roles`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`type`  tinyint(4) NOT NULL DEFAULT 1 ,
`register_date`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`department`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '科室' ,
`hospital`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`login_time`  datetime NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=9

;

-- ----------------------------
-- Records of wouuser
-- ----------------------------
BEGIN;
INSERT INTO `wouuser` VALUES ('1', 'admin', 'Admin', 'a71db71af245cc8380fe6e7bde3b8afa194f8990', '43b526276f4d5f9b', 'admin', '0', '2012-06-04 01:00:00', '科室', null, null), ('8', 'user', '测试医生', '9b0b1cc7e0e598868dddbc2cdfa8c01b22192d80', 'ed80d456481e26f6', 'user', '1', '2017-06-10 10:06:32', '创伤科', null, null);
COMMIT;

-- ----------------------------
-- Auto increment value for `woutask`
-- ----------------------------
ALTER TABLE `woutask` AUTO_INCREMENT=7;

-- ----------------------------
-- Indexes structure for table wouuser
-- ----------------------------
CREATE UNIQUE INDEX `login_name` USING BTREE ON `wouuser`(`login_name`) ;

-- ----------------------------
-- Auto increment value for `wouuser`
-- ----------------------------
ALTER TABLE `wouuser` AUTO_INCREMENT=9;
