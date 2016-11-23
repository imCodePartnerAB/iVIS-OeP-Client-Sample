CREATE TABLE `pages` (
  `pageID` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL default '',
  `description` varchar(255) NOT NULL default '',
  `text` mediumtext NOT NULL,
  `enabled` varchar(45) NOT NULL default '',
  `visibleInMenu` tinyint(1) NOT NULL default '0',
  `anonymousAccess` tinyint(1) NOT NULL default '0',
  `userAccess` tinyint(1) NOT NULL default '0',
  `adminAccess` tinyint(1) NOT NULL default '0',
  `sectionID` int(10) unsigned NOT NULL default '0',
  `alias` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`pageID`),
  UNIQUE KEY `Index_3` (`sectionID`,`alias`),
  KEY `FK_pages_1` (`sectionID`),
  CONSTRAINT `FK_pages_1` FOREIGN KEY (`sectionID`) REFERENCES `sections` (`sectionID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1