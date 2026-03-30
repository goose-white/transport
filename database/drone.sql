/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 90001 (9.0.1)
 Source Host           : localhost:3306
 Source Schema         : drone

 Target Server Type    : MySQL
 Target Server Version : 90001 (9.0.1)
 File Encoding         : 65001

 Date: 30/03/2026 18:34:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for drone
-- ----------------------------
DROP TABLE IF EXISTS `drone`;
CREATE TABLE `drone`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '唯一标识（主键）',
  `model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备类型',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '设备状态 (0 - 待命，1 - 飞行，2 - 维修)',
  `battery` tinyint NULL DEFAULT NULL COMMENT '电池电量',
  `max_speed` decimal(4, 2) NULL DEFAULT NULL COMMENT '最快飞行速度',
  `max_flight_time` int NULL DEFAULT NULL COMMENT '最大续航时间（分钟）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for recognition
-- ----------------------------
DROP TABLE IF EXISTS `recognition`;
CREATE TABLE `recognition`  (
  `id` int NOT NULL COMMENT '识别记录标识',
  `time` datetime NULL DEFAULT NULL COMMENT '识别时间',
  `device_id` int NULL DEFAULT NULL COMMENT '关联 drone_device.id（采集设备）',
  `task_id` int NULL DEFAULT NULL COMMENT '关联 patrol_task.id（所属任务）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '识别结果详情',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '取证图片 / 视频存储路径',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '任务唯一标识',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务唯一名称',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '任务类型（0 - 道路巡航、1 - 水域巡逻、2 - 事故取证、3 - 隐患排查）',
  `device_id` int NOT NULL COMMENT '关联drone_id(执行设备)',
  `start_time` datetime NULL DEFAULT NULL COMMENT '任务开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '任务结束时间',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '任务状态（0-未开始，1-执行中，2-已完成,3-已取消）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1788407815 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
