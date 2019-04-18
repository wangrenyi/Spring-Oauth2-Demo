/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : rbac_db

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2019-04-18 18:06:47
*/

CREATE TABLE `auth_client_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `clientId` varchar(100) NOT NULL,
  `clientSecret` varchar(255) NOT NULL,
  `clientName` varchar(100) DEFAULT NULL,
  `redirectUri` varchar(1000) NOT NULL,
  `grantType` varchar(100) DEFAULT 'authorization_code,refresh_token,client_credentials,password,implicit',
  `scope` varchar(100) DEFAULT 'read',
  `description` varchar(1000) DEFAULT NULL,
  `createUser` varchar(50) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `updateUser` varchar(50) DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  `status` int(2) DEFAULT '0' COMMENT '0表示未开通1表示正常使用2表示已被禁用',
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_client_id` (`clientId`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
insert into auth_client_details(clientId, clientSecret, clientName, redirectUri, grantType, scope)
values ('client_1', '123456', 'client01', 'localhost:8080', 'client_credentials,refresh_token', 'select');
insert into auth_client_details(clientId, clientSecret, clientName, redirectUri, grantType, scope)
values ('client_2', '123456', 'client02', 'localhost:8080', 'password,refresh_token', 'select');


CREATE TABLE `mst_user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `age` int(11) NOT NULL,
  `sex` bit(1) DEFAULT b'0',
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
insert into mst_user_info( username, password, age, sex) values('user_1','123456',20,1);
insert into mst_user_info( username, password, age, sex) values('user_2','123456',20,1);

/*
get token:
http://localhost:8081/oauth/token?username=user_1&password=123456&grant_type=password&scope=select&client_id=client_2&client_secret=123456

get resource:
http://localhost:8080/user/hello?access_token=1c15831c-2c38-4b89-acdd-23b515bc4070
*/