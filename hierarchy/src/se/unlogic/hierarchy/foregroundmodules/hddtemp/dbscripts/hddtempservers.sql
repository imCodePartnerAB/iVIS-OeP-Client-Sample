CREATE TABLE `hddtempservers` (
  `serverID` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `host` varchar(255) NOT NULL,
  `port` varchar(5) NOT NULL,
  `monitor` tinyint(1) NOT NULL,
  `missingDriveWarning` tinyint(1) NOT NULL,
  PRIMARY KEY  (`serverID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;