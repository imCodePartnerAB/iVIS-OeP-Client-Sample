CREATE TABLE `hddtempdrives` (
  `driveID` int(10) unsigned NOT NULL auto_increment,
  `device` varchar(255) NOT NULL,
  `serverID` int(10) unsigned NOT NULL,
  `maxTemp` int(10) unsigned default NULL,
  `minTemp` int(10) unsigned default NULL,
  `lastAlarm` timestamp NULL default NULL,
  PRIMARY KEY  (`driveID`),
  UNIQUE KEY `Index_3` (`device`,`serverID`),
  KEY `FK_hddtempdrives_1` (`serverID`),
  CONSTRAINT `FK_hddtempdrives_1` FOREIGN KEY (`serverID`) REFERENCES `hddtempservers` (`serverID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;