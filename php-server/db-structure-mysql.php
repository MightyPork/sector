<?php

// DUMP OF MYSQL DATABASE FOR SECTOR

/*


SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";




CREATE TABLE IF NOT EXISTS `SECTOR_INFO` (
  `key` varchar(30) NOT NULL COMMENT 'Property key',
  `value` text NOT NULL COMMENT 'Property value (text)',
  PRIMARY KEY (`key`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Information for Sector, eg. Info about updates, messages etc';




INSERT INTO `SECTOR_INFO` (`key`, `value`) VALUES
('LEVELS_PATH', 'http://www.ondrovo.com/sector/api/levels/'),
('VERSION', 'Alpha 14'),
('VERSION_NUMBER', '14'),
('LEVELS_PATH_RELATIVE_TO_SERVER', 'levels/');




CREATE TABLE IF NOT EXISTS `SECTOR_LEVELS` (
  `lid` varchar(20) NOT NULL COMMENT 'Level id',
  `title` varchar(120) NOT NULL COMMENT 'Level title (shown is game)',
  `filename` varchar(100) NOT NULL COMMENT 'File name in storage',
  `checksum` varchar(100) NOT NULL COMMENT 'hashcode of the file',
  `time` int(12) unsigned NOT NULL COMMENT 'time of creation',
  PRIMARY KEY (`lid`),
  UNIQUE KEY `title` (`title`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Table of official sector levels';




INSERT INTO `SECTOR_LEVELS` (`lid`, `title`, `filename`, `checksum`, `time`) VALUES
('L-i5dvKm6Fv', 'Demo level', 'alpha14test.xml', 'b9d8db8da9c2ca54e78bf8d2a8a2dc6c', 1356545440);




CREATE TABLE IF NOT EXISTS `SECTOR_SCORES` (
  `id` int(12) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Entry ID',
  `uid` varchar(20) NOT NULL COMMENT 'User ID',
  `lid` varchar(20) NOT NULL COMMENT 'Level ID',
  `time` int(12) NOT NULL COMMENT 'Time when this score has been made',
  `score` int(12) NOT NULL COMMENT 'Highest score for player/level',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COMMENT='Table of SECTOR highscores';




CREATE TABLE IF NOT EXISTS `SECTOR_USERS` (
  `uid` varchar(20) NOT NULL COMMENT 'User ID',
  `name` varchar(50) NOT NULL COMMENT 'User''s nickname',
  `password` varchar(80) NOT NULL COMMENT 'User''s password',
  `auth_token` varchar(120) NOT NULL COMMENT 'Authentication code used to make communication more secure.',
  `email` varchar(120) NOT NULL COMMENT 'Email for password recovery',
  `reg_time` int(12) NOT NULL COMMENT 'Registration timestamp (for stats)',
  `country` varchar(120) NOT NULL DEFAULT '' COMMENT 'Country for stats',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `uname` (`name`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COMMENT='Table of SECTOR''s registered users';


*/